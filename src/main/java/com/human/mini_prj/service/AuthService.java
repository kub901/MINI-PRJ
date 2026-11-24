package com.human.mini_prj.service;

import com.human.mini_prj.constant.Authority;
import com.human.mini_prj.constant.Provider;
import com.human.mini_prj.dto.KakaoUserInfo;
import com.human.mini_prj.dto.MemberResDto;
import com.human.mini_prj.dto.SignUpReqDto;
import com.human.mini_prj.dto.TokenDto;
import com.human.mini_prj.entity.Member;
import com.human.mini_prj.entity.RefreshToken;
import com.human.mini_prj.exception.CustomException;
import com.human.mini_prj.repository.MemberRepository;
import com.human.mini_prj.repository.RefreshTokenRepository;
import com.human.mini_prj.security.SecurityUtil;
import com.human.mini_prj.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


@Slf4j  // log 메시지 출력을 위해 사용
@Service  // Spring Container에 Bean 등록
@Transactional // 트랜잭션 처리: 여러개의 물리적인 작업 단위를 한개의 논리적인 단위로 묶음
@RequiredArgsConstructor  // 생성자를 통한 의존성 주입을 자동으로 만들어 줌
public class AuthService {
    private final MemberRepository memberRepository;  // 생성자를 통한 의존성 주입
    private final RefreshTokenRepository refreshTokenRepository;  // 리플레시 토큰
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final KakaoOAuthService kakaoOAuthService; // 카카오 통신 부분

    // 회원 가입 여부 확인
    public boolean isDuplicatedEmail(String userId) {
        return memberRepository.existsByEmail(userId);
    }

    // 회원 가입
    public boolean signUp(SignUpReqDto dto) {
        try {
            Member member = toEntity(dto);
            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            log.error("회원 가입 시 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    // 로그인: 성공 → Access + Refresh 토큰 발급, Refresh는 DB 저장
    public TokenDto login(String userId, String pwd) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(this::loginFailed);
        if (member.isWithdrawn()) {
            throw new IllegalStateException("탈퇴 처리된 계정입니다.");
        }
        // 카카오 회원은 일반 로그인 불가 (카카오 회원은 비밀번호가 없음)
        if (member.getPwd() == null || !passwordEncoder.matches(pwd, member.getPwd())) {   // 평문 vs 해시 비교
            throw loginFailed();
        }
        TokenDto tokenDto = tokenProvider.createToken(userId, member.getAuthority());
        saveRefreshToken(member, tokenDto);   // 추가
        return tokenDto;
    }


    // 카카오 로그인: 인가 코드 -> 카카오 사용자 확인 -> 우리 JWT 발급
    public TokenDto kakaoLogin(String code){
        String kakaoToken = kakaoOAuthService.getAccessToken(code);
        KakaoUserInfo info = kakaoOAuthService.getUserInfo(kakaoToken);

        // 처음 이면 자동 가입, 이미 있으면 그대로 사용
        Member member = memberRepository.findByProviderAndProviderId(Provider.KAKAO, info.id())
                .orElseGet(() -> memberRepository.save(toKakaoMember(info)));

        // 여기부터는 일반 로그인과 완전히 같음 (기존 코드 재사용)
        TokenDto tokenDto = tokenProvider.createToken(member.getUserId(), member.getAuthority());
        saveRefreshToken(member, tokenDto);
        return tokenDto;

    }

    // ★ 카카오 사용자 → Member
    private Member toKakaoMember(KakaoUserInfo info) {
        // ① 카카오에서 필수 정보가 아예 안 넘어왔는지 방어
        if (info == null || info.id() == null || info.id().isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "KAKAO_INFO_MISSING", "카카오로부터 필수 사용자 정보를 가져오지 못했습니다.");
        }

        Member member = new Member();
        member.setUserId("kakao_" + info.id());
        member.setEmail("kakao_" + info.id());

        // ② 닉네임이 비어있는 비정상 케이스 방어
        if (info.nickname() == null || info.nickname().isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "KAKAO_NICKNAME_MISSING", "카카오 닉네임 정보가 제공되지 않았습니다.");
        }
        member.setName(info.nickname());

        member.setImage(info.profileImage());
        member.setProvider(Provider.KAKAO);
        member.setProviderId(info.id());
        member.setAuthority(Authority.ROLE_USER);
        return member;
    }


    // 재발급: Refresh 검증 → DB 값과 비교 → 새 토큰 쌍 발급 + DB 교체 (Rotation)
    // 재사용 감지 시 DB 토큰을 지우고 예외를 던지므로, 삭제가 롤백되지 않게 noRollbackFor 지정
    @Transactional(noRollbackFor = CustomException.class)
    public TokenDto reissue(String refreshToken) {
        // ① 서명·만료·종류(refresh) 검증
        if (!tokenProvider.validate(refreshToken, TokenProvider.REFRESH)) {
            throw invalidRefresh();
        }
        String userId = tokenProvider.getUserId(refreshToken);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(this::invalidRefresh);

        // ② DB에 저장된 토큰 조회 (로그아웃·강제 만료됐다면 없음)
        RefreshToken saved = refreshTokenRepository.findByMember(member)
                .orElseThrow(this::invalidRefresh);

        // ③ 서명은 유효한데 DB 값과 다름 = 이미 교체된 옛 토큰 재사용 → 탈취로 판단
        if (!saved.getToken().equals(refreshToken)) {
            refreshTokenRepository.delete(saved);   // 공격자·사용자 모두 재로그인 필요
            log.warn("Refresh Token 재사용 감지 → DB 토큰 삭제: {}", userId);
            throw new CustomException(HttpStatus.UNAUTHORIZED, "REFRESH_REUSED",
                    "토큰 재사용이 감지되어 로그아웃되었습니다. 다시 로그인하세요.");
        }

        // ④ 새 토큰 쌍 발급 + DB 값 교체 → 방금 쓴 Refresh는 더 이상 사용할 수 없음
        TokenDto newToken = tokenProvider.createToken(member.getUserId(), member.getAuthority());
        saved.setToken(newToken.refreshToken());
        saved.setExpiresAt(toLocalDateTime(newToken.refreshTokenExpiresIn()));
        return newToken;   // 변경 감지(Dirty Checking)로 UPDATE
    }

    // 로그아웃: 내 Refresh Token 삭제 → 더 이상 재발급 불가
    public void logout(String userId) {
        memberRepository.findByUserId(userId)
                .ifPresent(refreshTokenRepository::deleteByMember);
    }

    // 관리자 강제 만료: 탈취 신고 시 해당 회원의 Refresh Token 삭제
    public void revoke(String userId) {
        refreshTokenRepository.deleteByMemberUserId(userId);
    }

    // 마이페이지 회원 정보 조회
    @Transactional(readOnly = true)
    public MemberResDto getMemberInfo() {

        String userId = SecurityUtil.currentUserId();

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        MemberResDto dto = new MemberResDto();
        dto.setId(member.getId());
        dto.setUserId(member.getUserId());
        dto.setEmail(member.getEmail());
        dto.setName(member.getName());
        dto.setImage(member.getImage());
        dto.setPhone(member.getPhone());
        dto.setRegDate(member.getRegDate());

        return dto;
    }

    // 회원당 1개: 있으면 덮어쓰고, 없으면 새로 저장
    private void saveRefreshToken(Member member, TokenDto tokenDto) {
        RefreshToken refreshToken = refreshTokenRepository.findByMember(member)
                .orElseGet(() -> new RefreshToken(member));
        refreshToken.setToken(tokenDto.refreshToken());
        refreshToken.setExpiresAt(toLocalDateTime(tokenDto.refreshTokenExpiresIn()));
        refreshTokenRepository.save(refreshToken);
    }

    private LocalDateTime toLocalDateTime(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }


    private CustomException loginFailed() {
        // 이메일/비밀번호 중 무엇이 틀렸는지 알려주지 않는 것이 보안상 안전
        return new CustomException(HttpStatus.UNAUTHORIZED, "LOGIN_FAILED", "이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    private CustomException invalidRefresh() {
        return new CustomException(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH", "Refresh Token이 유효하지 않습니다. 다시 로그인하세요.");
    }
    public boolean existsUserId(String userId) {
        return memberRepository.existsByUserId(userId);
    }

    // DTO -> Entity
    private Member toEntity(SignUpReqDto dto) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setPwd(passwordEncoder.encode(dto.getPwd()));
        member.setUserId(dto.getUserId());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        return member;

    }

}
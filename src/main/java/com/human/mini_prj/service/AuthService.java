package com.human.mini_prj.service;

import com.human.mini_prj.dto.LoginResDto;
import com.human.mini_prj.dto.SignUpReqDto;
import com.human.mini_prj.entity.Member;
import com.human.mini_prj.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;

    // 회원가입
    public boolean signUp(SignUpReqDto dto) {
        try {
            if (memberRepository.existsByUserId(dto.getUserId())) {
                return false;
            }

            if (memberRepository.existsByEmail(dto.getEmail())) {
                return false;
            }

            Member member = new Member();

            member.setUserId(dto.getUserId());
            member.setName(dto.getName());
            member.setPhone(dto.getPhone());
            member.setEmail(dto.getEmail());
            member.setPwd(dto.getPwd());

            memberRepository.save(member);

            return true;

        } catch (Exception e) {
            log.error("회원가입 오류", e);
            return false;
        }
    }

    // 아이디 로그인
    public LoginResDto login(String userId, String pwd) {

        Optional<Member> member =
                memberRepository.findByUserIdAndPwd(userId, pwd);

        if (member.isPresent()) {
            return new LoginResDto(member.get());
        }

        return null;
    }

    // MyPage 회원정보 조회
    public LoginResDto getMemberInfo(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new RuntimeException("회원이 존재하지 않습니다."));

        return new LoginResDto(member);
    }

    // 아이디 중복 확인
    public boolean existsUserId(String userId) {
        return memberRepository.existsByUserId(userId);
    }
    // 이메일 중복 확인
    public boolean isDuplicatedEmail(String email) {
        return memberRepository.existsByEmail(email);
    }
}
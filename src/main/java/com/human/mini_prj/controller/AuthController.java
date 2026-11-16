package com.human.mini_prj.controller;

import com.human.mini_prj.dto.*;
import com.human.mini_prj.security.SecurityUtil;
import com.human.mini_prj.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.human.mini_prj.dto.ReissueReqDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;




    // 아이디 중복 확인
    @GetMapping("/exists/id/{userId}")
    public boolean existsUserId(@PathVariable String userId) {
        return authService.existsUserId(userId);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Boolean> signUp(@RequestBody SignUpReqDto dto) {
        return ResponseEntity.ok(authService.signUp(dto));
    }

    // 마이페이지 회원 정보 조회
    @GetMapping("/mypage")
    public ResponseEntity<MemberResDto> getMyPage() {
        return ResponseEntity.ok(authService.getMemberInfo());
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(
            @RequestBody LoginReqDto dto) {
        return ResponseEntity.ok(authService.login(dto.getUserId(), dto.getPwd()));
    }
    // 재발급
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody ReissueReqDto dto) {
        return ResponseEntity.ok(authService.reissue(dto.refreshToken()));
    }
    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout(SecurityUtil.currentUserId());
        return ResponseEntity.noContent().build();
    }
    // 카카오 로그인: 인카 코드--> 우리 서비스 토큰 발행
    @PostMapping("/kakao")
    public ResponseEntity<TokenDto> kakaoLogin (@RequestBody KakaoLoginReqDto dto) {
        return ResponseEntity.ok(authService.kakaoLogin(dto.code()));
    }
}
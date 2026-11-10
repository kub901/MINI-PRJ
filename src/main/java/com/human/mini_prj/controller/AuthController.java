package com.human.mini_prj.controller;

import com.human.mini_prj.dto.LoginReqDto;
import com.human.mini_prj.dto.LoginResDto;
import com.human.mini_prj.dto.SignUpReqDto;
import com.human.mini_prj.dto.TokenDto;
import com.human.mini_prj.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 이메일 중복 확인
    @GetMapping("/exists/{email}")
    public boolean existsEmail(@PathVariable String email) {
        return authService.isDuplicatedEmail(email);
    }

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
    @GetMapping("/mypage/{memberId}")
    public ResponseEntity<LoginResDto> getMyPage(
            @PathVariable Long memberId) {

        return ResponseEntity.ok(authService.getMemberInfo(memberId));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(
            @RequestBody LoginReqDto dto) {
        return ResponseEntity.ok(authService.login(dto.getUserId(), dto.getPwd()));
    }
}
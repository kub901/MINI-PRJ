package com.human.mini_prj.controller;

import com.human.mini_prj.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/mypage")
@RequiredArgsConstructor
public class MemberWithdrawController {

    private final MemberService memberService;

    @PostMapping("/withdraw")
    public ResponseEntity withdraw(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "인증이 필요합니다."));
        }

        memberService.withdraw(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "회원탈퇴가 완료되었습니다."));
    }
}
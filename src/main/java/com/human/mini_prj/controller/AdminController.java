package com.human.mini_prj.controller;

import com.human.mini_prj.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AuthService authservice;

    // 탈취 신고 시 강제 로그아웃
    @DeleteMapping("/token/{email}")
    public ResponseEntity<Void> revoke(@PathVariable String userId) {
        authservice.revoke(userId);
        return ResponseEntity.noContent().build();
    }

}
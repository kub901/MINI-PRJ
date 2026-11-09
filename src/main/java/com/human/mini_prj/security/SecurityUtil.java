package com.human.mini_prj.security;

import com.human.mini_prj.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // permitAll 경로에서는 "anonymousUser"가 들어 있으므로 함께 걸러냄
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인이 필요합니다.");
        }
        return auth.getName();   // 토큰의 sub = email
    }
}
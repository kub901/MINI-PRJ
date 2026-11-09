package com.human.mini_prj.security;

import com.human.mini_prj.constant.Authority;
import com.human.mini_prj.dto.TokenDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class TokenProvider {
    private static final String AUTH_KEY = "auth";
    private final SecretKey key;
    private final long accessExpMs;

    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.access-exp-ms}") long accessExpMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessExpMs = accessExpMs;
    }

    // 로그인 성공 시 토큰 발급
    public TokenDto createToken(String userId, Authority authority) {
        long now = System.currentTimeMillis();
        Date exp = new Date(now + accessExpMs);
        String token = Jwts.builder()
                .subject(userId)                     // sub: 이메일
                .claim(AUTH_KEY, authority.name())  // auth: ROLE_USER
                .issuedAt(new Date(now))
                .expiration(exp)
                .signWith(key)                      // 64바이트 키 → HS512
                .compact();
        return new TokenDto("Bearer", token, exp.getTime());
    }

    // 서명 + 만료 검증
    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT");
        } catch (JwtException | IllegalArgumentException e) {
            log.info("잘못된 JWT: {}", e.getMessage());
        }
        return false;
    }

    // 토큰 → Authentication (DB 조회 없음 = Stateless)
    public Authentication getAuthentication(String token) {
        Claims claims = parse(token);
        var authorities = List.of(new SimpleGrantedAuthority(claims.get(AUTH_KEY, String.class)));
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }
}
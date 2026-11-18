
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
import java.util.UUID;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTH_KEY = "auth";
    private static final String TYPE_KEY = "typ";
    private static final String INSTANCE_KEY = "serverInstance";

    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    private final SecretKey key;
    private final long accessExpMs;
    private final long refreshExpMs;

    // Spring Boot가 시작될 때마다 새로 생성되는 서버 식별값
    private final String serverInstanceId = UUID.randomUUID().toString();

    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.access-exp-ms}") long accessExpMs,
                         @Value("${jwt.refresh-exp-ms}") long refreshExpMs) {

        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessExpMs = accessExpMs;
        this.refreshExpMs = refreshExpMs;

        log.info("현재 서버 식별값: {}", serverInstanceId);
    }

    public TokenDto createToken(String userId, Authority authority) {

        long now = System.currentTimeMillis();

        Date accessExp = new Date(now + accessExpMs);
        Date refreshExp = new Date(now + refreshExpMs);

        log.info("========== JWT 발급 확인 ==========");
        log.info("Access 만료 설정: {}ms", accessExpMs);
        log.info("Access 발급 시간: {}", new Date(now));
        log.info("Access 만료 시간: {}", accessExp);

        String accessToken = Jwts.builder()
                .subject(userId)
                .claim(AUTH_KEY, authority.name())
                .claim(TYPE_KEY, ACCESS)
                .claim(INSTANCE_KEY, serverInstanceId)
                .issuedAt(new Date(now))
                .expiration(accessExp)
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(userId)
                .claim(TYPE_KEY, REFRESH)
                .claim(INSTANCE_KEY, serverInstanceId)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(now))
                .expiration(refreshExp)
                .signWith(key)
                .compact();

        return new TokenDto(
                "Bearer",
                accessToken,
                accessExp.getTime(),
                refreshToken,
                refreshExp.getTime()
        );
    }

    public boolean validate(String token, String expectedType) {

        try {
            Claims claims = parse(token);

            String actualType = claims.get(TYPE_KEY, String.class);
            String tokenInstanceId = claims.get(INSTANCE_KEY, String.class);

            // 토큰 종류 확인
            if (!expectedType.equals(actualType)) {
                log.warn("JWT 종류 불일치: {}", actualType);
                return false;
            }

            // 서버 재시작 전 발급된 토큰인지 확인
            if (!serverInstanceId.equals(tokenInstanceId)) {
                log.warn("이전 서버에서 발급된 JWT입니다.");
                return false;
            }

            log.info("JWT 검증 성공");
            log.info("토큰 종류: {}", actualType);
            log.info("회원 ID: {}", claims.getSubject());

            return true;

        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT: {}", e.getMessage());

        } catch (JwtException | IllegalArgumentException e) {
            log.error("잘못된 JWT: {}", e.getMessage());
        }

        return false;
    }

    public String getUserId(String token) {
        return parse(token).getSubject();
    }

    public Authentication getAuthentication(String token) {

        Claims claims = parse(token);

        var authorities = List.of(
                new SimpleGrantedAuthority(
                        claims.get(AUTH_KEY, String.class)
                )
        );

        User principal = new User(
                claims.getSubject(),
                "",
                authorities
        );

        return new UsernamePasswordAuthenticationToken(
                principal,
                token,
                authorities
        );
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
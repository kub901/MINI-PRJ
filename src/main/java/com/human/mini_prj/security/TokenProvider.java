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
    private static final String AUTH_KEY = "auth";   // 권한 클레임
    private static final String TYPE_KEY = "typ";    // 토큰 종류 클레임 ★
    public static final String ACCESS = "access";    // ★
    public static final String REFRESH = "refresh";  // ★

    private final SecretKey key;
    private final long accessExpMs;
    private final long refreshExpMs;                 // ★

    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.access-exp-ms}") long accessExpMs,
                         @Value("${jwt.refresh-exp-ms}") long refreshExpMs) {   // ★
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessExpMs = accessExpMs;
        this.refreshExpMs = refreshExpMs;
    }

    // 로그인·재발급 시 Access + Refresh 한 쌍 발급
    public TokenDto createToken(String userId, Authority authority) {
        long now = System.currentTimeMillis();
        Date accessExp = new Date(now + accessExpMs);
        Date refreshExp = new Date(now + refreshExpMs);

        String accessToken = Jwts.builder()
                .subject(userId)                       // sub: 이메일
                .claim(AUTH_KEY, authority.name())    // auth: ROLE_USER
                .claim(TYPE_KEY, ACCESS)              // typ: access ★
                .issuedAt(new Date(now))
                .expiration(accessExp)
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()          // ★ Refresh 발급
                .subject(userId)
                .claim(TYPE_KEY, REFRESH)             // typ: refresh (권한 정보 없음)
                .id(UUID.randomUUID().toString())     // jti: 같은 초에 발급해도 값이 달라지도록
                .issuedAt(new Date(now))
                .expiration(refreshExp)
                .signWith(key)
                .compact();

        return new TokenDto("Bearer", accessToken, accessExp.getTime(),
                refreshToken, refreshExp.getTime());
    }

    // 서명 + 만료 + 토큰 종류 검증 ★ expectedType 인자 추가
    public boolean validate(String token, String expectedType) {
        try {
            Claims claims = parse(token);
            return expectedType.equals(claims.get(TYPE_KEY, String.class));
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT");
        } catch (JwtException | IllegalArgumentException e) {
            log.info("잘못된 JWT: {}", e.getMessage());
        }
        return false;
    }

    // 토큰의 subject(userId) ★
    public String getUserId(String token) {
        return parse(token).getSubject();
    }

    // Access 토큰 → Authentication (DB 조회 없음 = Stateless)
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
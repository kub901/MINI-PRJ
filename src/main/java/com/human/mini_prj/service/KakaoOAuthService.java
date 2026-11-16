package com.human.mini_prj.service;

import com.human.mini_prj.dto.KakaoUserInfo;
import com.human.mini_prj.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

// 카카오 서버와 통신하는 부분만 담당
@Slf4j
@Service
public class KakaoOAuthService {
    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_URL = "https://kapi.kakao.com/v2/user/me";
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient = RestClient.create();   // Spring 6.1+ HTTP 클라이언트
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public KakaoOAuthService(@Value("${kakao.client-id}") String clientId,
                             @Value("${kakao.client-secret}") String clientSecret,
                             @Value("${kakao.redirect-uri}") String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }

    // ① 인가 코드 → 카카오 Access Token
    public String getAccessToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", clientId);          // REST API 키
        form.add("redirect_uri", redirectUri);    // 인가 코드 요청 때와 같은 값
        form.add("code", code);
        form.add("client_secret", clientSecret);  // 백엔드에서만 사용 (브라우저 노출 금지)
        try {
            Map<String, Object> res = restClient.post()
                    .uri(TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)   // JSON 아님!
                    .body(form)
                    .retrieve()
                    .body(MAP_TYPE);
            return (String) res.get("access_token");
        } catch (RestClientException e) {
            log.warn("카카오 토큰 요청 실패: {}", e.getMessage());
            throw kakaoFailed();
        }
    }

    // ② 카카오 Access Token → 사용자 정보
    @SuppressWarnings("unchecked")
    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        try {
            Map<String, Object> res = restClient.get()
                    .uri(USER_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .body(MAP_TYPE);

            String id = String.valueOf(res.get("id"));   // 카카오 회원번호 (항상 제공)
            Map<String, Object> account = (Map<String, Object>) res.get("kakao_account");
            Map<String, Object> profile = account == null ? null : (Map<String, Object>) account.get("profile");
            String nickname = profile == null ? "카카오회원" : (String) profile.get("nickname");
            String image = profile == null ? null : (String) profile.get("profile_image_url");
            return new KakaoUserInfo(id, nickname, image);
        } catch (RestClientException e) {
            log.warn("카카오 사용자 정보 요청 실패: {}", e.getMessage());
            throw kakaoFailed();
        }
    }

    private CustomException kakaoFailed() {
        return new CustomException(HttpStatus.UNAUTHORIZED, "KAKAO_LOGIN_FAILED", "카카오 로그인에 실패했습니다.");
    }
}
package com.human.mini_prj.rag;

import com.human.mini_prj.rag.dto.IndexReqDto;
import com.human.mini_prj.rag.dto.IndexResDto;
import com.human.mini_prj.rag.dto.QueryReqDto;
import com.human.mini_prj.rag.dto.QueryResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.function.Supplier;

// RagIndexService -> RagClient -> FastAPI

// FastAPI 호출 담당
@Component // Spring Container에 Bean 등록
@RequiredArgsConstructor  // 생성자를 통한 의존성 주입을 자동으로 만들어 줌
public class RagClient {
    private final RestClient ragRestClient;

    public int index(IndexReqDto req) {
        IndexResDto res = call("색인", () -> ragRestClient.post()
                .uri("/index/concert")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(IndexResDto.class));
        return res == null ? 0 : res.chunks();
    }

    public void delete(Long concertId) {
        call("삭제", () -> ragRestClient.delete()
                .uri("/index/concert/{concert}", concertId)
                .retrieve()
                .toBodilessEntity());
    }

    public QueryResDto query(QueryReqDto req) {
        return call("질의", () -> ragRestClient.post()
                .uri("/query")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(QueryResDto.class));
    }

    // 타입 변수를 사용하므로 어떤 객체 타입이든 반환 가능
    private <T> T call(String action, Supplier<T> request) {
        try {
            return request.get();
        } catch (ResourceAccessException e) {          // 연결 실패 · 시간 초과
            throw new RagUnavailableException("AI 서비스에 연결할 수 없습니다.", e);
        } catch (RestClientResponseException e) {      // 401 · 422 · 502 등 오류 응답
            throw new RagUnavailableException("AI 서비스 " + action + " 오류 (" + e.getStatusCode().value() + ")", e);
        } catch (RestClientException e) {
            throw new RagUnavailableException("AI 서비스 " + action + " 실패", e);
        }
    }

}
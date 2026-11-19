package com.human.mini_prj.rag.dto;

import java.util.List;

// Spring -> FastAPI 질의 요청: 질문 + READY 허용 목록 + 가져 올 개수
public record QueryReqDto(String question, List<Long> concertIds, int topK) {
}
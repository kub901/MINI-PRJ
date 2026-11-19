package com.human.mini_prj.rag.dto;

// FastAPI 색인 응답을 받음, 몇 개의 청크로 나뉘었는지
public record IndexResDto(Long concertId, int chunks) {
}
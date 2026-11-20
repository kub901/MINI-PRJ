
package com.human.mini_prj.rag.dto;

// 답변의 근거가 되는 게시글
// boardId: 원본 게시글 ID
// snippet: 실제로 검색된 청크
// score: 질문과 청크의 유사도 점수
public record SourceDto(
        Long concertId,
        String title,
        Integer price,
        String location,
        String snippet,
        double score) {}
package com.human.mini_prj.rag.dto;

import java.util.List;

// FastAPI 질의 응답 = 화면으로 보내는 응답
public record QueryResDto(String answer, boolean abstained, List<SourceDto> sources, List<String> trace) {
    // 출처만 바꾼 복사본 (record 는 값을 바꿀 수 없으므로 새로 만듦)
    public QueryResDto withSources(List<SourceDto> newSources) {
        return new QueryResDto(answer, abstained, newSources, trace);
    }
}
package com.human.mini_prj.rag;

import com.human.mini_prj.entity.Concert;
import com.human.mini_prj.exception.CustomException;
import com.human.mini_prj.rag.dto.QueryReqDto;
import com.human.mini_prj.rag.dto.QueryResDto;
import com.human.mini_prj.rag.dto.RagChatReqDto;
import com.human.mini_prj.rag.dto.SourceDto;
import com.human.mini_prj.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagChatService {
    private static final int MAX_QUESTION_LENGTH = 500;

    private final RagClient ragClient;  // FastAPI 통신
    private final RagProperties ragProperties;  // 환경설정 정보
    private final ConcertRepository concertRepository;  // 게시들 DB 접근

    // RagChatController -> RagChatService -> RagClient -> FastAPI
    // 질문 검증 -> 허용 목록 계산 -> 응답
    @Transactional(readOnly = true)
    public QueryResDto ask(RagChatReqDto req) {
        String question = req.question() == null ? "" : req.question().strip();
        if (question.isEmpty()) {
            throw new CustomException("EMPTY_QUESTION", "질문을 입력해 주세요.");
        }
        if (question.length() > MAX_QUESTION_LENGTH) {
            throw new CustomException("QUESTION_TOO_LONG", "질문은 " + MAX_QUESTION_LENGTH + "자 이내로 입력해 주세요.");
        }
        List<Long> allowConcertIds = concertRepository.findAll()
                .stream()
                .map(Concert::getId)
                .toList();
        log.info("[RAG] 질문='{}'  허용목록={}건", question, allowConcertIds.size());

        QueryResDto res = ragClient.query(
                new QueryReqDto(question, allowConcertIds, ragProperties.topK())
        );

        // FastAPI 가 실수로 허용목록 밖의 글을 돌려줘도 화면에 나가지 않게 한 번 더 거름
        Set<Long> allowSet = new HashSet<>(allowConcertIds);
        List<SourceDto> safeSources = res.sources() == null ? List.of()
                : res.sources().stream().filter(s -> allowSet.contains(s.concertId())).toList();
        return res.withSources(safeSources);
    }
}
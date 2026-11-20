package com.human.mini_prj.rag;

import com.human.mini_prj.entity.Concert;
import com.human.mini_prj.exception.CustomException;
import com.human.mini_prj.rag.dto.*;

import com.human.mini_prj.repository.ConcertRepository;
import com.human.mini_prj.security.SecurityUtil;
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
    private final ConcertRepository concertRepository;

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
        List<Long> allowIds = allowList(req.scope(), SecurityUtil.currentUserId());
        log.info("[RAG] 질문='{}' scope={} 허용목록={}건", question, req.scope(), allowIds.size());

        QueryResDto res = ragClient.query(new QueryReqDto(question, allowIds, ragProperties.topK()));

        // FastAPI 가 실수로 허용목록 밖의 글을 돌려줘도 화면에 나가지 않게 한 번 더 거름
        Set<Long> allowSet = new HashSet<>(allowIds);
        List<SourceDto> safeSources = res.sources() == null ? List.of()
                : res.sources().stream().filter(s -> allowSet.contains(s.concertId())).toList();
        return res.withSources(safeSources);
    }
    private List<Long> allowList(String scope, String userId) {
        return concertRepository.findAll().stream()
                .map(Concert::getId)
                .toList();
    }
    // DB에 저장된 전체 공연 색인
    public int indexAllConcerts() {

        // 1. DB에서 공연 전체 조회
        List<Concert> concerts = concertRepository.findAll();

        int totalChunks = 0;

        // 2. 공연 하나씩 FastAPI로 전송
        for (Concert concert : concerts) {

            IndexReqDto req = new IndexReqDto(
                    concert.getId(),
                    concert.getTitle(),
                    concert.getPrice(),
                    concert.getLocation(),
                    concert.getNotice(),
                    concert.getCasting(),
                    concert.getDetail(),
                    concert.getPriceInfo(),
                    concert.getGuide(),
                    concert.getRefund(),
                    concert.getStory(),
                    concert.getMood()
            );

            // 3. 색인 결과 청크 수 누적
            totalChunks += ragClient.index(req);
        }

        return totalChunks;
    }
}
package com.human.mini_prj.rag;

import com.human.mini_prj.rag.dto.QueryResDto;
import com.human.mini_prj.rag.dto.RagChatReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagChatController {
    private final RagChatService ragChatService;

    // AI 질문
    @PostMapping("/chat")
    public ResponseEntity<QueryResDto> chat (@RequestBody RagChatReqDto req) {
        return ResponseEntity.ok(ragChatService.ask(req));
    }
    // DB 에 저장된 전체 공연 색인
    @PostMapping("/index")
    public ResponseEntity<String> indexAllConcerts() {

        int totalChunks = ragChatService.indexAllConcerts();

        return ResponseEntity.ok(
                "공연 전체 색인 완료: 총" + totalChunks + "개 청크"
        );
    }
}
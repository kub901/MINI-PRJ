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

    @PostMapping("/chat")
    public ResponseEntity<QueryResDto> chat(@RequestBody RagChatReqDto req) {
        return ResponseEntity.ok(ragChatService.ask(req));
    }

}
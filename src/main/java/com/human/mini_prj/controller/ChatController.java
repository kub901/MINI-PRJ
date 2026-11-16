package com.human.mini_prj.controller;


import com.human.mini_prj.dto.ChatRoomReqDto;
import com.human.mini_prj.dto.ChatRoomResDto;
import com.human.mini_prj.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // 채팅방 생성
    @PostMapping("/new")
    public ResponseEntity<String> createRoom(@RequestBody ChatRoomReqDto dto) {
        return ResponseEntity.ok(chatService.createRoom(dto.name()).getRoomId());
    }

    // 채팅방 목록
    @GetMapping("/list")
    public ResponseEntity<List<ChatRoomResDto>> list() {
        return ResponseEntity.ok(chatService.findAllRoom());
    }

    // 채팅방 정보
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoomResDto> room(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.findRoomById(roomId));
    }
}
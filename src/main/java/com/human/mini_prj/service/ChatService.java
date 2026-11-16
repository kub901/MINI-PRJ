
package com.human.mini_prj.service;

import com.human.mini_prj.dto.ChatMessageDto;
import com.human.mini_prj.dto.ChatRoomResDto;
import com.human.mini_prj.entity.ChatMessage;
import com.human.mini_prj.entity.ChatRoom;
import com.human.mini_prj.repository.ChatMessageRepository;
import com.human.mini_prj.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.human.mini_prj.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final JsonMapper jsonMapper;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 현재 접속한 WebSocket 세션 관리
    private final Map<String, ChatRoomResDto> chatRooms = new ConcurrentHashMap<>();

    @Transactional(readOnly = true)
    public List<ChatRoomResDto> findAllRoom() {
        return chatRoomRepository.findAllByDeletedFalse()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // 채팅방 조회
    @Transactional(readOnly = true)
    public ChatRoomResDto findRoomById(String roomId) {

        ChatRoomResDto cachedRoom = chatRooms.get(roomId);

        if (cachedRoom != null) {
            return cachedRoom;
        }

        return chatRoomRepository.findById(roomId)
                .map(this::toDto)
                .orElse(null);
    }

    // Entity를 DTO로 변환
    private ChatRoomResDto toDto(ChatRoom room) {

        ChatRoomResDto dto = ChatRoomResDto.builder()
                .roomId(room.getRoomId())
                .name(room.getName())
                .regDate(room.getRegDate())
                .build();

        return chatRooms.computeIfAbsent(room.getRoomId(), id -> dto);
    }

    // 채팅방 생성
    @Transactional
    public ChatRoomResDto createRoom(String name) {

        String roomId = UUID.randomUUID().toString();

        // 현재 로그인한 사용자를 방장으로 지정
        String ownerUserId = SecurityUtil.currentUserId();

        ChatRoom room = new ChatRoom(roomId, name, ownerUserId);

        chatRoomRepository.save(room);

        ChatRoomResDto dto = ChatRoomResDto.builder()
                .roomId(roomId)
                .name(name)
                .regDate(room.getRegDate())
                .build();

        chatRooms.put(roomId, dto);

        return dto;
    }

    @Transactional
    public void saveChatMessage(ChatMessageDto chat) {

        ChatRoom room = chatRoomRepository.findById(chat.getRoomId())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채팅방입니다.")
                );

        ChatMessage message = new ChatMessage(
                room,
                chat.getSender() == null ? "시스템" : chat.getSender(),
                chat.getMessage()
        );

        chatMessageRepository.save(message);
    }
    // 입장: 세션 등록 + 입장 메시지
    public void addSessionAndHandleEnter(
            String roomId,
            WebSocketSession session,
            ChatMessageDto chat
    ) {

        ChatRoomResDto room = findRoomById(roomId);

        if (room == null) return;

        room.getSessions().add(
                new ConcurrentWebSocketSessionDecorator(session, 5000, 64 * 1024)
        );

        if (chat.getSender() != null) {
            chat.setMessage(chat.getSender() + "님이 입장했습니다.");
            sendMessageToAll(roomId, chat);
        }
    }

    // 퇴장: 세션 제거 + 퇴장 메시지
    public void removeSessionAndHandleExit(
            String roomId,
            WebSocketSession session,
            ChatMessageDto chat
    ) {

        ChatRoomResDto room = findRoomById(roomId);

        if (room == null) return;

        room.getSessions().removeIf(
                s -> s.getId().equals(session.getId())
        );

        if (chat.getSender() != null) {
            chat.setMessage(chat.getSender() + "님이 퇴장했습니다.");
            sendMessageToAll(roomId, chat);
        }

        // 마지막 사용자가 나가도 채팅방은 삭제하지 않음
    }

    // 같은 방 세션 전체에 전송
    public void sendMessageToAll(String roomId, ChatMessageDto chat) {

        ChatRoomResDto room = findRoomById(roomId);

        if (room == null) return;

        TextMessage text = new TextMessage(
                jsonMapper.writeValueAsString(chat)
        );

        for (WebSocketSession s : room.getSessions()) {

            try {
                if (s.isOpen()) {
                    s.sendMessage(text);
                }
            } catch (IOException e) {
                log.error("전송 실패: {}", e.getMessage());
            }
        }
    }
}
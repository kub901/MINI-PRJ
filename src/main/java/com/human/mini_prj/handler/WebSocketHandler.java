
package com.human.mini_prj.handler;

import com.human.mini_prj.dto.ChatMessageDto;
import com.human.mini_prj.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final JsonMapper jsonMapper;
    private final ChatService chatService;

    private final Map<WebSocketSession, String> sessionRoomIdMap =
            new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    ) {

        ChatMessageDto chat =
                jsonMapper.readValue(message.getPayload(), ChatMessageDto.class);

        log.info("수신: {} / {} / {}",
                chat.getType(), chat.getSender(), chat.getMessage());

        switch (chat.getType()) {

            // 채팅방 입장
            case ENTER -> {
                sessionRoomIdMap.put(session, chat.getRoomId());

                chatService.addSessionAndHandleEnter(
                        chat.getRoomId(), session, chat
                );

                // 입장 알림 DB 저장
                chatService.saveChatMessage(chat);
            }

            // 일반 메시지 전송
            case TALK -> {
                chatService.saveChatMessage(chat);

                chatService.sendMessageToAll(
                        chat.getRoomId(), chat
                );
            }

            // 채팅방 퇴장
            case CLOSE -> {
                String roomId = sessionRoomIdMap.remove(session);

                if (roomId != null) {

                    chatService.removeSessionAndHandleExit(
                            roomId, session, chat
                    );

                    // 퇴장 알림 DB 저장
                    chatService.saveChatMessage(chat);
                }
            }
        }
    }

    // 브라우저 종료 등으로 연결이 끊긴 경우
    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ) {

        String roomId = sessionRoomIdMap.remove(session);

        if (roomId != null) {

            ChatMessageDto chat = new ChatMessageDto();

            chat.setType(ChatMessageDto.MessageType.CLOSE);
            chat.setRoomId(roomId);
            chat.setSender("시스템");

            chatService.removeSessionAndHandleExit(
                    roomId, session, chat
            );

            // 브라우저 종료 퇴장 알림 DB 저장
            chatService.saveChatMessage(chat);
        }

        log.info("연결 종료: {} ({})", session.getId(), status);
    }
}
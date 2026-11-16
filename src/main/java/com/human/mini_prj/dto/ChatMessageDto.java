package com.human.mini_prj.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {
    public enum MessageType { ENTER,TALK,CLOSE}
    private MessageType type;
    private String roomId;      // 방마다의 고유값 번호
    private String sender;      // 보내는 사람
    private String message;     //
}
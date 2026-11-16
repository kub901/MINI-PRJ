
package com.human.mini_prj.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @Column(name = "room_id", length = 36)
    private String roomId;

    @Column(nullable = false)
    private String name;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    // 방장 userId
    @Column(name = "owner_user_id")
    private String ownerUserId;

    // 방 삭제 여부 (기존 방은 기본 false)
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public ChatRoom(String roomId, String name, String ownerUserId) {
        this.roomId = roomId;
        this.name = name;
        this.ownerUserId = ownerUserId;
        this.regDate = LocalDateTime.now();
        this.deleted = false;
    }
}
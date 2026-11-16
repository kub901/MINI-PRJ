package com.human.mini_prj.repository;

import com.human.mini_prj.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findByChatRoom_RoomIdOrderBySentAtAsc(String roomId);
}
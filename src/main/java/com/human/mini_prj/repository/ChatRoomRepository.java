package com.human.mini_prj.repository;

import com.human.mini_prj.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,String> {
    // 삭제되지 않은 채팅방만 조회
    List<ChatRoom> findAllByDeletedFalse();
}
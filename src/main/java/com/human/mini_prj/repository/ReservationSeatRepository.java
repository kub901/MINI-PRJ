package com.human.mini_prj.repository;

import com.human.mini_prj.entity.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository <ReservationSeat ,Long> {
    // 예매 번호에 연결된 좌석목록 조회
    List<ReservationSeat> findByReservation_Id(Long reservationId);
}
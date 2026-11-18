package com.human.mini_prj.repository;


import com.human.mini_prj.entity.Seat;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SeatRepository extends JpaRepository<Seat, Long> {
    //공연 회차에 해당하는 전체 좌석 조회
    List<Seat> findBySchedule_Id(Long scheduleId);
    // 공연 회차 와 좌석번호로 좌석을 조회
    Optional<Seat>findBySchedule_IdAndSeatNumber(
            Long scheduleId, String seatNumber
    );

    // 좌석을 잠금 상태로 조회 (동시 예매 방지)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.schedule.id = :scheduleId AND s.seatNumber = :seatNumber")
    Optional<Seat> findForUpdateByScheduleAndSeatNumber(
            @Param("scheduleId") Long scheduleId,
            @Param("seatNumber") String seatNumber
    );

    //공연 회차별 예매 가능한 좌석 개수 조회
    long countBySchedule_IdAndStatus(
            Long scheduleId, String status
    );
}
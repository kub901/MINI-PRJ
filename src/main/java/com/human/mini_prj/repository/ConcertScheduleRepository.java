package com.human.mini_prj.repository;

import com.human.mini_prj.entity.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ConcertScheduleRepository
        extends JpaRepository<ConcertSchedule, Long> {

    // 공연 ID로 전체 회차 조회
    List<ConcertSchedule>
    findByConcertIdOrderByConcertDateAscConcertTimeAsc(Long concertId);

    // 동일한 공연 ID, 날짜, 시간의 기존 회차 조회
    Optional<ConcertSchedule>
    findFirstByConcertIdAndConcertDateAndConcertTimeOrderByIdAsc(
            Long concertId,
            LocalDate concertDate,
            LocalTime concertTime
    );
}
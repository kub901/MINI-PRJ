package com.human.mini_prj.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="reservation")
@Getter @Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)


    private Long id;        // 예매 번호 (1번)

    private String status ="PENDING";

    private LocalDateTime reservedAt = LocalDateTime.now();         // 날짜 불러오는거
    // seatNum 제거한 이유는 ReservationSeat에서 좌석 연결 정보를 관리하려고

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ConcertSchedule concertSchedule;



}
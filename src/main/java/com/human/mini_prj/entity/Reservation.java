package com.human.mini_prj.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

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
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private ConcertSchedule concertSchedule;

    // 예매한 회원 정보 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;



}
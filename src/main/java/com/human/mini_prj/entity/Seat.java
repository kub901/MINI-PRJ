package com.human.mini_prj.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "seat")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String seatNumber;
    private String status = "AVAILABLE";
    private int price;
    // 현재 좌석을 임시 점유한 예매 번호
    private Long heldReservationId;

    @ManyToOne
    @JoinColumn(name="schedule_id")
    private ConcertSchedule schedule;
}
package com.human.mini_prj.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "reservation_seat")
@Getter @Setter
public class ReservationSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤  회차에 속한 좌석인지
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    // 어떤 예매에 속한 좌석인가
    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;
}
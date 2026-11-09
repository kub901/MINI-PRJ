package com.human.mini_prj.dto;

import com.human.mini_prj.entity.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ReservationResDto {


    private final Long reservationId;

    private final Long scheduleId;
    private final String status;

    private LocalDateTime reservedAt;       // 예매 정보를 시간과 날짜로 저장하는 역할

    public ReservationResDto(Reservation reservation) {
        this.reservationId =reservation.getId();

        this.scheduleId = reservation.getConcertSchedule().getId();
        this.status =reservation.getStatus();
        this.reservedAt = reservation.getReservedAt();
    }
}
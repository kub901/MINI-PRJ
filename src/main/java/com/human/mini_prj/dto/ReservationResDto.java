package com.human.mini_prj.dto;

import com.human.mini_prj.entity.Reservation;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Getter
@Setter
public class ReservationResDto {


    private final Long reservationId;
    private Long concertId;
    private final Long scheduleId;  // 공연번호
    private final String status;
    private LocalDate concertDate;  //공연 날짜
    private LocalTime concertTime;  // 공연 시간
    private LocalDateTime reservedAt;       // 예매 정보를 시간과 날짜로 저장하는 역할

    public ReservationResDto(Reservation reservation) {
        this.reservationId =reservation.getId();

        this.scheduleId = reservation.getConcertSchedule() !=null
                ? reservation.getConcertSchedule().getId()
                : null;
        this.status =reservation.getStatus();
        this.reservedAt = reservation.getReservedAt();

    }
}
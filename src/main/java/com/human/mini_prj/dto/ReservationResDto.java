package com.human.mini_prj.dto;

import com.human.mini_prj.entity.Reservation;
import com.human.mini_prj.entity.Concert;
import com.human.mini_prj.entity.ReservationSeat;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
public class ReservationResDto {

    // 예매 정보
    private final Long reservationId;
    private final String status;
    private final LocalDateTime reservedAt;

    // 공연 회차 정보
    private final Long scheduleId;
    private final LocalDate concertDate;
    private final LocalTime concertTime;

    // 공연 정보
    private final String concertTitle;
    private final String posterUrl;
    private final String location;
    private final Long concertId;

    // 좌석 및 가격 정보
    private final List<String> seats;
    private final int price;

    public ReservationResDto(
            Reservation reservation,
            Concert concert,
            List<ReservationSeat> reservationSeats
    ) {
        // 예매 정보
        this.reservationId = reservation.getId();
        this.status = reservation.getStatus();
        this.reservedAt = reservation.getReservedAt();

        // 공연 회차 정보
        this.scheduleId = reservation.getConcertSchedule().getId();
        this.concertDate = reservation.getConcertSchedule().getConcertDate();
        this.concertTime = reservation.getConcertSchedule().getConcertTime();

        // 공연 정보
        this.concertTitle = concert.getTitle();
        this.posterUrl = concert.getPosterUrl();
        this.location = concert.getLocation();
        this.concertId = concert.getId();

        // 예매한 좌석 번호 목록
        this.seats = reservationSeats.stream()
                .map(rs -> rs.getSeat().getSeatNumber())
                .toList();

        // 공연 가격 × 예매 좌석 수
        this.price = concert.getPrice() * reservationSeats.size();
    }
}
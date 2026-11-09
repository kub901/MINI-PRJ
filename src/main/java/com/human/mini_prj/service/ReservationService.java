package com.human.mini_prj.service;

import com.human.mini_prj.dto.ReservationReqDto;
import com.human.mini_prj.dto.ReservationResDto;
import com.human.mini_prj.entity.*;
import com.human.mini_prj.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    // 예매 생성
    @Transactional
    public ReservationResDto createReservation(ReservationReqDto dto) {

        // 1. 공연 회차 조회
        ConcertSchedule schedule = concertScheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() ->
                        new RuntimeException("공연 회차가 존재하지 않습니다.")
                );

        // 2. 선택한 좌석 조회
        List<Seat> seats = new ArrayList<>();

        for (String seatNumber : dto.getSeatNumbers()) {

            Seat seat = seatRepository
                    .findBySchedule_IdAndSeatNumber(
                            dto.getScheduleId(),
                            seatNumber
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    seatNumber + " 좌석이 해당 공연 회차에 없습니다."
                            )
                    );

            seats.add(seat);
        }

        // 3. 예매 객체 생성
        Reservation reservation = new Reservation();

        reservation.setConcertSchedule(schedule);
        reservation.setStatus("PENDING");

        Reservation saved = reservationRepository.save(reservation);

        // 4. 선택한 좌석을 예매 정보에 연결
        for (Seat seat : seats) {

            ReservationSeat reservationSeat = new ReservationSeat();

            reservationSeat.setReservation(saved);
            reservationSeat.setSeat(seat);

            reservationSeatRepository.save(reservationSeat);
        }

        return new ReservationResDto(saved);
    }

    // 예매 상세 조회
    @Transactional(readOnly = true)
    public ReservationResDto getReservation(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("예매 정보가 없습니다.")
                );

        return new ReservationResDto(reservation);
    }

    // 결제 처리
    @Transactional
    public ReservationResDto confirmPayment(Long reservationId) {

        //1. 예매 정보 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(()->
                        new IllegalArgumentException("예매 정보가 없습니다.")
                );

        // 2. 결제 대기 상태인지 확인
        if(!"PENDING".equals(reservation.getStatus())) {
            throw new IllegalArgumentException(
                    "결제 대기 상태의 예매만 결제할 수 있습니다."
            );
        }

        // 3. 결제 완료 처리
        reservation.setStatus("PAID");

        //4. 해당 예매의 연결된 좌석 조회
        List<ReservationSeat>reservationSeats =
                reservationSeatRepository.findByReservation_Id(reservationId);

        // 5. 좌석 상태 변경
        for(ReservationSeat reservationSeat : reservationSeats) {
            Seat seat = reservationSeat.getSeat();

            seat.setStatus("RESERVED");
            seatRepository.save(seat);
        }
        Reservation saved =reservationRepository.save(reservation);
        return  new ReservationResDto(saved);
    }
}
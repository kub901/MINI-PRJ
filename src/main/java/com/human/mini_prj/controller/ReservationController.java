package com.human.mini_prj.controller;

import com.human.mini_prj.dto.ReservationReqDto;
import com.human.mini_prj.dto.ReservationResDto;
import com.human.mini_prj.service.ReservationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    private final ReservationService reservationService;

    // 예매 생성
    @PostMapping
    public ReservationResDto createReservation(
            @RequestBody ReservationReqDto dto) {

        return reservationService.createReservation(dto);
    }

    // 결제 처리
    @PostMapping("/{reservationId}/payment")
    public ReservationResDto confirmPayment(
            @PathVariable Long reservationId) {

        return reservationService.confirmPayment(reservationId);
    }
}
package com.human.mini_prj.scheduler;

import com.human.mini_prj.service.ReservationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

    private final ReservationService reservationService;

    // 1분마다 만료된 예매 확인
    @Scheduled(fixedDelay = 60000)
    public void expireReservations() {

        log.info("만료된 예매 확인 시작");

        reservationService.expirePendingReservations();

        log.info("만료된 예매 확인 완료");
    }
}
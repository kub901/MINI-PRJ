package com.human.mini_prj.service;

import com.human.mini_prj.dto.SeatResDto;
import com.human.mini_prj.entity.ConcertSchedule;
import com.human.mini_prj.entity.Seat;
import com.human.mini_prj.repository.ConcertScheduleRepository;
import com.human.mini_prj.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final ConcertScheduleRepository concertScheduleRepository;

    // 좌석 등록
    @Transactional
    public String createSeats(Long scheduleId, List<String> seatNumbers) {

        // 1. 공연 회차 조회
        ConcertSchedule schedule =
                concertScheduleRepository.findById(scheduleId)
                        .orElseThrow(() ->
                                new RuntimeException("공연 회차가 없습니다."));

        // 2. 좌석 등록
        for (String seatNumber : seatNumbers) {
            Seat seat = new Seat();

            seat.setSeatNumber(seatNumber);
            seat.setStatus("AVAILABLE");
            seat.setPrice(10000);
            seat.setSchedule(schedule);

            seatRepository.save(seat);
        }

        return "좌석 등록 완료";
    }

    // 좌석 전체 조회
    @Transactional(readOnly = true)
    public List<SeatResDto> getSeats(Long scheduleId) {

        List<Seat> seats =
                seatRepository.findBySchedule_Id(scheduleId);

        List<SeatResDto> result = new ArrayList<>();

        for (Seat seat : seats) {
            result.add(new SeatResDto(seat));
        }

        return result;
    }
    // 회차별 잔여 좌석 개수 조회
    @Transactional(readOnly = true)
    public long getRemainingSeats(Long scheduleId) {

        return seatRepository.countBySchedule_IdAndStatus(
                scheduleId,"AVAILABLE"
        );
    }
}
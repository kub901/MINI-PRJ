package com.human.mini_prj.service;

import com.human.mini_prj.dto.ScheduleReqDto;
import com.human.mini_prj.dto.ScheduleResDto;
import com.human.mini_prj.entity.ConcertSchedule;
import com.human.mini_prj.entity.Seat;
import com.human.mini_prj.repository.ConcertScheduleRepository;
import com.human.mini_prj.repository.SeatRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertScheduleService {

    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;

    // 공연 회차 전체 조회
    public List<ScheduleResDto> getSchedule(Long concertId) {
        return concertScheduleRepository
                .findByConcertIdOrderByConcertDateAscConcertTimeAsc(concertId)
                .stream()
                .map(ScheduleResDto::new)
                .toList();
    }

    // 공연 회차 등록 또는 기존 회차 조회
    @Transactional
    public ScheduleResDto createSchedule(Long concertId, ScheduleReqDto dto) {

        ConcertSchedule schedule =
                concertScheduleRepository
                        .findFirstByConcertIdAndConcertDateAndConcertTimeOrderByIdAsc(
                                concertId,
                                dto.getConcertDate(),
                                dto.getConcertTime()
                        )
                        .orElse(null);

        // 기존 회차가 없으면 신규 등록
        if (schedule == null) {
            schedule = new ConcertSchedule();
            schedule.setConcertId(concertId);
            schedule.setConcertDate(dto.getConcertDate());
            schedule.setConcertTime(dto.getConcertTime());

            schedule = concertScheduleRepository.save(schedule);
        }

        // 회차에 없는 좌석 생성
        createSeatsIfMissing(schedule);

        return new ScheduleResDto(schedule);
    }
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void createSeatsForAllSchedules() {

        List<ConcertSchedule> schedules =
                concertScheduleRepository.findAll();

        for (ConcertSchedule schedule : schedules) {
            createSeatsIfMissing(schedule);
        }
    }

    // 반드시 같은 클래스 안에 있어야 하는 메서드
    private void createSeatsIfMissing(ConcertSchedule schedule) {

        List<Seat> existingSeats =
                seatRepository.findBySchedule_Id(schedule.getId());

        Set<String> existingSeatNumbers = new HashSet<>();

        for (Seat seat : existingSeats) {
            existingSeatNumbers.add(seat.getSeatNumber());
        }

        List<Seat> newSeats = new ArrayList<>();

        // A1 ~ E6 좌석 생성
        for (char row = 'A'; row <= 'E'; row++) {
            for (int number = 1; number <= 6; number++) {

                String seatNumber = row + String.valueOf(number);

                if (existingSeatNumbers.contains(seatNumber)) {
                    continue;
                }

                Seat seat = new Seat();
                seat.setSeatNumber(seatNumber);
                seat.setStatus("AVAILABLE");
                seat.setPrice(10000);
                seat.setSchedule(schedule);

                newSeats.add(seat);
            }
        }

        if (!newSeats.isEmpty()) {
            seatRepository.saveAll(newSeats);
        }
    }
}
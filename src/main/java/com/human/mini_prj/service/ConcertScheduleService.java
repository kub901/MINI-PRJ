
package com.human.mini_prj.service;

import com.human.mini_prj.dto.ScheduleReqDto;
import com.human.mini_prj.dto.ScheduleResDto;
import com.human.mini_prj.entity.ConcertSchedule;
import com.human.mini_prj.entity.Seat;
import com.human.mini_prj.repository.ConcertScheduleRepository;
import com.human.mini_prj.repository.SeatRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // 선택한 날짜의 3개 회차 조회 또는 생성
    @Transactional
    public List<ScheduleResDto> getOrCreateSchedulesForDate(
            Long concertId, LocalDate concertDate) {

        // 하루 공연 시간: 11시, 16시, 20시
        List<LocalTime> showTimes = List.of(
                LocalTime.of(11, 0),
                LocalTime.of(16, 0),
                LocalTime.of(20, 0)
        );

        List<ScheduleResDto> result = new ArrayList<>();

        for (LocalTime showTime : showTimes) {

            // 해당 날짜와 시간의 회차가 있는지 확인
            ConcertSchedule schedule =
                    concertScheduleRepository
                            .findFirstByConcertIdAndConcertDateAndConcertTimeOrderByIdAsc(
                                    concertId,
                                    concertDate,
                                    showTime
                            )
                            .orElse(null);

            // 회차가 없으면 새로 생성
            if (schedule == null) {
                schedule = new ConcertSchedule();
                schedule.setConcertId(concertId);
                schedule.setConcertDate(concertDate);
                schedule.setConcertTime(showTime);

                schedule = concertScheduleRepository.save(schedule);
            }

            // 해당 회차에 없는 좌석만 생성
            createSeatsIfMissing(schedule);

            result.add(new ScheduleResDto(schedule));
        }

        return result;
    }

    // 기존 좌석은 유지하고 없는 좌석만 생성
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
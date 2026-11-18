
package com.human.mini_prj.controller;

import com.human.mini_prj.dto.ScheduleReqDto;
import com.human.mini_prj.dto.ScheduleResDto;
import com.human.mini_prj.dto.ScheduleDateReqDto;
import com.human.mini_prj.service.ConcertScheduleService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ConcertScheduleController {

    private final ConcertScheduleService scheduleService;

    // 공연 회차 전체 조회
    @GetMapping("/{concertId}/schedules")
    public List<ScheduleResDto> getSchedule(
            @PathVariable Long concertId) {

        return scheduleService.getSchedule(concertId);
    }

    // 공연 회차 단일 등록
    @PostMapping("/{concertId}/schedules")
    public ScheduleResDto createSchedule(
            @PathVariable Long concertId,
            @RequestBody ScheduleReqDto dto) {

        return scheduleService.createSchedule(concertId, dto);
    }

    // 선택한 날짜의 3개 회차 조회 또는 생성
    @PostMapping("/{concertId}/schedules/by-date")
    public List<ScheduleResDto> getOrCreateSchedulesByDate(
            @PathVariable Long concertId,
            @RequestBody ScheduleDateReqDto dto) {

        return scheduleService.getOrCreateSchedulesForDate(
                concertId,
                dto.getConcertDate()
        );
    }
}
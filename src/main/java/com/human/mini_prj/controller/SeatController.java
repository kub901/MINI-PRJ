package com.human.mini_prj.controller;

import com.human.mini_prj.dto.SeatResDto;
import com.human.mini_prj.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SeatController {

    private final SeatService seatService;

    // 좌석 등록
    @PostMapping("/demo/{scheduleId}")
    public String createDemoSeats(
            @PathVariable Long scheduleId,
            @RequestBody List<String> seatNumbers) {

        return seatService.createSeats(scheduleId, seatNumbers);
    }

    // 좌석 조회
    @GetMapping("/{scheduleId}")
    public List<SeatResDto> getSeats(
            @PathVariable Long scheduleId) {

        return seatService.getSeats(scheduleId);
    }
    // 회차별 잔여 좌석 조회
    @GetMapping("/{scheduleId}/remaining")
    public long getRemainingSeats(@PathVariable Long scheduleId) {
        return seatService.getRemainingSeats(scheduleId);
    }
}
package com.human.mini_prj.controller;
import com.human.mini_prj.dto.ScheduleReqDto;
import com.human.mini_prj.dto.ScheduleResDto;
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

    @GetMapping("/{concertId}/schedules")
    public List<ScheduleResDto> getSchedule(
            @PathVariable Long concertId) {
        return scheduleService.getSchedule(concertId);

    }
    @PostMapping("/{concertId}/schedules")
    public ScheduleResDto createSchedule(
            @PathVariable Long concertId,
            @RequestBody ScheduleReqDto dto) {
        return scheduleService.createSchedule(concertId, dto);
    }
}
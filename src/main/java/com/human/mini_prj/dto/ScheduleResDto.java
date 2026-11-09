package com.human.mini_prj.dto;

import com.human.mini_prj.entity.ConcertSchedule;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ScheduleResDto {
    private final Long id;
    private final Long scheduleId;
    private final LocalDate concertDate;
    private final LocalTime concertTime;

    public ScheduleResDto(ConcertSchedule schedule) {
        this.id =schedule.getId();
        this.scheduleId = schedule.getId();
        this.concertDate = schedule.getConcertDate();
        this.concertTime = schedule.getConcertTime();
    }
}
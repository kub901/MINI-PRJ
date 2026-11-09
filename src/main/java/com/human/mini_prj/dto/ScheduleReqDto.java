package com.human.mini_prj.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
public class ScheduleReqDto {
    private LocalDate concertDate;      // 콘서트 날짜 읽어오는거
    private LocalTime concertTime;

}
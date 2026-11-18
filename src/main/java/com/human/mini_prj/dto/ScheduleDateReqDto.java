package com.human.mini_prj.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ScheduleDateReqDto {
    private LocalDate concertDate;
}
package com.human.mini_prj.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 리액트에 예매 요청을 보내는 DTo 임
@Getter @Setter
public class ReservationReqDto {

    private Long scheduleId;
    private List<String> seatNumbers;

}
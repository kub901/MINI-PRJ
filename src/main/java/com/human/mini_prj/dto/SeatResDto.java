package com.human.mini_prj.dto;

import com.human.mini_prj.entity.Seat;
import lombok.Getter;

@Getter
public class SeatResDto {
    private final Long id;
    private final String SeatNumber;
    private final String status;
    private final  int price;

    public SeatResDto(Seat seat) {
        this.id = seat.getId();
        this.SeatNumber = seat.getSeatNumber();
        this.status =seat.getStatus();
        this.price =seat.getPrice();

    }
}
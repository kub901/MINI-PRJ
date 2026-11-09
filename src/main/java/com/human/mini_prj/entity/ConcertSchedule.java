package com.human.mini_prj.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name ="concert_schedule",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_concert_schedule",
                columnNames = {"concert_id","concert_date","concert_time"}
        )
)
@Getter @Setter

public class ConcertSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)


    private Long id;   // 콘서트 id (1,2 )
    @Column(name = "concert_id")
    private Long concertId;
    @Column(name = "concert_date")
    private LocalDate concertDate;
    @Column(name = "concert_time")
    private LocalTime concertTime;
}
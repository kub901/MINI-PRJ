package com.human.mini_prj.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MemberResDto {
    private Long id;
    private String email;
    private String name;
    private String image;
    private LocalDateTime regDate;
}
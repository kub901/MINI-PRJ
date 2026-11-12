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
    private String userId;
    private String name;
    private String image;
    private String phone;
    private LocalDateTime regDate;
}
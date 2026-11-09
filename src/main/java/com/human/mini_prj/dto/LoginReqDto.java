package com.human.mini_prj.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 로그인 요청
@Getter
@Setter
@NoArgsConstructor
public class LoginReqDto {
    private String email;
    private String pwd;
    private String userId;
}
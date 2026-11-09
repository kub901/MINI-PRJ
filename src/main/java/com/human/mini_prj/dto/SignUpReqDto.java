package com.human.mini_prj.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 회원 가입 요청 DTO
@Setter
@Getter
@NoArgsConstructor
public class SignUpReqDto {
    private String userId;  // 로그인 아이디
    private String name;    // 닉네임
    private String phone;   // 휴대폰번호
    private String email;   // 이메일
    private String pwd;     // 비밀번호
}
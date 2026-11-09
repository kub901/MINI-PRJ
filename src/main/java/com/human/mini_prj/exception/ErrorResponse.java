package com.human.mini_prj.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor  // 매개변수가 전부 있는 생성자를 자동으로 만들어줌
public class ErrorResponse {
    private String message;
    private String code;
}
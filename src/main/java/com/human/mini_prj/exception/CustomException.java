package com.human.mini_prj.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{
    private final String code;
    private final HttpStatus status;

    public CustomException(String code, String message) {
        this(HttpStatus.BAD_REQUEST, code, message);   // null 대신 기본값 400
    }
    // 새 생성자: 401, 403 등 상태 코드를 직접 지정
    public CustomException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
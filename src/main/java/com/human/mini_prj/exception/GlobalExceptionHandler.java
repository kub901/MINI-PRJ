package com.human.mini_prj.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice  // 모든 컨트롤러에 공통으로 적용되는 예외 처리 담당 클래스를 만드는 어노테이션
public class GlobalExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam() {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("MISSING_PARAM", "이메일 파라미터가 누락되었습니다."));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDBError() {
        return ResponseEntity
                .status(500)
                .body(new ErrorResponse("DB_ERROR", "데이터베이스 오류가 발생했습니다."));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustom(CustomException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(ex.getMessage(), ex.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleDefault(Exception ex) {
        return ResponseEntity
                .status(500)
                .body(new ErrorResponse("UNKNOWN", "알 수 없는 서버 오류"));
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("잘못된 요청: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)   // 404
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

}
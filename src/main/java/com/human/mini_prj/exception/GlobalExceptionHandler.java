package com.human.mini_prj.exception;

import com.human.mini_prj.rag.RagUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// ErrorResponse 생성자 순서: (message, code)
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("필수 파라미터가 누락되었습니다: " + ex.getParameterName(), "MISSING_PARAM"));
    }

    // GET/POST 등 HTTP 메서드를 잘못 호출한 경우 → 500이 아니라 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("지원하지 않는 메서드: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)   // 405
                .body(new ErrorResponse("지원하지 않는 요청 방식입니다: " + ex.getMethod(), "METHOD_NOT_ALLOWED"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDBError(DataAccessException ex) {
        log.error("DB 오류: {}", ex.getMessage());   // 원인은 로그로 확인 (화면에는 노출하지 않음)
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("데이터베이스 오류가 발생했습니다.", "DB_ERROR"));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustom(CustomException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(ex.getMessage(), ex.getCode()));
    }

    // 기존 IllegalAccessException(오타) → IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("잘못된 요청: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)   // 404
                .body(new ErrorResponse(ex.getMessage(), "NOT_FOUND"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleDefault(Exception ex) {
        log.error("알 수 없는 오류", ex);   // 스택 트레이스까지 로그에 남김
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("알 수 없는 서버 오류가 발생했습니다.", "UNKNOWN"));
    }

    @ExceptionHandler(RagUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleRag(RagUnavailableException ex) {
        log.error("AI 서비스 오류: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)   // 502
                .body(new ErrorResponse(ex.getMessage(), "RAG_UNAVAILABLE"));
    }
}
package com.human.mini_prj.rag;

// FastAPI 서버가 꺼져 있어 오류를 돌려준 경우에 대한 처리
public class RagUnavailableException extends RuntimeException{
    public RagUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
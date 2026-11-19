package com.human.mini_prj.rag.dto;

// React -> Spring 으로 질문 요청
public record RagChatReqDto(String question, String scope) {}
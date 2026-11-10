package com.human.mini_prj.dto;

// record 는 불변 객체를 만드는 문법

public record TokenDto(String grantType,
                       String accessToken,
                       long accessTokenExpiresIn
) {}
package com.human.mini_prj.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewReqDto {

    private Integer rating;
    private String content;
}
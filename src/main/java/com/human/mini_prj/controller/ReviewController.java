package com.human.mini_prj.controller;

import com.human.mini_prj.dto.ReviewReqDto;
import com.human.mini_prj.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping("/{reservationId}")
    public ResponseEntity<String>createReview(
            @PathVariable Long reservationId,
            @RequestBody ReviewReqDto dto) {

        reviewService.createReview(
                reservationId,
                dto.getRating(),
                dto.getContent()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("리뷰가 등록되었습니다.");
    }

}
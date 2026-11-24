package com.human.mini_prj.controller;

import com.human.mini_prj.dto.ReviewReqDto;
import com.human.mini_prj.dto.ReviewResDto;
import com.human.mini_prj.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 내가 작성한 리뷰 조회
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResDto>> getMyReviews() {
        return ResponseEntity.ok(reviewService.getMyReviews());
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewReqDto dto) {

        reviewService.updateReview(
                reviewId,
                dto.getRating(),
                dto.getContent()
        );

        return ResponseEntity.ok("리뷰가 수정되었습니다.");
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId) {

        reviewService.deleteReview(reviewId);

        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }
}
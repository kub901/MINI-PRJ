
package com.human.mini_prj.service;

import com.human.mini_prj.dto.ReviewResDto;
import com.human.mini_prj.entity.Concert;
import com.human.mini_prj.entity.Member;
import com.human.mini_prj.entity.Reservation;
import com.human.mini_prj.entity.Review;
import com.human.mini_prj.repository.ConcertRepository;
import com.human.mini_prj.repository.MemberRepository;
import com.human.mini_prj.repository.ReservationRepository;
import com.human.mini_prj.repository.ReviewRepository;
import com.human.mini_prj.security.SecurityUtil;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final ConcertRepository concertRepository;

    // 리뷰 작성
    @Transactional
    public Review createReview(Long reservationId, Integer rating, String content) {

        // 1. 로그인한 회원 확인
        String userId = SecurityUtil.currentUserId();

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 2. 예약 정보 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다."));

        // 3. 본인의 예약인지 확인
        if (reservation.getMember() == null ||
                !reservation.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("본인의 예약에만 리뷰를 작성할 수 있습니다.");
        }

        // 4. 중복 리뷰 확인
        if (reviewRepository.existsByReservation_Id(reservationId)) {
            throw new RuntimeException("이미 리뷰를 작성한 예약입니다.");
        }

        // 5. 별점 검증
        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException("별점은 1점부터 5점까지 입력할 수 있습니다.");
        }

        // 6. 리뷰 내용 검증
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("리뷰 내용을 입력해 주세요.");
        }

        // 7. 리뷰 저장
        Review review = new Review();
        review.setMember(member);
        review.setReservation(reservation);
        review.setRating(rating);
        review.setContent(content);

        return reviewRepository.save(review);
    }

    // 내가 작성한 리뷰 조회
    @Transactional(readOnly = true)
    public List<ReviewResDto> getMyReviews() {

        String userId = SecurityUtil.currentUserId();

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        List<Review> reviews = reviewRepository.findByMember_Id(member.getId());

        return reviews.stream()
                .map(review -> {
                    Long concertId = review.getReservation()
                            .getConcertSchedule()
                            .getConcertId();

                    Concert concert = concertRepository.findById(concertId)
                            .orElseThrow(() -> new RuntimeException("콘서트 정보를 찾을 수 없습니다."));

                    return new ReviewResDto(review, concert.getTitle());
                })
                .toList();
    }
    // 리뷰 수정
    @Transactional
    public void updateReview(Long reviewId, Integer rating, String content) {

        String userId = SecurityUtil.currentUserId();

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        // 본인이 작성한 리뷰인지 확인
        if (!review.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        // 별점 검증
        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException("별점은 1점부터 5점까지 입력할 수 있습니다.");
        }

        // 내용 검증
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("리뷰 내용을 입력해 주세요.");
        }

        review.setRating(rating);
        review.setContent(content);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId) {

        String userId = SecurityUtil.currentUserId();

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        // 본인이 작성한 리뷰인지 확인
        if (!review.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }
}
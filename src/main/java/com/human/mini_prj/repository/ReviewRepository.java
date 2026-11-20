package com.human.mini_prj.repository;

import com.human.mini_prj.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 회원이 작성한 리뷰 조회
    List<Review> findByMember_Id(Long memberId);

    // 특정 예야겡 이미 리뷰가 작성되어 있는지 확인
    boolean existsByReservation_Id(Long reservationId);

    // 특정 예약의 리뷰 조회
    Optional<Review> findByReservation_Id(Long ReservationId);
}
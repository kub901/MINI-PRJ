
package com.human.mini_prj.repository;

import com.human.mini_prj.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDateTime;

// 예매 정보를 DB에 저장하고 조회하는 Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 특정 회원의 예매 내역을 최신순으로 조회
    List<Reservation> findByMember_IdOrderByReservedAtDesc(Long memberId);

    // 결제 대기 중이고 만료 시간이 지난 예매 조회
    List<Reservation> findByStatusAndExpiresAtLessThanEqual(
            String status,
            LocalDateTime now
    );

}
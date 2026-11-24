
package com.human.mini_prj.repository;
import com.human.mini_prj.entity.Member;
import com.human.mini_prj.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    // 진행 중인 예매 확인
    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            WHERE r.member = :member
              AND r.status NOT IN ('CANCELLED', 'COMPLETED')
            """)
    boolean existsActiveReservationByMember(@Param("member") Member member);

}
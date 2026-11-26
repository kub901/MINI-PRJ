
package com.human.mini_prj.service;

import com.human.mini_prj.dto.ReservationReqDto;
import com.human.mini_prj.dto.ReservationResDto;
import com.human.mini_prj.entity.*;
import com.human.mini_prj.repository.*;
import com.human.mini_prj.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.human.mini_prj.tosspay.PaymentPrepareResDto;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final MemberRepository memberRepository;
    private final ConcertRepository concertRepository;

    // 예매 생성
    @Transactional
    public ReservationResDto createReservation(ReservationReqDto dto) {

        // 1. JWT에서 로그인한 회원의 userId 가져오기
        String userId = SecurityUtil.currentUserId();

        // 2. 로그인한 회원 조회
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("회원 정보를 찾을 수 없습니다.")
                );

        // 3. 공연 회차 조회
        ConcertSchedule schedule = concertScheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() ->
                        new RuntimeException("공연 회차가 존재하지 않습니다.")
                );

        // 4. 선택한 좌석 조회 및 예매 가능 여부 확인
        List<Seat> seats = new ArrayList<>();

        for (String seatNumber : dto.getSeatNumbers()) {

            // 좌석을 잠금 상태로 조회
            Seat seat = seatRepository.findForUpdateByScheduleAndSeatNumber(
                            dto.getScheduleId(),
                            seatNumber
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    seatNumber + " 좌석이 해당 공연 회차에 없습니다."
                            )
                    );

            // 이미 다른 사용자가 점유했거나 예매한 좌석인지 확인
            if (!"AVAILABLE".equals(seat.getStatus())) {
                throw new RuntimeException(
                        seatNumber + " 좌석은 이미 선택된 좌석입니다."
                );
            }

            seats.add(seat);
        }

        // 5. 예매 객체 생성 및 회원 연결
        Reservation reservation = new Reservation();

        LocalDateTime now = LocalDateTime.now();

        reservation.setMember(member);
        reservation.setConcertSchedule(schedule);
        reservation.setStatus("PENDING");
        reservation.setReservedAt(now);
        reservation.setExpiresAt(now.plusMinutes(5));

        Reservation saved = reservationRepository.save(reservation);

        // 6. 선택한 좌석을 임시 점유하고 예매 정보에 연결
        for (Seat seat : seats) {

            // 좌석을 5분 동안 임시 점유
            seat.setStatus("HELD");

            // 현재 좌석을 점유한 예매 번호 저장
            seat.setHeldReservationId(saved.getId());

            seatRepository.save(seat);

            ReservationSeat reservationSeat = new ReservationSeat();

            reservationSeat.setReservation(saved);
            reservationSeat.setSeat(seat);

            reservationSeatRepository.save(reservationSeat);
        }

        return convertToDto(saved);
    }

    // 예매 상세 조회
    @Transactional(readOnly = true)
    public ReservationResDto getReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new RuntimeException("예매 정보가 없습니다.")
                );

        return convertToDto(reservation);
    }

    // 로그인한 회원의 예매 내역 조회
    @Transactional(readOnly = true)
    public List<ReservationResDto> getMyReservations() {

        // 1. JWT에서 현재 로그인한 회원의 userId 가져오기
        String userId = SecurityUtil.currentUserId();

        // 2. 회원 정보 조회
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("회원 정보를 찾을 수 없습니다.")
                );

        // 3. 해당 회원의 예매 내역 조회 (최신순)
        List<Reservation> reservations =
                reservationRepository.findByMember_IdOrderByReservedAtDesc(member.getId());

        // 4. Reservation 목록을 ReservationResDto 목록으로 변환
        return reservations.stream()
                .map(this::convertToDto)
                .toList();
    }

    // 결제 처리
    @Transactional
    public ReservationResDto confirmPayment(Long reservationId) {

        // 1. 예매 정보 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("예매 정보가 없습니다.")
                );

        // 2. 결제 대기 상태인지 확인
        if (!"PENDING".equals(reservation.getStatus())) {
            throw new IllegalArgumentException(
                    "결제 대기 상태의 예매만 결제할 수 있습니다."
            );
        }

        // 3. 결제 만료 시간 확인
        if (reservation.getExpiresAt() == null ||
                !LocalDateTime.now().isBefore(reservation.getExpiresAt())) {

            throw new IllegalArgumentException(
                    "결제 가능 시간이 만료되었습니다. 다시 예매해 주세요."
            );
        }

        // 3. 결제 완료 처리
        reservation.setStatus("PAID");

        // 4. 해당 예매에 연결된 좌석 조회
        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findByReservation_Id(reservationId);

        // 5. 좌석 상태 변경
        for (ReservationSeat reservationSeat : reservationSeats) {

            Seat seat = reservationSeat.getSeat();

            // 현재 예매가 점유하고 있는 좌석인지 확인
            if (!Long.valueOf(reservationId).equals(seat.getHeldReservationId())) {
                throw new IllegalArgumentException(
                        "현재 예매에서 점유한 좌석이 아닙니다."
                );
            }

            // 결제 완료 상태로 변경
            seat.setStatus("RESERVED");

            // 임시 점유 예매 번호 초기화
            seat.setHeldReservationId(null);

            seatRepository.save(seat);
        }

        Reservation saved = reservationRepository.save(reservation);

        return convertToDto(saved);
    }

    // 예매 취소
    @Transactional
    public ReservationResDto cancelReservation(Long reservationId) {

        // 1. 로그인한 회원 정보 조회
        String userId = SecurityUtil.currentUserId();

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("회원 정보를 찾을 수 없습니다.")
                );

        // 2. 취소할 예매 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new RuntimeException("예매 정보가 없습니다.")
                );

        // 3. 본인의 예매인지 확인
        if (!reservation.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException(
                    "본인의 예매만 취소할 수 있습니다."
            );
        }

        // 4. 결제 완료 상태인지 확인
        if (!"PAID".equals(reservation.getStatus())) {
            throw new IllegalArgumentException(
                    "결제가 완료된 예매만 취소할 수 있습니다."
            );
        }

        // 5. 예매 상태를 취소로 변경
        reservation.setStatus("CANCELLED");

        // 6. 해당 예매에 연결된 좌석 조회
        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findByReservation_Id(reservationId);

        // 7. 좌석을 다시 예매 가능 상태로 변경
        for (ReservationSeat reservationSeat : reservationSeats) {

            Seat seat = reservationSeat.getSeat();

            // 결제 완료된 좌석인지 확인
            if (!"RESERVED".equals(seat.getStatus())) {
                throw new IllegalArgumentException(
                        "취소할 수 없는 좌석 상태입니다."
                );
            }

            seat.setStatus("AVAILABLE");
            seat.setHeldReservationId(null);

            seatRepository.save(seat);
        }

        // 8. 예매 정보 저장
        Reservation saved = reservationRepository.save(reservation);

        return convertToDto(saved);
    }

    // 예매 정보를 화면에 전달할 DTO로 변환
    private ReservationResDto convertToDto(Reservation reservation) {

        // 예매에 연결된 공연 회차에서 콘서트 ID 가져오기
        Long concertId = reservation.getConcertSchedule().getConcertId();

        // 콘서트 정보 조회
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() ->
                        new RuntimeException("공연 정보를 찾을 수 없습니다.")
                );

        // 예매에 연결된 좌석 목록 조회
        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findByReservation_Id(
                        reservation.getId()
                );

        return new ReservationResDto(
                reservation,
                concert,
                reservationSeats
        );
    }

    // Toss 결제 준비
    @Transactional(readOnly = true)
    public PaymentPrepareResDto preparePayment(Long reservationId) {

        // 1. 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("예약 정보가 없습니다.")
                );

        // 2. 로그인 회원 확인
        String userId = SecurityUtil.currentUserId();

        if (!reservation.getMember().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 예약만 결제할 수 있습니다.");
        }

        // 3. 결제 대기 상태 확인
        if (!"PENDING".equals(reservation.getStatus())) {
            throw new IllegalArgumentException("결제 대기 상태의 예약만 결제할 수 있습니다.");
        }

        // 4. 결제 시간 확인
        if (reservation.getExpiresAt() == null ||
                !LocalDateTime.now().isBefore(reservation.getExpiresAt())) {
            throw new IllegalArgumentException("예약 결제 시간이 만료되었습니다.");
        }

        // 5. 예약 좌석 조회
        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findByReservation_Id(reservationId);

        if (reservationSeats.isEmpty()) {
            throw new IllegalArgumentException("예약된 좌석이 없습니다.");
        }

        // 6. 좌석 가격 합산
        int amount = reservationSeats.stream()
                .mapToInt(rs -> rs.getSeat().getPrice())
                .sum();

        if (amount <= 0) {
            throw new IllegalArgumentException("결제 금액이 올바르지 않습니다.");
        }

        // 7. Toss에 전달할 주문 정보 생성
        String orderId = "reservation-" + reservationId + "-" +
                UUID.randomUUID();

        String orderName = "공연 예매";

        return new PaymentPrepareResDto(
                orderId,
                orderName,
                amount
        );
    }

    // 5분이 지난 예매 자동 만료 처리
    @Transactional
    public void expirePendingReservations() {

        // 현재 시간 기준으로 만료된 예매 조회
        List<Reservation> expiredReservations =
                reservationRepository.findByStatusAndExpiresAtLessThanEqual(
                        "PENDING",
                        LocalDateTime.now()
                );

        // 만료된 예매 하나씩 처리
        for (Reservation reservation : expiredReservations) {

            // 예매 상태를 EXPIRED로 변경
            reservation.setStatus("EXPIRED");

            // 해당 예매에 연결된 좌석 조회
            List<ReservationSeat> reservationSeats =
                    reservationSeatRepository.findByReservation_Id(
                            reservation.getId()
                    );

            // 해당 예매가 아직 점유 중인 좌석만 반환
            for (ReservationSeat reservationSeat : reservationSeats) {

                Seat seat = reservationSeat.getSeat();

                // 좌석의 점유 예매 번호가 현재 예매 번호와 같은지 확인
                if (Long.valueOf(reservation.getId())
                        .equals(seat.getHeldReservationId())) {

                    seat.setStatus("AVAILABLE");
                    seat.setHeldReservationId(null);

                    seatRepository.save(seat);
                }
            }


            reservationRepository.save(reservation);
        }
    }
}

package com.human.mini_prj.tosspay;
import com.human.mini_prj.entity.Concert;
import com.human.mini_prj.repository.ConcertRepository;
import com.human.mini_prj.tosspay.PaymentConfirmReqDto;
import com.human.mini_prj.tosspay.PaymentPrepareResDto;
import com.human.mini_prj.tosspay.Payment;
import com.human.mini_prj.entity.Reservation;
import com.human.mini_prj.entity.ReservationSeat;
import com.human.mini_prj.tosspay.PaymentRepository;
import com.human.mini_prj.repository.ReservationRepository;
import com.human.mini_prj.repository.ReservationSeatRepository;
import com.human.mini_prj.repository.SeatRepository;
import com.human.mini_prj.security.SecurityUtil;
import com.human.mini_prj.service.ReservationService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ConcertRepository concertRepository;
    private final ReservationService reservationService;

    @Value("${toss.payments.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String TOSS_CONFIRM_URL =
            "https://api.tosspayments.com/v1/payments/confirm";


    // 결제 준비
    @Transactional
    public PaymentPrepareResDto preparePayment(Long reservationId) {

        Reservation reservation = reservationRepository
                .findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "예매 정보를 찾을 수 없습니다."
                ));

        // 로그인한 회원의 예매인지 확인
        validateReservationOwner(reservation);

        // 결제 가능한 예매인지 확인
        validateReservation(reservation);

        // 예매에 연결된 좌석 조회
        List<ReservationSeat> reservationSeats =
                reservationSeatRepository
                        .findByReservation_Id(reservationId);

        if (reservationSeats.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "예매 좌석이 없습니다."
            );
        }

        // 좌석 가격을 서버에서 직접 합산

        // 예매 회차에 연결된 공연 ID 가져오기
        Long concertId = reservation.getConcertSchedule().getConcertId();

        // 공연 정보 조회
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "공연 정보를 찾을 수 없습니다."
                ));

        // 공연 가격 × 예매 좌석 수
        int amount = concert.getPrice() * reservationSeats.size();

        if (amount <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "결제 금액이 올바르지 않습니다."
            );
        }

        // 이미 준비된 결제가 있으면 재사용
        Payment existingPayment = paymentRepository
                .findFirstByReservationIdOrderByCreatedAtDesc(
                        reservationId
                )
                .orElse(null);

        if (existingPayment != null
                && "READY".equals(existingPayment.getStatus())
                && existingPayment.getAmount() == amount) {

            return new PaymentPrepareResDto(
                    existingPayment.getOrderId(),
                    "공연 예매",
                    existingPayment.getAmount()
            );
        }

        // 새로운 결제 주문 생성
        Payment payment = new Payment();

        payment.setReservationId(reservationId);

        payment.setOrderId(
                "reservation-" + reservationId + "-"
                        + UUID.randomUUID()
        );

        payment.setAmount(amount);
        payment.setStatus("READY");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        return new PaymentPrepareResDto(
                payment.getOrderId(),
                "공연 예매",
                payment.getAmount()
        );
    }


    // 토스 결제 승인
    @Transactional
    public Map<String, Object> confirmPayment(
            PaymentConfirmReqDto dto) {

        // 주문번호로 결제 정보 조회
        Payment payment = paymentRepository
                .findByOrderId(dto.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "결제 주문을 찾을 수 없습니다."
                ));

        // 이미 승인된 결제인지 확인
        if ("DONE".equals(payment.getStatus())) {

            if (payment.getPaymentKey() != null
                    && payment.getPaymentKey()
                    .equals(dto.getPaymentKey())) {

                return createPaymentResult(payment);
            }

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 승인된 결제입니다."
            );
        }

        // 결제 금액 검증
        if (payment.getAmount() != dto.getAmount()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "결제 금액이 일치하지 않습니다."
            );
        }

        // 결제 준비 상태인지 확인
        if (!"READY".equals(payment.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "결제 가능한 상태가 아닙니다."
            );
        }

        // 예매 정보 확인
        Reservation reservation = reservationRepository
                .findById(payment.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "예매 정보를 찾을 수 없습니다."
                ));

        validateReservationOwner(reservation);
        validateReservation(reservation);

        // 토스 서버에 결제 승인 요청
        Map<String, Object> tossResponse =
                requestTossApproval(dto);

        // 토스 승인 성공 후 결제 정보 저장
        payment.setPaymentKey(dto.getPaymentKey());
        payment.setStatus("DONE");

        Object method = tossResponse.get("method");

        if (method != null) {
            payment.setMethod(String.valueOf(method));
        }

        Object approvedAt = tossResponse.get("approvedAt");

        if (approvedAt != null) {
            payment.setApprovedAt(
                    OffsetDateTime.parse(
                            String.valueOf(approvedAt)
                    ).toLocalDateTime()
            );
        }

        paymentRepository.save(payment);

        // 기존 예매 확정 로직 호출
        reservationService.confirmPayment(
                payment.getReservationId()
        );

        return createPaymentResult(payment);
    }


    // 토스 결제 승인 API 호출
    private Map<String, Object> requestTossApproval(
            PaymentConfirmReqDto dto) {

        // 토스 서버에 보낼 데이터
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("paymentKey", dto.getPaymentKey());
        requestBody.put("orderId", dto.getOrderId());
        requestBody.put("amount", dto.getAmount());

        // Secret Key를 Basic 인증으로 변환
        String encodedSecretKey = Base64.getEncoder()
                .encodeToString(
                        (secretKey + ":")
                                .getBytes(StandardCharsets.UTF_8)
                );

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set(
                "Authorization",
                "Basic " + encodedSecretKey
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(requestBody, headers);

        try {

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            TOSS_CONFIRM_URL,
                            request,
                            Map.class
                    );

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "토스 결제 승인에 실패했습니다."
                );
            }

            return response.getBody();

        } catch (RestClientResponseException e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "토스 결제 승인 실패: " + e.getResponseBodyAsString()
            );
        }
    }


    // 예매가 결제 가능한 상태인지 확인
    private void validateReservation(
            Reservation reservation) {

        if (!"PENDING".equals(reservation.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "결제 대기 상태의 예매가 아닙니다."
            );
        }

        if (reservation.getExpiresAt() == null
                || reservation.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "예매 시간이 만료되었습니다."
            );
        }
    }


    // 로그인한 회원의 예매인지 확인
    private void validateReservationOwner(
            Reservation reservation) {

        String loginUserId =
                String.valueOf(SecurityUtil.currentUserId());

        String reservationUserId =
                String.valueOf(
                        reservation.getMember().getUserId()
                );

        if (!loginUserId.equals(reservationUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "본인의 예매만 결제할 수 있습니다."
            );
        }
    }


    // 결제 결과 응답
    private Map<String, Object> createPaymentResult(
            Payment payment) {

        Map<String, Object> result = new HashMap<>();

        result.put("paymentKey", payment.getPaymentKey());
        result.put("orderId", payment.getOrderId());
        result.put("amount", payment.getAmount());
        result.put("status", payment.getStatus());
        result.put("reservationId", payment.getReservationId());

        return result;
    }
}

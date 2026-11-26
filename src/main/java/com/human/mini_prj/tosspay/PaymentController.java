
package com.human.mini_prj.tosspay;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 준비
    @PostMapping("/prepare")
    public ResponseEntity<PaymentPrepareResDto> preparePayment(
            @RequestBody PaymentPrepareReqDto dto) {

        PaymentPrepareResDto result =
                paymentService.preparePayment(dto.getReservationId());

        return ResponseEntity.ok(result);
    }

    // 결제 승인
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @RequestBody PaymentConfirmReqDto dto) {

        Map<String, Object> result =
                paymentService.confirmPayment(dto);

        return ResponseEntity.ok(result);
    }
}

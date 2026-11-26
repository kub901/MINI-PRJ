
package com.human.mini_prj.tosspay;

import com.human.mini_prj.tosspay.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findByPaymentKey(String paymentKey);

    Optional<Payment>
    findFirstByReservationIdOrderByCreatedAtDesc(
            Long reservationId
    );
}

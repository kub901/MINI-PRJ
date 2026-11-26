
package com.human.mini_prj.tosspay;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "payment",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payment_order_id",
                        columnNames = "order_id"
                ),
                @UniqueConstraint(
                        name = "uk_payment_payment_key",
                        columnNames = "payment_key"
                )
        }
)
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 결제할 예매 ID
    @Column(name = "reservation_id", nullable = false)
    private Long reservationId;

    // 토스에 전달할 주문번호
    @Column(name = "order_id", nullable = false)
    private String orderId;

    // 토스가 승인 후 반환하는 결제키
    @Column(name = "payment_key", unique = true)
    private String paymentKey;

    // 서버에서 계산한 결제 금액
    @Column(nullable = false)
    private int amount;

    // READY, DONE, FAILED
    @Column(nullable = false)
    private String status = "READY";

    // 카드, 간편결제 등
    private String method;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime approvedAt;
}

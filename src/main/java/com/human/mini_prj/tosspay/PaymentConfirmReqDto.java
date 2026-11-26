
package com.human.mini_prj.tosspay;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentConfirmReqDto {

    // 토스에서 결제 성공 후 전달받는 결제키
    private String paymentKey;

    // 우리가 결제 준비 단계에서 생성한 주문번호
    private String orderId;

    // 결제 금액
    private int amount;
}

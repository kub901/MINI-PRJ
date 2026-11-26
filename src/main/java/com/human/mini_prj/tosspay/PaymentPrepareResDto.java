
package com.human.mini_prj.tosspay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentPrepareResDto {

    private String orderId;

    private String orderName;

    private int amount;
}

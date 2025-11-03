package io.github.isaevisa05.bank.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TakeMoneyResponse {

    // 0 -> Недостаточно средств
    // 1 -> Успех
    private final int result;
    private final String error;


}

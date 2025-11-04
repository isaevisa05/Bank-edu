package io.github.isaevisa05.bank.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TakeMoneyResponse {

    // -3 -> Сумма должна быть положительной
    // -2 -> Неверный формат суммы
    // -1 -> Аккаунт не найден
    // 0 -> Недостаточно средств
    // 1 -> Успех
    private int result;
    private String error;


}

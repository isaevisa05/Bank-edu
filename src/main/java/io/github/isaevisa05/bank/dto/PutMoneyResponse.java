package io.github.isaevisa05.bank.dto;

import lombok.Data;

@Data
public class PutMoneyResponse {

    // -3 -> Сумма должна быть положительной
    // -2 -> Неверный формат суммы
    // -1 -> Аккаунт не найден
    // 1 -> Успех
    private int result;
    private String error;
}

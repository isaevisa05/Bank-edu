package io.github.isaevisa05.bank.dto;

import lombok.Data;

@Data
public class PutMoneyResponse {

    // 0 -> Ошибка при выполнении операции
    // 1 -> Успех
    private final int result;
    private final String error;
}

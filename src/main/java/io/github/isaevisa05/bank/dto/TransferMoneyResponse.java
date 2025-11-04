package io.github.isaevisa05.bank.dto;

import lombok.Data;

@Data
public class TransferMoneyResponse {

    // 0 -> Ошибка при выполнении
    // 1 -> Успех
    private int result;
    private String error;
}

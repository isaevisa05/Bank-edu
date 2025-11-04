package io.github.isaevisa05.bank.dto;

import lombok.Data;

@Data
public class TransferMoneyResponse {

    // -4 -> Нельзя переводить самому себе
    // -3 -> Сумма должна быть положительной
    // -2 -> Неверный формат суммы
    // -1 -> Аккаунт не найден
    // 0 -> Недостаточно средств
    // 1 -> Успех
    private int result;
    private String error;
}

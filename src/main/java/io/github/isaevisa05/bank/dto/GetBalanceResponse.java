package io.github.isaevisa05.bank.dto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetBalanceResponse {

    // -1 -> Ошибка при выполнении операции
    // всё остальное -> Успех
    private BigDecimal result;
    private String error;
}

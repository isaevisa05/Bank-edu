package io.github.isaevisa05.bank.controllers;

import io.github.isaevisa05.bank.dto.*;
import io.github.isaevisa05.bank.service.AccountsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountsService accountsService;

    @Operation(
            summary = "Получить баланс пользователя",
            description = "Возвращает текущий баланс пользователя"
    )
    @GetMapping("/getBalance")
    public GetBalanceResponse getBalance(@RequestParam(name = "accountId") long accountId) {
        return accountsService.getBalance(accountId);
    }

    @Operation(
            summary = "Снять деньги с баланса пользователя",
            description = "Снимет деньги с баланса пользователя"
    )
    @PostMapping("/takeMoney")
    public TakeMoneyResponse takeMoney(@RequestParam(name = "accountId") long accountId, @RequestParam(name = "amount") String amountString) {
        return accountsService.takeMoney(accountId, amountString);
    }

    @Operation(
            summary = "Добавить денег на баланс пользователя",
            description = "Добавляет деньги на счёт пользователя"
    )
    @PostMapping("/putMoney")
    public PutMoneyResponse putMoney(@RequestParam(name = "accountId") long accountId, @RequestParam(name = "amount") String amountString) {
        return accountsService.putMoney(accountId, amountString);
    }

    @Operation(
            summary = "Получить историю операций",
            description = "Возвращает историю операций за выбранный период"
    )
    @GetMapping("/getOperationList")
    public GetOperationListResponse getOperationList(
            @RequestParam(name = "accountId") long accountId,
            @RequestParam(name = "startDate") Instant startDate,
            @RequestParam(name = "endDate") Instant endDate) {
        System.out.println(startDate);
        System.out.println(endDate);
        return accountsService.getOperationList(accountId, startDate, endDate);
    }

    @Operation(
            summary = "Перевести деньги другому пользователю",
            description = "Переводит деньги другому пользователю"
    )
    @PostMapping("/transferMoney")
    public TransferMoneyResponse transferMoney(@RequestParam(name = "payer") long payer, @RequestParam(name = "recipient") long recipient, @RequestParam(name = "amount") String amountString) {
        return accountsService.transferMoney(payer, recipient, amountString);
    }


}

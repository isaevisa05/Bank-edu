package io.github.isaevisa05.bank.controllers;

import io.github.isaevisa05.bank.dto.*;
import io.github.isaevisa05.bank.service.AccountsService;
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

    @GetMapping("/getBalance")
    public GetBalanceResponse getBalance(@RequestParam(name = "accountId") long accountId) {
        return accountsService.getBalance(accountId);
    }

    @PostMapping("/takeMoney")
    public TakeMoneyResponse takeMoney(@RequestParam(name = "accountId") long accountId, @RequestParam(name = "amount") String amountString) {
        return accountsService.takeMoney(accountId, amountString);
    }

    @PostMapping("/putMoney")
    public PutMoneyResponse putMoney(@RequestParam(name = "accountId") long accountId, @RequestParam(name = "amount") String amountString) {
        return accountsService.putMoney(accountId, amountString);
    }

    @GetMapping("/getOperationList")
    public GetOperationListResponse getOperationList(
            @RequestParam(name = "accountId") long accountId,
            @RequestParam(name = "startDate") Instant startDate,
            @RequestParam(name = "endDate") Instant endDate) {
        System.out.println(startDate);
        System.out.println(endDate);
        return accountsService.getOperationList(accountId, startDate, endDate);
    }

    @PostMapping("/transferMoney")
    public TransferMoneyResponse transferMoney(@RequestParam(name = "payer") long payer, @RequestParam(name = "recipient") long recipient, @RequestParam(name = "amount") String amountString) {
        return accountsService.transferMoney(payer, recipient, amountString);
    }


}

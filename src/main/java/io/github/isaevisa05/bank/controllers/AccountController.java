package io.github.isaevisa05.bank.controllers;

import io.github.isaevisa05.bank.dto.GetBalanceResponse;
import io.github.isaevisa05.bank.service.AccountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountsService accountsService;

    @PostMapping("/getBalance")
    public GetBalanceResponse getBalance(@RequestParam(name = "accountId") long accountId) {
        return accountsService.getBalance(accountId);
    }


}

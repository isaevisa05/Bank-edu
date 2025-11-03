package io.github.isaevisa05.bank.service;

import io.github.isaevisa05.bank.dto.GetBalanceResponse;
import io.github.isaevisa05.bank.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountsService {

    private static final BigDecimal error = new BigDecimal("-1");

    private final AccountsRepository accountsRepository;

    public GetBalanceResponse getBalance(long accountId) {
        var response = new GetBalanceResponse();
        var findAccount = accountsRepository.findById(accountId);

        findAccount.ifPresentOrElse(account -> {
            response.setResult(account.getBalance());
        }, () -> {
            response.setResult(error);
            response.setError("Account not found");
        });

        return response;
    }

}

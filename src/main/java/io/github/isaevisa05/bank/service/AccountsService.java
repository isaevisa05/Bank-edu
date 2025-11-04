package io.github.isaevisa05.bank.service;

import io.github.isaevisa05.bank.dto.GetBalanceResponse;
import io.github.isaevisa05.bank.dto.TakeMoneyResponse;
import io.github.isaevisa05.bank.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountsService {

    private static final BigDecimal error1 = BigDecimal.ONE;
    private static final BigDecimal error0 = BigDecimal.ZERO;
    private static final BigDecimal errorAccountNotFound = new BigDecimal("-1");
    private static final BigDecimal errorBigDecimal = new BigDecimal("-2");

    private final AccountsRepository accountsRepository;

    public GetBalanceResponse getBalance(long accountId) {
        var response = new GetBalanceResponse();
        var findAccount = accountsRepository.findById(accountId);

        findAccount.ifPresentOrElse(account -> {
            response.setResult(account.getBalance());
        }, () -> {
            response.setResult(errorAccountNotFound);
            response.setError("Account not found");
        });

        return response;
    }

    public TakeMoneyResponse takeMoney(long accountId, String amountString) {
        var response = new TakeMoneyResponse();
        var findAccount = accountsRepository.findById(accountId);
        BigDecimal amount;

        try {
            amount = new BigDecimal(amountString).setScale(2);
        } catch (Exception e) {
            response.setResult(-2);
            response.setError("The amount is in an invalid format");
            return response;
        }

        findAccount.ifPresentOrElse(account -> {
            BigDecimal newBalance = account.getBalance().subtract(amount);
            if(newBalance.compareTo(BigDecimal.ZERO) < 0) {
                response.setResult(0);
                response.setError("Insufficient funds");
            }
        }, () -> {
            response.setResult(-1);
            response.setError("Account not found");
        });
        return response;
    }



}

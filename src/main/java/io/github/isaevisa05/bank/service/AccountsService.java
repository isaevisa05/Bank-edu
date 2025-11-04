package io.github.isaevisa05.bank.service;

import io.github.isaevisa05.bank.dto.*;
import io.github.isaevisa05.bank.entity.History;
import io.github.isaevisa05.bank.entity.enums.OperationType;
import io.github.isaevisa05.bank.repository.AccountsRepository;
import io.github.isaevisa05.bank.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AccountsService {

    private static final BigDecimal errorAccountNotFound = new BigDecimal("-1");

    private final AccountsRepository accountsRepository;
    private final HistoryRepository historyRepository;

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

    @Transactional
    public TakeMoneyResponse takeMoney(long accountId, String amountString) {
        var response = new TakeMoneyResponse();
        BigDecimal amount;

        try {
            amount = new BigDecimal(amountString).setScale(2, RoundingMode.DOWN);
        } catch (Exception e) {
            response.setResult(-2);
            response.setError("The amount is in an invalid format");
            return response;
        }

        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            response.setResult(-3);
            response.setError("The amount must be positive");
            return response;
        }

        var findAccount = accountsRepository.findById(accountId);
        if(findAccount.isEmpty()) {
            response.setResult(-1);
            response.setError("Account not found");
            return response;
        }

        var account = findAccount.get();

        if(account.subtractBalance(amount)) {
            response.setResult(1);

            History history = new History();
            history.setRecipient(account.getId());
            history.setType(OperationType.WITHDRAW);
            history.setAmount(amount);
            historyRepository.save(history);
        } else {
            response.setResult(0);
            response.setError("Insufficient funds");
        }

        return response;
    }

    @Transactional
    public PutMoneyResponse putMoney(long accountId, String amountString) {
        var response = new PutMoneyResponse();
        BigDecimal amount;

        try {
            amount = new BigDecimal(amountString).setScale(2, RoundingMode.DOWN);
        } catch (Exception e) {
            response.setResult(-2);
            response.setError("The amount is in an invalid format");
            return response;
        }

        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            response.setResult(-3);
            response.setError("The amount must be positive");
            return response;
        }

        var findAccount = accountsRepository.findById(accountId);
        if(findAccount.isEmpty()) {
            response.setResult(-1);
            response.setError("Account not found");
            return response;
        }

        var account = findAccount.get();

        account.addBalance(amount);
        response.setResult(1);

        History history = new History();
        history.setRecipient(account.getId());
        history.setType(OperationType.DEPOSIT);
        history.setAmount(amount);
        historyRepository.save(history);

        return response;
    }

    public GetOperationListResponse getOperationList(long accountId, Instant startDate, Instant endDate) {
        var response = new GetOperationListResponse();

        var findAccount = accountsRepository.findById(accountId);
        if(findAccount.isEmpty()) {
            response.setResult(null);
            response.setError("Account not found");
            return response;
        }

        var account = findAccount.get();

        response.setResult(historyRepository.findAllByAccountIdAndTimeStampRange(account.getId(), startDate, endDate));

        return response;
    }

    public GetOperationListResponse getOperationList(long accountId) {
        var response = new GetOperationListResponse();

        var findAccount = accountsRepository.findById(accountId);
        if(findAccount.isEmpty()) {
            response.setResult(null);
            response.setError("Account not found");
            return response;
        }

        var account = findAccount.get();

        response.setResult(historyRepository.findAllByAccountId(account.getId()));

        return response;
    }

    @Transactional
    public TransferMoneyResponse transferMoney(long payerId, long recipientId, String amountString) {
        var response = new TransferMoneyResponse();
        BigDecimal amount;

        try {
            amount = new BigDecimal(amountString).setScale(2, RoundingMode.DOWN);
        } catch (Exception e) {
            response.setResult(-2);
            response.setError("The amount is in an invalid format");
            return response;
        }

        if(payerId == recipientId) {
            response.setResult(-4);
            response.setError("Translations to yourself are prohibited");
            return response;
        }
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            response.setResult(-3);
            response.setError("The amount must be positive");
            return response;
        }

        var findPayerAccount = accountsRepository.findById(payerId);
        if(findPayerAccount.isEmpty()) {
            response.setResult(-1);
            response.setError("Account payer not found");
            return response;
        }

        var findRecipientAccount = accountsRepository.findById(recipientId);
        if(findRecipientAccount.isEmpty()) {
            response.setResult(-1);
            response.setError("Account recipient not found");
            return response;
        }

        var payer = findPayerAccount.get();
        var recipient = findRecipientAccount.get();

        if(payer.subtractBalance(amount)) {
            recipient.addBalance(amount);

            History history = new History();
            history.setPayer(payerId);
            history.setRecipient(recipientId);
            history.setType(OperationType.PAY);
            history.setAmount(amount);
            historyRepository.save(history);

            response.setResult(1);
        } else {
            response.setResult(0);
            response.setError("Insufficient funds");
        }

        return response;
    }
}

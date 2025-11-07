package io.github.isaevisa05.bank.service;

import io.github.isaevisa05.bank.dto.*;
import io.github.isaevisa05.bank.entity.Account;
import io.github.isaevisa05.bank.entity.History;
import io.github.isaevisa05.bank.entity.enums.OperationType;
import io.github.isaevisa05.bank.repository.AccountsRepository;
import io.github.isaevisa05.bank.repository.HistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountsServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private AccountsService accountsService;

    private Account testAccount;
    private final Long existingAccountId = 1L;
    private final Long nonExistingAccountId = 999L;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(existingAccountId);
        testAccount.setBalance(new BigDecimal("1000.00"));
    }

    @Test // Получение баланса существующего пользователя
    void getBalance_WhenAccountExists_ShouldReturnBalance() {
        when(accountsRepository.findById(existingAccountId)).thenReturn(Optional.of(testAccount));

        GetBalanceResponse response = accountsService.getBalance(existingAccountId);

        assertEquals(new BigDecimal("1000.00"), response.getResult());
        assertNull(response.getError());
        verify(accountsRepository).findById(existingAccountId);
    }

    @Test // Получение баланса не существующего пользователя
    void getBalance_WhenAccountNotExists_ShouldReturnError() {
        when(accountsRepository.findById(nonExistingAccountId)).thenReturn(Optional.empty());

        GetBalanceResponse response = accountsService.getBalance(nonExistingAccountId);

        assertEquals(new BigDecimal("-1"), response.getResult());
        assertEquals("Account not found", response.getError());
    }

    @Test // Снятие денег с баланса существующего пользователя
    void takeMoney_WhenValidRequest_ShouldSuccess() {
        when(accountsRepository.findById(existingAccountId)).thenReturn(Optional.of(testAccount));

        TakeMoneyResponse response = accountsService.takeMoney(existingAccountId, "500.00");

        assertEquals(1, response.getResult());
        assertNull(response.getError());
        assertEquals(new BigDecimal("500.00"), testAccount.getBalance());
        verify(historyRepository).save(any(History.class));
    }

    @Test // Снятие денег у существующего пользователя, но сумма была введена неверно
    void takeMoney_WhenInvalidAmountFormat_ShouldReturnError() {
        TakeMoneyResponse response = accountsService.takeMoney(existingAccountId, "invalid");

        assertEquals(-2, response.getResult());
        assertEquals("The amount is in an invalid format", response.getError());
        verify(accountsRepository, never()).findById(anyLong());
    }

    @Test // Снятие денег у существующего пользователя, но сумма была не положительной
    void takeMoney_WhenNegativeAmount_ShouldReturnError() {
        TakeMoneyResponse response = accountsService.takeMoney(existingAccountId, "-100.00");

        assertEquals(-3, response.getResult());
        assertEquals("The amount must be positive", response.getError());
    }

    @Test // Снятие денег у существующего пользователя, но сумма была не положительной
    void takeMoney_WhenZeroAmount_ShouldReturnError() {
        TakeMoneyResponse response = accountsService.takeMoney(existingAccountId, "0.00");

        assertEquals(-3, response.getResult());
        assertEquals("The amount must be positive", response.getError());
    }

    @Test // Снятие денег у не существующего пользователя
    void takeMoney_WhenAccountNotFound_ShouldReturnError() {
        when(accountsRepository.findById(nonExistingAccountId)).thenReturn(Optional.empty());

        TakeMoneyResponse response = accountsService.takeMoney(nonExistingAccountId, "100.00");

        assertEquals(-1, response.getResult());
        assertEquals("Account not found", response.getError());
    }

    @Test // Снятие денег у существующего пользователя, но у пользователя недостаточно средств
    void takeMoney_WhenInsufficientFunds_ShouldReturnError() {
        when(accountsRepository.findById(existingAccountId)).thenReturn(Optional.of(testAccount));

        TakeMoneyResponse response = accountsService.takeMoney(existingAccountId, "1500.00");

        assertEquals(0, response.getResult());
        assertEquals("Insufficient funds", response.getError());
        assertEquals(new BigDecimal("1000.00"), testAccount.getBalance());
        verify(historyRepository, never()).save(any());
    }

    @Test // Добавление средств существующим пользователям
    void putMoney_WhenValidRequest_ShouldSuccess() {
        when(accountsRepository.findById(existingAccountId)).thenReturn(Optional.of(testAccount));

        PutMoneyResponse response = accountsService.putMoney(existingAccountId, "500.00");

        assertEquals(1, response.getResult());
        assertNull(response.getError());
        assertEquals(new BigDecimal("1500.00"), testAccount.getBalance());

        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        verify(historyRepository).save(historyCaptor.capture());
        History savedHistory = historyCaptor.getValue();
        assertEquals(OperationType.DEPOSIT, savedHistory.getType());
        assertEquals(new BigDecimal("500.00"), savedHistory.getAmount());
    }

    @Test // Перевод между существующими пользователями
    void transferMoney_WhenValidRequest_ShouldSuccess() {
        Account recipient = new Account();
        recipient.setId(2L);
        recipient.setBalance(new BigDecimal("500.00"));

        when(accountsRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountsRepository.findById(2L)).thenReturn(Optional.of(recipient));

        TransferMoneyResponse response = accountsService.transferMoney(1L, 2L, "300.00");

        assertEquals(1, response.getResult());
        assertNull(response.getError());
        assertEquals(new BigDecimal("700.00"), testAccount.getBalance());
        assertEquals(new BigDecimal("800.00"), recipient.getBalance());
        verify(historyRepository).save(any(History.class));
    }

    @Test // Попытка перевести самому себе
    void transferMoney_WhenSelfTransfer_ShouldReturnError() {
        TransferMoneyResponse response = accountsService.transferMoney(1L, 1L, "100.00");

        assertEquals(-4, response.getResult());
        assertEquals("Translations to yourself are prohibited", response.getError());
    }

    @Test // Перевод при котором аккаунта плательщика не существует
    void transferMoney_WhenPayerNotFound_ShouldReturnError() {
        when(accountsRepository.findById(1L)).thenReturn(Optional.empty());

        TransferMoneyResponse response = accountsService.transferMoney(1L, 2L, "100.00");

        assertEquals(-1, response.getResult());
        assertEquals("Account payer not found", response.getError());
    }

    @Test // Перевод при котором аккаунта получателя не существует
    void transferMoney_WhenRecipientNotFound_ShouldReturnError() {
        when(accountsRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountsRepository.findById(2L)).thenReturn(Optional.empty());

        TransferMoneyResponse response = accountsService.transferMoney(1L, 2L, "100.00");

        assertEquals(-1, response.getResult());
        assertEquals("Account recipient not found", response.getError());
    }

    @Test // Попытка пополнить счёт не существующему пользователю
    void putMoney_WhenAccountNotFound_ShouldReturnError() {
        when(accountsRepository.findById(nonExistingAccountId)).thenReturn(Optional.empty());

        PutMoneyResponse response = accountsService.putMoney(nonExistingAccountId, "100.00");

        assertEquals(-1, response.getResult());
        assertEquals("Account not found", response.getError());
    }

    @Test // Проверяет созданную историю снятия средств
    void takeMoney_ShouldSaveCorrectHistory() {
        when(accountsRepository.findById(existingAccountId)).thenReturn(Optional.of(testAccount));

        accountsService.takeMoney(existingAccountId, "200.00");

        ArgumentCaptor<History> historyCaptor = ArgumentCaptor.forClass(History.class);
        verify(historyRepository).save(historyCaptor.capture());
        History savedHistory = historyCaptor.getValue();

        assertEquals(existingAccountId, savedHistory.getRecipient());
        assertEquals(OperationType.WITHDRAW, savedHistory.getType());
        assertEquals(new BigDecimal("200.00"), savedHistory.getAmount());
        assertNull(savedHistory.getPayer());
    }
}
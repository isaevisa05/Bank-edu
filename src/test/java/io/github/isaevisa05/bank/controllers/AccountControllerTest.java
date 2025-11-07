package io.github.isaevisa05.bank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.isaevisa05.bank.dto.*;
import io.github.isaevisa05.bank.entity.History;
import io.github.isaevisa05.bank.service.AccountsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountsService accountsService;

    private GetBalanceResponse getBalanceResponse;
    private TakeMoneyResponse takeMoneyResponse;
    private PutMoneyResponse putMoneyResponse;
    private GetOperationListResponse getOperationListResponse;
    private TransferMoneyResponse transferMoneyResponse;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных
        getBalanceResponse = new GetBalanceResponse();
        takeMoneyResponse = new TakeMoneyResponse();
        putMoneyResponse = new PutMoneyResponse();
        getOperationListResponse = new GetOperationListResponse();
        transferMoneyResponse = new TransferMoneyResponse();
    }

    @Test // Получение баланса
    void getBalance_WhenValidRequest_ShouldReturnBalance() throws Exception {
        long accountId = 1L;
        getBalanceResponse.setResult(new BigDecimal("1000.00"));
        when(accountsService.getBalance(accountId)).thenReturn(getBalanceResponse);

        mockMvc.perform(get("/getBalance")
                        .param("accountId", String.valueOf(accountId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(1000.00)));
    }

    @Test // Получаем ошибку при получении баланса так как аккаунта с этим ид не существует
    void getBalance_WhenAccountNotFound_ShouldReturnError() throws Exception {
        long accountId = 999L;
        getBalanceResponse.setResult(new BigDecimal("-1"));
        getBalanceResponse.setError("Account not found");
        when(accountsService.getBalance(accountId)).thenReturn(getBalanceResponse);

        mockMvc.perform(get("/getBalance")
                        .param("accountId", String.valueOf(accountId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(-1)))
                .andExpect(jsonPath("$.error", is("Account not found")));
    }

    @Test // Успешная попытка снять деньги со счёта
    void takeMoney_WhenValidRequest_ShouldSuccess() throws Exception {
        long accountId = 1L;
        String amount = "500.00";
        takeMoneyResponse.setResult(1);
        when(accountsService.takeMoney(accountId, amount)).thenReturn(takeMoneyResponse);

        mockMvc.perform(post("/takeMoney")
                        .param("accountId", String.valueOf(accountId))
                        .param("amount", amount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(1)));
    }

    @Test // Ошибка при попытке снять деньги со счёта так как ввели неверный формат суммы
    void takeMoney_WhenInvalidAmount_ShouldReturnError() throws Exception {
        long accountId = 1L;
        String invalidAmount = "invalid";
        takeMoneyResponse.setResult(-2);
        takeMoneyResponse.setError("The amount is in an invalid format");
        when(accountsService.takeMoney(accountId, invalidAmount)).thenReturn(takeMoneyResponse);

        mockMvc.perform(post("/takeMoney")
                        .param("accountId", String.valueOf(accountId))
                        .param("amount", invalidAmount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(-2)))
                .andExpect(jsonPath("$.error", is("The amount is in an invalid format")));
    }

    @Test // Успешное пополнение счёта
    void putMoney_WhenValidRequest_ShouldSuccess() throws Exception {
        long accountId = 1L;
        String amount = "300.00";
        putMoneyResponse.setResult(1);
        when(accountsService.putMoney(accountId, amount)).thenReturn(putMoneyResponse);

        mockMvc.perform(post("/putMoney")
                        .param("accountId", String.valueOf(accountId))
                        .param("amount", amount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(1)));
    }

    @Test // Ошибка при пополнении счёта так как аккаунта с этим ид не существует
    void putMoney_WhenAccountNotFound_ShouldReturnError() throws Exception {
        long accountId = 999L;
        String amount = "300.00";
        putMoneyResponse.setResult(-1);
        putMoneyResponse.setError("Account not found");
        when(accountsService.putMoney(accountId, amount)).thenReturn(putMoneyResponse);

        mockMvc.perform(post("/putMoney")
                        .param("accountId", String.valueOf(accountId))
                        .param("amount", amount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(-1)))
                .andExpect(jsonPath("$.error", is("Account not found")));
    }

    @Test // Получение истории платежей
    void getOperationList_WithZeroNano_ShouldCallWithoutDates() throws Exception {
        long accountId = 1L;
        Instant startDate = Instant.ofEpochSecond(Instant.now().getEpochSecond()); // nano = 0
        Instant endDate = Instant.ofEpochSecond(Instant.now().getEpochSecond());   // nano = 0

        List<History> operations = List.of(new History(), new History(), new History());
        getOperationListResponse.setResult(operations);
        when(accountsService.getOperationList(accountId)).thenReturn(getOperationListResponse);

        mockMvc.perform(get("/getOperationList")
                        .param("accountId", String.valueOf(accountId))
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", hasSize(3)));
    }

    @Test // Успешный перевод другому пользователю
    void transferMoney_WhenValidRequest_ShouldSuccess() throws Exception {
        long payerId = 1L;
        long recipientId = 2L;
        String amount = "200.00";
        transferMoneyResponse.setResult(1);
        when(accountsService.transferMoney(payerId, recipientId, amount)).thenReturn(transferMoneyResponse);

        mockMvc.perform(post("/transferMoney")
                        .param("payer", String.valueOf(payerId))
                        .param("recipient", String.valueOf(recipientId))
                        .param("amount", amount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(1)));
    }

    @Test // Ошибка перевода нельзя переводить самому себе
    void transferMoney_WhenSelfTransfer_ShouldReturnError() throws Exception {
        long payerId = 1L;
        long recipientId = 1L; // same as payer
        String amount = "200.00";
        transferMoneyResponse.setResult(-4);
        transferMoneyResponse.setError("Translations to yourself are prohibited");
        when(accountsService.transferMoney(payerId, recipientId, amount)).thenReturn(transferMoneyResponse);

        mockMvc.perform(post("/transferMoney")
                        .param("payer", String.valueOf(payerId))
                        .param("recipient", String.valueOf(recipientId))
                        .param("amount", amount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(-4)))
                .andExpect(jsonPath("$.error", is("Translations to yourself are prohibited")));
    }

    @Test // Ошибка перевода недостаточно средств
    void transferMoney_WhenInsufficientFunds_ShouldReturnError() throws Exception {
        long payerId = 1L;
        long recipientId = 2L;
        String amount = "2000.00";
        transferMoneyResponse.setResult(0);
        transferMoneyResponse.setError("Insufficient funds");
        when(accountsService.transferMoney(payerId, recipientId, amount)).thenReturn(transferMoneyResponse);

        mockMvc.perform(post("/transferMoney")
                        .param("payer", String.valueOf(payerId))
                        .param("recipient", String.valueOf(recipientId))
                        .param("amount", amount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(0)))
                .andExpect(jsonPath("$.error", is("Insufficient funds")));
    }

    @Test // Ошибка перевода неверный формат суммы
    void transferMoney_WhenInvalidAmountFormat_ShouldReturnError() throws Exception {
        long payerId = 1L;
        long recipientId = 2L;
        String invalidAmount = "invalid";
        transferMoneyResponse.setResult(-2);
        transferMoneyResponse.setError("The amount is in an invalid format");
        when(accountsService.transferMoney(payerId, recipientId, invalidAmount)).thenReturn(transferMoneyResponse);

        mockMvc.perform(post("/transferMoney")
                        .param("payer", String.valueOf(payerId))
                        .param("recipient", String.valueOf(recipientId))
                        .param("amount", invalidAmount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(-2)))
                .andExpect(jsonPath("$.error", is("The amount is in an invalid format")));
    }

    // Дополнительные тесты на валидацию параметров

    @Test
    void getBalance_WhenMissingAccountId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/getBalance"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void takeMoney_WhenMissingParameters_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/takeMoney"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOperationList_WhenMissingParameters_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/getOperationList"))
                .andExpect(status().isBadRequest());
    }
}
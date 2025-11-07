package io.github.isaevisa05.bank.repository;

import io.github.isaevisa05.bank.entity.History;
import io.github.isaevisa05.bank.entity.enums.OperationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DataJpaTest
class HistoryRepositoryTest {

    @Autowired
    private AccountsRepository accountsRepository;
    @Autowired
    private HistoryRepository historyRepository;

    @Test
    void createHistory() {
        History history = createTestHistory();
        historyRepository.save(history);

        assertNotEquals(Optional.empty(), historyRepository.findById(history.getId()));

        var findHistory = historyRepository.findById(history.getId()).get();
        assertNotEquals(0, findHistory.getId());
        assertEquals(history, findHistory);
    }

    @Test
    void findAllByAccountIdAndTimeStampRange() {
        History history1 = createTestHistory();
        History history2 = createTestHistory();
        History history3 = createTestHistory();
        history1.setTime(Instant.ofEpochMilli(0L));
        history2.setTime(Instant.ofEpochMilli(10L));
        history3.setTime(Instant.ofEpochMilli(50L));

        assertEquals(List.of(history2), historyRepository.findAllByAccountIdAndTimeStampRange(1L, Instant.ofEpochMilli(5L), Instant.ofEpochMilli(40L)));
    }

    @Test
    void findAllByAccountIdAndTimeStampRange2() {
        History history1 = createTestHistory();
        History history2 = createTestHistory();
        History history3 = createTestHistory();
        history1.setTime(Instant.ofEpochMilli(0L));
        history2.setTime(Instant.ofEpochMilli(10L));
        history3.setTime(Instant.ofEpochMilli(50L));

        History history4 = createTestHistory();
        history4.setRecipient(1L);
        history4.setTime(Instant.ofEpochMilli(12L));

        assertEquals(List.of(history2, history4), historyRepository.findAllByAccountIdAndTimeStampRange(1L, Instant.ofEpochMilli(5L), Instant.ofEpochMilli(40L)));
    }

    @Test
    void findAllByAccountId() {
        final List<History> findAllByAccountId = new ArrayList<>();
        findAllByAccountId.add(createTestHistory());
        findAllByAccountId.add(createTestHistory());

        assertEquals(findAllByAccountId, historyRepository.findAllByAccountId(1L));
    }

    // accountId payer=1L recipient=2L
    private History createTestHistory() {
        History history = new History();
        history.setPayer(1L);
        history.setRecipient(2L);
        history.setType(OperationType.DEPOSIT);
        history.setAmount(new BigDecimal("0.00"));
        return historyRepository.save(history);
    }
}
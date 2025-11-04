package io.github.isaevisa05.bank.repository;

import io.github.isaevisa05.bank.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("SELECT h FROM History h WHERE (h.payer = :accountId OR h.recipient = :accountId) AND h.time BETWEEN :startDate AND :endDate")
    List<History> findAllByAccountIdAndTimeStampRange(Long accountId, Instant startDate, Instant endDate);
    @Query("SELECT h FROM History h WHERE (h.payer = :accountId OR h.recipient = :accountId)")
    List<History> findAllByAccountId(Long accountId);
}

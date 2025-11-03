package io.github.isaevisa05.bank.repository;

import io.github.isaevisa05.bank.entity.Account;
import io.github.isaevisa05.bank.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}

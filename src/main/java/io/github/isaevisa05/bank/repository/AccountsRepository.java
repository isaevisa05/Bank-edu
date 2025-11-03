package io.github.isaevisa05.bank.repository;

import io.github.isaevisa05.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Account, Long> {
}

package io.github.isaevisa05.bank.entity;

import io.github.isaevisa05.bank.entity.enums.OperationType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "balance", precision = 16, scale = 2)
    private BigDecimal balance;

    public void addBalance(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public boolean subtractBalance(BigDecimal amount) {
        BigDecimal newBalance = balance.subtract(amount);
        if(newBalance.compareTo(BigDecimal.ZERO) < 0) return false;
        balance = newBalance;
        return true;
    }
}

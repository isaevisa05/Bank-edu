package io.github.isaevisa05.bank.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        balance = balance.add(amount).stripTrailingZeros().setScale(2, RoundingMode.DOWN);
    }

    public boolean subtractBalance(BigDecimal amount) {
        BigDecimal newBalance = balance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) return false;
        balance = newBalance;
        return true;
    }

    @PrePersist
    protected void onCreate() {
        balance = new BigDecimal("0.00");
    }
}

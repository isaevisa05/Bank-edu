package io.github.isaevisa05.bank.entity;

import io.github.isaevisa05.bank.entity.enums.OperationType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "history")
@Data
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "payer")
    private long payer;

    @Column(name = "recipient")
    private Long recipient;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private OperationType type;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "time", nullable = false)
    private Instant time;

    @PrePersist
    protected void onCreate() {
        time = Instant.now();
    }


}

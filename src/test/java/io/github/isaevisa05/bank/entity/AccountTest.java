package io.github.isaevisa05.bank.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    public static final BigDecimal onCreateBalance = new BigDecimal("0.00");

    @Test
    void addBalance() {
        Account account = new Account();
        account.onCreate();

        BigDecimal startBalance = account.getBalance();
        BigDecimal added = new BigDecimal("100.00");
        BigDecimal newBalance = startBalance.add(added);

        account.addBalance(added);

        assertEquals(newBalance, account.getBalance());
    }

    @Test
    void subtractBalance() {
        Account account = new Account();
        account.onCreate();

        // Update account balance set to 100.00
        account.setBalance(new BigDecimal("100.00"));

        BigDecimal startBalance = account.getBalance();
        BigDecimal removed = new BigDecimal("100.00");
        BigDecimal newRemovedBalance = startBalance.subtract(removed);

        assertTrue(account.subtractBalance(removed));
        assertEquals(newRemovedBalance, account.getBalance());
        assertFalse(account.subtractBalance(removed));
    }

    @Test
    void onCreate() {
        Account account = new Account();
        account.onCreate();
        assertEquals(onCreateBalance, account.getBalance());
    }
}
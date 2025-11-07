package io.github.isaevisa05.bank.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HistoryTest {

    @Test
    void onCreate() {
        History history = new History();
        history.onCreate();

        assertNotNull(history.getTime());
    }
}
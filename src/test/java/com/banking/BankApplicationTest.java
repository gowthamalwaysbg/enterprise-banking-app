package com.banking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankApplicationTest {

    @Test
    void testCheckBalance() {
        BankApplication bank = new BankApplication(1000);
        assertEquals(1000, bank.checkBalance());
    }

    @Test
    void testDeposit() {
        BankApplication bank = new BankApplication(1000);
        bank.deposit(500);
        assertEquals(1500, bank.checkBalance());
    }

    @Test
    void testWithdraw() {
        BankApplication bank = new BankApplication(1000);
        bank.withdraw(300);
        assertEquals(700, bank.checkBalance());
    }

    @Test
    void testInvalidDeposit() {
        BankApplication bank = new BankApplication(1000);
        assertThrows(IllegalArgumentException.class,
                () -> bank.deposit(-100));
    }
}

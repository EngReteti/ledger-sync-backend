package com.ledger;

import java.math.BigDecimal;

/**
 * Day 7: Quality Assurance
 * Automated verification of TransactionManager logic.
 */
public class TransactionTest {
    public static void main(String[] args) {
        testSuccessfulTransfer();
        testInsufficientFunds();
    }

    public static void testSuccessfulTransfer() {
        System.out.println("[TEST] Running Successful Transfer...");
        Account a = new Account(1, "Test User A", new BigDecimal("100.00"));
        Account b = new Account(2, "Test User B", new BigDecimal("50.00"));
        TransactionManager tm = new TransactionManager();

        boolean result = tm.transfer(a, b, new BigDecimal("40.00"));
        
        if (result && a.getBalance().equals(new BigDecimal("60.00"))) {
            System.out.println("Result: PASS");
        } else {
            System.out.println("Result: FAIL");
        }
    }

    public static void testInsufficientFunds() {
        System.out.println("\n[TEST] Running Insufficient Funds Check...");
        Account a = new Account(1, "Test User A", new BigDecimal("10.00"));
        Account b = new Account(2, "Test User B", new BigDecimal("50.00"));
        TransactionManager tm = new TransactionManager();

        // Attempting to send more than available
        boolean result = tm.transfer(a, b, new BigDecimal("100.00"));

        if (!result && a.getBalance().equals(new BigDecimal("10.00"))) {
            System.out.println("Result: PASS (Blocked illegal transaction)");
        } else {
            System.out.println("Result: FAIL");
        }
    }
}


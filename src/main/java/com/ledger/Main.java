package com.ledger;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        TransactionManager manager = new TransactionManager();
        Account alice = new Account(1, "Alice Reteti", new BigDecimal("1000.00"));
        Account bob = new Account(2, "Bob Lerionka", new BigDecimal("500.00"));

        try {
            System.out.println("Executing transfer...");
            manager.transfer(alice, bob, new BigDecimal("1200.00")); // This will fail
        } catch (InsufficientFundsException e) {
            System.err.println("ALERT: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: " + e.getMessage());
        }

        System.out.println("Final Alice Balance: " + alice.getBalance());
    }
}


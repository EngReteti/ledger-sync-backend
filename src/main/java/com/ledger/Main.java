package com.ledger;

import java.math.BigDecimal;

/**
 * Day 6: CLI Engine
 * Entry point to simulate ledger transactions.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Ledger-Sync System Initialization ===");

        // 1. Initialize Transaction Manager
        TransactionManager manager = new TransactionManager();

        // 2. Create sample accounts
        Account alice = new Account(1, "Alice Reteti", new BigDecimal("1000.00"));
        Account bob = new Account(2, "Bob Lerionka", new BigDecimal("500.00"));

        System.out.println("Initial State:");
        System.out.println(alice);
        System.out.println(bob);
        System.out.println("------------------------------------------");

        // 3. Simulate a successful transfer
        BigDecimal transferAmount = new BigDecimal("250.00");
        System.out.println("Action: Transferring " + transferAmount + " from Alice to Bob...");
        manager.transfer(alice, bob, transferAmount);

        // 4. Simulate a failed transfer (Insufficient Funds)
        BigDecimal invalidAmount = new BigDecimal("2000.00");
        System.out.println("\nAction: Attempting to transfer " + invalidAmount + " from Alice to Bob...");
        manager.transfer(alice, bob, invalidAmount);

        System.out.println("------------------------------------------");
        System.out.println("Final State:");
        System.out.println(alice);
        System.out.println(bob);
        System.out.println("=== System Shutdown ===");
    }
}


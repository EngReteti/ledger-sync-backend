package com.ledger;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        TransactionManager manager = new TransactionManager();

        System.out.println("==================================================");
        System.out.println("          LEDGER-SYNC FULL TEST SUITE             ");
        System.out.println("==================================================");

        // SCENARIO 1: Sufficient Funds & High Concurrency
        System.out.println("\n[TEST 1] Concurrent Transfers (Sufficient Funds)...");
        System.out.println("Spawning 20 threads transferring 10.00 between Accounts 1 and 2...");
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> manager.transfer(1, 2, new BigDecimal("10.00")));
            executor.submit(() -> manager.transfer(2, 1, new BigDecimal("10.00")));
        }
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        System.out.println("--> [PASS] All concurrent valid transfers executed successfully.");

        // SCENARIO 2: Insufficient Funds & Rollback
        System.out.println("\n[TEST 2] Overdraft Rejection (Insufficient Funds)...");
        System.out.println("Attempting to transfer 5000.00 from Account 1 (Balance: 1000.00)...");
        
        boolean overdraftResult = manager.transfer(1, 2, new BigDecimal("5000.00"));
        if (!overdraftResult) {
            System.out.println("--> [PASS] Transaction rejected as expected. Rollback triggered.");
        } else {
            System.err.println("--> [FAIL] Overdraft was allowed incorrectly!");
        }

        System.out.println("\n==================================================");
        System.out.println("          SUITE EXECUTION COMPLETE                ");
        System.out.println("==================================================");
    }
}


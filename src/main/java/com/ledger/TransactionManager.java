package com.ledger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private final LedgerRepository repository = new LedgerRepository();

    public boolean transfer(int srcId, int targetId, BigDecimal amount) {
        // Enforce lock acquisition order to eliminate circular deadlocks
        int firstLock = Math.min(srcId, targetId);
        int secondLock = Math.max(srcId, targetId);

        try (Connection conn = DatabaseConfig.getConnection()) {
            try {
                // Acquire row-level pessimistic locks in deterministic order
                repository.getBalanceForUpdate(conn, firstLock);
                repository.getBalanceForUpdate(conn, secondLock);

                BigDecimal srcBalance = repository.getBalanceForUpdate(conn, srcId);
                BigDecimal targetBalance = repository.getBalanceForUpdate(conn, targetId);

                // Verify source account balance
                if (srcBalance.compareTo(amount) < 0) {
                    repository.logTransaction(conn, srcId, targetId, amount, "FAILED_INSUFFICIENT_FUNDS");
                    conn.commit();
                    return false;
                }

                // Compute updated balances
                BigDecimal newSrcBalance = srcBalance.subtract(amount);
                BigDecimal newTargetBalance = targetBalance.add(amount);

                // Perform updates and audit log entry
                repository.updateBalance(conn, srcId, newSrcBalance);
                repository.updateBalance(conn, targetId, newTargetBalance);
                repository.logTransaction(conn, srcId, targetId, amount, "SUCCESS");

                // Commit transaction atomically
                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback(); // Rollback on any SQL failure
                System.err.println("[ROLLBACK] Transaction aborted: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[CONNECTION_ERROR] " + e.getMessage());
            return false;
        }
    }
}


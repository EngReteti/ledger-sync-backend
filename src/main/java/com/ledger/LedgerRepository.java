package com.ledger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LedgerRepository {

    // Issue a pessimistic row-level lock using SELECT ... FOR UPDATE
    public BigDecimal getBalanceForUpdate(Connection conn, int accountId) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE account_id = ? FOR UPDATE";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                } else {
                    throw new SQLException("Account ID " + accountId + " not found.");
                }
            }
        }
    }

    // Update account balance
    public void updateBalance(Connection conn, int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setInt(2, accountId);
            pstmt.executeUpdate();
        }
    }

    // Insert transaction audit record
    public void logTransaction(Connection conn, int srcId, int targetId, BigDecimal amount, String status) throws SQLException {
        String sql = "INSERT INTO transaction_logs (source_account_id, target_account_id, amount, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, srcId);
            pstmt.setInt(2, targetId);
            pstmt.setBigDecimal(3, amount);
            pstmt.setString(4, status);
            pstmt.executeUpdate();
        }
    }
}


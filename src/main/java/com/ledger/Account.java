package com.ledger;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Day 4: Account Entity
 * Represents a financial account in the ledger system.
 */
public class Account {
    private int accountId;
    private String ownerName;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    // Constructor
    public Account(int accountId, String ownerName, BigDecimal balance) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + accountId +
                ", owner='" + ownerName + '\'' +
                ", balance=" + balance +
                '}';
    }
}


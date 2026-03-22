package com.ledger;

import java.math.BigDecimal;

public class TransactionManager {

    public void transfer(Account sender, Account receiver, BigDecimal amount) throws InsufficientFundsException {
        // Validation for nulls
        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Accounts cannot be null.");
        }

        // Validation for negative amounts
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }

        // Validation for balance
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Account " + sender.getAccountId() + " has insufficient funds.");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        System.out.println("SUCCESS: " + amount + " transferred from " + sender.getOwnerName() + " to " + receiver.getOwnerName());
    }
}


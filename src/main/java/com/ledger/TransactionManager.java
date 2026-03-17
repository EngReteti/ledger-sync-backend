package com.ledger;

import java.math.BigDecimal;

/**
 * Day 5: Business Logic
 * Manages fund transfers and ensures data integrity.
 */
public class TransactionManager {

    /**
     * Executes a transfer between two accounts.
     * @param sender The account sending funds.
     * @param receiver The account receiving funds.
     * @param amount The value to be transferred.
     * @return true if successful, false if insufficient funds.
     */
    public boolean transfer(Account sender, Account receiver, BigDecimal amount) {
        // Validation: Ensure amount is positive and sender has enough funds
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || sender.getBalance().compareTo(amount) < 0) {
            System.out.println("Transaction Failed: Insufficient funds or invalid amount.");
            return false;
        }

        // Deduct from sender
        sender.setBalance(sender.getBalance().subtract(amount));
        
        // Add to receiver
        receiver.setBalance(receiver.getBalance().add(amount));

        System.out.println("Transaction Successful: " + amount + " moved from " 
                           + sender.getOwnerName() + " to " + receiver.getOwnerName());
        return true;
    }
}


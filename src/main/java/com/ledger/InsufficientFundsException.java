package com.ledger;

/**
 * Day 8: Resilience
 * Custom exception for handling ledger-specific errors.
 */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}


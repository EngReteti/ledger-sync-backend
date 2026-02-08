-- Main table to store user balances
CREATE TABLE accounts (
    account_id VARCHAR(50) PRIMARY KEY,
    owner_name VARCHAR(100) NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table to track every transaction for audit integrity
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id VARCHAR(50),
    receiver_id VARCHAR(50),
    amount DECIMAL(15, 2),
    status ENUM('PENDING', 'COMPLETED', 'FAILED'),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES accounts(account_id),
    FOREIGN KEY (receiver_id) REFERENCES accounts(account_id)
);


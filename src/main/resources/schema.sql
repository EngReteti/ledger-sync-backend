-- Day 3: Persistence Layer Setup
-- Table to store account details
CREATE TABLE IF NOT EXISTS accounts (
    account_id SERIAL PRIMARY KEY,
    owner_name VARCHAR(100) NOT NULL,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table to log every transaction for integrity
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id SERIAL PRIMARY KEY,
    sender_id INT REFERENCES accounts(account_id),
    receiver_id INT REFERENCES accounts(account_id),
    amount DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Day 9: Performance Optimization
-- Indexing for faster account lookups and transaction history sorting
CREATE INDEX IF NOT EXISTS idx_accounts_owner ON accounts(owner_name);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(transaction_date);


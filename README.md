# Ledger-Sync Backend

A high-integrity Java backend designed for robust financial transaction management. This system implements core banking principles, ensuring atomic operations and defensive data handling.

## 🛠 Technical Highlights
- **Financial Precision:** Employs `java.math.BigDecimal` for all monetary calculations to eliminate floating-point rounding errors.
- **Defensive Architecture:** Custom exception handling via `InsufficientFundsException` ensures system resilience against invalid state transitions.
- **Data Modeling:** SQL persistence layer optimized with indexing for high-performance account lookups and transaction auditing.
- **Package Integrity:** Adheres to the Maven Standard Directory Layout, ensuring modularity and professional scalability.

## 🚀 Getting Started

### Compilation
From the project root, compile the full package:
```bash
javac src/main/java/com/ledger/*.java


# 💸 Ledger-Sync Backend

A high-integrity Java backend designed to handle concurrent financial transactions with strict data validation. This project demonstrates the intersection of **distributed systems logic** and **relational database management**.

## 🛠️ System Architecture
The system is built using a modular approach to ensure scalability and reliability:
- **Models**: Encapsulated Java objects (`Account.java`) representing database entities.
- **Logic Layer**: Centralized transaction management (`TransactionManager.java`) to prevent data race conditions.
- **Persistence**: Structured SQL schema (`schema.sql`) optimized for audit trails and ACID compliance.



## 🚀 Key Engineering Features
- **Strict Validation Logic**: The system performs state-checks to prevent negative balances before any data is moved.
- **Audit-Ready Design**: Every movement is modeled to be tracked via a separate relational table for data integrity.
- **Standardized Structure**: Follows the Maven/Gradle directory convention used in professional enterprise environments.

## 📂 Project Structure
```text
src/main/java/        # Core Backend Logic (Account, Manager, Main)
src/main/resources/   # SQL Schemas & Database Blueprints
docs/                 # Architectural Research & Documentation

## 💻 Usage & Execution
To simulate the logic-driven transaction:
1. Ensure you have **Java (JDK 8+)** installed.
2. Compile the files: `javac src/main/java/*.java`.
3. Run the simulation: `java -cp src/main/java Main`.

---
*Developed as part of the Logic Nexus initiative by [EngReteti](https://github.com/EngReteti).*

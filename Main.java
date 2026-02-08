public class Main {
    public static void main(String[] args) {
        // 1. Initialize two test accounts with different balances
        Account userA = new Account("USR-001", "Amason", 1500.00);
        Account userB = new Account("USR-002", "Reteti", 500.00);

        // 2. Initialize the Logic Layer
        TransactionManager manager = new TransactionManager();

        System.out.println("--- Starting Logic-Sync Simulation ---");
        System.out.println("Initial Balance - " + userA.ownerName + ": $" + userA.getBalance());
        System.out.println("Initial Balance - " + userB.ownerName + ": $" + userB.getBalance());

        // 3. Execute a secure transfer of $300
        System.out.println("\nExecuting Transfer: $300.00...");
        boolean success = manager.transfer(userA, userB, 300.00);

        // 4. Final Output to verify data integrity
        if (success) {
            System.out.println("Status: TRANSACTION SUCCESSFUL");
            System.out.println("New Balance - " + userA.ownerName + ": $" + userA.getBalance());
            System.out.println("New Balance - " + userB.ownerName + ": $" + userB.getBalance());
        } else {
            System.out.println("Status: TRANSACTION FAILED - Logic Check Triggered");
        }
        System.out.println("--- Simulation Complete ---");
    }
}


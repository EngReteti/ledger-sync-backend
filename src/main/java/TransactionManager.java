public class TransactionManager {
    
    // Core logic to transfer funds between two accounts
    public boolean transfer(Account sender, Account receiver, double amount) {
        // Logic Check: Does the sender have enough funds?
        if (sender.getBalance() >= amount) {
            
            // Deduct from sender
            sender.setBalance(sender.getBalance() - amount);
            
            // Add to receiver
            receiver.setBalance(receiver.getBalance() + amount);
            
            System.out.println("Transaction Successful: " + amount + " moved.");
            return true;
        } else {
            System.out.println("Transaction Failed: Insufficient funds.");
            return false;
        }
    }
}


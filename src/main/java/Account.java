public class Account {
    private String accountId;
    private String ownerName;
    private double balance;

    // Constructor to initialize a new account logic
    public Account(String accountId, String ownerName, double balance) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    // Method to check the current logic of the balance
    public double getBalance() {
        return balance;
    }

    // Logic to update balance during a transaction
    public void setBalance(double balance) {
        this.balance = balance;
    }
}


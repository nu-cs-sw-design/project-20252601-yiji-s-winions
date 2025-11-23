package domain;

import datasource.AccountRepository;

// Directly inherits from the base Account
public class CheckingAccount extends Account {
    private final double overdraftLimit = 500.00;

    // Must call the super constructor
    public CheckingAccount(double initialBalance, AccountRepository repo) {
        super(initialBalance, repo);
        this.accountType = "Checking";
    }

    @Override
    public void withdraw(double amount) {
        // Logic specific to checking is now directly inside the class
        if (this.balance + overdraftLimit >= amount) {
            this.balance -= amount; // Directly manipulate base balance
            // Transaction logging and persistence logic would be here or delegated
            // ... (code omitted for brevity)
        } else {
            throw new IllegalStateException("Withdrawal exceeds overdraft limit.");
        }
    }
}
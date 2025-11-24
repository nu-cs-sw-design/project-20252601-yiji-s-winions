package domain;

import datasource.AccountRepository;

public class CheckingAccount extends Account {
    private final double overdraftLimit = 500.00;

    // Constructor 1: For NEW Account creation (Generates ID)
    public CheckingAccount(String userId, double initialBalance, AccountRepository repo) {
        super(userId, initialBalance, repo);
        this.accountType = "Checking";
    }

    // Constructor 2: For LOADING from CSV (Accepts persistent ID)
    public CheckingAccount(String accountId, String userId, double initialBalance, AccountRepository repo) {
        // Calls the base Account loading constructor with the explicit type string
        super(accountId, userId, "Checking", initialBalance, repo);
    }

    @Override
    public void withdraw(double amount) {
        if (this.balance + overdraftLimit >= amount) {
            super.withdraw(amount);
            if (this.balance < 0) {
                System.out.println("ALERT: Account is in overdraft!");
            }
        } else {
            throw new IllegalStateException("Withdrawal exceeds overdraft limit.");
        }
    }
}
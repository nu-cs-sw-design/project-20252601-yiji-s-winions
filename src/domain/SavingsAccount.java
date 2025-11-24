package domain;

import datasource.AccountRepository;

public class SavingsAccount extends Account {
    private final double annualInterestRate = 0.025;
    private final int maxMonthlyWithdrawals = 4;
    private int withdrawalsThisMonth = 0;

    // Constructor 1: For NEW Account creation
    public SavingsAccount(String userId, double initialBalance, AccountRepository repo) {
        super(userId, initialBalance, repo);
        this.accountType = "Savings";
    }

    // Constructor 2: For LOADING from CSV (Accepts persistent ID)
    public SavingsAccount(String accountId, String userId, double initialBalance, AccountRepository repo) {
        // Calls the base Account loading constructor with the explicit type string
        super(accountId, userId, "Savings", initialBalance, repo);
    }

    @Override
    public void withdraw(double amount) {
        if (withdrawalsThisMonth >= maxMonthlyWithdrawals) {
            throw new IllegalStateException("Savings account withdrawal limit of " + maxMonthlyWithdrawals + " per month reached.");
        }

        super.withdraw(amount);
        withdrawalsThisMonth++;
    }

    public void applyMonthlyInterest() {
        double interestAmount = this.balance * (annualInterestRate / 12);

        if (interestAmount > 0) {
            super.deposit(interestAmount);
            System.out.printf("INFO: Interest of $%.2f credited to Savings Account %s.%n", interestAmount, getAccountId());
        }
        withdrawalsThisMonth = 0;
    }
}
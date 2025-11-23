package domain;

import datasource.AccountRepository;

public class SavingsAccount extends Account {
    private final double annualInterestRate = 0.025; // 2.5%
    private final int maxMonthlyWithdrawals = 4;
    private int withdrawalsThisMonth = 0;

    // Must call the super constructor
    public SavingsAccount(double initialBalance, AccountRepository repo) {
        super(initialBalance, repo);
        this.accountType = "Savings";
    }

    @Override
    public String getAccountType() { return accountType; }

    /**
     * Overrides withdraw to restrict the number of withdrawals per month.
     */
    @Override
    public void withdraw(double amount) {
        if (withdrawalsThisMonth >= maxMonthlyWithdrawals) {
            throw new IllegalStateException("Savings account withdrawal limit of " + maxMonthlyWithdrawals + " per month reached.");
        }

        // Use the base Account class's withdrawal logic (it handles balance change, transaction recording, and persistence)
        super.withdraw(amount);
        withdrawalsThisMonth++;
    }

    /**
     * Adds a Savings-specific method to calculate and credit interest.
     */
    public void applyMonthlyInterest() {
        double interestAmount = this.balance * (annualInterestRate / 12);

        if (interestAmount > 0) {
            // Use the base Account class's deposit method to update the balance and record the transaction
            super.deposit(interestAmount);
            // Since the base deposit records the transaction, we just print a confirmation
            System.out.printf("INFO: Interest of $%.2f credited to Savings Account %s.%n",
                    interestAmount, getAccountId());
        }
        // Reset count for a new period
        withdrawalsThisMonth = 0;
    }
}
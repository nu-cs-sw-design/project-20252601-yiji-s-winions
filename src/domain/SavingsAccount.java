package domain;

public class SavingsAccount extends AccountDecorator {
    private final double annualInterestRate = 0.025; // 2.5%
    private final int maxMonthlyWithdrawals = 4;
    private int withdrawalsThisMonth = 0;

    public SavingsAccount(Account account) {
        super(account);
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

        // Delegate core withdrawal logic to the decorated account (the BasicAccount logic)
        decoratedAccount.withdraw(amount);
        withdrawalsThisMonth++;
    }

    /**
     * Adds a Savings-specific method to calculate and credit interest.
     * This logic is applied directly to the account balance.
     */
    public void applyMonthlyInterest() {
        double interestAmount = decoratedAccount.getBalance() * (annualInterestRate / 12);
        if (interestAmount > 0) {
            // Use the decorated account's deposit method to update the balance and record the transaction
            decoratedAccount.deposit(interestAmount);
            // Manually save a special 'Interest' transaction for history tracking (conceptual)
            Transaction interestTx = new Transaction(
                    TransactionType.DEPOSIT,
                    interestAmount,
                    decoratedAccount.getAccountId(),
                    null
            );
            // Since we don't have a direct link to the AccountRepository here,
            // the logic is conceptually handled by the delegated deposit() method,
            // but in a real system, the Service layer would ensure this is flagged correctly.

            System.out.printf("INFO: Interest of $%.2f credited to Savings Account %s.%n",
                    interestAmount, decoratedAccount.getAccountId());
        }
        // Reset count for a new period (in a real app, this would be based on the calendar date)
        withdrawalsThisMonth = 0;
    }
}
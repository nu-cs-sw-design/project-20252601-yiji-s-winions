package domain;

// Concrete decorator implementation
public class CheckingAccount extends AccountDecorator {
    private final double overdraftLimit = 500.00;

    public CheckingAccount(Account account) {
        super(account);
        this.accountType = "Checking";
    }

    @Override
    public String getAccountType() { return accountType; }

    @Override
    public void withdraw(double amount) {
        // Checking-specific logic applied *before* delegation
        if (decoratedAccount.getBalance() + overdraftLimit >= amount) {
            // Delegate to the original account's core withdrawal logic
            decoratedAccount.withdraw(amount);
            if (decoratedAccount.getBalance() < 0) {
                System.out.println("ALERT: Checking account is in overdraft!");
            }
        } else {
            throw new IllegalStateException("Withdrawal exceeds overdraft limit.");
        }
    }
}
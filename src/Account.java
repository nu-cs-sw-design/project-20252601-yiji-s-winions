
import java.util.List;
import java.util.UUID;

// This class replaces the 'Account' interface AND the 'BasicAccount' concrete class
public class Account {
    private final String accountId;
    protected double balance;
    protected String accountType = "Basic";

    // Direct dependency on concrete repository
    protected final AccountRepository accountRepository;

    public Account(double initialBalance, AccountRepository repo) {
        this.accountId = UUID.randomUUID().toString();
        this.balance = initialBalance;
        this.accountRepository = repo;
    }

    public String getAccountId() { return accountId; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }

    // Core business logic methods (not abstract anymore)
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        this.balance += amount;
        Transaction tx = new Transaction(TransactionType.DEPOSIT, amount, this.accountId, null);
        accountRepository.saveTransaction(tx);
        accountRepository.save(this);
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (this.balance < amount) throw new IllegalStateException("Insufficient funds.");
        this.balance -= amount;
        Transaction tx = new Transaction(TransactionType.WITHDRAWAL, amount, this.accountId, null);
        accountRepository.saveTransaction(tx);
        accountRepository.save(this);
    }

    public void transfer(Account targetAccount, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Transfer amount must be positive.");

        // Use the withdrawal logic of this account
        this.withdraw(amount);

        // Use the deposit logic of the target account
        targetAccount.deposit(amount);

        // Save transfer transaction (usually done in the service layer, but here for completeness)
        Transaction tx = new Transaction(
                TransactionType.INTERNAL_TRANSFER,
                amount,
                this.accountId,
                targetAccount.getAccountId()
        );
        accountRepository.saveTransaction(tx);
        accountRepository.save(this);
    }

    public List<Transaction> viewTransactions() {
        return accountRepository.findTransactionsByAccountId(accountId);
    }
}
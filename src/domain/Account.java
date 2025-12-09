package domain;

import datasource.AccountRepository;
import java.util.List;
import java.util.UUID;

public class Account {
    private final String accountId;
    private final String userId;
    protected double balance;
    protected String accountType = "Basic";
    protected final AccountRepository accountRepository;

    public Account(String userId, double initialBalance, AccountRepository repo) {
        this.accountId = UUID.randomUUID().toString();
        this.userId = userId;
        this.balance = initialBalance;
        this.accountRepository = repo;
    }

    public Account(String accountId, String userId, String accountType, double initialBalance, AccountRepository repo) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountType = accountType;
        this.balance = initialBalance;
        this.accountRepository = repo;
    }

    // Getters
    public String getAccountId() { return accountId; }
    public String getUserId() { return userId; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }

    /**
     * Serializes the Account object into a CSV line format.
     */
    public String toCsvString() {
        return String.format("%s,%s,%s,%.2f",
                this.accountId, this.userId, this.accountType, this.balance);
    }

    // Core business logic methods (deposit, withdraw, transfer, viewTransactions remain as before...)
    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        this.balance += amount;
        Transaction tx = new Transaction(TransactionType.DEPOSIT, amount, this.accountId, null);
        accountRepository.saveTransaction(tx);
        accountRepository.save(this); // Saves to CSV
    }

    public void withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (this.balance < amount) throw new IllegalStateException("Insufficient funds.");
        this.balance -= amount;
        Transaction tx = new Transaction(TransactionType.WITHDRAWAL, amount, this.accountId, null);
        accountRepository.saveTransaction(tx);
        accountRepository.save(this); // Saves to CSV
    }

    public void pay(Account targetAccount, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Transfer amount must be positive.");

        this.withdraw(amount);
        targetAccount.deposit(amount);

        // Save transfer transaction
        Transaction tx = new Transaction(
                TransactionType.EXTERNAL_TRANSFER,
                amount,
                this.accountId,
                targetAccount.getAccountId()
        );
        accountRepository.saveTransaction(tx);
        accountRepository.save(this);
    }

    public void transfer(Account targetAccount, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Transfer amount must be positive.");

        this.withdraw(amount);
        targetAccount.deposit(amount);

        // Save transfer transaction
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
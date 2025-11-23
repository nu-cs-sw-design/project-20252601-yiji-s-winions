
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// The Account class is responsible for transaction logic and maintaining its own history/state.
public class Account {

    private final String accountId;
    private final String ownerId;
    private final String accountType;
    private double balance;

    // Internal class/Record for storing transaction details

    // Account history list (acts as the persistence/Datasource for Transactions)
    private final List<Transaction> history;

    // --- CONSTRUCTOR ---
    public Account(String ownerId, String accountType, double initialBalance) {
        this.accountId = UUID.randomUUID().toString();
        this.ownerId = ownerId;
        this.accountType = accountType;
        this.balance = 0.0;
        this.history = new ArrayList<>();

        if (initialBalance > 0) {
            deposit(initialBalance); // Log initial deposit
        }
    }

    // --- DOMAIN LOGIC: Transactions ---

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive.");
        this.balance += amount;
        logTransaction("DEPOSIT", amount, null);
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (this.balance < amount) {
            System.err.println("Withdrawal failed: Insufficient funds in " + accountId);
            return false;
        }
        this.balance -= amount;
        logTransaction("WITHDRAWAL", amount, null);
        return true;
    }

    // Transfers require collaboration between two Account objects.
    public boolean internalTransfer(Account targetAccount, double amount) {
        if (this.withdraw(amount)) {
            // Log the transfer FROM this account before depositing
            logTransaction("TRANSFER_OUT", amount, targetAccount.getAccountId());

            // Deposit to the target account
            targetAccount.deposit(amount);
            // Log the transfer INTO the target account
            targetAccount.logTransaction("TRANSFER_IN", amount, this.accountId);

            System.out.println("Transfer successful: " + amount + " from " + this.accountId + " to " + targetAccount.accountId);
            return true;
        }
        return false;
    }

    // --- DOMAIN LOGIC: History Management ---

    private void logTransaction(String type, double amount, String targetId) {
        Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                type,
                amount,
                targetId
        );
        this.history.add(tx);
        // In a real system, this is where you'd write to a database table.
    }

    public List<String> viewPastTransactions() {
        // Returns a string representation of the history for easy viewing
        return history.stream()
                .map(tx -> String.format("[%s] %s: %.2f (Target: %s)",
                        tx.timestamp.toLocalTime(),
                        tx.type,
                        tx.amount,
                        tx.targetId != null ? tx.targetId : "N/A"))
                .collect(Collectors.toList());
    }

    // --- GETTERS ---
    public String getAccountId() { return accountId; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
}
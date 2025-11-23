package domain;
import datasource.AccountRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

public class AccountHistory {
    private final String accountId;
    private final AccountRepository accountRepository;

    public AccountHistory(String accountId, AccountRepository repo) {
        this.accountId = accountId;
        this.accountRepository = repo;
    }

    /**
     * Inner class representing the balance at a specific point in time.
     */
    public static class BalanceSnapshot {
        private final Date date;
        private final double balance;

        public BalanceSnapshot(Date date, double balance) {
            this.date = date;
            this.balance = balance;
        }

        // Getters
        public Date getDate() { return date; }
        public double getBalance() { return balance; }

        @Override
        public String toString() {
            return String.format("[%s] Balance: $%.2f", date.toString(), balance);
        }
    }

    // --- History Logic Methods ---

    /**
     * Retrieves all transactions for the account.
     */
    public List<Transaction> getAllTransactions() {
        return accountRepository.findTransactionsByAccountId(accountId);
    }

    /**
     * Filters the account transactions based on date range and type.
     */
    public List<Transaction> getFilteredHistory(Date startDate, Date endDate, TransactionType type) {
        List<Transaction> transactions = getAllTransactions();

        return transactions.stream()
                .filter(tx -> {
                    boolean withinDate = tx.getDate().after(startDate) && tx.getDate().before(endDate);
                    boolean matchingType = (type == null) || tx.getType().equals(type);
                    return withinDate && matchingType;
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculates the running balance after each transaction.
     */
    public List<BalanceSnapshot> calculateRunningBalance(double initialBalance) {
        List<Transaction> transactions = getAllTransactions();
        List<BalanceSnapshot> snapshots = new ArrayList<>();
        double currentBalance = initialBalance;

        // Ensure transactions are processed in chronological order
        transactions.sort(Comparator.comparing(Transaction::getDate));

        for (Transaction tx : transactions) {
            double amount = tx.getAmount();

            // Determine if the transaction increases or decreases the balance
            if (tx.getType() == TransactionType.DEPOSIT ||
                    tx.getType() == TransactionType.INTERNAL_TRANSFER) {
                currentBalance += amount;
            } else if (tx.getType() == TransactionType.WITHDRAWAL ||
                    tx.getType() == TransactionType.EXTERNAL_TRANSFER) {
                currentBalance -= amount;
            }

            snapshots.add(new BalanceSnapshot(tx.getDate(), currentBalance));
        }

        return snapshots;
    }

    /**
     * Provides a summary of total amounts by transaction type.
     */
    public Map<String, Double> getSummary() {
        return getAllTransactions().stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.getType().name(),
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }
}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Replaces the AccountRepository interface
public class AccountRepository {
    private final Map<String, Account> accountStorage = new HashMap<>();
    private final Map<String, List<Transaction>> transactionStorage = new HashMap<>();

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accountStorage.get(id));
    }

    public void save(Account account) {
        accountStorage.put(account.getAccountId(), account);
        System.out.println("Account saved: " + account.getAccountId());
    }

    public void delete(String id) {
        accountStorage.remove(id);
    }

    public List<Transaction> findTransactionsByAccountId(String id) {
        return transactionStorage.getOrDefault(id, new ArrayList<>());
    }

    public void saveTransaction(Transaction transaction) {
        // Save transaction for source account
        transactionStorage.computeIfAbsent(transaction.getSourceAccountId(), k -> new ArrayList<>()).add(transaction);

        // Simple logging for transfer to target account (not strictly necessary but aids history logic)
        if (transaction.getTargetAccountId() != null && !transaction.getTargetAccountId().isEmpty()) {
            // Create a related deposit transaction for the target account's history view
            Transaction targetTxn = new Transaction(
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getTargetAccountId(), // Source is now the target
                    transaction.getSourceAccountId()  // Target is now the source
            );
            transactionStorage.computeIfAbsent(transaction.getTargetAccountId(), k -> new ArrayList<>()).add(targetTxn);
        }
    }
}
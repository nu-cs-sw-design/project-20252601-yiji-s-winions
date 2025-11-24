package datasource;

import domain.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class AccountRepository {
    private static final String FILE_PATH = "accounts.csv";
    // Stores all accounts, key is AccountId
    private final Map<String, Account> accountCache = new HashMap<>();
    // Simplified transaction storage (keeping it in-memory for this repo for now)
    private final Map<String, List<Transaction>> transactionStorage = new HashMap<>();

    public AccountRepository() {
        loadDataFromCsv();
    }

    // --- NEW METHOD TO HANDLE OBJECT CREATION ---
    private Account createAccountInstance(String accountId, String userId, String accountType, double balance, AccountRepository repo) {
        // This logic replaces the external AccountFactory
        switch (accountType) {
            case "Checking":
                return new CheckingAccount(accountId, userId, balance, repo);
            case "Savings":
                return new SavingsAccount(accountId, userId, balance, repo);
            case "Investment":
                return new InvestmentAccount(accountId, userId, balance, repo);
            default:
                // Fallback to the base Account class for "Basic" or unknown types
                return new Account(accountId, userId, accountType, balance, repo);
        }
    }
    // ---------------------------------------------


    private void loadDataFromCsv() {
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            try {
                Files.writeString(path, "accountId,userId,accountType,balance\n", StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                System.err.println("Error creating accounts.csv: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String accountId = parts[0];
                    String userId = parts[1];
                    String type = parts[2];
                    double balance = Double.parseDouble(parts[3]);

                    // CALL THE NEW PRIVATE CREATION METHOD
                    Account loadedAccount = createAccountInstance(accountId, userId, type, balance, this);

                    accountCache.put(accountId, loadedAccount);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading accounts from CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeDataToCsv() {
        List<String> lines = accountCache.values().stream()
                .map(Account::toCsvString)
                .collect(Collectors.toList());

        lines.add(0, "accountId,userId,accountType,balance");

        try {
            Files.write(Paths.get(FILE_PATH), lines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error writing accounts to CSV: " + e.getMessage());
        }
    }

    // --- Standard Repository Methods ---
    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accountCache.get(id));
    }

    public List<Account> findByUserId(String userId) {
        return accountCache.values().stream()
                .filter(account -> account.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void save(Account account) {
        accountCache.put(account.getAccountId(), account);
        writeDataToCsv();
    }

    // findTransactionsByAccountId and saveTransaction logic omitted for brevity...
    public List<Transaction> findTransactionsByAccountId(String id) {
        return transactionStorage.getOrDefault(id, new ArrayList<>());
    }

    public void saveTransaction(Transaction transaction) {
        // ... (existing in-memory logic)
        transactionStorage.computeIfAbsent(transaction.getSourceAccountId(), k -> new ArrayList<>()).add(transaction);

        if (transaction.getTargetAccountId() != null && !transaction.getTargetAccountId().isEmpty()) {
            Transaction targetTxn = new Transaction(
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getTargetAccountId(),
                    transaction.getSourceAccountId()
            );
            transactionStorage.computeIfAbsent(transaction.getTargetAccountId(), k -> new ArrayList<>()).add(targetTxn);
        }
    }
}
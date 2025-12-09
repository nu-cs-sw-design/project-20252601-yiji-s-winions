package datasource;

import domain.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class AccountRepository {
    private static final String FILE_PATH = "accounts.csv";
    private static final String TRANSACTIONS_FILE_PATH = "transactions.csv";

    // Stores all accounts, key is AccountId
    private final Map<String, Account> accountCache = new HashMap<>();
    // Stores transactions in memory grouped by accountId
    private final Map<String, List<Transaction>> transactionStorage = new HashMap<>();

    public AccountRepository() {
        loadDataFromCsv();
        loadTransactionsFromCsv();
    }

    private Account createAccountInstance(String accountId, String userId,
                                          String accountType, double balance,
                                          AccountRepository repo) {
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

    private void loadDataFromCsv() {
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            try {
                Files.writeString(path,
                        "accountId,userId,accountType,balance\n",
                        StandardOpenOption.CREATE_NEW);
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

                    Account loadedAccount = createAccountInstance(
                            accountId, userId, type, balance, this);

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

    private void ensureTransactionsFileExists() {
        Path path = Paths.get(TRANSACTIONS_FILE_PATH);
        if (!Files.exists(path)) {
            try {
                Files.writeString(path,
                        "transactionType,amount,sourceAccountId,targetAccountId\n",
                        StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                System.err.println("Error creating transactions.csv: " + e.getMessage());
            }
        }
    }

    private void loadTransactionsFromCsv() {
        Path path = Paths.get(TRANSACTIONS_FILE_PATH);
        if (!Files.exists(path)) {
            ensureTransactionsFileExists();
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                // keep empty last column, then trim
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;

                String typeStr = parts[0].trim();
                String amountStr = parts[1].trim();
                String sourceId = parts[2].trim();
                String targetRaw = parts[3].trim();
                String targetId = targetRaw.isEmpty() ? null : targetRaw;

                TransactionType type = TransactionType.valueOf(typeStr);
                double amount = Double.parseDouble(amountStr);

                Transaction txn = new Transaction(type, amount, sourceId, targetId);

                transactionStorage
                        .computeIfAbsent(sourceId, k -> new ArrayList<>())
                        .add(txn);

                // Debug:
                System.out.println("Loaded txn: " + type + " " + amount +
                        " for " + sourceId + " -> storage size now " +
                        transactionStorage.get(sourceId).size());
            }
        } catch (Exception e) {
            System.err.println("Error loading transactions from CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void appendTransactionToCsv(Transaction transaction) {
        ensureTransactionsFileExists();

        String target = transaction.getTargetAccountId();
        if (target == null) target = "";

        String line = String.format(
                "%s,%s,%s,%s%n",
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getSourceAccountId(),
                target
        );

        try {
            Files.write(Paths.get(TRANSACTIONS_FILE_PATH),
                    line.getBytes(),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Error writing transaction to CSV: " + e.getMessage());
        }
    }

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

    public List<Transaction> findTransactionsByAccountId(String id) {
        List<Transaction> txs = transactionStorage.getOrDefault(id, new ArrayList<>());
        System.out.println("History lookup for " + id + " -> " + txs.size() + " transactions");
        return transactionStorage.getOrDefault(id, new ArrayList<>());
    }

    public void saveTransaction(Transaction transaction) {
        // Store original transaction for source account
        transactionStorage
                .computeIfAbsent(transaction.getSourceAccountId(), k -> new ArrayList<>())
                .add(transaction);
        appendTransactionToCsv(transaction);

        // Mirror for target account (if any), same behavior as before
        if (transaction.getTargetAccountId() != null && !transaction.getTargetAccountId().isEmpty()) {
            Transaction targetTxn = new Transaction(
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getTargetAccountId(),
                    transaction.getSourceAccountId()
            );
            transactionStorage
                    .computeIfAbsent(transaction.getTargetAccountId(), k -> new ArrayList<>())
                    .add(targetTxn);
            appendTransactionToCsv(targetTxn);
        }
    }

    // Delete a single account and all of its transactions
    public void deleteAccountById(String accountId) {
        if (accountId == null) return;

        // Remove from in-memory cache
        Account removed = accountCache.remove(accountId);

        // Persist updated accounts.csv
        if (removed != null) {
            writeDataToCsv();
        }

        // Remove all transactions where this account is source or target
        deleteTransactionsForAccountIds(Collections.singleton(accountId));
    }

    // Delete all accounts for a given userId and all related transactions
    public void deleteAccountsByUserId(String userId) {
        if (userId == null) return;

        // Find all accounts for this user
        List<Account> accountsForUser = findByUserId(userId);
        if (accountsForUser.isEmpty()) {
            return;
        }

        // Collect their account IDs
        Set<String> accountIdsToDelete = accountsForUser.stream()
                .map(Account::getAccountId)
                .collect(Collectors.toSet());

        // Remove from in-memory cache
        for (String accountId : accountIdsToDelete) {
            accountCache.remove(accountId);
        }

        // Persist updated accounts.csv
        writeDataToCsv();

        // Remove all related transactions
        deleteTransactionsForAccountIds(accountIdsToDelete);
    }

    // Helper: remove all transactions involving any of the given account IDs
    private void deleteTransactionsForAccountIds(Set<String> accountIds) {
        if (accountIds == null || accountIds.isEmpty()) return;

        Path path = Paths.get(TRANSACTIONS_FILE_PATH);
        if (!Files.exists(path)) {
            // Nothing to clean up
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String header = reader.readLine(); // read header
            if (header == null) {
                header = "transactionType,amount,sourceAccountId,targetAccountId";
            }

            List<String> keptLines = new ArrayList<>();
            keptLines.add(header);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;

                String sourceId = parts[2].trim();
                String targetRaw = parts[3].trim();
                String targetId = targetRaw.isEmpty() ? null : targetRaw;

                // Skip any transaction where source or target is in the delete set
                if (accountIds.contains(sourceId) ||
                        (targetId != null && accountIds.contains(targetId))) {
                    continue;
                }

                keptLines.add(line);
            }

            // Rewrite transactions.csv with remaining transactions
            Files.write(path, keptLines,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE);

        } catch (IOException e) {
            System.err.println("Error rewriting transactions.csv: " + e.getMessage());
        }

        // Rebuild in-memory transactionStorage from the cleaned CSV
        transactionStorage.clear();
        loadTransactionsFromCsv();
    }

}

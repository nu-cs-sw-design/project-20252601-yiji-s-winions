package domain;

import java.util.Date;
import java.util.UUID;

public class Transaction {
    private final String transactionId;
    private final TransactionType type;
    private final double amount;
    private final Date date;
    private final String sourceAccountId;
    private final String targetAccountId; // Optional for DEPOSIT/WITHDRAWAL

    public Transaction(TransactionType type, double amount, String sourceAccountId, String targetAccountId) {
        this(UUID.randomUUID().toString(), type, amount, new Date(), sourceAccountId, targetAccountId);
    }

    public Transaction(String id, TransactionType type, double amount, Date date, String sourceAccountId, String targetAccountId) {
        this.transactionId = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }
}
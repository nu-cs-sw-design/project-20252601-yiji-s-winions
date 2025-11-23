
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;

public final class Transaction {

    private final String transactionId;
    private final LocalDateTime timestamp;
    private final String type;
    private final double amount;
    private final String sourceAccountId;
    private final String targetAccountId;

    public Transaction(String type, double amount, String sourceAccountId, String targetAccountId) {
        this.transactionId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
    }

    // --- Static Factory Methods ---
    public static Transaction createDeposit(double amount, String targetId) {
        // Source is null for an external deposit
        return new Transaction("DEPOSIT", amount, null, targetId);
    }

    public static Transaction createWithdrawal(double amount, String sourceId) {
        // Target is null for an external withdrawal
        return new Transaction("WITHDRAWAL", amount, sourceId, null);
    }

    public static Transaction createTransfer(String type, double amount, String sourceId, String targetId) {
        // Used for the paired TRANSFER_IN and TRANSFER_OUT logs
        return new Transaction(type, amount, sourceId, targetId);
    }

    // --- Public Getter Methods (Required in JDK 11 class) ---
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }

    // --- Override necessary methods for comparison and debugging ---
    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionId + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                '}';
    }
    // equals() and hashCode() would typically be overridden for a value object
}
package domain;

import datasource.AccountRepository;
import java.util.HashMap;
import java.util.Map;

public class InvestmentAccount extends Account {
    private final Map<String, Integer> portfolio = new HashMap<>();
    private final double managementFeeRate = 0.005;

    // Constructor 1: For NEW Account creation
    public InvestmentAccount(String userId, double initialBalance, AccountRepository repo) {
        super(userId, initialBalance, repo);
        this.accountType = "Investment";
    }

    // Constructor 2: For LOADING from CSV (Accepts persistent ID)
    public InvestmentAccount(String accountId, String userId, double initialBalance, AccountRepository repo) {
        // Calls the base Account loading constructor with the explicit type string
        super(accountId, userId, "Investment", initialBalance, repo);
        // Portfolio state would ideally be loaded from a separate data source here.
    }

    public void buySecurity(String ticker, int shares, double pricePerShare) {
        double cost = shares * pricePerShare;
        if (this.balance < cost) {
            throw new IllegalStateException("Insufficient funds to buy securities.");
        }

        super.withdraw(cost);
        portfolio.put(ticker, portfolio.getOrDefault(ticker, 0) + shares);
        System.out.printf("INFO: Purchased %d shares of %s for $%.2f.%n", shares, ticker, cost);
    }

    public void applyQuarterlyMaintenance() {
        double managementFee = this.balance * managementFeeRate;

        try {
            super.withdraw(managementFee);
            System.out.printf("INFO: Applied management fee of $%.2f to Investment Account %s.%n", managementFee, getAccountId());
        } catch (IllegalStateException e) {
            System.err.println("WARNING: Cannot apply management fee. " + e.getMessage());
        }
    }
}
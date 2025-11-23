package domain;

import datasource.AccountRepository;
import java.util.HashMap;
import java.util.Map;

public class InvestmentAccount extends Account {
    // Mock portfolio data: security name -> number of shares
    private final Map<String, Integer> portfolio = new HashMap<>();
    private final double managementFeeRate = 0.005; // 0.5%

    // Must call the super constructor
    public InvestmentAccount(double initialBalance, AccountRepository repo) {
        super(initialBalance, repo);
        this.accountType = "Investment";
    }

    @Override
    public String getAccountType() { return accountType; }

    /**
     * Adds an Investment-specific method to simulate buying securities.
     */
    public void buySecurity(String ticker, int shares, double pricePerShare) {
        double cost = shares * pricePerShare;
        if (this.balance < cost) {
            throw new IllegalStateException("Insufficient funds to buy securities.");
        }

        // Use the base Account class's withdrawal method
        super.withdraw(cost);

        // Update mock portfolio
        portfolio.put(ticker, portfolio.getOrDefault(ticker, 0) + shares);
        System.out.printf("INFO: Purchased %d shares of %s for $%.2f.%n", shares, ticker, cost);
    }

    /**
     * Adds an Investment-specific method to simulate calculating and applying management fees.
     */
    public void applyQuarterlyMaintenance() {
        // 1. Calculate management fee based on current balance
        double managementFee = this.balance * managementFeeRate;

        // 2. Use the base Account class's withdrawal method
        try {
            super.withdraw(managementFee);
            System.out.printf("INFO: Applied management fee of $%.2f to Investment Account %s.%n",
                    managementFee, getAccountId());
        } catch (IllegalStateException e) {
            // Handle case where fee cannot be withdrawn
            System.err.println("WARNING: Cannot apply management fee. " + e.getMessage());
        }
    }

    /**
     * Returns a string representation of the mock portfolio.
     */
    public String viewPortfolio() {
        return "Portfolio: " + portfolio.toString();
    }
}
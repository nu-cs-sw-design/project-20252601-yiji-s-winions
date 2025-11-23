
import java.util.HashMap;
import java.util.Map;

public class InvestmentAccount extends AccountDecorator {
    // Mock portfolio data: security name -> number of shares
    private final Map<String, Integer> portfolio = new HashMap<>();
    private final double managementFeeRate = 0.005; // 0.5%

    public InvestmentAccount(Account account) {
        super(account);
        this.accountType = "Investment";
    }

    @Override
    public String getAccountType() { return accountType; }

    /**
     * Adds an Investment-specific method to simulate buying securities.
     */
    public void buySecurity(String ticker, int shares, double pricePerShare) {
        double cost = shares * pricePerShare;
        if (decoratedAccount.getBalance() < cost) {
            throw new IllegalStateException("Insufficient funds to buy securities.");
        }

        // 1. Delegate withdrawal from the decorated account
        decoratedAccount.withdraw(cost);

        // 2. Update mock portfolio
        portfolio.put(ticker, portfolio.getOrDefault(ticker, 0) + shares);
        System.out.printf("INFO: Purchased %d shares of %s for $%.2f.%n", shares, ticker, cost);
    }

    /**
     * Adds an Investment-specific method to simulate calculating portfolio value and applying fees.
     */
    public void applyQuarterlyMaintenance() {
        // 1. Calculate management fee based on current balance
        double managementFee = decoratedAccount.getBalance() * managementFeeRate;

        // 2. Delegate withdrawal for the fee
        decoratedAccount.withdraw(managementFee);

        System.out.printf("INFO: Applied management fee of $%.2f to Investment Account %s.%n",
                managementFee, decoratedAccount.getAccountId());

        // Note: In a real system, methods to get mock portfolio value would also be here.
    }

    /**
     * Returns a string representation of the mock portfolio.
     */
    public String viewPortfolio() {
        return "Portfolio: " + portfolio.toString();
    }
}
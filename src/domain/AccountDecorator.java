package domain;

// Note: This pattern becomes less pure when the decorated object is not of the 'interface' type.
// We are forced to extend the concrete 'Account' class.
public abstract class AccountDecorator extends Account {
    // The account being decorated is now a concrete Account type
    protected final Account decoratedAccount;

    public AccountDecorator(Account account) {
        // Pass minimal initial balance and the repository from the decorated account to the super constructor
        super(account.getBalance(), account.accountRepository);
        this.decoratedAccount = account;
    }

    // All methods must now explicitly delegate or override the concrete Account methods
    @Override
    public String getAccountId() { return decoratedAccount.getAccountId(); }

    @Override
    public double getBalance() { return decoratedAccount.getBalance(); }

    @Override
    public void deposit(double amount) { decoratedAccount.deposit(amount); }

    // This is the method Decorators will typically override
    @Override
    public void withdraw(double amount) { decoratedAccount.withdraw(amount); }

    // The transfer method is complex and usually better handled by the base class logic.
    @Override
    public void transfer(Account targetAccount, double amount) { decoratedAccount.transfer(targetAccount, amount); }
}
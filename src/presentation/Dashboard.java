package presentation;

import domain.*;
import datasource.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Dashboard extends JPanel {

    private static final int PANEL_HEIGHT = 120;

    private final JPanel contentPanel;
    private final User user;
    private final AccountRepository accountRepository;

    public Dashboard(User user) {
        this.user = user;
        this.accountRepository = new AccountRepository();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Banking System Dashboard - " + user.getEmail());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JButton createBtn = new JButton("Create");
        JButton logoutBtn = new JButton("Logout");
        JButton closeUserBtn = new JButton("Close User Account");

        Dimension smallBtn = new Dimension(110, 28);
        createBtn.setPreferredSize(smallBtn);
        logoutBtn.setPreferredSize(smallBtn);
        closeUserBtn.setPreferredSize(new Dimension(160, 28));

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonBar.add(createBtn);
        buttonBar.add(logoutBtn);
        buttonBar.add(closeUserBtn);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(buttonBar, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);

        loadAccountsForUser();

        createBtn.addActionListener(e -> handleCreate());
        logoutBtn.addActionListener(e -> moveToLogin());

        closeUserBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Close user account not implemented yet.",
                        "Info", JOptionPane.INFORMATION_MESSAGE));
    }

    private void loadAccountsForUser() {
        contentPanel.removeAll();

        List<Account> accounts = accountRepository.findByUserId(user.getUserId());
        for (Account account : accounts) {
            addSectionPanel(createAccountPanel(account));
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void handleCreate() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Create New Account", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(400, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel typeLbl = new JLabel("Account Type:");
        String[] options = {"Checking", "Savings", "Investment"};
        JComboBox<String> typeDropdown = new JComboBox<>(options);

        JLabel nicknameLbl = new JLabel("Account Nickname:");
        JTextField nicknameField = new JTextField(20);

        JButton createBtn = new JButton("Create");
        JButton cancelBtn = new JButton("Cancel");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.add(createBtn);
        btnPanel.add(cancelBtn);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(typeLbl, gbc);

        gbc.gridx = 1;
        dialog.add(typeDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(nicknameLbl, gbc);

        gbc.gridx = 1;
        dialog.add(nicknameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(btnPanel, gbc);

        createBtn.addActionListener(e -> {
            String selectedType = (String) typeDropdown.getSelectedItem();
            String nickname = nicknameField.getText().trim();

            if (nickname.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter a nickname.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Account newAccount;
                double initialBalance = 0.0;

                switch (selectedType) {
                    case "Checking":
                        newAccount = new CheckingAccount(user.getUserId(), initialBalance, accountRepository);
                        break;
                    case "Savings":
                        newAccount = new SavingsAccount(user.getUserId(), initialBalance, accountRepository);
                        break;
                    case "Investment":
                        newAccount = new InvestmentAccount(user.getUserId(), initialBalance, accountRepository);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + selectedType);
                }

                accountRepository.save(newAccount);

                addSectionPanel(createAccountPanel(newAccount, nickname));

                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Failed to create account: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void moveToLogin() {
        JFrame oldFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        oldFrame.dispose();

        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new Login());
        frame.setVisible(true);
    }

    public void addSectionPanel(JPanel panel) {
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        int fixedHeight = PANEL_HEIGHT;

        Dimension maxSize = new Dimension(Integer.MAX_VALUE, fixedHeight);
        Dimension prefSize = new Dimension(1, fixedHeight);

        panel.setPreferredSize(prefSize);
        panel.setMinimumSize(prefSize);
        panel.setMaximumSize(maxSize);

        contentPanel.add(panel);
        contentPanel.add(Box.createVerticalStrut(10));

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createAccountPanel(Account account) {
        return createAccountPanel(account, null);
    }

    private JPanel createAccountPanel(Account account, String nicknameOverride) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String shortId = account.getAccountId().length() > 8
                ? account.getAccountId().substring(0, 8)
                : account.getAccountId();

        String displayName;
        if (nicknameOverride != null && !nicknameOverride.isEmpty()) {
            displayName = String.format("%s (%s Account, %s)", nicknameOverride,
                    account.getAccountType(), shortId);
        } else {
            displayName = String.format("%s Account • %s", account.getAccountType(), shortId);
        }

        JLabel nameLbl = new JLabel(displayName);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel balanceLbl = new JLabel();
        balanceLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        updateBalanceLabel(balanceLbl, account);

        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton payBtn = new JButton("Pay");
        JButton transferBtn = new JButton("Transfer");
        JButton closeBtn = new JButton("Close");
        JButton accHistoryBtn = new JButton("History");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.add(depositBtn);
        btnPanel.add(withdrawBtn);
        btnPanel.add(payBtn);
        btnPanel.add(transferBtn);
        btnPanel.add(closeBtn);
        btnPanel.add(accHistoryBtn);

        depositBtn.addActionListener(e -> handleDeposit(account, balanceLbl));
        withdrawBtn.addActionListener(e -> handleWithdraw(account, balanceLbl));

        payBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                Dashboard.this,
                "Pay functionality not implemented yet.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        ));

        transferBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                Dashboard.this,
                "Transfer functionality not implemented yet.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        ));

        closeBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                Dashboard.this,
                "Close account not implemented yet.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE
        ));

        accHistoryBtn.addActionListener(e -> showHistory(account));

        p.add(nameLbl, BorderLayout.WEST);
        p.add(balanceLbl, BorderLayout.EAST);
        p.add(btnPanel, BorderLayout.SOUTH);

        return p;
    }

    private void updateBalanceLabel(JLabel balanceLbl, Account account) {
        balanceLbl.setText(String.format("$%.2f", account.getBalance()));
    }

    private void handleDeposit(Account account, JLabel balanceLbl) {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter deposit amount:",
                "Deposit",
                JOptionPane.PLAIN_MESSAGE
        );
        if (input == null) return;

        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                throw new NumberFormatException("Amount must be positive.");
            }
            account.deposit(amount);
            accountRepository.save(account);
            updateBalanceLabel(balanceLbl, account);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid positive number.",
                    "Invalid Amount",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleWithdraw(Account account, JLabel balanceLbl) {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter withdrawal amount:",
                "Withdraw",
                JOptionPane.PLAIN_MESSAGE
        );
        if (input == null) return;

        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                throw new NumberFormatException("Amount must be positive.");
            }
            account.withdraw(amount);
            accountRepository.save(account);
            updateBalanceLabel(balanceLbl, account);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid positive number.",
                    "Invalid Amount",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showHistory(Account account) {
        List<Transaction> transactions = account.viewTransactions();

        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No transactions found for this account.",
                    "History",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Transaction tx : transactions) {
            sb.append(String.format(
                    "%s | %s | $%.2f | from %s to %s%n",
                    tx.getDate(),
                    tx.getType(),
                    tx.getAmount(),
                    tx.getSourceAccountId(),
                    tx.getTargetAccountId()
            ));
        }

        JTextArea area = new JTextArea(sb.toString(), 15, 50);
        area.setEditable(false);
        JScrollPane pane = new JScrollPane(area);

        JOptionPane.showMessageDialog(
                this,
                pane,
                "Transaction History",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}

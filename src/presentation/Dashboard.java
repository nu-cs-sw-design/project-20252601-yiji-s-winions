package presentation;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JPanel {

    private static final int PANEL_HEIGHT = 120;
    private final JPanel contentPanel;

    public Dashboard(String email) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Banking System Dashboard");
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

        createBtn.addActionListener(e -> handleCreate());
        logoutBtn.addActionListener(e -> moveToLogin());
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

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(typeLbl, gbc);

        gbc.gridx = 1;
        dialog.add(typeDropdown, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
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

            System.out.println("Create account:");
            System.out.println("Type = " + selectedType);
            System.out.println("Nickname = " + nickname);

            dialog.dispose();

            addSectionPanel(createSampleAccountPanel(
                    nickname + "; " + selectedType.trim() + " Account",
                    "$" + 0));
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

    private JPanel createSampleAccountPanel(String name, String balance) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel balanceLbl = new JLabel(balance);
        balanceLbl.setHorizontalAlignment(SwingConstants.RIGHT);

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

        p.add(nameLbl, BorderLayout.WEST);
        p.add(balanceLbl, BorderLayout.EAST);
        p.add(btnPanel, BorderLayout.SOUTH);

        return p;
    }
}

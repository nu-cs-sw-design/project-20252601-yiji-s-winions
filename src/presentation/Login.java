package presentation;

import domain.AuthService;

import javax.swing.*;
import java.awt.*;

public class Login extends JPanel {
    private final JTextField userField;
    private final JPasswordField pwdField;
    private final JButton loginBtn;
    private final JButton createAccBtn;
    private final JButton forgotPwdBtn;
    private final JLabel statusLbl;
    public Login() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Banking System Login");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel emailLbl = new JLabel("Email:");
        userField = new JTextField(15);

        JLabel pwdLbl = new JLabel("Password:");
        pwdField = new JPasswordField(15);

        loginBtn = new JButton("Login");

        statusLbl = new JLabel("");

        forgotPwdBtn = new JButton("Forgot Password");

        createAccBtn = new JButton("Create New Account");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        add(emailLbl, gbc);

        gbc.gridx = 1;
        add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(pwdLbl, gbc);

        gbc.gridx = 1;
        add(pwdField, gbc);
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        btnPanel.add(loginBtn);
        btnPanel.add(forgotPwdBtn);
        btnPanel.add(createAccBtn);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(btnPanel, gbc);

        gbc.gridy++;
        add(statusLbl, gbc);

        loginBtn.addActionListener(e -> handleLogin());
        createAccBtn.addActionListener(e -> handleCreateAcc());
        forgotPwdBtn.addActionListener(e -> handleForgotPwd());
    }

    private void updateStatus(String msg) {
        statusLbl.setText(msg);
    }

    private void moveToDashboard() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        new javax.swing.Timer(1000, e -> {
            frame.dispose();

            JFrame dashboardFrame = new JFrame("Dashboard");
            dashboardFrame.add(new Dashboard("Email"));
            dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            dashboardFrame.setSize(800, 800);
            dashboardFrame.setLocationRelativeTo(null);

            dashboardFrame.setVisible(true);

            ((javax.swing.Timer) e.getSource()).stop();
        }).start();
    }

    private void handleLogin() {
        boolean authenticated = true; // TODO: check if backend found account

        if (authenticated) {
            updateStatus("Logging in...");

            moveToDashboard();
        } else {
            updateStatus("Account not found, please create account with these credentials");
        }
    }

    private void handleCreateAcc() {
        boolean createdAccount = true; // TODO: check if backend created account

        if (createdAccount) {
            updateStatus("Logging in...");

            moveToDashboard();
        } else {
            updateStatus("Email already exists in database, try to reset password");
        }
    }

    private void handleForgotPwd() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reset Password", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(600, 240);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.setResizable(false);

        JLabel emailLbl = new JLabel("Enter your email:");

        JTextField emailField = new JTextField(20);
        Dimension emailSize = new Dimension(300, 28);
        emailField.setPreferredSize(emailSize);
        emailField.setMinimumSize(emailSize);
        emailField.setMaximumSize(emailSize);

        JButton submitBtn = new JButton("Submit");
        Dimension btnSize = new Dimension(120, 28);
        submitBtn.setPreferredSize(btnSize);
        submitBtn.setMinimumSize(btnSize);
        submitBtn.setMaximumSize(btnSize);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(btnSize);
        cancelBtn.setMinimumSize(btnSize);
        cancelBtn.setMaximumSize(btnSize);

        JLabel responseLbl = new JLabel("");

        String longestMsg = "Please enter existing email, or create new account with your email";
        JLabel measure = new JLabel(longestMsg);
        Dimension respSize = measure.getPreferredSize();
        responseLbl.setPreferredSize(respSize);
        responseLbl.setMinimumSize(respSize);
        responseLbl.setMaximumSize(respSize);


        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(emailLbl, gbc);

        gbc.gridy++;
        dialog.add(emailField, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);

        gbc.gridy++;
        dialog.add(btnPanel, gbc);

        gbc.gridy++;
        dialog.add(responseLbl, gbc);

        // Event listeners
        submitBtn.addActionListener(e -> {
            String email = emailField.getText().trim();

            System.out.println("Email submitted: " + email);

            // TODO: check if backend has an account with this email
            boolean emailExists = false;

            if (emailExists) {
                // TODO: send email with backend including new password for this account
                responseLbl.setText("Email sent with reset password!");
            } else {
                responseLbl.setText("Please enter existing email, or create new account with your email");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}

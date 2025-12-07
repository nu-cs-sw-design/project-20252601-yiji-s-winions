package presentation;

import domain.*;
import datasource.*;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.Random;

public class Login extends JPanel {

    private final AuthService authService;
    private final JTextField userField;
    private final JPasswordField pwdField;
    private final JButton loginBtn;
    private final JButton createAccBtn;
    private final JButton forgotPwdBtn;
    private final JTextField statusLbl;
    public Login() {
        UserRepository userRepo = new UserRepository();
        this.authService = new AuthService(userRepo);

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

        statusLbl = new JTextField("");
        statusLbl.setEditable(false);
        statusLbl.setBorder(null);
        statusLbl.setOpaque(false);
//        statusLbl.setForeground(Color.RED);   // optional
//        statusLbl.setFocusable(false);        // OPTIONAL — remove this if you *want* focus for copy
        statusLbl.setHorizontalAlignment(SwingConstants.CENTER);

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

    private void moveToDashboard(User user) {
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
        boolean authenticated = false;

        String email = userField.getText().trim();
        String password = new String(pwdField.getPassword());

        Optional<User> userOpt = authService.login(email, password);

        if (userOpt.isPresent()) {
            authenticated = true;
        }

        if (authenticated) {
            User user = userOpt.get();
            updateStatus("Logging in...");

            moveToDashboard(user);
        } else {
            updateStatus("Account not found, please create account with these credentials");
        }
    }

    private void handleCreateAcc() {
        String email = userField.getText().trim();
        String password = new String(pwdField.getPassword());

        try {
            User userOpt = authService.register(email, password);

            updateStatus("Logging in...");

            moveToDashboard(userOpt);
        } catch (Exception ex) {
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

        JTextField responseLbl = new JTextField("");
        responseLbl.setEditable(false);
        responseLbl.setBorder(null);
        responseLbl.setOpaque(false);
        responseLbl.setHorizontalAlignment(SwingConstants.CENTER);

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

            User user = authService.getUser(email);

            if (user != null) {
                String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                StringBuilder stringBuilder = new StringBuilder();
                Random rnd = new Random();

                while (stringBuilder.length() < 18) { // length of the random string.
                    int index = (int) (rnd.nextFloat() * CHARS.length());
                    stringBuilder.append(CHARS.charAt(index));
                }
                String newPassword = stringBuilder.toString();

                responseLbl.setText("Your new password is " + newPassword);
                authService.changePassword(user, newPassword);
            } else {
                responseLbl.setText("Please enter existing email, or create new account");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}

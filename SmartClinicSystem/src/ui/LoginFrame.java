package ui;

import service.UserService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserService userService = new UserService();

    public LoginFrame() {

        setTitle("Smart Clinic Login");
        setSize(520, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 248, 252));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 45, 30, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Smart Clinic System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Login to continue");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(260, 36));

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(260, 36));

        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(260, 40));

        JButton registerButton = new JButton("Register as Patient");
        registerButton.setPreferredSize(new Dimension(260, 40));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        gbc.gridwidth = 1;

        addRow(mainPanel, gbc, 2, "Username:", usernameField);
        addRow(mainPanel, gbc, 3, "Password:", passwordField);

        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(loginButton, gbc);

        gbc.gridy = 5;
        mainPanel.add(registerButton, gbc);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> registerPatient());

        add(mainPanel);
        setVisible(true);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;

        panel.add(field, gbc);
    }

    private void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        String role = userService.login(username, password);

        if (role != null) {
            JOptionPane.showMessageDialog(this, "Login successful as " + role + ".");
            dispose();
            new MainDashboard(role, username);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    private void registerPatient() {

        String username = JOptionPane.showInputDialog(this, "Choose username:");

        if (username == null || username.trim().isEmpty()) {
            return;
        }

        String password = JOptionPane.showInputDialog(this, "Choose password:");

        if (password == null || password.trim().isEmpty()) {
            return;
        }

        boolean success = userService.createUser(
                username.trim(),
                password.trim(),
                "Patient");

        if (success) {
            JOptionPane.showMessageDialog(this, "Patient account created. You can now login.");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists.");
        }
    }
}
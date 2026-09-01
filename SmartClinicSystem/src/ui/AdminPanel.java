package ui;

import service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    private JTable table;
    private DefaultTableModel model;

    private JButton addButton;
    private JButton deleteButton;

    private UserService userService = new UserService();

    public AdminPanel() {

        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Admin User Management");
        UITheme.styleTitle(titleLabel);

        JPanel topPanel = UITheme.card();
        topPanel.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        UITheme.styleTextField(usernameField);
        UITheme.styleTextField(passwordField);

        roleBox = new JComboBox<>(new String[] {
                "Patient",
                "Doctor",
                "Receptionist"
        });

        roleBox.setPreferredSize(new Dimension(170, 30));

        addButton = new JButton("Add User");
        deleteButton = new JButton("Delete Selected User");

        UITheme.styleButton(addButton);
        UITheme.styleDangerButton(deleteButton);

        addFormRow(formPanel, gbc, 0, "Username:", usernameField);
        addFormRow(formPanel, gbc, 1, "Password:", passwordField);
        addFormRow(formPanel, gbc, 2, "Role:", roleBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        String[] columns = {
                "ID",
                "Username",
                "Role"
        };

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        UITheme.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> addUser());
        deleteButton.addActionListener(e -> deleteUser());

        loadUsers();
    }

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            Component field) {

        JLabel label = new JLabel(labelText);
        UITheme.styleLabel(label);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;

        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;

        panel.add(field, gbc);
    }

    private void loadUsers() {

        model.setRowCount(0);

        List<String[]> users =
                userService.getAllUsers();

        for (String[] row : users) {
            model.addRow(row);
        }
    }

    private void addUser() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(passwordField.getPassword()).trim();

        String role =
                (String) roleBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.");

            return;
        }

        boolean success =
                userService.createUser(
                        username,
                        password,
                        role);

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "User created successfully.");

            usernameField.setText("");
            passwordField.setText("");
            roleBox.setSelectedIndex(0);

            loadUsers();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Username already exists.");
        }
    }

    private void deleteUser() {

        int selectedRow =
                table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a user first.");

            return;
        }

        int userId =
                Integer.parseInt(
                        model.getValueAt(selectedRow, 0).toString());

        String username =
                model.getValueAt(selectedRow, 1).toString();

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete user: " + username + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            boolean success =
                    userService.deleteUser(userId);

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "User deleted successfully.");

                loadUsers();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Cannot delete admin user.");
            }
        }
    }
}
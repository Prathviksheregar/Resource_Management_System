
package company.ui;

import company.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private JButton registerButton;

    private final AuthService authService;

    public RegisterFrame() {

        authService = new AuthService();

        setTitle("Resource Management System - Registration");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 244, 248));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel(
                "Employee Registration",
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(18);

        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(18);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Confirm password
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Confirm Password:"), gbc);

        confirmPasswordField = new JPasswordField(18);

        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Register button
        registerButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> registerUser());

        // Back to login
        JButton backButton = new JButton("Back to Login");

        gbc.gridy = 5;
        panel.add(backButton, gbc);

        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        add(panel);
        setVisible(true);
    }

    private void registerUser() {

        String username = usernameField.getText().trim();

        String password =
                new String(passwordField.getPassword());

        String confirmPassword =
                new String(confirmPasswordField.getPassword());

        if (username.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields."
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match."
            );

            return;
        }

        if (password.length() < 8) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password must contain at least 8 characters."
            );

            return;
        }

        registerButton.setEnabled(false);

        try {

            boolean registered =
                    authService.registerEmployee(username, password);

            if (registered) {

                JOptionPane.showMessageDialog(
                        this,
                        "Registration successful! You can now log in."
                );

                dispose();
                new LoginFrame();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Username already exists or registration failed."
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();

        } finally {

            registerButton.setEnabled(true);
        }
    }
}

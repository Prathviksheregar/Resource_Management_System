
package company.ui;

import company.model.User;
import company.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import company.ui.RegisterFrame;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private final AuthService authService;

    public LoginFrame() {

        authService = new AuthService();

        setTitle("Resource Management System - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 244, 248));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel(
                "Resource Management System",
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(18);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 15));

        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(18);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));

        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.setBackground(new Color(35, 100, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(loginButton, gbc);

        loginButton.addActionListener(e -> loginUser());

        passwordField.addActionListener(e -> loginUser());

        JButton registerButton = new JButton("Create Account");

        registerButton.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });

        gbc.gridy = 4;
        mainPanel.add(registerButton, gbc);

        add(mainPanel);

        setVisible(true);
    }

    private void loginUser() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password."
            );
            return;
        }

        loginButton.setEnabled(false);

        try {

            User user = authService.login(username, password);

            if (user != null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Login successful! Welcome " + user.getUsername()
                );

                dispose();

                if ("ADMIN".equalsIgnoreCase(user.getRole())) {

                    new AdminDashboardFrame(user);

                } else if ("USER".equalsIgnoreCase(user.getRole())) {

                    new EmployeeDashboardFrame(user);

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Unknown user role. Please contact the administrator."
                    );

                    new LoginFrame();
                }

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );

                passwordField.setText("");
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();

        } finally {
            loginButton.setEnabled(true);
        }
    }
}

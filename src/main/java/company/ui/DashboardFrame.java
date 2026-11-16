
package company.ui;

import company.model.User;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final User loggedInUser;

    public DashboardFrame(User user) {

        this.loggedInUser = user;

        setTitle("Resource Management System - Dashboard");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 244, 248));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(35, 70, 120));
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        JLabel titleLabel = new JLabel("Resource Management Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // User information
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 244, 248));

        JLabel welcomeLabel = new JLabel(
                "Welcome, " + loggedInUser.getUsername() + "!"
        );

        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 25));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel roleLabel = new JLabel(
                "Your Role: " + loggedInUser.getRole()
        );

        roleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel(
                "You have successfully logged in."
        );

        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(roleLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(messageLabel);
        centerPanel.add(Box.createVerticalGlue());

        // Logout
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 15));

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 244, 248));
        bottomPanel.add(logoutButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setVisible(true);
    }
}

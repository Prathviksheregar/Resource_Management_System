
package company.ui;

import company.model.User;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {

    private final User loggedInUser;

    public AdminDashboardFrame(User user) {

        this.loggedInUser = user;

        setTitle("Resource Management System - Admin");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 244, 248));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(35, 70, 120));
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel titleLabel = new JLabel("Admin Dashboard");

        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel(
                "Logged in as: " + loggedInUser.getUsername()
        );

        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);

        // Dashboard content
        JPanel contentPanel = new JPanel(
                new GridLayout(0, 2, 20, 20)
        );

        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        );

        contentPanel.setBackground(new Color(240, 244, 248));

        // Dashboard buttons
        JButton addResourceButton =
                createButton("Add Resource");

        JButton viewResourcesButton =
                createButton("View Resources");

        JButton manageRequestsButton =
                createButton("Manage Requests");

        JButton reportsButton =
                createButton("View Reports");

        JButton historyButton =
                createButton("Resource History");

        // Add buttons to dashboard
        contentPanel.add(addResourceButton);
        contentPanel.add(viewResourcesButton);
        contentPanel.add(manageRequestsButton);
        contentPanel.add(reportsButton);
        contentPanel.add(historyButton);

        // Add Resource action
        addResourceButton.addActionListener(e ->
                new ResourceManagementFrame(loggedInUser)
        );

        // View Resources action
        viewResourcesButton.addActionListener(e ->
                new ResourceManagementFrame(loggedInUser)
        );

        // Manage Requests action
        manageRequestsButton.addActionListener(e ->
                new AdminRequestFrame(loggedInUser)
        );

        // View Reports action
        reportsButton.addActionListener(e ->
                new AdminReportsFrame(loggedInUser)
        );

        // Resource History action
        historyButton.addActionListener(e ->
                new ResourceHistoryFrame(loggedInUser)
        );

        // Footer
        JPanel footerPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        footerPanel.setBackground(new Color(240, 244, 248));

        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> logout());

        footerPanel.add(logoutButton);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setVisible(true);
    }

    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(35, 100, 180));
        button.setForeground(Color.WHITE);

        return button;
    }

    private void showComingSoon(String feature) {

        JOptionPane.showMessageDialog(
                this,
                feature + " will be implemented in the next development step.",
                "Module Not Available Yet",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }
}

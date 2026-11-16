
package company.ui;

import company.model.User;

import javax.swing.*;
import java.awt.*;

public class EmployeeDashboardFrame extends JFrame {

    private final User loggedInUser;

    public EmployeeDashboardFrame(User user) {

        this.loggedInUser = user;

        setTitle("Resource Management System - Employee");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 244, 248));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(35, 120, 90));
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel titleLabel =
                new JLabel("Employee Dashboard");

        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel(
                "Welcome: " + loggedInUser.getUsername()
        );

        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);

        // Dashboard content
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
        );
        contentPanel.setBackground(new Color(240, 244, 248));

        JButton availableResourcesButton =
                createButton("View Available Resources");

        JButton requestResourceButton =
                createButton("Request Resource");

        JButton myRequestsButton =
                createButton("My Requests");

        JButton profileButton =
                createButton("My Profile");

        contentPanel.add(availableResourcesButton);
        contentPanel.add(requestResourceButton);
        contentPanel.add(myRequestsButton);
        contentPanel.add(profileButton);

        // These modules will be implemented in upcoming steps.
        availableResourcesButton.addActionListener(e ->
                new EmployeeResourceFrame(loggedInUser)
        );

        requestResourceButton.addActionListener(e ->
                new EmployeeRequestFrame(loggedInUser)
        );

        myRequestsButton.addActionListener(e ->
                new MyRequestsFrame(loggedInUser)
        );

        profileButton.addActionListener(e ->
                showProfile()
        );

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(240, 244, 248));

        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> logout());

        footerPanel.add(logoutButton);

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
        button.setBackground(new Color(35, 120, 90));
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

    private void showProfile() {

        JOptionPane.showMessageDialog(
                this,
                "Username: " + loggedInUser.getUsername()
                        + "\nRole: " + loggedInUser.getRole(),
                "My Profile",
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

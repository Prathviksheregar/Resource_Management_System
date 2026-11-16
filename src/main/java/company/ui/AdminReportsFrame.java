
package company.ui;

import company.model.User;
import company.model.ReportSummary;
import company.model.CategoryReport;
import company.service.ReportService;
import company.service.ReportExportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminReportsFrame extends JFrame {

    private final User loggedInUser;

    private final ReportService reportService =
            new ReportService();

    private final ReportExportService exportService =
            new ReportExportService();

    private JLabel totalResourcesLabel;
    private JLabel totalQuantityLabel;
    private JLabel pendingLabel;
    private JLabel approvedLabel;
    private JLabel rejectedLabel;
    private JLabel inventoryValueLabel;

    private DefaultTableModel tableModel;

    public AdminReportsFrame(User user) {

        if (user == null ||
                !"ADMIN".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only administrators can view reports."
            );
        }

        this.loggedInUser = user;

        setTitle("Admin Reports");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createUI();
        loadReports();

        setVisible(true);
    }

    private void createUI() {

        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Resource Management Reports",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 24));

        add(heading, BorderLayout.NORTH);

        // Statistics panel
        JPanel statisticsPanel =
                new JPanel(new GridLayout(3, 2, 15, 15));

        statisticsPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        totalResourcesLabel = new JLabel("Total Resources: 0");
        totalQuantityLabel = new JLabel("Available Units: 0");
        pendingLabel = new JLabel("Pending Requests: 0");
        approvedLabel = new JLabel("Approved Requests: 0");
        rejectedLabel = new JLabel("Rejected Requests: 0");
        inventoryValueLabel = new JLabel("Inventory Value: 0.00");

        JLabel[] labels = {
                totalResourcesLabel,
                totalQuantityLabel,
                pendingLabel,
                approvedLabel,
                rejectedLabel,
                inventoryValueLabel
        };

        for (JLabel label : labels) {

            label.setFont(new Font("Arial", Font.BOLD, 16));

            label.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.GRAY),
                            BorderFactory.createEmptyBorder(
                                    15, 15, 15, 15
                            )
                    )
            );

            statisticsPanel.add(label);
        }

        // Category report table
        String[] columns = {
                "Category",
                "Resource Count",
                "Available Quantity",
                "Inventory Value"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(25);

        JScrollPane tableScrollPane =
                new JScrollPane(categoryTable);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        centerPanel.add(statisticsPanel, BorderLayout.NORTH);

        JLabel categoryHeading = new JLabel(
                "Category-wise Resource Report",
                SwingConstants.CENTER
        );

        categoryHeading.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        JPanel categoryPanel = new JPanel(new BorderLayout());

        categoryPanel.add(categoryHeading, BorderLayout.NORTH);
        categoryPanel.add(tableScrollPane, BorderLayout.CENTER);

        centerPanel.add(categoryPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom buttons
        JButton refreshButton =
                new JButton("Refresh Reports");

        JButton exportResourcesButton =
                new JButton("Export Resources CSV");

        JButton exportRequestsButton =
                new JButton("Export Requests CSV");

        JButton closeButton =
                new JButton("Close");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(refreshButton);
        buttonPanel.add(exportResourcesButton);
        buttonPanel.add(exportRequestsButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        refreshButton.addActionListener(e -> loadReports());

        exportResourcesButton.addActionListener(
                e -> chooseExportLocation(true)
        );

        exportRequestsButton.addActionListener(
                e -> chooseExportLocation(false)
        );

        closeButton.addActionListener(e -> dispose());
    }

    private void loadReports() {

        try {

            ReportSummary summary =
                    reportService.getReportSummary(loggedInUser);

            totalResourcesLabel.setText(
                    "Total Resources: " +
                            summary.getTotalResources()
            );

            totalQuantityLabel.setText(
                    "Available Units: " +
                            summary.getTotalAvailableQuantity()
            );

            pendingLabel.setText(
                    "Pending Requests: " +
                            summary.getPendingRequests()
            );

            approvedLabel.setText(
                    "Approved Requests: " +
                            summary.getApprovedRequests()
            );

            rejectedLabel.setText(
                    "Rejected Requests: " +
                            summary.getRejectedRequests()
            );

            inventoryValueLabel.setText(
                    String.format(
                            "Inventory Value: %.2f",
                            summary.getTotalInventoryValue()
                    )
            );

            // Load category reports
            tableModel.setRowCount(0);

            List<CategoryReport> reports =
                    reportService.getCategoryReports(loggedInUser);

            for (CategoryReport report : reports) {

                Object[] row = {
                        report.getCategory(),
                        report.getResourceCount(),
                        report.getAvailableQuantity(),
                        String.format(
                                "%.2f",
                                report.getInventoryValue()
                        )
                };

                tableModel.addRow(row);
            }

        } catch (SQLException | SecurityException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load reports: " + e.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Choose where to save the CSV file
    private void chooseExportLocation(boolean exportResources) {

        JFileChooser fileChooser = new JFileChooser();

        String defaultFileName;

        if (exportResources) {
            defaultFileName = "Resources_Report.csv";
        } else {
            defaultFileName = "Requests_Report.csv";
        }

        fileChooser.setSelectedFile(new File(defaultFileName));

        int result = fileChooser.showSaveDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();

        // Add .csv extension if missing
        if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {

            selectedFile = new File(
                    selectedFile.getAbsolutePath() + ".csv"
            );
        }

        // Confirm before overwriting
        if (selectedFile.exists()) {

            int overwriteChoice = JOptionPane.showConfirmDialog(
                    this,
                    "This file already exists. Do you want to replace it?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (overwriteChoice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {

            if (exportResources) {

                exportService.exportResources(
                        loggedInUser,
                        selectedFile
                );

            } else {

                exportService.exportRequests(
                        loggedInUser,
                        selectedFile
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Report exported successfully!\n\nSaved at:\n" +
                            selectedFile.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException | IOException |
                 SecurityException | IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not export report: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

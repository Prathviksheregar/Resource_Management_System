
package company.ui;

import company.model.ResourceHistory;
import company.model.User;
import company.service.ResourceHistoryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ResourceHistoryFrame extends JFrame {

    private final User loggedInUser;

    private final ResourceHistoryService historyService =
            new ResourceHistoryService();

    private DefaultTableModel tableModel;

    public ResourceHistoryFrame(User user) {

        if (user == null ||
                !"ADMIN".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only administrators can view history."
            );
        }

        this.loggedInUser = user;

        setTitle("Resource History");
        setSize(850, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createUI();
        loadHistory();

        setVisible(true);
    }

    private void createUI() {

        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Resource History",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 22));

        add(heading, BorderLayout.NORTH);

        String[] columns = {
                "History ID",
                "Resource",
                "Performed By",
                "Quantity",
                "Action",
                "Action Time"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadHistory());

        closeButton.addActionListener(e -> dispose());
    }

    private void loadHistory() {

        try {

            tableModel.setRowCount(0);

            List<ResourceHistory> historyList =
                    historyService.getAllHistory(loggedInUser);

            for (ResourceHistory history : historyList) {

                Object[] row = {
                        history.getHistoryId(),
                        history.getResourceName(),
                        history.getUsername(),
                        history.getQuantity(),
                        history.getAction(),
                        history.getActionTime()
                };

                tableModel.addRow(row);
            }

            if (historyList.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No resource history records found yet."
                );
            }

        } catch (SQLException | SecurityException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load history: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

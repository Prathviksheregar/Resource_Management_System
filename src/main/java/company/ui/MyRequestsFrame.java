
package company.ui;

import company.model.ResourceRequest;
import company.model.User;
import company.service.RequestService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MyRequestsFrame extends JFrame {

    private final User loggedInUser;

    private final RequestService requestService =
            new RequestService();

    private JTable requestTable;
    private DefaultTableModel tableModel;

    public MyRequestsFrame(User user) {

        if (user == null ||
                !"USER".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only employees can view their requests."
            );
        }

        this.loggedInUser = user;

        setTitle("My Resource Requests");
        setSize(850, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createUI();
        loadMyRequests();

        setVisible(true);
    }

    private void createUI() {

        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "My Resource Requests",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 22));

        add(heading, BorderLayout.NORTH);

        String[] columns = {
                "Request ID",
                "Resource Name",
                "Requested Quantity",
                "Status",
                "Request Time"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requestTable = new JTable(tableModel);

        requestTable.setRowHeight(25);

        JScrollPane scrollPane =
                new JScrollPane(requestTable);

        add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(
                e -> loadMyRequests()
        );

        closeButton.addActionListener(
                e -> dispose()
        );
    }

    private void loadMyRequests() {

        try {

            tableModel.setRowCount(0);

            List<ResourceRequest> requests =
                    requestService.getMyRequests(loggedInUser);

            for (ResourceRequest request : requests) {

                Object[] row = {
                        request.getRequestId(),
                        request.getResourceName(),
                        request.getRequestedQuantity(),
                        request.getStatus(),
                        request.getRequestTime()
                };

                tableModel.addRow(row);
            }

            if (requests.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "You have not submitted any resource requests yet."
                );
            }

        } catch (SQLException | SecurityException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load your requests: "
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

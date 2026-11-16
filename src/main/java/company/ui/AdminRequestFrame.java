
package company.ui;

import company.model.ResourceRequest;
import company.model.User;
import company.service.RequestService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AdminRequestFrame extends JFrame {

    private final User loggedInUser;

    private final RequestService requestService =
            new RequestService();

    private JTable requestTable;
    private DefaultTableModel tableModel;

    public AdminRequestFrame(User user) {

        if (user == null ||
                !"ADMIN".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only administrators can manage requests."
            );
        }

        this.loggedInUser = user;

        setTitle("Manage Resource Requests");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createUI();
        loadRequests();

        setVisible(true);
    }

    private void createUI() {

        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "Manage Employee Resource Requests",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 22));

        add(heading, BorderLayout.NORTH);

        String[] columns = {
                "Request ID",
                "Employee",
                "Resource",
                "Quantity",
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

        JButton approveButton =
                new JButton("Approve Request");

        JButton rejectButton =
                new JButton("Reject Request");

        JButton refreshButton =
                new JButton("Refresh");

        JButton closeButton =
                new JButton("Close");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        approveButton.addActionListener(
                e -> approveSelectedRequest()
        );

        rejectButton.addActionListener(
                e -> rejectSelectedRequest()
        );

        refreshButton.addActionListener(
                e -> loadRequests()
        );

        closeButton.addActionListener(
                e -> dispose()
        );
    }

    private void loadRequests() {

        try {

            tableModel.setRowCount(0);

            List<ResourceRequest> requests =
                    requestService.getAllRequests(loggedInUser);

            for (ResourceRequest request : requests) {

                Object[] row = {
                        request.getRequestId(),
                        request.getUsername(),
                        request.getResourceName(),
                        request.getRequestedQuantity(),
                        request.getStatus(),
                        request.getRequestTime()
                };

                tableModel.addRow(row);
            }

        } catch (SQLException | SecurityException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load requests: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Integer getSelectedRequestId() {

        int selectedRow = requestTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a request first."
            );

            return null;
        }

        return (Integer) tableModel.getValueAt(
                selectedRow, 0
        );
    }

    private void approveSelectedRequest() {

        Integer requestId = getSelectedRequestId();

        if (requestId == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to approve this request?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            requestService.approveRequest(
                    loggedInUser,
                    requestId
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Request approved successfully!\n"
                            + "Resource stock has been updated."
            );

            loadRequests();

        } catch (SQLException | SecurityException |
                 IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Approval Error",
                    JOptionPane.ERROR_MESSAGE
            );

            loadRequests();
        }
    }

    private void rejectSelectedRequest() {

        Integer requestId = getSelectedRequestId();

        if (requestId == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reject this request?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            requestService.rejectRequest(
                    loggedInUser,
                    requestId
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Request rejected successfully."
            );

            loadRequests();

        } catch (SQLException | SecurityException |
                 IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Rejection Error",
                    JOptionPane.ERROR_MESSAGE
            );

            loadRequests();
        }
    }
}

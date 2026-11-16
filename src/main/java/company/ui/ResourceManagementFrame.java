
package company.ui;

import company.model.Resource;
import company.model.User;
import company.service.ResourceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ResourceManagementFrame extends JFrame {

    private final User loggedInUser;
    private final ResourceService resourceService;

    private JTable resourceTable;
    private DefaultTableModel tableModel;

    public ResourceManagementFrame(User user) {

        if (user == null ||
                !"ADMIN".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only administrators can access resource management."
            );
        }

        loggedInUser = user;
        resourceService = new ResourceService();

        setTitle("Resource Management");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Heading
        JLabel heading = new JLabel(
                "Manage Company Resources",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 24));

        mainPanel.add(heading, BorderLayout.NORTH);

        // Table
        String[] columns = {
                "ID", "Resource Name", "Category",
                "Timeline", "Quantity", "Cost"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resourceTable = new JTable(tableModel);
        resourceTable.setRowHeight(25);
        resourceTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(resourceTable);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 10)
        );

        JButton addButton = new JButton("Add Resource");
        JButton updateButton = new JButton("Update Resource");
        JButton deleteButton = new JButton("Delete Resource");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addResource());

        updateButton.addActionListener(e -> updateResource());

        deleteButton.addActionListener(e -> deleteResource());

        refreshButton.addActionListener(e -> loadResources());

        add(mainPanel);

        loadResources();

        setVisible(true);
    }

    // READ: Load resources into table
    private void loadResources() {

        try {

            List<Resource> resources =
                    resourceService.getAllResources(loggedInUser);

            tableModel.setRowCount(0);

            for (Resource resource : resources) {

                tableModel.addRow(new Object[]{
                        resource.getResourceId(),
                        resource.getResourceName(),
                        resource.getCategory(),
                        resource.getTimeline(),
                        resource.getQuantity(),
                        resource.getCost()
                });
            }

        } catch (SQLException | SecurityException ex) {

            showError(ex);
        }
    }

    // CREATE: Add resource
    private void addResource() {

        Resource resource = showResourceDialog(null);

        if (resource == null) {
            return;
        }

        try {

            boolean added =
                    resourceService.addResource(
                            loggedInUser, resource
                    );

            if (added) {

                JOptionPane.showMessageDialog(
                        this,
                        "Resource added successfully!"
                );

                loadResources();
            }

        } catch (SQLException | IllegalArgumentException |
                 SecurityException ex) {

            showError(ex);
        }
    }

    // UPDATE: Edit selected resource
    private void updateResource() {

        int selectedRow =
                resourceTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a resource to update."
            );

            return;
        }

        int modelRow =
                resourceTable.convertRowIndexToModel(selectedRow);

        int id = (int) tableModel.getValueAt(modelRow, 0);

        String name =
                tableModel.getValueAt(modelRow, 1).toString();

        String category =
                tableModel.getValueAt(modelRow, 2).toString();

        String timeline =
                tableModel.getValueAt(modelRow, 3).toString();

        int quantity =
                Integer.parseInt(
                        tableModel.getValueAt(modelRow, 4).toString()
                );

        double cost =
                Double.parseDouble(
                        tableModel.getValueAt(modelRow, 5).toString()
                );

        Resource existing = new Resource(
                id, name, category, timeline, quantity, cost
        );

        Resource updated = showResourceDialog(existing);

        if (updated == null) {
            return;
        }

        try {

            boolean success =
                    resourceService.updateResource(
                            loggedInUser, updated
                    );

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Resource updated successfully!"
                );

                loadResources();
            }

        } catch (SQLException | IllegalArgumentException |
                 SecurityException ex) {

            showError(ex);
        }
    }

    // DELETE: Remove selected resource
    private void deleteResource() {

        int selectedRow =
                resourceTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a resource to delete."
            );

            return;
        }

        int modelRow =
                resourceTable.convertRowIndexToModel(selectedRow);

        int id =
                (int) tableModel.getValueAt(modelRow, 0);

        String name =
                tableModel.getValueAt(modelRow, 1).toString();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete resource: " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            boolean deleted =
                    resourceService.deleteResource(
                            loggedInUser, id
                    );

            if (deleted) {

                JOptionPane.showMessageDialog(
                        this,
                        "Resource deleted successfully!"
                );

                loadResources();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Resource was not found."
                );
            }

        } catch (SQLException | SecurityException ex) {

            showError(ex);
        }
    }

    // Input dialog used for both adding and updating
    private Resource showResourceDialog(Resource existing) {

        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField timelineField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField costField = new JTextField();

        if (existing != null) {

            nameField.setText(existing.getResourceName());
            categoryField.setText(existing.getCategory());
            timelineField.setText(existing.getTimeline());
            quantityField.setText(
                    String.valueOf(existing.getQuantity())
            );
            costField.setText(
                    String.valueOf(existing.getCost())
            );
        }

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));

        panel.add(new JLabel("Resource Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Category:"));
        panel.add(categoryField);

        panel.add(new JLabel("Timeline:"));
        panel.add(timelineField);

        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        panel.add(new JLabel("Cost:"));
        panel.add(costField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                existing == null ? "Add Resource" : "Update Resource",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {

            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String timeline = timelineField.getText().trim();

            if (name.isEmpty() || category.isEmpty()) {

                throw new IllegalArgumentException(
                        "Resource name and category are required."
                );
            }

            int quantity = Integer.parseInt(
                    quantityField.getText().trim()
            );

            double cost = Double.parseDouble(
                    costField.getText().trim()
            );

            if (quantity < 0 || cost < 0 ||
                    !Double.isFinite(cost)) {

                throw new IllegalArgumentException(
                        "Quantity and cost must be valid non-negative numbers."
                );
            }

            int id = existing == null
                    ? 0
                    : existing.getResourceId();

            return new Resource(
                    id,
                    name,
                    category,
                    timeline,
                    quantity,
                    cost
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Quantity must be a whole number and cost must be numeric.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

            return null;

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

            return null;
        }
    }

    private void showError(Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Resource Management Error",
                JOptionPane.ERROR_MESSAGE
        );

        ex.printStackTrace();
    }
}

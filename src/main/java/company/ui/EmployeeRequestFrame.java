
package company.ui;

import company.model.Resource;
import company.model.User;
import company.service.RequestService;
import company.service.ResourceService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EmployeeRequestFrame extends JFrame {

    private final User loggedInUser;

    private final ResourceService resourceService =
            new ResourceService();

    private final RequestService requestService =
            new RequestService();

    private JComboBox<Resource> resourceComboBox;
    private JTextField quantityField;

    public EmployeeRequestFrame(User user) {

        if (user == null ||
                !"USER".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only employees can access this page."
            );
        }

        this.loggedInUser = user;

        setTitle("Request Resource");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createUI();
        loadResources();

        setVisible(true);
    }

    private void createUI() {

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 15));
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        JLabel titleLabel = new JLabel("Request a Resource");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        resourceComboBox = new JComboBox<>();

        resourceComboBox.setRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {
                        super.getListCellRendererComponent(
                                list, value, index,
                                isSelected, cellHasFocus
                        );

                        if (value instanceof Resource) {
                            Resource resource = (Resource) value;

                            setText(
                                    resource.getResourceName()
                                            + " (Available: "
                                            + resource.getQuantity() + ")"
                            );
                        }

                        return this;
                    }
                }
        );

        quantityField = new JTextField();

        JButton submitButton = new JButton("Submit Request");
        JButton closeButton = new JButton("Close");

        panel.add(titleLabel);
        panel.add(new JLabel(""));

        panel.add(new JLabel("Select Resource:"));
        panel.add(resourceComboBox);

        panel.add(new JLabel("Requested Quantity:"));
        panel.add(quantityField);

        panel.add(submitButton);
        panel.add(closeButton);

        add(panel);

        submitButton.addActionListener(e -> submitRequest());

        closeButton.addActionListener(e -> dispose());
    }

    private void loadResources() {

        try {
            List<Resource> resources =
                    resourceService.getAvailableResources(loggedInUser);

            resourceComboBox.removeAllItems();

            for (Resource resource : resources) {
                resourceComboBox.addItem(resource);
            }

            if (resources.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No resources are currently available."
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load resources: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void submitRequest() {

        Resource selectedResource =
                (Resource) resourceComboBox.getSelectedItem();

        if (selectedResource == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a resource."
            );
            return;
        }

        int requestedQuantity;

        try {
            requestedQuantity =
                    Integer.parseInt(quantityField.getText().trim());

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid whole number."
            );
            return;
        }

        try {

            requestService.submitRequest(
                    loggedInUser,
                    selectedResource.getResourceId(),
                    requestedQuantity
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Your resource request was submitted successfully!\n"
                            + "Status: PENDING"
            );

            quantityField.setText("");

        } catch (SQLException | IllegalArgumentException |
                 SecurityException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Request Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

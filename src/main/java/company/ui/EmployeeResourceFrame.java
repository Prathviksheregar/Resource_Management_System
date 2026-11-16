
package company.ui;

import company.model.Resource;
import company.model.User;
import company.service.ResourceService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class EmployeeResourceFrame extends JFrame {

    private final User loggedInUser;
    private final ResourceService resourceService;

    private JTable resourceTable;
    private DefaultTableModel tableModel;

    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField searchField;
    private JComboBox<String> categoryFilter;

    public EmployeeResourceFrame(User user) {

        if (user == null ||
                !"USER".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only employees can access this screen."
            );
        }

        loggedInUser = user;
        resourceService = new ResourceService();

        setTitle("Available Company Resources");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // Heading
        JLabel heading = new JLabel(
                "Available Resources",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 24));

        mainPanel.add(heading, BorderLayout.NORTH);

        // Search and filter panel
        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        searchField = new JTextField(18);

        categoryFilter = new JComboBox<>();

        JButton refreshButton = new JButton("Refresh");

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);

        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);

        filterPanel.add(refreshButton);

        // Table
        String[] columns = {
                "Resource ID",
                "Resource Name",
                "Category",
                "Timeline",
                "Available Quantity"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resourceTable = new JTable(tableModel);

        resourceTable.setRowHeight(28);

        resourceTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        sorter = new TableRowSorter<>(tableModel);

        resourceTable.setRowSorter(sorter);

        JScrollPane scrollPane =
                new JScrollPane(resourceTable);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Information label
        JLabel infoLabel = new JLabel(
                "Only resources with available quantity are displayed."
        );

        infoLabel.setForeground(Color.GRAY);

        centerPanel.add(infoLabel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        JButton closeButton = new JButton("Close");

        bottomPanel.add(closeButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Search listener
        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        applyFilters();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        applyFilters();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        applyFilters();
                    }
                }
        );

        // Category listener
        categoryFilter.addActionListener(
                e -> applyFilters()
        );

        // Refresh listener
        refreshButton.addActionListener(
                e -> loadResources()
        );

        // Close listener
        closeButton.addActionListener(e -> dispose());

        add(mainPanel);

        loadResources();

        setVisible(true);
    }

    // Load resources from database
    private void loadResources() {

        try {

            List<Resource> resources =
                    resourceService.getAvailableResources(
                            loggedInUser
                    );

            tableModel.setRowCount(0);

            Set<String> categories = new TreeSet<>();

            for (Resource resource : resources) {

                tableModel.addRow(new Object[]{
                        resource.getResourceId(),
                        resource.getResourceName(),
                        resource.getCategory(),
                        resource.getTimeline(),
                        resource.getQuantity()
                });

                categories.add(resource.getCategory());
            }

            // Update category dropdown
            categoryFilter.removeAllItems();

            categoryFilter.addItem("All Categories");

            for (String category : categories) {
                categoryFilter.addItem(category);
            }

            applyFilters();

        } catch (SQLException | SecurityException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load resources: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // Apply search and category filters
    private void applyFilters() {

        String searchText =
                searchField.getText().trim();

        String selectedCategory =
                (String) categoryFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Integer> searchFilter = null;
        RowFilter<DefaultTableModel, Integer> categoryRowFilter = null;

        if (!searchText.isEmpty()) {

            searchFilter = RowFilter.regexFilter(
                    "(?i)" + Pattern.quote(searchText),
                    1, 2
            );
        }

        if (selectedCategory != null &&
                !selectedCategory.equals("All Categories")) {

            categoryRowFilter = RowFilter.regexFilter(
                    "^" + Pattern.quote(selectedCategory) + "$",
                    2
            );
        }

        if (searchFilter != null && categoryRowFilter != null) {

            sorter.setRowFilter(
                    RowFilter.andFilter(
                            java.util.List.of(
                                    searchFilter,
                                    categoryRowFilter
                            )
                    )
            );

        } else if (searchFilter != null) {

            sorter.setRowFilter(searchFilter);

        } else if (categoryRowFilter != null) {

            sorter.setRowFilter(categoryRowFilter);

        } else {

            sorter.setRowFilter(null);
        }
    }
}

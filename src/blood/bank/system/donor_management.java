package blood.bank.system;

import java.util.List;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import CacheManager.Connect;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class donor_management extends JFrame {
    private JTable donorTable;
    private JButton addButton;
    private JButton deleteButton;
    private JTextField searchField;
    private JButton searchButton;
    private JButton backButton;
    private JButton modeButton; // New button for mode switching
    private Connection connection;
    private boolean darkMode = false; // Track current mode

    // Add a flag column index
    private static final int FLAG_COLUMN_INDEX = 9;
    private static final int BIN_COLUMN_INDEX = 10;

    public donor_management() {
        setTitle("Donor Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);

        // Connect to the database
        connectToDatabase();

        // Create the table
        donorTable = new JTable();
        donorTable.setModel(new DefaultTableModel(
                new Object[][] {},
                new String[] { "DonorID", "Cnic_D", "BloodGroup", "RhFactor", "Name", "LastDonation", "Contact",
                        "Address", "Age", "Flag" })); // Add a flag column

        donorTable.setFillsViewportHeight(true);
        donorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        donorTable.setCellSelectionEnabled(true);
        // donorTable.setBackground(Color.decode("#FFCCCC")); // Light red color
        // Add the bin icon column to the table model
        DefaultTableModel model = (DefaultTableModel) donorTable.getModel();
        model.addColumn("Delete"); // Add column header

        // Add the bin icon renderer and editor to the BIN_COLUMN_INDEX
        int binIconSize = 15; // Adjust the size as needed
        donorTable.getColumnModel().getColumn(BIN_COLUMN_INDEX).setCellRenderer(new BinIconRenderer(binIconSize));
        donorTable.getColumnModel().getColumn(BIN_COLUMN_INDEX).setCellEditor(new BinIconEditor(new JCheckBox()));

        JTableHeader tableHeader = donorTable.getTableHeader();

        // Allow editing
        donorTable.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
        // Set column widths to zero to hide them
        donorTable.getColumnModel().getColumn(FLAG_COLUMN_INDEX).setMinWidth(0);
        donorTable.getColumnModel().getColumn(FLAG_COLUMN_INDEX).setMaxWidth(0);
        donorTable.getColumnModel().getColumn(FLAG_COLUMN_INDEX).setWidth(0);
        // Increase width of specific columns
        TableColumnModel columnModel = donorTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(60); // DonorID column
        columnModel.getColumn(1).setPreferredWidth(100); // Cnic_D column
        columnModel.getColumn(2).setPreferredWidth(80); // BloodGroup column
        columnModel.getColumn(3).setPreferredWidth(70); // RhFactor column
        columnModel.getColumn(4).setPreferredWidth(100); // Name column
        columnModel.getColumn(5).setPreferredWidth(100); // last donation column
        columnModel.getColumn(6).setPreferredWidth(105); // contact column
        columnModel.getColumn(7).setPreferredWidth(105); // address column
        columnModel.getColumn(8).setPreferredWidth(80); // age column

        // Increase font size and height of table cells
        Font cellFont = new Font("DejaVu Sans", Font.PLAIN, 14); // Adjust the font size as needed
        donorTable.setFont(cellFont);
        // Adjust the width of the Delete column and make its header invisible
        donorTable.getColumnModel().getColumn(BIN_COLUMN_INDEX).setMaxWidth(55); // Set maximum width to 50
        donorTable.getColumnModel().getColumn(BIN_COLUMN_INDEX).setMinWidth(55); // Set minimum width to 50
        donorTable.getColumnModel().getColumn(BIN_COLUMN_INDEX).setWidth(55); // Set preferred width to 50
        donorTable.getTableHeader().getColumnModel().getColumn(BIN_COLUMN_INDEX).setMaxWidth(0); // Hide header
        donorTable.getTableHeader().getColumnModel().getColumn(BIN_COLUMN_INDEX).setMinWidth(0); // Hide header
        donorTable.getTableHeader().getColumnModel().getColumn(BIN_COLUMN_INDEX).setWidth(0); // Hide header
        donorTable.getTableHeader().getColumnModel().getColumn(BIN_COLUMN_INDEX).setHeaderValue("");
        // Increase row height
        donorTable.setRowHeight(25); // Adjust the row height as needed
        // Set table border with curved edges and padding

        // Initialize UI components
        initializeComponents();

        // Fetch data from the database and populate the table
        fetchData();

        // Display the frame
        setVisible(true);
    }

    private void initializeComponents() {
        // Create mode button
        modeButton = new JButton("Switch Mode");
        modeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchMode();
            }
        });

        // Create buttons and search field
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        backButton = new JButton("Home");
        // Add a combobox for search options
        String[] searchOptions = { "Search by Name", "Search by Blood Group" };
        JComboBox<String> searchOptionsComboBox = new JComboBox<>(searchOptions);

        // Add action listeners
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                // Code to navigate to the home screen
                dispose(); // Close the current frame
                new blood.bank.system.Home().setVisible(true); // Open the Home screen
            }
        });
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Add an empty row to the table with flag set to true
                DefaultTableModel model = (DefaultTableModel) donorTable.getModel();
                model.addRow(new Object[] { null, null, null, null, null, null, null, null, null, true });
            }
        });

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText().trim().toLowerCase();
                int searchColumn = searchOptionsComboBox.getSelectedIndex() == 0 ? 4 : 2; // Search by name or blood
                                                                                          // group

                if (!searchText.isEmpty()) {
                    DefaultTableModel model = (DefaultTableModel) donorTable.getModel();
                    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                    donorTable.setRowSorter(sorter);

                    RowFilter<DefaultTableModel, Object> filter;
                    if (searchColumn == 2) {
                        // If searching by blood group, extract the blood group and Rh factor
                        String bloodGroup = searchText.substring(0, searchText.length() - 1).toUpperCase();
                        String rhFactor = searchText.substring(searchText.length() - 1).toUpperCase();

                        // Validate Rh factor
                        if (!rhFactor.equals("+") && !rhFactor.equals("-")) {
                            JOptionPane.showMessageDialog(null, "Please enter a valid Rh factor ('+' or '-').",
                                    "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        String bloodGroupRegex = "(?i)^" + Pattern.quote(bloodGroup) + "$";
                        String rhFactorRegex = "(?i)^" + Pattern.quote(rhFactor) + "$";

                        RowFilter<DefaultTableModel, Object> bloodGroupFilter = RowFilter.regexFilter(bloodGroupRegex,
                                searchColumn);
                        RowFilter<DefaultTableModel, Object> rhFactorFilter = RowFilter.regexFilter(rhFactorRegex,
                                searchColumn + 1);

                        List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
                        filters.add(bloodGroupFilter);
                        filters.add(rhFactorFilter);

                        filter = RowFilter.andFilter(filters);
                        sorter.setRowFilter(filter);

                        if (sorter.getViewRowCount() == 0) {
                            JOptionPane.showMessageDialog(null,
                                    "No records found for blood group " + bloodGroup + rhFactor, "No Records Found",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        // If searching by name, match exact name or first name
                        String searchString = "(?i)" + Pattern.quote(searchText); // Match any part of the name
                        filter = new RowFilter<DefaultTableModel, Object>() {
                            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                                // Check if either the full name or just the first name matches
                                String fullName = entry.getStringValue(4).toLowerCase(); // Assuming full name is in
                                                                                         // column 4
                                String firstName = fullName.split("\\s+")[0]; // Extracting the first name

                                return fullName.contains(searchText) || firstName.contains(searchText);
                            }
                        };
                        sorter.setRowFilter(filter);

                        if (sorter.getViewRowCount() == 0) {
                            JOptionPane.showMessageDialog(null, "No records found for name " + searchText,
                                    "No Records Found", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } else {
                    donorTable.setRowSorter(null);
                }
            }
        });
        // Add key listener for saving changes
        donorTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                try {
                    if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                        // Handle Ctrl+S, save changes
                        saveChanges();
                    }
                } catch (ClassCastException ex) {
                    // Show alert message within AWT EventQueue thread
                    EventQueue.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null,
                                "Error occurred while saving changes. Please review your changes.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
            }
        });

        // Add components to the frame
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.add(backButton);
        buttonPanel.add(addButton);
        buttonPanel.add(new JLabel("Search:"));
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);
        buttonPanel.add(searchOptionsComboBox); // Add search options combobox
        buttonPanel.add(searchButton);
        buttonPanel.add(modeButton); // Add mode button

        // Create rounded border
        Border roundedBorder = new LineBorder(Color.BLACK); // You can adjust the color as needed
        int borderRadius = 10; // Adjust the radius as needed
        Border emptyBorder = new EmptyBorder(borderRadius, borderRadius, borderRadius, borderRadius);
        Border compoundBorder = new CompoundBorder(roundedBorder, emptyBorder);

        // Set rounded border to the table
        donorTable.setBorder(compoundBorder);

        buttonPanel.setBackground(darkMode ? Color.decode("#333333") : Color.WHITE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(donorTable), BorderLayout.CENTER);
        contentPanel.setBackground(darkMode ? Color.decode("#333333") : Color.WHITE);
        getContentPane().add(contentPanel);
        // Set the background color of the main content pane to dark red
        getContentPane().setBackground(darkMode ? Color.decode("#333333") : Color.WHITE);

        // Set table and header color based on mode
        setTableColor(darkMode);
    }

    private void switchMode() {
        darkMode = !darkMode;
        getContentPane().setBackground(darkMode ? Color.decode("#333333") : Color.WHITE);
        JPanel contentPanel = (JPanel) getContentPane().getComponent(0);
        contentPanel.setBackground(darkMode ? Color.decode("#333333") : Color.WHITE);
        JPanel buttonPanel = (JPanel) contentPanel.getComponent(0);
        buttonPanel.setBackground(darkMode ? Color.decode("#333333") : Color.WHITE);
        setTableColor(darkMode);
    }

    private void setTableColor(boolean isDarkMode) {
        Color bgColor = isDarkMode ? Color.decode("#333333") : Color.WHITE;
        donorTable.setBackground(bgColor);
        donorTable.getTableHeader().setBackground(bgColor);
        donorTable.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        donorTable.getTableHeader().setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
    }

    private void connectToDatabase() {
        try {
            Connect connector = new Connect();
            connection = connector.getConnection(); // Using getConnector() function
            System.out.println("Connected to the database");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchData() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Donor");

            // Populate the DefaultTableModel with data from the ResultSet
            DefaultTableModel model = (DefaultTableModel) donorTable.getModel();
            while (resultSet.next()) {
                Object[] row = new Object[10]; // Adjusted for the added Flag column
                row[0] = resultSet.getInt("DonorID");
                row[1] = resultSet.getLong("Cnic_D");
                row[2] = resultSet.getString("BloodGroup");
                row[3] = resultSet.getString("RhFactor");
                row[4] = resultSet.getString("Name");
                row[5] = resultSet.getString("LastDonation"); // Retrieve as String
                row[6] = resultSet.getString("Contact");
                row[7] = resultSet.getString("Address");
                row[8] = resultSet.getInt("Age");
                row[9] = false; // Flag set to false for existing records
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isCNICUnique(String cnic, int currentDonorID) {
        try {
            // Create the SQL query to check if the CNIC exists in the database
            String query = "SELECT COUNT(*) FROM Donor WHERE Cnic_D = ? AND DonorID != ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, cnic);
            statement.setInt(2, currentDonorID); // Exclude the current donor ID if editing
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // Return true if count is 0 (unique), false otherwise
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // In case of an error, return false
    }

    private boolean isDonorIDUnique(int donorID) {
        try {
            // Create the SQL query to check if the Donor ID exists in the database
            String query = "SELECT COUNT(*) FROM Donor WHERE DonorID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, donorID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // Return true if count is 0 (unique), false otherwise
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // In case of an error, return false
    }

    private void saveChanges() {
        DefaultTableModel model = (DefaultTableModel) donorTable.getModel();
        int rowCount = model.getRowCount();

        try {
            // Start a transaction
            connection.setAutoCommit(false);

            for (int i = 0; i < rowCount; i++) {

                int donorID;
                // Check the flag column to determine if the row is new
                boolean isNewRow = (Boolean) model.getValueAt(i, FLAG_COLUMN_INDEX);

                if (isNewRow) {

                    // Insert new row logic here
                    String insertQuery = "INSERT INTO Donor (DonorID, Cnic_D, BloodGroup, RhFactor, Name, LastDonation, Contact, Address, Age) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery,
                            Statement.RETURN_GENERATED_KEYS);
                    insertStatement.setInt(1, Integer.parseInt((String) model.getValueAt(i, 0))); // Convert to Integer
                    insertStatement.setLong(2, Long.parseLong((String) model.getValueAt(i, 1)));
                    insertStatement.setString(3, (String) model.getValueAt(i, 2));
                    insertStatement.setString(4, (String) model.getValueAt(i, 3));
                    insertStatement.setString(5, (String) model.getValueAt(i, 4));
                    insertStatement.setString(6, (String) model.getValueAt(i, 5)); // Set as String
                    insertStatement.setString(7, (String) model.getValueAt(i, 6));
                    insertStatement.setString(8, (String) model.getValueAt(i, 7));
                    insertStatement.setInt(9, Integer.parseInt((String) model.getValueAt(i, 8)));
                    insertStatement.executeUpdate();

                    donorID = Integer.parseInt((String) model.getValueAt(i, 0));
                    model.setValueAt(donorID, i, 0); // Update the DonorID in the table model

                    // Insert into BloodInventory table
                    String bloodGroup = (String) model.getValueAt(i, 2);
                    String rhFactor = (String) model.getValueAt(i, 3);
                    Date expiration = calculateExpirationDate();
                    String inventoryInsertQuery = "INSERT INTO BloodInventory (BloodGroup, RhFactor, Expiration, DonorID) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement inventoryInsertStatement = connection
                            .prepareStatement(inventoryInsertQuery)) {
                        inventoryInsertStatement.setString(1, bloodGroup);
                        inventoryInsertStatement.setString(2, rhFactor);
                        inventoryInsertStatement.setDate(3, expiration);
                        inventoryInsertStatement.setInt(4, donorID);
                        inventoryInsertStatement.executeUpdate();
                    } catch (SQLException ex) {
                        System.err.println("Error inserting into BloodInventory table: " + ex.getMessage());
                        ex.printStackTrace();
                    }

                    // Set the flag to false for existing rows
                    model.setValueAt(false, i, FLAG_COLUMN_INDEX);
                } else {

                    // Update existing row logic here
                    donorID = (Integer) model.getValueAt(i, 0);
                    long cnic = Long.parseLong(model.getValueAt(i, 1).toString());

                    // long cnic = (Long) model.getValueAt(i, 1);
                    String bloodGroup = (String) model.getValueAt(i, 2);
                    String rhFactor = (String) model.getValueAt(i, 3);
                    String name = (String) model.getValueAt(i, 4);
                    String lastDonation = (String) model.getValueAt(i, 5); // Retrieve as String
                    String contact = (String) model.getValueAt(i, 6);
                    String address = (String) model.getValueAt(i, 7);
                    int age = (Integer) model.getValueAt(i, 8); // Convert to Integer

                    // Update the corresponding record in the database
                    String updateQuery = "UPDATE Donor SET Cnic_D=?, BloodGroup=?, RhFactor=?, Name=?, LastDonation=?, Contact=?, Address=?, Age=? WHERE DonorID=?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setLong(1, cnic);
                    updateStatement.setString(2, bloodGroup);
                    updateStatement.setString(3, rhFactor);
                    updateStatement.setString(4, name);
                    updateStatement.setString(5, lastDonation);
                    updateStatement.setString(6, contact);
                    updateStatement.setString(7, address);
                    updateStatement.setInt(8, age);
                    updateStatement.setInt(9, donorID);
                    updateStatement.executeUpdate();
                }
            }

            // Commit the transaction
            connection.commit();
            System.out.println("Changes saved successfully.");
        } catch (SQLException e) {
            try {
                // Rollback the transaction if an exception occurs
                connection.rollback();
                System.err.println("Transaction rolled back.");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                // Reset auto-commit mode
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Date calculateExpirationDate() {
        // Calculate expiration date as two months from the current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        return new Date(calendar.getTimeInMillis());
    }

    // Implement custom cell renderer for the bin icon
    class BinIconRenderer extends JLabel implements TableCellRenderer {
        private final int iconSize; // Size of the icon

        public BinIconRenderer(int iconSize) {
            this.iconSize = iconSize;
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            // Load the bin icon and set its size
            ImageIcon binIcon = new ImageIcon(getClass().getResource("/icon/bin.png"));
            Image scaledBinIcon = binIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(scaledBinIcon));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            return this;
        }
    }

    // Implement custom cell editor for the bin icon
    class BinIconEditor extends DefaultCellEditor {
        public BinIconEditor(JCheckBox checkBox) {
            super(checkBox);
            checkBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped(); // Stop cell editing when the checkbox is clicked
                    // Handle deletion functionality here
                    int selectedRow = donorTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // Get the donor ID from the selected row
                        int donorID = (int) donorTable.getValueAt(selectedRow, 0);
                        // Implement logic to delete the row and corresponding entries in the database
                        deleteDonor(donorID);
                    }
                }
            });
        }

        // Override getTableCellEditorComponent to return the checkbox
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            return editorComponent;
        }

        // Method to delete donor and corresponding entries in the database
        private void deleteDonor(int donorID) {
            try {
                // Delete from Donor table
                String deleteDonorQuery = "DELETE FROM Donor WHERE DonorID = ?";
                PreparedStatement deleteDonorStatement = connection.prepareStatement(deleteDonorQuery);
                deleteDonorStatement.setInt(1, donorID);
                deleteDonorStatement.executeUpdate();

                // Delete from BloodInventory table
                String deleteBloodInventoryQuery = "DELETE FROM BloodInventory WHERE DonorID = ?";
                PreparedStatement deleteBloodInventoryStatement = connection
                        .prepareStatement(deleteBloodInventoryQuery);
                deleteBloodInventoryStatement.setInt(1, donorID);
                deleteBloodInventoryStatement.executeUpdate();

                // Remove row from the table
                DefaultTableModel model = (DefaultTableModel) donorTable.getModel();
                model.removeRow(donorTable.getSelectedRow());
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Handle exception
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(donor_management::new);
    }
}

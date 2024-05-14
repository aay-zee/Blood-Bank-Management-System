package blood.bank.system;

import java.util.List;
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
import blood.bank.system.donor_management.BinIconEditor;
import blood.bank.system.donor_management.BinIconRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class recipient_management extends JFrame {
    private JTable recipientTable;
    private JButton addButton;
    private JButton deleteButton;
    private JTextField searchField;
    private JButton searchButton;
    private JButton modeButton; // New button for mode switching
    private Connection connection;
    private boolean darkMode = false; // Track current mode

    // Add a flag column index
    private static final int FLAG_COLUMN_INDEX = 8;
    private static final int BIN_COLUMN_INDEX = 9;

    public recipient_management() {
        setTitle("Recipient Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Connect to the database
        connectToDatabase();

        // Create the table
        recipientTable = new JTable();
        recipientTable.setModel(new DefaultTableModel(
                new Object[][] {},
                new String[] { "RecipientID", "Cnic_R", "Name", "Contact", "Address", "BloodGroup", "RhFactor",
                        "PriorityLevel", "Flag" })); // Add a flag column
                        recipientTable.setFillsViewportHeight(true);
                        recipientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        recipientTable.setCellSelectionEnabled(true);
                        recipientTable.setCellSelectionEnabled(true);
        // donorTable.setBackground(Color.decode("#FFCCCC")); // Light red color
        // Add the bin icon column to the table model
        DefaultTableModel model = (DefaultTableModel) recipientTable.getModel();
        model.addColumn("Delete"); // Add column header

        // Add the bin icon renderer and editor to the BIN_COLUMN_INDEX
        int binIconSize = 15; // Adjust the size as needed
        recipientTable.getColumnModel().getColumn(BIN_COLUMN_INDEX).setCellRenderer(new BinIconRenderer(binIconSize));
        recipientTable.getColumnModel().getColumn(BIN_COLUMN_INDEX).setCellEditor(new BinIconEditor(new JCheckBox()));
        JTableHeader tableHeader = recipientTable.getTableHeader();

        // Allow editing
        recipientTable.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
        // Set column widths to zero to hide them
        recipientTable.getColumnModel().getColumn(FLAG_COLUMN_INDEX).setMinWidth(0);
        recipientTable.getColumnModel().getColumn(FLAG_COLUMN_INDEX).setMaxWidth(0);
        recipientTable.getColumnModel().getColumn(FLAG_COLUMN_INDEX).setWidth(0);
        // Increase width of specific columns
        TableColumnModel columnModel = recipientTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(60); // ID column
        columnModel.getColumn(1).setPreferredWidth(100); // Cnic_R column
        columnModel.getColumn(5).setPreferredWidth(80); // BloodGroup column
        columnModel.getColumn(6).setPreferredWidth(70); // RhFactor column
        columnModel.getColumn(3).setPreferredWidth(100); // Name column
        columnModel.getColumn(4).setPreferredWidth(105); // contact column
        columnModel.getColumn(5).setPreferredWidth(105); // address column
        columnModel.getColumn(6).setPreferredWidth(70); // PL column

        // Increase font size and height of table cells
        Font cellFont = new Font("DejaVu Sans", Font.PLAIN, 14); // Adjust the font size as needed
        recipientTable.setFont(cellFont);

        // Increase row height
        recipientTable.setRowHeight(25); // Adjust the row height as needed
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
        // Add a combobox for search options
        String[] searchOptions = { "Search by Name", "Search by Blood Group" };
        JComboBox<String> searchOptionsComboBox = new JComboBox<>(searchOptions);

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Add an empty row to the table with flag set to true
                DefaultTableModel model = (DefaultTableModel) recipientTable.getModel();
                model.addRow(new Object[] { null, null, null, null, null, null, null, null, true });
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Implement deleting donor functionality
            }
        });
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText().trim().toLowerCase();
                int searchColumn = searchOptionsComboBox.getSelectedIndex() == 0 ? 2 : 5; // Search by name or blood
                                                                                          // group
                if (!searchText.isEmpty()) {
                    DefaultTableModel model = (DefaultTableModel) recipientTable.getModel();
                    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                    recipientTable.setRowSorter(sorter);

                    RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter("(?i)" + searchText,
                            searchColumn); // Use case-insensitive search
                    sorter.setRowFilter(filter);
                } else {
                    recipientTable.setRowSorter(null);
                }
            }
        });
        // Add key listener for saving changes
        recipientTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    // Handle Ctrl+S, save changes
                    saveChanges();
                }
            }
        });

        // Add components to the frame
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.add(addButton);
        // buttonPanel.add(deleteButton);
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
        recipientTable.setBorder(compoundBorder);

        buttonPanel.setBackground(darkMode ? Color.decode("#333333") : Color.WHITE); // Set background color of button
                                                                                     // panel

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(recipientTable), BorderLayout.CENTER);
        contentPanel.setBackground(darkMode ? Color.decode("#333333") : Color.WHITE);
        getContentPane().add(contentPanel);
        // Set the background color of the main content pane to dark red
        getContentPane().setBackground(darkMode ? Color.decode("#333333") : Color.WHITE);

        // Set table and header color based on mode
        setTableColor(darkMode);
    }

    private void switchMode() {
        darkMode = !darkMode;
        getContentPane().setBackground(darkMode ? Color.decode("#333333") : Color.WHITE); // Set background color of
                                                                                          // frame
        JPanel contentPanel = (JPanel) getContentPane().getComponent(0);
        contentPanel.setBackground(darkMode ? Color.decode("#333333") : Color.WHITE); // Set background color of content
                                                                                      // panel
        JPanel buttonPanel = (JPanel) contentPanel.getComponent(0);
        buttonPanel.setBackground(darkMode ? Color.decode("#333333") : Color.WHITE); // Set background color of button
                                                                                     // panel
        setTableColor(darkMode);
    }

    private void setTableColor(boolean isDarkMode) {
        Color bgColor = isDarkMode ? Color.decode("#333333") : Color.WHITE;
        recipientTable.setBackground(bgColor);
        recipientTable.getTableHeader().setBackground(bgColor);
        recipientTable.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        recipientTable.getTableHeader().setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
    }

    private void connectToDatabase() {
        try {
            Connect connector = new Connect();
            connection = connector.connection;
            System.out.println("Connected to the database");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    int selectedRow = recipientTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // Get the donor ID from the selected row
                        int recipientID = (int) recipientTable.getValueAt(selectedRow, 0);
                        // Implement logic to delete the row and corresponding entries in the database
                        deleteRecipient(recipientID);
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
        private void deleteRecipient(int donorID) {
            try {
                // Delete from Donor table
                String deleteRecipientQuery = "DELETE FROM Recipient WHERE RecipientID = ?";
                PreparedStatement deleteRecipientStatement = connection.prepareStatement(deleteRecipientQuery);
                deleteRecipientStatement.setInt(1, donorID);
                deleteRecipientStatement.executeUpdate();
                // Remove row from the table
                DefaultTableModel model = (DefaultTableModel) recipientTable.getModel();
                model.removeRow(recipientTable.getSelectedRow());
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Handle exception
            }
        }
    }


    private void fetchData() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Recipient");

            // Populate the DefaultTableModel with data from the ResultSet
            DefaultTableModel model = (DefaultTableModel) recipientTable.getModel();
            while (resultSet.next()) {
                Object[] row = new Object[9]; // Adjusted for the added Flag column
                row[0] = resultSet.getInt("RecipientID");
                row[1] = resultSet.getLong("Cnic_R");
                row[2] = resultSet.getString("Name");
                row[3] = resultSet.getString("Contact");
                row[4] = resultSet.getString("Address");
                row[5] = resultSet.getString("BloodGroup");
                row[6] = resultSet.getString("RhFactor"); // Include RhFactor
                row[7] = resultSet.getInt("PriorityLevel");
                row[8] = false; // Flag set to false for existing records
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveChanges() {
        DefaultTableModel model = (DefaultTableModel) recipientTable.getModel();
        int rowCount = model.getRowCount();

        try {
            // Start a transaction
            connection.setAutoCommit(false);

            for (int i = 0; i < rowCount; i++) {
                int recipientID;
                // Check the flag column to determine if the row is new
                boolean isNewRow = (Boolean) model.getValueAt(i, FLAG_COLUMN_INDEX);

                if (isNewRow) {
                    // Insert new row logic here
                    String insertQuery = "INSERT INTO Recipient (Cnic_R, Name, Contact, Address, BloodGroup, RhFactor, PriorityLevel) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery,
                            Statement.RETURN_GENERATED_KEYS);
                    insertStatement.setLong(1, Long.parseLong((String) model.getValueAt(i, 1)));
                    insertStatement.setString(2, (String) model.getValueAt(i, 2)); // Name
                    insertStatement.setString(3, (String) model.getValueAt(i, 3)); // Contact
                    insertStatement.setString(4, (String) model.getValueAt(i, 4)); // Address
                    insertStatement.setString(5, (String) model.getValueAt(i, 5)); // BloodGroup
                    insertStatement.setString(6, (String) model.getValueAt(i, 6)); // RhFactor
                    insertStatement.setInt(7, Integer.parseInt((String) model.getValueAt(i, 7))); // PriorityLevel
                    insertStatement.executeUpdate();

                    ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        recipientID = generatedKeys.getInt(1);
                        model.setValueAt(recipientID, i, 0); // Update the RecipientID in the table model
                    }

                    // Set the flag to false for existing rows
                    model.setValueAt(false, i, FLAG_COLUMN_INDEX);
                } else {
                    // Update existing row logic here
                    recipientID = (Integer) model.getValueAt(i, 0);
                    long cnic = Long.parseLong((String) model.getValueAt(i, 1));
                    String name = (String) model.getValueAt(i, 2);
                    String contact = (String) model.getValueAt(i, 3);
                    String address = (String) model.getValueAt(i, 4);
                    String bloodGroup = (String) model.getValueAt(i, 5);
                    String rhFactor = (String) model.getValueAt(i, 6);
                    int priorityLevel = Integer.parseInt((String) model.getValueAt(i, 7)); // Convert String to Integer

                    // Update the corresponding record in the database
                    String updateQuery = "UPDATE Recipient SET Cnic_R=?, Name=?, Contact=?, Address=?, BloodGroup=?, RhFactor=?, PriorityLevel=? WHERE RecipientID=?";
                    PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                    updateStatement.setLong(1, cnic);
                    updateStatement.setString(2, name);
                    updateStatement.setString(3, contact);
                    updateStatement.setString(4, address);
                    updateStatement.setString(5, bloodGroup);
                    updateStatement.setString(6, rhFactor);
                    updateStatement.setInt(7, priorityLevel);
                    updateStatement.setInt(8, recipientID);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(recipient_management::new);
    }
}

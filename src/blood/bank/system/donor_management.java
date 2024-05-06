package blood.bank.system;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Calendar;

public class donor_management extends JFrame {
    private JTable donorTable;
    private JButton addButton;
    private JButton deleteButton;
    private JTextField searchField;
    private JButton searchButton;
    private Connection connection;

    // Add a flag column index
    private static final int FLAG_COLUMN_INDEX = 9;

    public donor_management() {
        setTitle("Donor Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

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
        donorTable.setBackground(Color.decode("#FFCCCC")); // Light red color
        JTableHeader tableHeader = donorTable.getTableHeader();

        // Set the background color of the column headers to a slightly darker shade of
        // red
        // tableHeader.setBackground(Color.decode("#FF9999"));

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

        // Increase row height
        donorTable.setRowHeight(25); // Adjust the row height as needed

        // Create buttons and search field
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        searchField = new JTextField(20);
        searchButton = new JButton("Search");

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Add an empty row to the table with flag set to true
                DefaultTableModel model = (DefaultTableModel) donorTable.getModel();
                model.addRow(new Object[] { null, null, null, null, null, null, null, null, null, true });
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
                if (!searchText.isEmpty()) {
                    DefaultTableModel model = (DefaultTableModel) donorTable.getModel();
                    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                    donorTable.setRowSorter(sorter);

                    RowFilter<DefaultTableModel, Object> rf = null;
                    try {
                        rf = RowFilter.regexFilter(searchText, 4, 2); // Filtering based on Name (column 4) and
                                                                      // BloodGroup (column 2)
                    } catch (java.util.regex.PatternSyntaxException ex) {
                        ex.printStackTrace();
                    }
                    sorter.setRowFilter(rf);
                } else {
                    donorTable.setRowSorter(null);
                }
            }
        });

        // Add key listener for saving changes
        donorTable.addKeyListener(new KeyAdapter() {
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
        buttonPanel.add(deleteButton);
        buttonPanel.add(new JLabel("Search:"));
        buttonPanel.add(searchField);
        buttonPanel.add(searchButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(donorTable), BorderLayout.CENTER);

        getContentPane().add(contentPanel);
        // Set the background color of the main content pane to dark red
        getContentPane().setBackground(Color.decode("#990000")); // Dark red color

        // Fetch data from the database and populate the table
        fetchData();

        // Display the frame
        setVisible(true);
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

                    ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        donorID = generatedKeys.getInt(1);
                        model.setValueAt(donorID, i, 0); // Update the DonorID in the table model

                        // Insert into BloodInventory table
                        int sampleID = getNextSampleID();
                        String bloodGroup = (String) model.getValueAt(i, 2);
                        String rhFactor = (String) model.getValueAt(i, 3);
                        Date expiration = calculateExpirationDate();

                        String inventoryInsertQuery = "INSERT INTO BloodInventory (SampleID, BloodGroup, RhFactor, Expiration, DonorID) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement inventoryInsertStatement = connection.prepareStatement(inventoryInsertQuery);
                        inventoryInsertStatement.setInt(1, sampleID);
                        inventoryInsertStatement.setString(2, bloodGroup);
                        inventoryInsertStatement.setString(3, rhFactor);
                        inventoryInsertStatement.setDate(4, expiration);
                        inventoryInsertStatement.setInt(5, donorID);
                        inventoryInsertStatement.executeUpdate();
                    }
                    // Set the flag to false for existing rows
                    model.setValueAt(false, i, FLAG_COLUMN_INDEX);
                } else {
                    // Update existing row logic here
                    donorID = (Integer) model.getValueAt(i, 0);
                    long cnic = (Long) model.getValueAt(i, 1);
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

    private int getNextSampleID() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT MAX(SampleID) FROM BloodInventory");
        if (resultSet.next()) {
            int maxSampleID = resultSet.getInt(1);
            return maxSampleID + 1;
        } else {
            return 1; // If no records exist, start with 1
        }
    }

    private Date calculateExpirationDate() {
        // Calculate expiration date as two months from the current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        return new Date(calendar.getTimeInMillis());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(donor_management::new);
    }
}

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;

public class matchSearch extends JFrame {
    private DatabaseAccess databaseAccess;
    private JTable table;

    public matchSearch() {
        setTitle("Search Recipient");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField searchField = new JTextField(20);
        // Increase the height of the search bar
        searchField.setPreferredSize(new Dimension(searchField.getPreferredSize().width, 50));
        searchField.setToolTipText("Enter Recipient ID");
        searchField.setFont(new Font("Arial", Font.PLAIN, 20)); // Larger font size
        JButton searchButton = new JButton("Search Matches");
        searchButton.setFont(new Font("Arial", Font.BOLD, 20)); // Larger font size

        // Initialize DatabaseAccess
        databaseAccess = new DatabaseAccess();

        searchButton.addActionListener(e -> {
            // Get the recipient ID entered in the search field
            String recipientIDString = searchField.getText();

            // Check if the entered ID is a valid integer
            if (!isValidInteger(recipientIDString)) {
                // Display error message if the entered ID is not a valid integer
                showNotification("Recipient ID must be an integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return; // Exit the action listener
            }

            // Parse the recipient ID string to an integer
            int recipientID = Integer.parseInt(recipientIDString);

            // Check if the recipient ID exists in the recipient table
            if (!databaseAccess.checkRecipientIDExists(recipientID)) {
                // Display notification if recipient ID does not exist in the recipient table
                showNotification("Recipient ID does not exist.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
                return; // Exit the action listener
            }

            // If recipient ID exists, fetch donor data and update table
            updateTable(recipientID);
        });

        // Add search components to the search panel
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Add search panel to the top
        add(searchPanel, BorderLayout.NORTH);

        // Initialize table with empty model
        table = new JTable(new DefaultTableModel());

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Add scroll pane to the center
        add(scrollPane, BorderLayout.CENTER);
    }

    // Method to check if a string represents a valid integer
    private boolean isValidInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Method to show a notification with custom font size
    private void showNotification(String message, String title, int messageType) {
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 24)); // Larger font size

        JOptionPane.showMessageDialog(this, messageLabel, title, messageType);
    }

    // Method to update the table with donor data based on recipient ID
    private void updateTable(int recipientID) {
        DefaultTableModel model = databaseAccess.getDonorDataForRecipient(recipientID);
        table.setModel(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            matchSearch frame = new matchSearch();
            frame.setVisible(true);
        });
    }
}

// DatabaseAccess class to handle database operations
class DatabaseAccess {
    private final String url = "jdbc:mysql://localhost:3306/Blood_Bank_System";
    private final String username = "root";
    private final String password = "abc_123";

    // Method to check if recipient ID exists in the database
    public boolean checkRecipientIDExists(int recipientID) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM recipient_table WHERE recipient_id = ?");
        ) {
            statement.setInt(1, recipientID);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
            return false;
        }
    }

    // Method to retrieve donor data for a recipient from the database
    public DefaultTableModel getDonorDataForRecipient(int recipientID) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Donor ID");
        model.addColumn("Blood Group");
        model.addColumn("Expiry Date");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT d.donor_id, d.blood_group, b.expiry_date " +
                                                                        "FROM donor d " +
                                                                        "JOIN bloodInventory b ON d.donor_id = b.donor_id " +
                                                                        "JOIN recipient r ON r.blood_group = d.blood_group " +
                                                                        "WHERE r.recipient_id = ?");
        ) {
            statement.setInt(1, recipientID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String donorID = resultSet.getString("donor_id");
                    String bloodGroup = resultSet.getString("blood_group");
                    String expiryDate = resultSet.getString("expiry_date");
                    model.addRow(new Object[]{donorID, bloodGroup, expiryDate});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
        return model;
    }
}

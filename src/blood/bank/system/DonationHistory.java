package blood.bank.system;

import CacheManager.Connect;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;

public class DonationHistory extends JFrame implements ActionListener {
    private JTable donorsTable, recipientsTable, matchTable, inventoryTable;
    private JScrollPane donorsPane, recipientsPane, matchPane, inventoryPane;
    private JPanel spacerPanel;
    private JLabel titleLabel;
    private JButton backButton;

    public DonationHistory(String var) {
        super(var);

        // Create a spacer panel with specific height to add white space at the top
        spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(800, 30)); // Adjust height as needed
        spacerPanel.setBackground(Color.WHITE);

        // Create back button
        backButton = new JButton("Home");
        backButton.addActionListener(this);

        // Set layout and add components
        setLayout(new BorderLayout());

        // Add spacer panel at the top
        add(spacerPanel, BorderLayout.NORTH);

        // JPanel contentPanel = new JPanel(new BorderLayout());

        // contentPanel.add(backButton, BorderLayout.NORTH); // Add back button at the
        // bottom

        spacerPanel.add(backButton);

        if (Objects.equals(var, "donors")) {
            titleLabel = new JLabel("Donors Data");
            donorsTable = new JTable();
            donorsPane = new JScrollPane(donorsTable);
            add(donorsPane);
        }

        else if (Objects.equals(var, "recipients")) {
            titleLabel = new JLabel("Recipients Data");
            recipientsTable = new JTable();
            recipientsPane = new JScrollPane(recipientsTable);
            add(recipientsPane);
        }

        else if (Objects.equals(var, "matches")) {
            titleLabel = new JLabel("Match Data");
            matchTable = new JTable();
            matchPane = new JScrollPane(matchTable);
            add(matchPane);
        }

        else {
            titleLabel = new JLabel("Inventory Data");
            inventoryTable = new JTable();
            inventoryPane = new JScrollPane(inventoryTable);
            add(inventoryPane);
        }

        // // Set layout and add back button
        // setLayout(new BorderLayout());
        // add(backButton, BorderLayout.SOUTH); // Add back button at the bottom

        // titleLabel.setFont(new Font("Raleway",Font.BOLD,25));
        // spacerPanel.add(titleLabel);

        // // Add spacer panel at the top
        // add(spacerPanel, BorderLayout.NORTH);

        // Set layout
        // setLayout(new GridLayout(1, 1));

        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        // Fetch data from the database and populate tables
        fetchDataAndPopulateTables(var);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (Objects.equals(e.getActionCommand(), "Home")) {
                setVisible(false);
                new Home();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fetchDataAndPopulateTables(String str) {
        try {
            Connect connect = new Connect();

            if (Objects.equals(str, "donors")) {
                // Donors Data
                PreparedStatement donorsStatement = connect.getConnection().prepareStatement("SELECT * FROM Donor");
                ResultSet donorsResult = donorsStatement.executeQuery();
                donorsTable.setModel(buildTableModel(donorsResult));
            }

            else if (Objects.equals(str, "recipients")) {
                // Fetch data for Recipients
                PreparedStatement recipientsStatement = connect.getConnection()
                        .prepareStatement("SELECT * FROM Recipient");
                ResultSet recipientsResult = recipientsStatement.executeQuery();
                recipientsTable.setModel(buildTableModel(recipientsResult));
            }

            else if (Objects.equals(str, "matches")) {
                // Fetch data for CrossMatches
                PreparedStatement matchStatement = connect.getConnection()
                        .prepareStatement("SELECT * FROM donationhistory");
                ResultSet matchResult = matchStatement.executeQuery();
                matchTable.setModel(buildTableModel(matchResult));
            }

            else {
                PreparedStatement inventoryStatement = connect.getConnection()
                        .prepareStatement("SELECT * FROM BloodInventory");
                ResultSet inventoryResult = inventoryStatement.executeQuery();
                inventoryTable.setModel(buildTableModel(inventoryResult));
            }

            // Close connection
            connect.getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to convert ResultSet to TableModel
    private TableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        // Get column count
        int columnCount = metaData.getColumnCount();

        // Create column names array
        String[] columnNames = new String[columnCount];
        for (int column = 1; column <= columnCount; column++) {
            columnNames[column - 1] = metaData.getColumnName(column);
        }

        // Create data array
        Object[][] data = new Object[100][columnCount]; // Assuming a maximum of 100 rows

        // Populate data array
        int row = 0;
        while (resultSet.next()) {
            for (int column = 1; column <= columnCount; column++) {
                data[row][column - 1] = resultSet.getObject(column);
            }
            row++;
        }

        // Return TableModel
        return new DefaultTableModel(data, columnNames);
    }

}

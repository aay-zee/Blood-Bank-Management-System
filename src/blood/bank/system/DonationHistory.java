package blood.bank.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;

public class DonationHistory extends JFrame {
    private JTable donorsTable,recipientsTable,matchTable;
    private JScrollPane donorsPane,recipientsPane,matchPane;
    private JPanel spacerPanel;
    private JLabel titleLabel;


    public DonationHistory(String var) {
        super(var);

        // Create spacer panel and set preferred size to add space at the top
        spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(800, 100)); // Adjust height as needed
        spacerPanel.setBackground(Color.WHITE);
        spacerPanel.setLayout(new FlowLayout()); // Set layout for spacer panel

        if(Objects.equals(var, "donors")){
            titleLabel = new JLabel("Donors Data");
            donorsTable = new JTable();
            donorsPane = new JScrollPane(donorsTable);
            add(donorsPane);
        }

        else if(Objects.equals(var, "recipients")){
            titleLabel = new JLabel("Recipients Data");
            recipientsTable = new JTable();
            recipientsPane = new JScrollPane(recipientsTable);
            add(recipientsPane);
        }

        else{
            titleLabel = new JLabel("Match Data");
            matchTable = new JTable();
            matchPane = new JScrollPane(matchTable);
            add(matchPane);
        }
        titleLabel.setFont(new Font("Raleway",Font.BOLD,25));
        spacerPanel.add(titleLabel);

        // Add spacer panel at the top
        add(spacerPanel, BorderLayout.NORTH);

        // Set layout
        setLayout(new GridLayout(1, 1));




        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        // Fetch data from the database and populate tables
        fetchDataAndPopulateTables(var);
    }

    private void fetchDataAndPopulateTables(String str) {
        try{
            Connect connect = new Connect();

            if(Objects.equals(str, "donors")) {
                //Donors Data
                PreparedStatement donorsStatement = connect.connection.prepareStatement("SELECT Name,BloodGroup FROM Donor");
                ResultSet donorsResult = donorsStatement.executeQuery();
                donorsTable.setModel(buildTableModel(donorsResult));
            }

            else if(Objects.equals(str, "recipients")) {
                // Fetch data for Recipients
                PreparedStatement recipientsStatement = connect.connection.prepareStatement("SELECT Name,BloodGroup FROM Recipient");
                ResultSet recipientsResult = recipientsStatement.executeQuery();
                recipientsTable.setModel(buildTableModel(recipientsResult));
            }

            else {
                // Fetch data for CrossMatches
                PreparedStatement matchStatement = connect.connection.prepareStatement("SELECT DonorID,RecipientID,BloodGroup FROM CrossMatch");
                ResultSet matchResult = matchStatement.executeQuery();
                matchTable.setModel(buildTableModel(matchResult));
            }

            // Close connection
            connect.connection.close();
        }catch(Exception e){
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

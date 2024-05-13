package CacheManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTables {
    public static void main(String[] args) {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();

        if (connection != null) {
            System.out.println("Connected to the database");
            Statement statement = connect.getStatement();

            // Creating tables
            createTables(statement);

            try {
                // Closing the connection
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createTables(Statement statement) {
        try {
            // Creating Donor table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Donor (" +
                    "DonorID INT PRIMARY KEY, " +
                    "Cnic_D INT(13), " +
                    "BloodGroup VARCHAR(2), " +
                    "Name VARCHAR(30), " +
                    "LastDonation DATE, " +
                    "Contact VARCHAR(20), " +
                    "Address VARCHAR(60), " +
                    "Age INT)");

            // Creating BloodInventory table

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS BloodInventory (" +
                    "SampleID INT PRIMARY KEY, " +
                    "BloodGroup VARCHAR(2), " +
                    "RhFactor VARCHAR(10), " +
                    "Expiration DATE, " +
                    "DonorID INT, " +
                    "FOREIGN KEY (DonorID) REFERENCES Donor(DonorID) ON UPDATE CASCADE ON DELETE CASCADE)");

            // Creating SignUp table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS SignUp (" +
                    "FacilityID INT PRIMARY KEY NOT NULL, " +
                    "Name VARCHAR(30), " +
                    "Email VARCHAR(30), " +
                    "Password VARCHAR(30), " +
                    "Contact VARCHAR(20), " +
                    "Address VARCHAR(60), " +
                    "Cnic_A INT(13))");

            // Creating Recipient table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Recipient (" +
                    "RecipientID INT PRIMARY KEY, " +
                    "Cnic_R VARCHAR(13) , " +
                    "Name VARCHAR(30), " +
                    "Contact VARCHAR(11), " +
                    "Address VARCHAR(60), " +
                    "BloodGroup VARCHAR(3), " +
                    "PriorityLevel INT)");

            // Creating Donation_History table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Donation_History (" +
                    "DonorID INT, " +
                    "RecipientID INT, " +
                    "BloodGroup VARCHAR(2), " +
                    "CrossID INT PRIMARY KEY, " +
                    "FOREIGN KEY (DonorID) REFERENCES Donor(DonorID) ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "FOREIGN KEY (RecipientID) REFERENCES Recipient(RecipientID) ON UPDATE CASCADE ON DELETE CASCADE)");

            // Creating Cross_Match table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Cross_Match (" +
                    "CrossID INT PRIMARY KEY, " +
                    "SampleID INT," +
                    "DonorID INT, " +
                    "RecipientID INT, " +
                    "BloodGroup VARCHAR(2), " +
                    "Cnic_R INT(13), " +
                    "FOREIGN KEY (DonorID) REFERENCES Donor(DonorID) ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "FOREIGN KEY (RecipientID) REFERENCES Recipient(RecipientID) ON UPDATE CASCADE ON DELETE CASCADE, "
                    +
                    "FOREIGN KEY (SampleID) REFERENCES BloodInventory(SampleID) ON UPDATE CASCADE ON DELETE CASCADE)");

            System.out.println("Tables created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

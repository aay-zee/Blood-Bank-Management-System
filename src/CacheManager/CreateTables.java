package CacheManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import CacheManager.Connect;

public class CreateTables {
    public static void main(String[] args) {
        Connect connect = new Connect();
        Connection connection = connect.getConnection();

        if (connection != null) {
            System.out.println("Connected to the database!");
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
                    "Cnic_D BIGINT NOT NULL, " +
                    "BloodGroup VARCHAR(2), " +
                    "RhFactor VARCHAR(1), " +
                    "Name VARCHAR(30), " +
                    "LastDonation DATE, " +
                    "Contact VARCHAR(20), " +
                    "Address VARCHAR(60), " +
                    "Age INT(3), " + "UNIQUE(Cnic_D))");

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
                    "UserID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "Name VARCHAR(30), " +
                    "Cnic_A BIGINT(13) NOT NULL, " +
                    "Email VARCHAR(30), " +
                    "Password VARCHAR(30), " +
                    "Contact VARCHAR(20), " +
                    "Address VARCHAR(60), " +
                    "UNIQUE(Cnic_A))");

            // Creating Recipient table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Recipient (" +
                    "RecipientID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "Name VARCHAR(30), " +
                    "Cnic_R BIGINT(13) NOT NULL, " +
                    "BloodGroup VARCHAR(2), " +
                    "RhFactor VARCHAR(1), " +
                    "Contact VARCHAR(20), " +
                    "Address VARCHAR(60), " +
                    "PriorityLevel INT(5), " +
                    "UNIQUE (Cnic_R))");

            // Creating CrossMatch table
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS CrossMatch (" +
                    "CrossID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "DonorID INT, " +
                    "RecipientID INT, " +
                    "BloodGroup VARCHAR(5), " +
                    "FOREIGN KEY (DonorID) REFERENCES Donor(DonorID) ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "FOREIGN KEY (RecipientID) REFERENCES Recipient(RecipientID) ON UPDATE CASCADE ON DELETE CASCADE)");

            System.out.println("Tables created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
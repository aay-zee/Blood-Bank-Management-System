package blood.bank.system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Connect {
    Connection connection;

    Statement statement;
    public Connect() {
        try {
            connection= DriverManager.getConnection("jdbc:mysql://localhost:3306/BloodBankSystem","root","l227881");
            statement=connection.createStatement();
        } catch (Exception E) {
            E.printStackTrace();
        }
    }
}

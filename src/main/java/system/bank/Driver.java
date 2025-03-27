package system.bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Driver {
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@1B2h8i9";
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb";
    private static Connection connection;
    private static Scanner sc;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }
    public static Scanner getScanner(){
        if (sc == null) {
            sc = new Scanner(System.in);
        }
        return sc;
    }
}

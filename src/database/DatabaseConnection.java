package database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/MovieBookingDB";
    private static final String userName = "root";
    private static final String password = "password123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, userName, password);
    }
}

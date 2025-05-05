package com.ryan.yowg.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
//    private static final String URL = "jdbc:sqlite:./lib/database.db"; // Path ke file database
    private static final String URL = "jdbc:sqlite:" + System.getProperty("user.home") + "/Program/yo-wg/lib/database.db"; // Path ke file database

    public static Connection connect() {
        Connection connection = null;
        try {
            // Membuat koneksi ke SQLite database
            connection = DriverManager.getConnection(URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite: " + e.getMessage());
        }
        return connection;
    }
}
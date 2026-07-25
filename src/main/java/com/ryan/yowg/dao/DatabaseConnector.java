package com.ryan.yowg.dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DatabaseConnector {
    private static final String OLD_DB_PATH = System.getProperty("user.home") + "/Program/yo-wg/lib/database.db";
    private static final String NEW_DB_DIR = System.getProperty("user.home") + "/.local/share/yo-wg";
    private static final String NEW_DB_PATH = NEW_DB_DIR + "/database.db";
    private static final String URL = "jdbc:sqlite:" + NEW_DB_PATH;

    static {
        // Ensure directory exists
        File newDir = new File(NEW_DB_DIR);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        // Auto-migration from legacy path
        File oldDb = new File(OLD_DB_PATH);
        File newDb = new File(NEW_DB_PATH);
        if (oldDb.exists() && !newDb.exists()) {
            try {
                System.out.println("Migrating database from " + OLD_DB_PATH + " to " + NEW_DB_PATH);
                Files.copy(oldDb.toPath(), newDb.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Database migration successful.");
            } catch (Exception e) {
                System.err.println("Failed to migrate database: " + e.getMessage());
            }
        }
    }

    public static Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("Connection to SQLite has been established at " + NEW_DB_PATH);
        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite: " + e.getMessage());
        }
        return connection;
    }
}
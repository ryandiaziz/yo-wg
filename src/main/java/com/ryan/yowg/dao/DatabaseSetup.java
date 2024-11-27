package com.ryan.yowg.dao;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {
    public static void createTable() {
        String sql_table_access = "CREATE TABLE IF NOT EXISTS access ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "address TEXT NOT NULL UNIQUE, "
                + "wireguard_id INTEGER, "
                + "FOREIGN KEY (wireguard_id) REFERENCES wireguards(id) ON DELETE CASCADE"
                + ");";

        String sql_table_wg = "CREATE TABLE IF NOT EXISTS wireguards ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL UNIQUE, "
                + "note TEXT NOT NULL, "
                + "content TEXT NOT NULL "
                + ");";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql_table_wg);
            stmt.execute(sql_table_access);
            System.out.println("Table 'users' created or already exists.");
        } catch (Exception e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }
}
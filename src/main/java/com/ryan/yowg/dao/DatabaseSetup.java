package com.ryan.yowg.dao;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {
    public static void createTable() {
        String sql_table_wg = "CREATE TABLE IF NOT EXISTS wireguards ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL UNIQUE, "
                + "note TEXT NOT NULL, "
                + "content TEXT NOT NULL "
                + ");";

        String sql_table_access = "CREATE TABLE IF NOT EXISTS access ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "address TEXT NOT NULL UNIQUE, "
                + "ssh_user TEXT DEFAULT 'administrator', "
                + "ssh_port INTEGER DEFAULT 22, "
                + "wireguard_id INTEGER, "
                + "FOREIGN KEY (wireguard_id) REFERENCES wireguards(id) ON DELETE CASCADE"
                + ");";

        // TAMBAHKAN INI: Tabel baru untuk menyimpan banyak URL per 'access'
        String sql_table_resources = "CREATE TABLE IF NOT EXISTS resources ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "url TEXT NOT NULL, "
                + "access_id INTEGER, "
                + "FOREIGN KEY (access_id) REFERENCES access(id) ON DELETE CASCADE"
                + ");";

        try (Connection conn = DatabaseConnector.connect();
                Statement stmt = conn.createStatement()) {

            stmt.execute(sql_table_wg);
            System.out.println("Tabel 'wireguards' siap.");

            stmt.execute(sql_table_access);

            // Migration: Add columns if they don't exist
            try {
                stmt.execute("ALTER TABLE access ADD COLUMN ssh_user TEXT DEFAULT 'administrator'");
                System.out.println("Added column ssh_user to access");
            } catch (Exception e) {
                // Column likely exists
            }
            try {
                stmt.execute("ALTER TABLE access ADD COLUMN ssh_port INTEGER DEFAULT 22");
                System.out.println("Added column ssh_port to access");
            } catch (Exception e) {
                // Column likely exists
            }

            System.out.println("Tabel 'access' siap.");

            stmt.execute(sql_table_resources);
            System.out.println("Tabel 'resources' siap.");

            System.out.println("Semua tabel berhasil disiapkan.");

        } catch (Exception e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }
}
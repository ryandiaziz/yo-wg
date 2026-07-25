package com.ryan.yowg.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SettingsDAO {

    public static String getSetting(String key) {
        String sql = "SELECT value FROM settings WHERE key = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveSetting(String key, String value) {
        String sql = "INSERT INTO settings(key, value) VALUES(?, ?) " +
                     "ON CONFLICT(key) DO UPDATE SET value=excluded.value;";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

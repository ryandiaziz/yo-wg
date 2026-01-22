package com.ryan.yowg.dao;

import com.ryan.yowg.models.Wireguard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WireguardDAO {

    // Menambahkan Wireguard baru ke database
    public static void insertWireguard(Wireguard wireguard) {
        String sql = "INSERT INTO wireguards (name, note, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, wireguard.getName());
            pstmt.setString(2, wireguard.getNote());
            pstmt.setString(3, wireguard.getContent());
            pstmt.executeUpdate();
            System.out.println("Wireguard added: " + wireguard.getName());
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Error: Wireguard name must be unique.");
            } else {
                System.out.println("Error inserting wireguard: " + e.getMessage());
            }
        }
    }

    // Mengambil semua Wireguard dari database
    public static List<Wireguard> getAllWireguards() {
        List<Wireguard> wireguards = new ArrayList<>();
        String sql = "SELECT * FROM wireguards";
        try (Connection conn = DatabaseConnector.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                wireguards.add(
                        new Wireguard(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("note"),
                                rs.getString("content")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching wireguards: " + e.getMessage());
        }
        return wireguards;
    }

    // Mencari Wireguard berdasarkan nama
    public static Wireguard findWireguardByName(String name) {
        String sql = "SELECT * FROM wireguards WHERE name = ?";
        try (Connection conn = DatabaseConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Wireguard(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("note"),
                        rs.getString("content"));
            }
        } catch (SQLException e) {
            System.out.println("Error finding wireguard by name: " + e.getMessage());
        }
        return null; // Jika tidak ditemukan
    }

    // Mencari Wireguard berdasarkan ID
    public static Wireguard findWireguardById(int id) {
        String sql = "SELECT * FROM wireguards WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Wireguard(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("note"),
                        rs.getString("content"));
            }
        } catch (SQLException e) {
            System.out.println("Error finding wireguard by ID: " + e.getMessage());
        }
        return null; // Jika tidak ditemukan
    }

    // Memperbarui Wireguard
    public static void updateWireguard(Wireguard wireguard) {
        String sql = "UPDATE wireguards SET name = ?, note = ?, content = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, wireguard.getName());
            pstmt.setString(2, wireguard.getNote());
            pstmt.setString(3, wireguard.getContent());
            pstmt.setInt(4, wireguard.getId());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Wireguard updated: " + wireguard.getName());
            } else {
                System.out.println("Wireguard not found for update: ID " + wireguard.getId());
            }
        } catch (SQLException e) {
            System.out.println("Error updating wireguard: " + e.getMessage());
        }
    }

    // Menghapus Wireguard berdasarkan ID
    public static void deleteWireguardById(int id) {
        String sql = "DELETE FROM wireguards WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Wireguard deleted with ID: " + id);
            } else {
                System.out.println("Wireguard not found for deletion: ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting wireguard: " + e.getMessage());
        }
    }

    public static List<Wireguard> findWireguardsByAccessName(String accessName) {
        List<Wireguard> wireguards = new ArrayList<>();
        String sql = "SELECT DISTINCT w.* FROM wireguards w JOIN access a ON w.id = a.wireguard_id WHERE a.name LIKE ?";
        try (Connection conn = DatabaseConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + accessName + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                wireguards.add(new Wireguard(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("note"),
                        rs.getString("content")));
            }
        } catch (SQLException e) {
            System.out.println("Error finding wireguards by access name: " + e.getMessage());
        }
        return wireguards;
    }
}

package com.ryan.yowg.dao;

import com.ryan.yowg.models.Credential;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CredentialDAO {

    public static void insertCredential(Credential credential) {
        String sql = "INSERT INTO credentials (name, username, type, secret) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, credential.getName());
            pstmt.setString(2, credential.getUsername());
            pstmt.setString(3, credential.getType());
            pstmt.setString(4, credential.getSecret());
            pstmt.executeUpdate();
            System.out.println("Credential added: " + credential.getName());
        } catch (SQLException e) {
            System.out.println("Error inserting credential: " + e.getMessage());
        }
    }

    public static List<Credential> getAllCredentials() {
        List<Credential> list = new ArrayList<>();
        String sql = "SELECT * FROM credentials ORDER BY name ASC";
        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Credential(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("type"),
                        rs.getString("secret")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching credentials: " + e.getMessage());
        }
        return list;
    }

    public static Credential getCredentialById(int id) {
        String sql = "SELECT * FROM credentials WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Credential(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("type"),
                        rs.getString("secret")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching credential by ID: " + e.getMessage());
        }
        return null;
    }

    public static void updateCredential(Credential credential) {
        String sql = "UPDATE credentials SET name = ?, username = ?, type = ?, secret = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, credential.getName());
            pstmt.setString(2, credential.getUsername());
            pstmt.setString(3, credential.getType());
            pstmt.setString(4, credential.getSecret());
            pstmt.setInt(5, credential.getId());
            pstmt.executeUpdate();
            System.out.println("Credential updated: " + credential.getName());
        } catch (SQLException e) {
            System.out.println("Error updating credential: " + e.getMessage());
        }
    }

    public static void deleteCredential(int id) {
        String sql = "DELETE FROM credentials WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Credential deleted with id: " + id);
        } catch (SQLException e) {
            System.out.println("Error deleting credential: " + e.getMessage());
        }
    }
}

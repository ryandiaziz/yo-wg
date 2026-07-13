package com.ryan.yowg.dao;

import com.ryan.yowg.models.Access;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessDAO {

    // Insert new access
    public static void insertAccess(Access access) {
        String sql = "INSERT INTO access (name, address, ssh_user, ssh_port, wireguard_id, credential_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, access.getName());
            pstmt.setString(2, access.getAddress());
            pstmt.setString(3, access.getSshUser());
            pstmt.setInt(4, access.getSshPort());
            pstmt.setInt(5, access.getWireguardId()); // Menghubungkan dengan wireguard
            if (access.getCredentialId() != null) {
                pstmt.setInt(6, access.getCredentialId());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            pstmt.executeUpdate();
            System.out.println("Access added: " + access.getName());
        } catch (SQLException e) {
            System.out.println("Error inserting access: " + e.getMessage());
        }
    }

    // Get all access
    public static List<Access> getAllAccess() {
        List<Access> accessList = new ArrayList<>();
        String sql = "SELECT * FROM access";
        try (Connection conn = DatabaseConnector.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Integer credId = rs.getObject("credential_id") != null ? rs.getInt("credential_id") : null;
                accessList.add(new Access(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("ssh_user"),
                        rs.getInt("ssh_port"),
                        rs.getInt("wireguard_id"), // wireguard_id yang terhubung
                        credId
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching access: " + e.getMessage());
        }
        return accessList;
    }

    // Get access by wireguard id
    public static List<Access> getAccessByWireguard(int wireguardId) {
        List<Access> accessList = new ArrayList<>();
        String sql = "SELECT * FROM access WHERE wireguard_id = ?";
        try (Connection conn = DatabaseConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, wireguardId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Integer credId = rs.getObject("credential_id") != null ? rs.getInt("credential_id") : null;
                accessList.add(new Access(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("ssh_user"),
                        rs.getInt("ssh_port"),
                        rs.getInt("wireguard_id"),
                        credId));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching access by wireguard_id: " + e.getMessage());
        }
        return accessList;
    }

    // Update access
    public static void updateAccess(Access access) {
        String sql = "UPDATE access SET name = ?, address = ?, ssh_user = ?, ssh_port = ?, wireguard_id = ?, credential_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, access.getName());
            pstmt.setString(2, access.getAddress());
            pstmt.setString(3, access.getSshUser());
            pstmt.setInt(4, access.getSshPort());
            pstmt.setInt(5, access.getWireguardId()); // Menyimpan id Wireguard
            if (access.getCredentialId() != null) {
                pstmt.setInt(6, access.getCredentialId());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            pstmt.setInt(7, access.getId());
            pstmt.executeUpdate();
            System.out.println("Access updated: " + access.getName());
        } catch (SQLException e) {
            System.out.println("Error updating access: " + e.getMessage());
        }
    }

    // Delete access by id
    public static void deleteAccess(int accessId) {
        String sql = "DELETE FROM access WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accessId);
            pstmt.executeUpdate();
            System.out.println("Access deleted with id: " + accessId);
        } catch (SQLException e) {
            System.out.println("Error deleting access: " + e.getMessage());
        }
    }
}

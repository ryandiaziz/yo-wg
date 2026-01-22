package com.ryan.yowg.dao;

import com.ryan.yowg.models.Resource; // Import model Resource

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    /**
     * Menyimpan Resource (URL) baru ke database, terhubung ke sebuah Access.
     */
    public static void insertResource(Resource resource) {
        String sql = "INSERT INTO resources (name, url, access_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, resource.getName());
            pstmt.setString(2, resource.getUrl());
            pstmt.setInt(3, resource.getAccessId()); // Foreign key ke tabel 'access'

            pstmt.executeUpdate();
            System.out.println("Resource added: " + resource.getName());
        } catch (SQLException e) {
            System.out.println("Error inserting resource: " + e.getMessage());
        }
    }

    /**
     * Mengambil semua resource yang terkait dengan satu 'access_id'
     */
    public static List<Resource> getResourcesByAccessId(int accessId) {
        List<Resource> resourceList = new ArrayList<>();
        String sql = "SELECT * FROM resources WHERE access_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, accessId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                resourceList.add(new Resource(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("url"),
                        rs.getInt("access_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching resources by access_id: " + e.getMessage());
        }
        return resourceList;
    }

    /**
     * Mengambil semua resource dari database (jarang digunakan, tapi baik untuk ada).
     */
    public static List<Resource> getAllResources() {
        List<Resource> resourceList = new ArrayList<>();
        String sql = "SELECT * FROM resources";

        try (Connection conn = DatabaseConnector.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resourceList.add(new Resource(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("url"),
                        rs.getInt("access_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all resources: " + e.getMessage());
        }
        return resourceList;
    }

    /**
     * Memperbarui data resource yang ada.
     */
    public static void updateResource(Resource resource) {
        String sql = "UPDATE resources SET name = ?, url = ?, access_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, resource.getName());
            pstmt.setString(2, resource.getUrl());
            pstmt.setInt(3, resource.getAccessId());
            pstmt.setInt(4, resource.getId());

            pstmt.executeUpdate();
            System.out.println("Resource updated: " + resource.getName());
        } catch (SQLException e) {
            System.out.println("Error updating resource: " + e.getMessage());
        }
    }

    /**
     * Menghapus resource berdasarkan ID-nya.
     */
    public static void deleteResource(int resourceId) {
        String sql = "DELETE FROM resources WHERE id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, resourceId);
            pstmt.executeUpdate();

            System.out.println("Resource deleted with id: " + resourceId);
        } catch (SQLException e) {
            System.out.println("Error deleting resource: " + e.getMessage());
        }
    }
}
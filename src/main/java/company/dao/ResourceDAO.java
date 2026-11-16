
package company.dao;

import company.config.DatabaseConnection;
import company.model.Resource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    // CREATE
    public boolean addResource(Resource resource)
            throws SQLException {

        String sql = """
                INSERT INTO resource
                (resource_name, category, timeline, quantity, cost)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, resource.getResourceName());
            ps.setString(2, resource.getCategory());
            ps.setString(3, resource.getTimeline());
            ps.setInt(4, resource.getQuantity());
            ps.setDouble(5, resource.getCost());

            return ps.executeUpdate() > 0;
        }
    }

    // READ
    public List<Resource> getAllResources()
            throws SQLException {

        List<Resource> resources = new ArrayList<>();

        String sql = "SELECT * FROM resource ORDER BY resource_id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Resource resource = new Resource(
                        rs.getInt("resource_id"),
                        rs.getString("resource_name"),
                        rs.getString("category"),
                        rs.getString("timeline"),
                        rs.getInt("quantity"),
                        rs.getDouble("cost")
                );

                resources.add(resource);
            }
        }

        return resources;
    }

    // UPDATE
    public boolean updateResource(Resource resource)
            throws SQLException {

        String sql = """
                UPDATE resource
                SET resource_name = ?,
                    category = ?,
                    timeline = ?,
                    quantity = ?,
                    cost = ?
                WHERE resource_id = ?
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, resource.getResourceName());
            ps.setString(2, resource.getCategory());
            ps.setString(3, resource.getTimeline());
            ps.setInt(4, resource.getQuantity());
            ps.setDouble(5, resource.getCost());
            ps.setInt(6, resource.getResourceId());

            return ps.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deleteResource(int resourceId)
            throws SQLException {

        String sql =
                "DELETE FROM resource WHERE resource_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, resourceId);

            return ps.executeUpdate() > 0;
        }
    }

    // Get resources currently available to employees
    public List<Resource> getAvailableResources()
            throws SQLException {

        List<Resource> resources = new ArrayList<>();

        String sql = """
                SELECT * FROM resource
                WHERE quantity > 0
                ORDER BY resource_name
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Resource resource = new Resource(
                        rs.getInt("resource_id"),
                        rs.getString("resource_name"),
                        rs.getString("category"),
                        rs.getString("timeline"),
                        rs.getInt("quantity"),
                        rs.getDouble("cost")
                );

                resources.add(resource);
            }
        }

        return resources;
    }

}

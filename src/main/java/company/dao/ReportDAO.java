
package company.dao;

import company.config.DatabaseConnection;
import company.model.ReportSummary;
import company.model.CategoryReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    // Get overall system statistics
    public ReportSummary getReportSummary() throws SQLException {

        String sql = """
                SELECT
                    (SELECT COUNT(*)
                     FROM resource) AS total_resources,

                    (SELECT COALESCE(SUM(quantity), 0)
                     FROM resource) AS total_quantity,

                    (SELECT COUNT(*)
                     FROM access_requests
                     WHERE status = 'PENDING') AS pending_requests,

                    (SELECT COUNT(*)
                     FROM access_requests
                     WHERE status = 'APPROVED') AS approved_requests,

                    (SELECT COUNT(*)
                     FROM access_requests
                     WHERE status = 'REJECTED') AS rejected_requests,

                    (SELECT COALESCE(SUM(quantity * cost), 0)
                     FROM resource) AS inventory_value
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                return new ReportSummary(
                        rs.getInt("total_resources"),
                        rs.getInt("total_quantity"),
                        rs.getInt("pending_requests"),
                        rs.getInt("approved_requests"),
                        rs.getInt("rejected_requests"),
                        rs.getDouble("inventory_value")
                );
            }
        }

        return new ReportSummary(0, 0, 0, 0, 0, 0);
    }

    // Get resource statistics grouped by category
    public List<CategoryReport> getCategoryReports()
            throws SQLException {

        List<CategoryReport> reports = new ArrayList<>();

        String sql = """
                SELECT
                    category,
                    COUNT(*) AS resource_count,
                    COALESCE(SUM(quantity), 0) AS available_quantity,
                    COALESCE(SUM(quantity * cost), 0) AS inventory_value
                FROM resource
                GROUP BY category
                ORDER BY category
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                CategoryReport report = new CategoryReport(
                        rs.getString("category"),
                        rs.getInt("resource_count"),
                        rs.getInt("available_quantity"),
                        rs.getDouble("inventory_value")
                );

                reports.add(report);
            }
        }

        return reports;
    }
}

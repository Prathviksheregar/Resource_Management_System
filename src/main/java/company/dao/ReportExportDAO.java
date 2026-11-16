
package company.dao;

import company.config.DatabaseConnection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportExportDAO {

    // Export resource details
    public void exportResources(File file)
            throws SQLException, IOException {

        String sql = """
                SELECT
                    resource_id,
                    resource_name,
                    category,
                    timeline,
                    quantity,
                    cost
                FROM resource
                ORDER BY resource_id
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(file),
                             StandardCharsets.UTF_8
                     )
             )) {

            writer.write(
                    "Resource ID,Resource Name,Category,Timeline,Quantity,Cost"
            );
            writer.newLine();

            while (rs.next()) {

                writer.write(
                        rs.getInt("resource_id") + ","
                                + csv(rs.getString("resource_name")) + ","
                                + csv(rs.getString("category")) + ","
                                + csv(rs.getString("timeline")) + ","
                                + rs.getInt("quantity") + ","
                                + rs.getBigDecimal("cost")
                );

                writer.newLine();
            }
        }
    }

    // Export employee request details
    public void exportRequests(File file)
            throws SQLException, IOException {

        String sql = """
                SELECT
                    ar.request_id,
                    u.username,
                    r.resource_name,
                    ar.requested_quantity,
                    ar.status,
                    ar.request_time
                FROM access_requests ar
                JOIN users u
                    ON ar.user_id = u.user_id
                JOIN resource r
                    ON ar.resource_id = r.resource_id
                ORDER BY ar.request_id DESC
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             BufferedWriter writer = new BufferedWriter(
                     new OutputStreamWriter(
                             new FileOutputStream(file),
                             StandardCharsets.UTF_8
                     )
             )) {

            writer.write(
                    "Request ID,Employee,Resource,Requested Quantity,Status,Request Time"
            );
            writer.newLine();

            while (rs.next()) {

                writer.write(
                        rs.getInt("request_id") + ","
                                + csv(rs.getString("username")) + ","
                                + csv(rs.getString("resource_name")) + ","
                                + rs.getInt("requested_quantity") + ","
                                + csv(rs.getString("status")) + ","
                                + csv(String.valueOf(rs.getTimestamp("request_time")))
                );

                writer.newLine();
            }
        }
    }

    // Escape CSV values and reduce spreadsheet formula risks
    private String csv(String value) {

        if (value == null) {
            return "";
        }

        String safeValue = value;

        if (safeValue.matches("^[\\s]*[=+@].*")
                || safeValue.matches("^[\\s]*-.*")) {
            safeValue = "'" + safeValue;
        }

        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }
}

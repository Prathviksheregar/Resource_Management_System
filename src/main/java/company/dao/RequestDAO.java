
package company.dao;

import company.config.DatabaseConnection;
import company.model.ResourceRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    // Submit a new employee request
    public void submitRequest(
            int userId,
            int resourceId,
            int requestedQuantity
    ) throws SQLException {

        String checkSql = """
                SELECT quantity
                FROM resource
                WHERE resource_id = ?
                """;

        String insertSql = """
                INSERT INTO access_requests
                (user_id, resource_id, requested_quantity)
                VALUES (?, ?, ?)
                """;

        try (Connection con = DatabaseConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int availableQuantity;

                try (PreparedStatement ps =
                             con.prepareStatement(checkSql)) {

                    ps.setInt(1, resourceId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Resource not found.");
                        }

                        availableQuantity = rs.getInt("quantity");
                    }
                }

                if (availableQuantity <= 0) {
                    throw new SQLException("Resource unavailable.");
                }

                if (requestedQuantity > availableQuantity) {
                    throw new SQLException(
                            "Only " + availableQuantity + " units available."
                    );
                }

                try (PreparedStatement ps =
                             con.prepareStatement(insertSql)) {

                    ps.setInt(1, userId);
                    ps.setInt(2, resourceId);
                    ps.setInt(3, requestedQuantity);

                    ps.executeUpdate();
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Get all employee requests
    public List<ResourceRequest> getAllRequests()
            throws SQLException {

        List<ResourceRequest> requests = new ArrayList<>();

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
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                ResourceRequest request = new ResourceRequest(
                        rs.getInt("request_id"),
                        rs.getString("username"),
                        rs.getString("resource_name"),
                        rs.getInt("requested_quantity"),
                        rs.getString("status"),
                        rs.getTimestamp("request_time")
                );

                requests.add(request);
            }
        }

        return requests;
    }

    // Approve a pending request and reduce resource stock
    public void approveRequest(int requestId, int adminUserId)
            throws SQLException {

        String requestSql = """
                SELECT resource_id, requested_quantity, status
                FROM access_requests
                WHERE request_id = ?
                FOR UPDATE
                """;

        String resourceSql = """
                SELECT quantity
                FROM resource
                WHERE resource_id = ?
                FOR UPDATE
                """;

        String reduceStockSql = """
                UPDATE resource
                SET quantity = quantity - ?
                WHERE resource_id = ?
                AND quantity >= ?
                """;

        String approveSql = """
                UPDATE access_requests
                SET status = 'APPROVED'
                WHERE request_id = ?
                AND status = 'PENDING'
                """;

        try (Connection con = DatabaseConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int resourceId;
                int requestedQuantity;

                // Lock and check the request
                try (PreparedStatement ps =
                             con.prepareStatement(requestSql)) {

                    ps.setInt(1, requestId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Request not found.");
                        }

                        if (!"PENDING".equalsIgnoreCase(
                                rs.getString("status"))) {

                            throw new SQLException(
                                    "This request has already been processed."
                            );
                        }

                        resourceId = rs.getInt("resource_id");
                        requestedQuantity =
                                rs.getInt("requested_quantity");
                    }
                }

                // Lock and check resource stock
                int availableQuantity;

                try (PreparedStatement ps =
                             con.prepareStatement(resourceSql)) {

                    ps.setInt(1, resourceId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Resource not found.");
                        }

                        availableQuantity = rs.getInt("quantity");
                    }
                }

                if (availableQuantity < requestedQuantity) {
                    throw new SQLException(
                            "Insufficient stock. Available: "
                                    + availableQuantity
                    );
                }

                // Reduce stock
                try (PreparedStatement ps =
                             con.prepareStatement(reduceStockSql)) {

                    ps.setInt(1, requestedQuantity);
                    ps.setInt(2, resourceId);
                    ps.setInt(3, requestedQuantity);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Could not update resource stock."
                        );
                    }
                }

                // Mark request as approved
                try (PreparedStatement ps =
                             con.prepareStatement(approveSql)) {

                    ps.setInt(1, requestId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "Could not approve the request."
                        );
                    }
                }

                String historySql = """
        INSERT INTO resource_history
        (resource_id, user_id, quantity, action)
        VALUES (?, ?, ?, 'APPROVED')
        """;

                try (PreparedStatement historyPs =
                             con.prepareStatement(historySql)) {

                    historyPs.setInt(1, resourceId);
                    historyPs.setInt(2, adminUserId);
                    historyPs.setInt(3, requestedQuantity);

                    historyPs.executeUpdate();
                }
                con.commit();

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Reject a pending request

    public void rejectRequest(int requestId, int adminUserId)
            throws SQLException {

        String requestSql = """
            SELECT resource_id, requested_quantity, status
            FROM access_requests
            WHERE request_id = ?
            FOR UPDATE
            """;

        String updateSql = """
            UPDATE access_requests
            SET status = 'REJECTED'
            WHERE request_id = ?
            AND status = 'PENDING'
            """;

        String historySql = """
            INSERT INTO resource_history
            (resource_id, user_id, quantity, action)
            VALUES (?, ?, ?, 'REJECTED')
            """;

        try (Connection con = DatabaseConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                int resourceId;
                int quantity;

                try (PreparedStatement ps =
                             con.prepareStatement(requestSql)) {

                    ps.setInt(1, requestId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException("Request not found.");
                        }

                        if (!"PENDING".equalsIgnoreCase(
                                rs.getString("status"))) {

                            throw new SQLException(
                                    "Request has already been processed."
                            );
                        }

                        resourceId = rs.getInt("resource_id");
                        quantity = rs.getInt("requested_quantity");
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(updateSql)) {

                    ps.setInt(1, requestId);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Could not reject request.");
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(historySql)) {

                    ps.setInt(1, resourceId);
                    ps.setInt(2, adminUserId);
                    ps.setInt(3, quantity);

                    ps.executeUpdate();
                }

                con.commit();

            } catch (SQLException | RuntimeException e) {

                con.rollback();
                throw e;

            } finally {
                con.setAutoCommit(true);
            }
        }
    }


    // Get requests belonging to one employee
    public List<ResourceRequest> getRequestsByUserId(int userId)
            throws SQLException {

        List<ResourceRequest> requests = new ArrayList<>();

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
                WHERE ar.user_id = ?
                ORDER BY ar.request_id DESC
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ResourceRequest request = new ResourceRequest(
                            rs.getInt("request_id"),
                            rs.getString("username"),
                            rs.getString("resource_name"),
                            rs.getInt("requested_quantity"),
                            rs.getString("status"),
                            rs.getTimestamp("request_time")
                    );

                    requests.add(request);
                }
            }
        }

        return requests;
    }

}

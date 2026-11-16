
package company.dao;

import company.config.DatabaseConnection;
import company.model.ResourceHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceHistoryDAO {

    public List<ResourceHistory> getAllHistory()
            throws SQLException {

        List<ResourceHistory> historyList = new ArrayList<>();

        String sql = """
                SELECT
                    h.history_id,
                    r.resource_name,
                    u.username,
                    h.quantity,
                    h.action,
                    h.action_time
                FROM resource_history h
                JOIN resource r
                    ON h.resource_id = r.resource_id
                JOIN users u
                    ON h.user_id = u.user_id
                ORDER BY h.history_id DESC
                """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                ResourceHistory history = new ResourceHistory(
                        rs.getInt("history_id"),
                        rs.getString("resource_name"),
                        rs.getString("username"),
                        rs.getInt("quantity"),
                        rs.getString("action"),
                        rs.getTimestamp("action_time")
                );

                historyList.add(history);
            }
        }

        return historyList;
    }
}

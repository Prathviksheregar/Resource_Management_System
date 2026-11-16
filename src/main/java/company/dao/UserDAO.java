
package company.dao;

import company.config.DatabaseConnection;
import company.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // 1. FIND USER BY USERNAME
    public User findByUsername(String username)
            throws SQLException {

        String sql =
                "SELECT * FROM users WHERE username = ?";

        try (Connection con =
                     DatabaseConnection.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("role")
                    );
                }
            }
        }

        return null;
    }


    // 2. SAVE NEW USER
    public boolean saveUser(User user)
            throws SQLException {

        String sql =
                "INSERT INTO users " +
                        "(username, password_hash, role) " +
                        "VALUES (?, ?, ?)";

        try (Connection con =
                     DatabaseConnection.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());

            ps.setString(2, user.getPasswordHash());

            ps.setString(3, user.getRole());

            int rows = ps.executeUpdate();

            return rows > 0;
        }
    }


    // 3. UPDATE EXISTING USER
    public boolean updateUser(User user)
            throws SQLException {

        String sql =
                "UPDATE users SET " +
                        "username = ?, " +
                        "password_hash = ?, " +
                        "role = ? " +
                        "WHERE user_id = ?";

        try (Connection con =
                     DatabaseConnection.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());

            ps.setString(2, user.getPasswordHash());

            ps.setString(3, user.getRole());

            ps.setInt(4, user.getUserId());

            int rows = ps.executeUpdate();

            return rows > 0;
        }
    }
}

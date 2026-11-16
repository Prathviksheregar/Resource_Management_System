
package company.service;

import company.dao.UserDAO;
import company.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO;

    // Constructor
    public AuthService() {
        this.userDAO = new UserDAO();
    }


    // 1. REGISTER NEW EMPLOYEE
    public boolean registerEmployee(
            String username, String password)
            throws SQLException {

        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty"
            );
        }

        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters"
            );
        }

        username = username.trim();

        // Check whether username already exists
        if (userDAO.findByUsername(username) != null) {
            return false;
        }

        // Hash password using BCrypt
        String hashedPassword =
                BCrypt.hashpw(password, BCrypt.gensalt(12));

        // Create User object
        User user = new User(
                0,
                username,
                hashedPassword,
                "USER"
        );

        // Save user in MySQL
        return userDAO.saveUser(user);
    }


    // 2. LOGIN AUTHENTICATION
    public User login(
            String username, String password)
            throws SQLException {

        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return null;
        }

        // Find user in database
        User user =
                userDAO.findByUsername(username.trim());

        // Check whether user exists
        if (user == null) {
            return null;
        }

        // Verify entered password with stored hash
        boolean passwordMatches =
                BCrypt.checkpw(
                        password,
                        user.getPasswordHash()
                );

        if (passwordMatches) {
            return user;
        }

        return null;
    }
}

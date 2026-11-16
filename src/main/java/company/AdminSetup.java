
package company;

import company.dao.UserDAO;
import company.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Scanner;

public class AdminSetup {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter admin username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter admin password: ");
            String password = scanner.nextLine();

            if (username.isEmpty() || password.length() < 12) {
                System.out.println(
                        "Username is required and password must be at least 12 characters."
                );
                return;
            }

            UserDAO userDAO = new UserDAO();

            // Check duplicate username
            if (userDAO.findByUsername(username) != null) {
                System.out.println("Username already exists.");
                return;
            }

            // Hash password
            String hashedPassword = BCrypt.hashpw(
                    password,
                    BCrypt.gensalt(12)
            );

            // Create ADMIN user
            User admin = new User(
                    0,
                    username,
                    hashedPassword,
                    "ADMIN"
            );

            if (userDAO.saveUser(admin)) {
                System.out.println(
                        "Admin account created successfully!"
                );
            } else {
                System.out.println(
                        "Admin account creation failed."
                );
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


package company.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/company_db";

    private static final String USER =
            System.getenv("DB_USER");

    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");

    public static Connection getConnection()
            throws SQLException {

        if (USER == null || PASSWORD == null) {
            throw new SQLException(
                    "Database environment variables are missing"
            );
        }

        return DriverManager.getConnection(
                URL, USER, PASSWORD
        );
    }
}

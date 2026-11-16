
package company.service;

import company.dao.ReportExportDAO;
import company.model.User;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ReportExportService {

    private final ReportExportDAO exportDAO =
            new ReportExportDAO();

    public void exportResources(User user, File file)
            throws SQLException, IOException {

        checkAdmin(user);
        exportDAO.exportResources(file);
    }

    public void exportRequests(User user, File file)
            throws SQLException, IOException {

        checkAdmin(user);
        exportDAO.exportRequests(file);
    }

    private void checkAdmin(User user) {

        if (user == null ||
                !"ADMIN".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only administrators can export reports."
            );
        }
    }
}

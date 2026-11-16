
package company.service;

import company.dao.ReportDAO;
import company.model.ReportSummary;
import company.model.CategoryReport;
import company.model.User;

import java.sql.SQLException;
import java.util.List;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    private void checkAdmin(User user) {

        if (user == null ||
                !"ADMIN".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only administrators can view reports."
            );
        }
    }

    public ReportSummary getReportSummary(User user)
            throws SQLException {

        checkAdmin(user);

        return reportDAO.getReportSummary();
    }

    public List<CategoryReport> getCategoryReports(User user)
            throws SQLException {

        checkAdmin(user);

        return reportDAO.getCategoryReports();
    }
}

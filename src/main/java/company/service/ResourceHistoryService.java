
package company.service;

import company.dao.ResourceHistoryDAO;
import company.model.ResourceHistory;
import company.model.User;

import java.sql.SQLException;
import java.util.List;

public class ResourceHistoryService {

    private final ResourceHistoryDAO historyDAO =
            new ResourceHistoryDAO();

    public List<ResourceHistory> getAllHistory(User user)
            throws SQLException {

        if (user == null ||
                !"ADMIN".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only administrators can view resource history."
            );
        }

        return historyDAO.getAllHistory();
    }
}

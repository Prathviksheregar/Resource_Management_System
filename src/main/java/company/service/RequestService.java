
package company.service;

import company.dao.RequestDAO;
import company.model.ResourceRequest;
import company.model.User;

import java.sql.SQLException;
import java.util.List;

public class RequestService {

    private final RequestDAO requestDAO = new RequestDAO();

    // Employee submits a request
    public void submitRequest(
            User user,
            int resourceId,
            int requestedQuantity
    ) throws SQLException {

        if (user == null ||
                !"USER".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only employees can submit requests."
            );
        }

        if (resourceId <= 0 || requestedQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Invalid resource or quantity."
            );
        }

        requestDAO.submitRequest(
                user.getUserId(),
                resourceId,
                requestedQuantity
        );
    }

    // Admin views requests
    public List<ResourceRequest> getAllRequests(User user)
            throws SQLException {

        checkAdmin(user);

        return requestDAO.getAllRequests();
    }

    // Admin approves a request
    public void approveRequest(User user, int requestId)
            throws SQLException {

        checkAdmin(user);

        if (requestId <= 0) {
            throw new IllegalArgumentException("Invalid request ID.");
        }

        requestDAO.approveRequest(
                requestId,
                user.getUserId()
        );
    }

    // Admin rejects a request
    public void rejectRequest(User user, int requestId)
            throws SQLException {

        checkAdmin(user);

        if (requestId <= 0) {
            throw new IllegalArgumentException("Invalid request ID.");
        }

        requestDAO.rejectRequest(
                requestId,
                user.getUserId()
        );
    }

    private void checkAdmin(User user) {

        if (user == null ||
                !"ADMIN".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only administrators can manage requests."
            );
        }
    }

    // Employee views their own requests
    public List<ResourceRequest> getMyRequests(User user)
            throws SQLException {

        if (user == null ||
                !"USER".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only employees can view their own requests."
            );
        }

        return requestDAO.getRequestsByUserId(user.getUserId());
    }

}

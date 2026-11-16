
package company.service;

import company.dao.ResourceDAO;
import company.model.Resource;
import company.model.User;

import java.sql.SQLException;
import java.util.List;

public class ResourceService {

    private final ResourceDAO resourceDAO;

    public ResourceService() {
        resourceDAO = new ResourceDAO();
    }

    // Check admin permission
    private void checkAdmin(User user) {

        if (user == null ||
                !"ADMIN".equalsIgnoreCase(user.getRole())) {

            throw new SecurityException(
                    "Only administrators can manage resources."
            );
        }
    }

    // CREATE
    public boolean addResource(User user, Resource resource)
            throws SQLException {

        checkAdmin(user);
        validateResource(resource);

        return resourceDAO.addResource(resource);
    }

    // READ
    public List<Resource> getAllResources(User user)
            throws SQLException {

        checkAdmin(user);

        return resourceDAO.getAllResources();
    }

    // UPDATE
    public boolean updateResource(User user, Resource resource)
            throws SQLException {

        checkAdmin(user);
        validateResource(resource);

        if (resource.getResourceId() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid resource ID."
            );
        }

        return resourceDAO.updateResource(resource);
    }

    // DELETE
    public boolean deleteResource(User user, int resourceId)
            throws SQLException {

        checkAdmin(user);

        if (resourceId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid resource ID."
            );
        }

        return resourceDAO.deleteResource(resourceId);
    }

    // Validate resource information
    private void validateResource(Resource resource) {

        if (resource == null) {
            throw new IllegalArgumentException(
                    "Resource cannot be empty."
            );
        }

        if (resource.getResourceName() == null ||
                resource.getResourceName().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Resource name is required."
            );
        }

        if (resource.getCategory() == null ||
                resource.getCategory().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Category is required."
            );
        }

        if (resource.getQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Quantity cannot be negative."
            );
        }

        if (!Double.isFinite(resource.getCost()) ||
                resource.getCost() < 0) {

            throw new IllegalArgumentException(
                    "Cost must be a valid non-negative number."
            );
        }
    }

    // Employees and admins can view available resources
    public List<Resource> getAvailableResources(User user)
            throws SQLException {

        if (user == null ||
                (!"USER".equalsIgnoreCase(user.getRole())
                        && !"ADMIN".equalsIgnoreCase(user.getRole()))) {

            throw new SecurityException(
                    "You are not authorized to view resources."
            );
        }

        return resourceDAO.getAvailableResources();
    }

}


package company.model;

import java.sql.Timestamp;

public class ResourceRequest {

    private int requestId;
    private String username;
    private String resourceName;
    private int requestedQuantity;
    private String status;
    private Timestamp requestTime;

    public ResourceRequest(
            int requestId,
            String username,
            String resourceName,
            int requestedQuantity,
            String status,
            Timestamp requestTime
    ) {
        this.requestId = requestId;
        this.username = username;
        this.resourceName = resourceName;
        this.requestedQuantity = requestedQuantity;
        this.status = status;
        this.requestTime = requestTime;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getUsername() {
        return username;
    }

    public String getResourceName() {
        return resourceName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }
}

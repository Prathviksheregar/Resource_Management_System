
package company.model;

import java.sql.Timestamp;

public class ResourceHistory {

    private int historyId;
    private String resourceName;
    private String username;
    private int quantity;
    private String action;
    private Timestamp actionTime;

    public ResourceHistory(
            int historyId,
            String resourceName,
            String username,
            int quantity,
            String action,
            Timestamp actionTime
    ) {
        this.historyId = historyId;
        this.resourceName = resourceName;
        this.username = username;
        this.quantity = quantity;
        this.action = action;
        this.actionTime = actionTime;
    }

    public int getHistoryId() {
        return historyId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getUsername() {
        return username;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getAction() {
        return action;
    }

    public Timestamp getActionTime() {
        return actionTime;
    }
}


package company.model;

public class ReportSummary {

    private int totalResources;
    private int totalAvailableQuantity;
    private int pendingRequests;
    private int approvedRequests;
    private int rejectedRequests;
    private double totalInventoryValue;

    public ReportSummary(
            int totalResources,
            int totalAvailableQuantity,
            int pendingRequests,
            int approvedRequests,
            int rejectedRequests,
            double totalInventoryValue
    ) {
        this.totalResources = totalResources;
        this.totalAvailableQuantity = totalAvailableQuantity;
        this.pendingRequests = pendingRequests;
        this.approvedRequests = approvedRequests;
        this.rejectedRequests = rejectedRequests;
        this.totalInventoryValue = totalInventoryValue;
    }

    public int getTotalResources() {
        return totalResources;
    }

    public int getTotalAvailableQuantity() {
        return totalAvailableQuantity;
    }

    public int getPendingRequests() {
        return pendingRequests;
    }

    public int getApprovedRequests() {
        return approvedRequests;
    }

    public int getRejectedRequests() {
        return rejectedRequests;
    }

    public double getTotalInventoryValue() {
        return totalInventoryValue;
    }
}

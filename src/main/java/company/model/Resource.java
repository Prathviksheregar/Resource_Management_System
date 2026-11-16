
package company.model;

public class Resource {

    private int resourceId;
    private String resourceName;
    private String category;
    private String timeline;
    private int quantity;
    private double cost;

    public Resource(
            int resourceId,
            String resourceName,
            String category,
            String timeline,
            int quantity,
            double cost
    ) {
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.category = category;
        this.timeline = timeline;
        this.quantity = quantity;
        this.cost = cost;
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getCategory() {
        return category;
    }

    public String getTimeline() {
        return timeline;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCost() {
        return cost;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}

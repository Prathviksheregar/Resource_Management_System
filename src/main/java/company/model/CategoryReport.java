
package company.model;

public class CategoryReport {

    private String category;
    private int resourceCount;
    private int availableQuantity;
    private double inventoryValue;

    public CategoryReport(
            String category,
            int resourceCount,
            int availableQuantity,
            double inventoryValue
    ) {
        this.category = category;
        this.resourceCount = resourceCount;
        this.availableQuantity = availableQuantity;
        this.inventoryValue = inventoryValue;
    }

    public String getCategory() {
        return category;
    }

    public int getResourceCount() {
        return resourceCount;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public double getInventoryValue() {
        return inventoryValue;
    }
}

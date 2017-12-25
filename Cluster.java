import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private List<Item> items = new ArrayList<>();

    private Item centroid = null;
    
    public void add(Item item) {
        items.add(item);
    }

    public void remove(Item item) {
        items.remove(item);
    }

    public void clear() {
        items.clear();
    }

    public void calculateCentroid() {
        if (items.isEmpty()) {
            return;
        }
        this.centroid = new Item();

        int featureCount = items.get(0).featureCount();
        for (int i = 0; i < featureCount; i++) {
            double featureSum = 0.0;
            for (Item item : items) {
                featureSum += item.get(i);
            }
            this.centroid.add(featureSum / (double)featureCount);
        }
    }

    /**
     * @return the centroid
     */
    public Item getCentroid() {
        return centroid;
    }

    /**
     * @param centroid the centroid to set
     */
    public void setCentroid(Item centroid) {
        this.centroid = centroid;
    }

    /**
     * @return the items
     */
    public List<Item> getItems() {
        return items;
    }
}
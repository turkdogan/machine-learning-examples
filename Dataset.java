import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.PieChart.Data;

public class Dataset {

    private List<Item> items = new ArrayList<>();

    public void add(Item item) {
        items.add(item);
    }

    /**
     * @return the items
     */
    public List<Item> getItems() {
        return items;
    }

    public void standardize() {
        throw new UnsupportedOperationException("standardize not implemented yet!");
    }
}
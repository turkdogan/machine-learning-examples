package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dataset {

    private List<Item> items = new ArrayList<>();
    private List<Item> originalItems = new ArrayList<>();

    public void add(Item item) {
		items.add(item);
		originalItems.add(new Item(item));
    }

    /**
     * @return the items
     */
    public List<Item> getItems() {
        return items;
	}

	public List<Item> getOriginalItems() {
		return originalItems;
	}
    
    public void shuffle() {
    	Collections.shuffle(items);
    }

    public void standardize() {
    	if (items.isEmpty()) {
    		throw new RuntimeException("No data to serialize");
    	}
    	int featureCount = items.get(0).featureCount();
    	double featureMins[] = new double[featureCount];
    	double featureMaxes[] = new double[featureCount];
    	for (int f = 0; f < featureCount; f++) {
    		featureMins[f] = Double.MAX_VALUE;
    		featureMaxes[f] = Double.MIN_VALUE;
    	}
    	for (Item item : items) {
			for (int i = 0; i < featureCount; i++) {
				double value = item.get(i);
				if (value > featureMaxes[i]) {
					featureMaxes[i] = value;
				}
				if (value < featureMins[i]) {
					featureMins[i] = value;
				}
			}
    	}
    	for (Item item : items) {
			for (int f = 0; f < featureCount; f++) {
				double value = item.get(f);
				double standardizedValue = 
						(value - featureMins[f]) / (featureMaxes[f] - featureMins[f]);
				item.set(f, standardizedValue);
			}
    	}
    }
}
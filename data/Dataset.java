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
	
    public void normalize() {
    	if (items.isEmpty()) {
    		throw new RuntimeException("No data to normalize");
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
				double normalizedValue = 
						(value - featureMins[f]) / (featureMaxes[f] - featureMins[f]);
				item.set(f, normalizedValue);
			}
    	}
	}
	
	// feature is zero based
	public double mean(int feature) {
    	if (items.isEmpty()) {
    		throw new RuntimeException("No data to calculate mean for feature: " + feature);
		}
		if (feature < 0 || items.get(0).featureCount() < feature + 1) {
    		throw new IndexOutOfBoundsException("Wrong index: " + feature);
		}
		double mean = 0;
		for (Item item : items) {
			mean += item.get(feature);
		}
		return mean / (double)items.size();
	}

	// feature is zero based
	public double sd(int feature) {
    	if (items.isEmpty()) {
    		throw new RuntimeException("No data to calculate standard deviation for feature: " + feature);
		}
		if (feature < 0 || items.get(0).featureCount() < feature + 1) {
    		throw new IndexOutOfBoundsException("Wrong index: " + feature);
		}
		double mean = mean(feature);
		double total = 0.0;
		for (Item item : items) {
			total += Math.pow((item.get(feature) - mean), 2);
		}
		return Math.sqrt(total / items.size());
	}

    public void standardize() {
    	if (items.isEmpty()) {
    		throw new RuntimeException("No data to standardize");
    	}
		int featureCount = items.get(0).featureCount();

    	for (int f = 0; f < featureCount; f++) {
			double mean = mean(f);
			double sd = sd(f);

			for (Item item : items) {
				double value = item.get(f);
				double standardizedValue = (value - mean) / sd;
				item.set(f, standardizedValue);
			}
    	}
	}
	
	public int size() {
		return items.size();
	}
}
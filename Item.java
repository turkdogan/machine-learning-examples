import java.util.ArrayList;
import java.util.List;

public class Item {

    private List<Double> values = new ArrayList<>();
    
    public String name;

    public double get(int index) {
        if (index >= values.size()) {
            throw new IndexOutOfBoundsException();
        }
        return values.get(index);
    }
    
    public void set(int feature, double value) {
        if (feature < 0 || feature >= values.size()) {
            throw new IndexOutOfBoundsException();
        }
        values.set(feature, value);
    }

    public void add(Double value) {
        values.add(value);
    }

    public int featureCount() {
        return values.size();
    }

    public void removeAll() {
        values.clear();
    }

    public double sum() {
        return values.stream().reduce(0.0, Double::sum);
    }

    public double average() {
        return values.stream().mapToDouble(a->a).average().getAsDouble();
    }

    public int size() {
        return values.size();
    }

    public double distance(Item item) {
        if (item.size() != size()) {
            throw new RuntimeException("Items sizes not match!");
        }
        double dist = 0.0;
        for (int i = 0; i < size(); i++) {
            dist += Math.pow(values.get(i) - item.get(i), 2);
        }
        return Math.sqrt(dist);
    }

    /**
     * @return the values
     */
    public List<Double> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(List<Double> values) {
        this.values = values;
    }
    
    public String getName() {
		return name;
	}
    
    public void setName(String name) {
		this.name = name;
	}
}
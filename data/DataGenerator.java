package data;

import java.util.Random;

public class DataGenerator {

    public static Dataset generateUniformDataset(final Dataset dataset) {
        Item mins = new Item();
        Item maxes = new Item();
        int featureCount = dataset.getItems().get(0).featureCount();
        for (int f = 0; f < featureCount; f++) {
            mins.add(Double.MAX_VALUE);
            maxes.add(Double.MIN_VALUE);
        }
        // find minimum and maximum value of each feature
        for (Item item : dataset.getItems()) {
            for (int f = 0; f < featureCount; f++) {
                if (item.get(f) > maxes.get(f)) {
                    maxes.set(f, item.get(f));
                }
                if (item.get(f) < mins.get(f)) {
                    mins.set(f, item.get(f));
                }
            }
        }
        Random random = new Random();
        Dataset randomDataset = new Dataset();
        for (int i = 0; i < dataset.getItems().size(); i++) {
            Item item = new Item();
            for (int f = 0; f < featureCount; f++) {
                double rand = random.nextDouble() * (maxes.get(f) - mins.get(f)) + mins.get(f);
                item.add(rand);
            }
            randomDataset.add(item);
        }
        return randomDataset;
    }
}
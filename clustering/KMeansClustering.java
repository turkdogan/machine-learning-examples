package clustering;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import data.Dataset;
import data.Item;

public class KMeansClustering {

    public static final String CLUSTER_NUMBER_KEY = "ClusterNumber";
    public static final String ITERATION_COUNT = "IterationCount";

    public Cluster[] fit(Dataset dataset, Map<String, Object> metadata) {
        if (!metadata.containsKey(CLUSTER_NUMBER_KEY)) {
            throw new InvalidParameterException("No number of cluster given to KMeans Clustering!");
        }
        int clusterCount = (Integer)metadata.get(CLUSTER_NUMBER_KEY);
        if (!metadata.containsKey(ITERATION_COUNT)) {
            throw new InvalidParameterException("Iteration count not given!");
        }
        int iterationCount = (Integer)metadata.get(ITERATION_COUNT);
        Random random = new Random();
        Cluster[] clusters = new Cluster[clusterCount];

        List<Item> items = dataset.getItems();

        // assign random items as cluster centroids
        for (int c = 0; c < clusterCount; c++) {
            Cluster cluster = new Cluster();
            int randomIndex = random.nextInt(items.size());
            Item centroid = items.get(randomIndex);
            cluster.setCentroid(centroid);
            clusters[c] = cluster;
        }

        for (int it = 0; it < iterationCount; it++) {
            // for each data item calculate distance to cluster centers
            for (Item item : items) {
                double minClusterDistance = Double.MAX_VALUE;
                int minClusterIndex = -1;

                for (int c = 0; c < clusterCount; c++) {
                    Item centroid = clusters[c].getCentroid();

                    double cDist = centroid.distance(item);
                    if (cDist < minClusterDistance) {
                        minClusterDistance = cDist;
                        minClusterIndex = c;
                    }
                }
                // now assign to the closest cluster
                clusters[minClusterIndex].add(item);
            }
            for (Cluster cluster : clusters) {
                cluster.calculateCentroid();

                // do not remove in the last iteration
                // to be able to show the assigned items to clusters
                if (it < iterationCount - 1) {
                    cluster.clear();
                }
            }
        }
        return clusters;
    }
    
}
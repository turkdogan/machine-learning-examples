import java.security.InvalidParameterException;
import java.util.Map;

public class KMeansClustering extends UnsupervisedLearning {

    public static final String CLUSTER_NUMBER_KEY = "ClusterNumber";

    public void fit(Dataset dataset, Map<String, Object> metadata) {
        if (!metadata.containsKey(CLUSTER_NUMBER_KEY)) {
            throw new InvalidParameterException("No number of cluster given to KMeans Clustering!");
        }
        int clusterNumber = metadata.get(CLUSTER_NUMBER_KEY);

        // assign clusterNumber of item as cluster center

        // for number of iterations
        //  for each item:
        //   calculate cluster centers
        //   assign to nearest cluster
        //   re-calculate mean of the assigned cluster

        double [][]data = dataset.get();
        for (int i = 0; i < clusterNumber; i++) {
        }

        throw new UnsupportedOperationException("KMeans not implemented yet");
    }

}
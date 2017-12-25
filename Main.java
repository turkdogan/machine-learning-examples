import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void runIrisKMeansExperiment() {
        Dataset irisDataset = new IrisDataLoader().load();

        /*
        for (Item item : irisDataset.getItems()) {
            for (int i = 0; i < item.size(); i++) {
                System.out.print(item.get(i) + " ");
            }
            System.out.println();
        }
        */

        KMeansClustering kMeansClustering = new KMeansClustering();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(KMeansClustering.CLUSTER_NUMBER_KEY, 3);
        kMeansClustering.fit(irisDataset, metadata);
    }

    public static void main(String[] args) {
        runIrisKMeansExperiment();
    }

}
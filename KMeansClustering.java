import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

public class KMeansClustering extends UnsupervisedLearning {

    public static final String CLUSTER_NUMBER_KEY = "ClusterNumber";

    public void fit(Dataset dataset, Map<String, Object> metadata) {
        if (!metadata.containsKey(CLUSTER_NUMBER_KEY)) {
            throw new InvalidParameterException("No number of cluster given to KMeans Clustering!");
        }
        int clusterCount = (Integer)metadata.get(CLUSTER_NUMBER_KEY);

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

        int iterationCount = 100;
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
        for (int c = 0; c < clusterCount; c++) {
            for (Item item : clusters[c].getItems()) {
                System.out.println("Item " + item.getName() + " is in cluster: " + (c + 1));
            }
        }
        
        plotClusters(clusters);
    }
    
    private void plotClusters(Cluster[] clusters) {
	    XYChart chart = new XYChartBuilder().width(600).height(500).title("Gaussian Blobs").xAxisTitle("X").yAxisTitle("Y").build();

	    // Customize Chart
	    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
	    chart.getStyler().setChartTitleVisible(false);
	    chart.getStyler().setLegendPosition(LegendPosition.InsideSW);
	    chart.getStyler().setMarkerSize(16);
	    
	    
	    int clusterIndex = 1;
	    for (Cluster cluster : clusters) {
	    	List<Item> items = cluster.getItems();
			double []petalLength = new double[items.size()];
			double []petalWidth = new double[items.size()];
			
			for (int i = 0; i < items.size(); i++) {
				petalLength[i] = items.get(i).get(2);
				petalWidth[i] = items.get(i).get(3);
			}
			
			XYSeries series = chart.addSeries("Cluster: " + clusterIndex , petalLength, petalWidth);
			//chart.addSeries("Cluster 1", getGaussian(1000, 1, 10), getGaussian(1000, 1, 10));
			//XYSeries series = chart.addSeries("Gaussian Blob 2", getGaussian(1000, 1, 10), getGaussian(1000, 0, 5));
			//series.setMarker(SeriesMarkers.DIAMOND);
			
			clusterIndex++;
	    }
	    chart.setXAxisTitle("Standardized Petal Length");
	    chart.setXAxisTitle("Standardized Petal Width");
	    chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
	    
	    // Series

	    try {
			BitmapEncoder.saveBitmap(chart, "./Iris.png", BitmapFormat.PNG);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    new SwingWrapper(chart).displayChart();
    }
}
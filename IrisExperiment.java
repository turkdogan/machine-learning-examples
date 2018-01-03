import java.util.Map;
import java.util.Random;
import java.util.PrimitiveIterator.OfDouble;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import data.Dataset;
import data.Item;
import data.DataGenerator;
import clustering.Cluster;
import clustering.KMeansClustering;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

public class IrisExperiment {

    public void runKMeansClustering() {
        Dataset irisDataset = load();
        irisDataset.standardize();
        irisDataset.shuffle();

        KMeansClustering kMeansClustering = new KMeansClustering();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(KMeansClustering.CLUSTER_NUMBER_KEY, 3);
        metadata.put(KMeansClustering.ITERATION_COUNT, 200);
        Cluster []clusters = kMeansClustering.fit(irisDataset, metadata);

        printClusters(clusters);
        plotClusters(clusters);
    }

    private void printClusters(Cluster[] clusters) {
        int clusterIndex = 1;
        for (Cluster cluster : clusters) {
            for (Item item : cluster.getItems()) {
                System.out.println("Cluster: " + clusterIndex + " = " + item.getName());
            }
            clusterIndex++;
        }
    }

    public void runElbowMethod() {
        Dataset irisDataset = load();
        irisDataset.standardize();
        irisDataset.shuffle();

        KMeansClustering kMeansClustering = new KMeansClustering();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(KMeansClustering.ITERATION_COUNT, 200);

        Map<Integer, Double> elbowMap = new HashMap<>();

        for (int k = 2; k < 10; k++) {
            metadata.put(KMeansClustering.CLUSTER_NUMBER_KEY, k);
            Cluster []clusters = kMeansClustering.fit(irisDataset, metadata);
            double totalIntraValue = 0.0;
            for (Cluster cluster: clusters) {
                totalIntraValue += cluster.intraClusterDistance();
            }
            elbowMap.put(k, totalIntraValue);
        }
        plotElbows(elbowMap);
    }

    public void runSilhouetteMethod() {
        Dataset irisDataset = load();
        irisDataset.standardize();
        irisDataset.shuffle();

        KMeansClustering kMeansClustering = new KMeansClustering();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(KMeansClustering.ITERATION_COUNT, 200);

        int K = 10;
        double []avgSilhouettes = new double[K];

        for (int k = 2; k < K; k++) {
            metadata.put(KMeansClustering.CLUSTER_NUMBER_KEY, k);
            Cluster []clusters = kMeansClustering.fit(irisDataset, metadata);
            double totalSilhouette = 0.0;
            for (Cluster cluster : clusters) {
                double neerestDistance = Double.MAX_VALUE;
                Cluster nearestCluster = null;
                for (Cluster otherCluster : clusters) {
                    if (cluster != otherCluster) {
                        Double dist = cluster.getCentroid().distance(otherCluster.getCentroid());
                        if (dist < neerestDistance) {
                            nearestCluster = otherCluster;
                            neerestDistance = dist;
                        }
                    }
                }
                double a = 0.0;
                double b = 0.0;
                for (Item item : cluster.getItems()) {
                    for (Item otherItem : cluster.getItems()) {
                        a += item.distance(otherItem);
                    }
                }
                for (Item item : cluster.getItems()) {
                    for (Item otherItem : nearestCluster.getItems()) {
                        b += item.distance(otherItem);
                    }
                }
                double silhouette = (b - a) / (Math.max(a, b));
                totalSilhouette += silhouette;
            }
            avgSilhouettes[k] = totalSilhouette / irisDataset.size();
        }
        for (int k = 2; k < K; k++) {
            System.out.println("k: " + k + " = " + avgSilhouettes[k]);
        }
    }

    private void plotSilhouette(Map<Integer, List<Double>> silhouetteMap, List<Double> silhouetteAverages) {
    }

    public void runGapStatistics() {
        Dataset irisDataset = load();
        irisDataset.standardize();
        irisDataset.shuffle();

        KMeansClustering kMeansClustering = new KMeansClustering();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(KMeansClustering.ITERATION_COUNT, 200);

        int K = 10;
        int B = 10;
        double[] wks = new double[K];
        double[] wkbs = new double[K];
        
        double sk[] = new double[B];

        for (int k = 1; k < K; k++) {
            metadata.put(KMeansClustering.CLUSTER_NUMBER_KEY, k);
            Cluster[] clusters = kMeansClustering.fit(irisDataset, metadata);
            double wk = 0;
            for (Cluster cluster : clusters) {
                wk  += cluster.intraClusterDistance();
            }
            wks[k] += Math.log(wk);

            double wkkB[] = new double[B];
            for (int i = 0; i < B; i++) {
                Dataset uniformRandomDataset = DataGenerator.generateUniformDataset(irisDataset);

                double wkB = 0;
                Cluster[] clustersB = kMeansClustering.fit(uniformRandomDataset, metadata);
                for (Cluster cluster : clustersB) {
                    wkB  += cluster.intraClusterDistance();
                }
                wkkB[i] = Math.log(wkB);
            }
            double sumWkkB = 0;
            for (int i = 0; i < B; i++) {
                sumWkkB += wkkB[i];
            }
            sumWkkB /= (double)B;
            wkbs[k] = sumWkkB;

            double summ = 0;
            for (int i = 0; i < B; i++) {
                summ += Math.pow(wkbs[k] - wkkB[i], 2);
            }
            sk[k] = Math.sqrt(summ / (double)B);
        }
        for (int k = 1; k < K; k++) {
            sk[k] = sk[k] * Math.sqrt(1 + 1.0/B);
        }

        double []gaps = new double[K];
        for (int k = 1; k < K - 1; k++) {
            double gap = wkbs[k] - wks[k];
            gaps[k] = gap;
        }
        int idealK = K;
        for (int k = 1; k < K - 1; k++) {
            if (gaps[k] >= gaps[k+1] - sk[k + 1]) {
                idealK = k;
                break;
            }
        }
        System.out.println("GAP statistics, ideak K: " + idealK);

        for (int k = 1; k < K; k++) {
            System.out.println(gaps[k] + " " + sk[k]);
        }
    }

    private Dataset load() {
        Dataset dataset = new Dataset();

        String fileName = "iris.data";
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(line -> addIrisItem(dataset, line));
		} catch (IOException e) {
			e.printStackTrace();
		}
        return dataset;
    }

    private void addIrisItem(Dataset dataset, String line) {
        int index = 0;
        Item item = new Item();
        for (String s : line.split(",")) {
            if (index == 4) {
            	item.setName(s);
                break;
            }
            item.add(Double.parseDouble(s));
            index++;
        }
        dataset.add(item);
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
			clusterIndex++;
	    }
	    chart.setXAxisTitle("Standardized Petal Length");
	    chart.setYAxisTitle("Standardized Petal Width");
	    chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
	    
	    // Series
	    try {
            BitmapEncoder.saveBitmapWithDPI(chart, "./iris-k-means", BitmapFormat.PNG, 300);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    new SwingWrapper(chart).displayChart();
    }

    void plotElbows(Map<Integer, Double> elbowMap) {
        double[] xData = new double[elbowMap.size()];
        double[] yData = new double[elbowMap.size()];

        int index = 0;
        for (Integer k : elbowMap.keySet()) {
            xData[index] = (double)k;
            yData[index] = elbowMap.get(k);

            index++;
        }
    
        // Create Chart
        XYChart chart = QuickChart.getChart("Iris Elbow Method", "K", "Intra-Cluster Total Distance", "Intra-Total Euclidean", xData, yData);
    
        // Show it
        new SwingWrapper(chart).displayChart();
    
        // Save it

	    try {
            BitmapEncoder.saveBitmapWithDPI(chart, "./iris-k-means-elbow", BitmapFormat.PNG, 300);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
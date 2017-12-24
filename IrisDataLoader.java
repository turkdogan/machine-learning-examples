
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class IrisDataLoader implements DataLoader {

    // Probably one of the ugliest quick solution!
    Dataset dataset = new Dataset(150, 4);
    // do not do it!
    int row = 0;

    public Dataset load() {
        String fileName = "iris.data";
        int row = 0;
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(line -> addIrisItem(line));
		} catch (IOException e) {
			e.printStackTrace();
		}
        return dataset;
    }

    private void addIrisItem(String line) {
        int index = 0;
        for (String s : line.split(",")) {
            dataset.set(row, index, Double.parseDouble(s));
            index++;
            if (index == 4) {
                return;
            }
        }
        row++;
    }
}
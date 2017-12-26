
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class IrisDataLoader implements DataLoader {

    // Probably one of the ugliest quick solution!
    Dataset dataset = new Dataset();

    public Dataset load() {
        String fileName = "iris.data";
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(line -> addIrisItem(line));
		} catch (IOException e) {
			e.printStackTrace();
		}
        return dataset;
    }

    private void addIrisItem(String line) {
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
}
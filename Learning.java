import java.util.Map;

public interface Learning {

    void fit(Dataset dataset, Map<String, Object> metadata);
}
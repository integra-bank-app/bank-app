package clf.integra.backend.model;

import org.springframework.stereotype.Component;

@Component
public class RandomUtils {
    public double random() {
        return Math.random();
    }
}

package clf.integra.backend.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FeeTaxJob {

    @Scheduled(fixedRate = 500)
    public void run() {
        //execute get tax and write taxes to the db
    }
}

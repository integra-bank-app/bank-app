package clf.integra.backend.job;

import clf.integra.backend.service.FeeTaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeeTaxJob {

    private final FeeTaxService feeTaxService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Bucharest")
    public void run() {
        feeTaxService.feeAndTaxUsers();
    }
}

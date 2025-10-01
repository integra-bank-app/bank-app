package clf.integra.backend.job;

import clf.integra.backend.service.FeeTaxService;
import clf.integra.backend.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CronJobs {

    private final FeeTaxService feeTaxService;
    private final InvestmentService investmentService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Bucharest")
    public void runFeeTaxJob() {
        feeTaxService.feeAndTaxUsers();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Bucharest")
    public void runInvestmentJob() {
        investmentService.updateBalanceByRisk();
    }

}

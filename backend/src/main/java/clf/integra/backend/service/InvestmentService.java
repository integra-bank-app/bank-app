package clf.integra.backend.service;

import clf.integra.backend.model.Investment;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.InvestmentsRepository;
import clf.integra.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvestmentService {
    private final InvestmentsRepository investmentsRepository;
    private final UserRepository userRepository;

    @Transactional
    public UUID createInvestment(int risk, Double balance, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Investment investment = Investment.builder().risk(risk).balance(balance).user(user).build();
        investmentsRepository.save(investment);

        return investment.getId();
    }

    public Investment getInvestmentByUserId(UUID userId, UUID investmentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return user.getInvestments().stream()
                .filter(investment -> investmentId.equals(investment.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Investment not found with id: " + investmentId + " for user: " + userId));
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // every day at midnight
    public void updateBalanceByRisk() {
        List<Investment> investments = investmentsRepository.findAll();
        for (Investment investment : investments) {
            int risk = investment.getRisk();
            Double balance = investment.getBalance();

            Random rand = new Random();
            // formula: 4*risk% chance to decrease by 2*risk%, and 1-(4*risk)% to increase by 2*risk%

            double probability = 4 * risk / 100.0;
            double random = rand.nextDouble();
            double changeAmount = 2 * risk / 100.0 * balance;

            if (random < probability) {
                if (balance - changeAmount < 0) // in case the change would bring a negative balance
                    balance = 0.0;
                else
                    balance -= changeAmount;
            } else {
                balance += changeAmount;
            }

            investment.setBalance(balance);
        }
        investmentsRepository.saveAll(investments);
    }
}

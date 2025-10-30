package clf.integra.backend.service;

import clf.integra.backend.dto.InvestmentDTO;
import clf.integra.backend.dto.InvestmentHistoryDTO;
import clf.integra.backend.mapper.InvestmentMapper;
import clf.integra.backend.model.Investment;
import clf.integra.backend.model.InvestmentHistory;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.InvestmentHistoryRepository;
import clf.integra.backend.repository.InvestmentsRepository;
import clf.integra.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvestmentService {
    private final InvestmentsRepository investmentsRepository;
    private final UserRepository userRepository;
    private final InvestmentHistoryRepository investmentHistoryRepository;

    @Transactional
    public UUID createInvestment(int risk, Double balance, UUID userId) {
        if (balance == null)
            throw new IllegalArgumentException("Balance object is set to null");

        if (risk < 1 || risk > 10)
            throw new IllegalArgumentException("Invalid risk, it has to be between 1 and 10");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Investment investment = Investment.builder()
                .risk(risk)
                .balance(balance)
                .user(user)
                .build();
        investmentsRepository.save(investment);

        investmentHistoryRepository.save(
                InvestmentHistory.builder()
                        .investment(investment)
                        .balance(balance)
                        .date(LocalDateTime.now())
                        .build()
        );

        return investment.getId();
    }

    public InvestmentDTO getInvestmentByUserId(UUID userId, UUID investmentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return user.getInvestments().stream()
                .filter(investment -> investmentId.equals(investment.getId()))
                .findFirst()
                .map(InvestmentMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Investment not found with id: " + investmentId + " for user: " + userId));
    }

    @Transactional
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

            double roundedBalance = BigDecimal.valueOf(balance)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            investment.setBalance(roundedBalance);

            investmentHistoryRepository.save(
                    InvestmentHistory.builder()
                            .investment(investment)
                            .balance(roundedBalance)
                            .date(LocalDateTime.now())
                            .build()
            );
        }
        investmentsRepository.saveAll(investments);
    }

    public List<InvestmentDTO> getAllInvestmentsByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<Investment> investments = investmentsRepository.findAllByUserId(userId);

        return investments.stream()
                .map(inv -> new InvestmentDTO(inv.getId(),inv.getRisk(), inv.getBalance(), inv.getCreatedDate()))
                .toList();
    }

    public List<InvestmentHistoryDTO> getInvestmentHistoryByUser(UUID userId) {
        List<InvestmentHistory> history = investmentHistoryRepository.findAllByInvestment_User_Id(userId);

        return history.stream()
                .map(h -> new InvestmentHistoryDTO(
                        h.getInvestment().getId().toString(),
                        h.getBalance(),
                        h.getDate()
                ))
                .toList();
    }
}

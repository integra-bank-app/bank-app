package clf.integra.backend.service;

import clf.integra.backend.dto.FeeTaxTransactionDTO;
import clf.integra.backend.dto.UserTransactionDTO;
import clf.integra.backend.mapper.FeeTaxTransactionMapper;
import clf.integra.backend.mapper.UserTransactionHistoryMapper;
import clf.integra.backend.model.FeeTaxTransaction;
import clf.integra.backend.model.NotificationType;
import clf.integra.backend.model.TransactionType;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.FeeTaxTransactionRepository;
import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeTaxService {

    private final UserRepository userRepository;
    private final FeeTaxTransactionRepository taxRepository;
    private final NotificationService notificationService;

    public List<FeeTaxTransactionDTO> getFeeTaxesFromLastNDays(int lastNDays) {
        LocalDateTime now = LocalDateTime.now();
        return taxRepository.findByCreatedAtAfter(now.minusDays(lastNDays)).stream().map(FeeTaxTransactionMapper::toDTO).collect(Collectors.toList());
    }

    public void feeAndTaxUsers() throws IOException {
        userRepository.findAll().forEach(user -> {
            double deductedValue = deductFeeAndTax(user);
            taxRepository.save(
                    FeeTaxTransaction.builder()
                            .user(user)
                            .amount(deductedValue)
                            .build()
            );
            try {
                notificationService.sendNotificationToUser(NotificationType.SUCCESS," A fee and tax of $" + deductedValue + " has been deducted from your account due to low balance.", user.getId());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public double deductFeeAndTax(User user) {
        double balance = user.getAccounts().getFirst().getBalance();
        double tax = calculateFee(balance);
        user.getAccounts().getFirst().setBalance(balance - tax);
        userRepository.save(user);
        return tax;
    }

    public double calculateFee(double balance) {
        if (balance < 0) return 0;
        if (balance < 100) return balance * 0.1;
        return 10;
    }

    public List<UserTransactionDTO> getUserFeeTaxesTransaction(UUID userId) {
        return taxRepository.findAll().stream()
                .filter(f -> f.getUser().getId().equals(userId))
                .map(f -> UserTransactionHistoryMapper.fromFeeTaxTransaction(f,userId))
                .collect(Collectors.toList());
    }
}

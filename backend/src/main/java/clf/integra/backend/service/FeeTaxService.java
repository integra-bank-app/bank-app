package clf.integra.backend.service;

import clf.integra.backend.model.FeeTaxTransaction;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.FeeTaxTransactionRepository;
import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeeTaxService {

    private final UserRepository userRepository;
    private final FeeTaxTransactionRepository taxRepository;

    public void feeAndTaxUsers() {
        userRepository.findAll().forEach(user -> {
            double deductedValue = deductFeeAndTax(user);
            taxRepository.save(new FeeTaxTransaction(user, deductedValue));

        });
    }

    public double deductFeeAndTax(User user) {
        double balance = user.getBalance();
        double tax = calculateFee(balance);
        user.setBalance(balance - tax);
        userRepository.save(user);
        return tax;
    }

    public double calculateFee(double balance) {
        if (balance < 0) return 0;
        if (balance < 100) return balance * 0.1;
        return 10;
    }
}

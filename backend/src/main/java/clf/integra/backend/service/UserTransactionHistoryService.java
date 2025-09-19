package clf.integra.backend.service;

import clf.integra.backend.dto.UserTransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserTransactionHistoryService {
    private final TransactionService transactionService;
    private final FeeTaxService feeTaxService;

    public List<UserTransactionDTO> getTransactionHistory(UUID userId) {
        List<UserTransactionDTO> regularTransaction= transactionService.getUserTransaction(userId);
        List<UserTransactionDTO> feeTransaction= feeTaxService.getUserFeeTaxesTransaction(userId);

        return Stream.concat(regularTransaction.stream(),feeTransaction.stream())
                .sorted((t1,t2) -> t2.timestamp().compareTo(t1.timestamp()))
                .toList();
    }
}

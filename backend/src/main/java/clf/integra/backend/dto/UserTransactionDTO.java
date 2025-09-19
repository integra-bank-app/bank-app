package clf.integra.backend.dto;

import clf.integra.backend.model.TransactionType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserTransactionDTO(
        UUID transactionId,
        TransactionType transactionType,
        double amount,
        LocalDateTime timestamp,
        String description,
        UUID fromUserId,
        UUID toUserId
) {
}

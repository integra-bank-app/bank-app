package clf.integra.backend.dto;

import java.time.LocalDateTime;

public record FeeTaxTransactionDTO(
        UserDTO user,
        double amount,
        LocalDateTime appliedAt
) {
}

package clf.integra.backend.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FeeTaxTransactionDTO(
        UserDTO user,
        double amount,
        LocalDateTime createdAt
) {
}

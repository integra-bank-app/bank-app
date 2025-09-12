package clf.integra.backend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionDTO(
        UUID id,

        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Amount is required")
        Double amount,

        @NotBlank(message = "Transaction type is required")
        String transactionType,

        LocalDateTime timestamp,

        String description,

        UUID referenceTransactionId
) {}
package clf.integra.backend.dto;

import clf.integra.backend.model.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TransactionDTO(
        UUID id,

        @NotNull(message = "Amount is required")
        Double amount,

        @NotNull(message = "Timestamp is required")
        LocalDateTime timestamp,

        @NotNull(message = "User ID is required")
        UUID userId,

        @NotBlank(message = "User name is required")
        String userName,

        @NotNull(message = "Transaction type is required")
        TransactionType type,

        String description
) {}
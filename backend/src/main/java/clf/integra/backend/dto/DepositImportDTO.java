package clf.integra.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DepositImportDTO(
        @NotNull
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        Double amount,

        @NotNull
        @DecimalMin(value = "0.0", message = "Interest rate must be non-negative")
        Double interest_rate,

        @NotNull(message = "UserId is required")
        UUID userId
) {}

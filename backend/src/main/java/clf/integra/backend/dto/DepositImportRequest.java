package clf.integra.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DepositImportRequest(
        @NotNull(message = "Deposit list cannot be null")
        @NotEmpty(message = "Deposit list cannot be empty")
        @Valid
        List<DepositImportDTO> depositImports
) {}

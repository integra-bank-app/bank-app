package clf.integra.backend.dto;

import java.util.UUID;

public record DepositDTO(UUID id, Double interest_rate, Double amount) {
}

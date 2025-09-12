package clf.integra.backend.dto;

import java.util.UUID;

public record DepositsDTO(UUID id, Double interest_rate, Double amount) {
}

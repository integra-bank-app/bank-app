package clf.integra.backend.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record InvestmentDTO(UUID id, int risk, Double balance, Instant createdDate) {
}

package clf.integra.backend.dto;

import lombok.Builder;

@Builder
public record InvestmentDTO(int risk, Double balance) {
}

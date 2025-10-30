package clf.integra.backend.dto;

import java.util.UUID;

import lombok.Builder;

@Builder
public record DepositsExportDTO(UUID id, Double interest_rate, Double amount, UUID user_id, UserDTO userDTO) {
}

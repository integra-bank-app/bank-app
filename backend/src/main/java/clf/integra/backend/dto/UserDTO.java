package clf.integra.backend.dto;

import lombok.Builder;

@Builder
public record UserDTO(String firstName, String middleName, String lastName) {
}

package clf.integra.backend.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserWithBranchDTO(String firstName, String middleName, String lastName, UUID branchId) {
}

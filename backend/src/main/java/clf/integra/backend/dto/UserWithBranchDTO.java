package clf.integra.backend.dto;

import clf.integra.backend.model.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserWithBranchDTO(String firstName, String middleName, String lastName, UUID branchId,
                                String email, String password, User.Role role) {
}

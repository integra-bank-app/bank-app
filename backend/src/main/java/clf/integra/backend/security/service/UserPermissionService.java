package clf.integra.backend.security.service;

import clf.integra.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("userPermissionService")
@RequiredArgsConstructor
public class UserPermissionService {

    public boolean canAccessUserData(UUID userId, Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        User user = (User) authentication.getPrincipal();
        return user.getId().equals(userId);
    }
}
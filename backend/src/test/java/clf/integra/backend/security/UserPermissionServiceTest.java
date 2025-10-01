package clf.integra.backend.security;

import clf.integra.backend.model.User;
import clf.integra.backend.security.service.UserPermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserPermissionServiceTest {

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserPermissionService userPermissionService;

    private UUID testUserId;
    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testUser = User.builder()
                .id(testUserId)
                .firstName("Test")
                .lastName("User")
                .email("testUser@gmail.com")
                .password("password123")
                .role(User.Role.USER)
                .build();

        adminUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Admin")
                .lastName("User")
                .email("adminUser@gmail.com")
                .password("adminPassword123")
                .role(User.Role.ADMIN)
                .build();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCanAccessUserData_withAdminRole_returnTrue() {
        Collection<GrantedAuthority> adminAuthorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) adminAuthorities);

        boolean result = userPermissionService.canAccessUserData(testUserId, authentication);

        assertTrue(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCanAccessUserData_withSameUserId_returnTrue() {
        Collection<GrantedAuthority> userAuthorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) userAuthorities);
        when(authentication.getPrincipal()).thenReturn(testUser);

        boolean result = userPermissionService.canAccessUserData(testUser.getId(), authentication);

        assertTrue(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCanAccessUserData_withDifferentUserId_returnFalse() {
        UUID differentUserId = UUID.randomUUID();
        Collection<GrantedAuthority> userAuthorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) userAuthorities);
        when(authentication.getPrincipal()).thenReturn(testUser);

        boolean result = userPermissionService.canAccessUserData(differentUserId, authentication);

        assertFalse(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCanAccessUserData_withUserRoleButNoAccess_returnFalse() {
        UUID otherUserId = UUID.randomUUID();

        Collection<GrantedAuthority> userAuthorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) userAuthorities);
        when(authentication.getPrincipal()).thenReturn(testUser);

        boolean result = userPermissionService.canAccessUserData(otherUserId, authentication);

        assertFalse(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCanAccessUserData_withAdminAndDifferentUserId_returnTrue() {
        UUID otherUserId = UUID.randomUUID();
        Collection<GrantedAuthority> adminAuthorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) adminAuthorities);

        boolean result = userPermissionService.canAccessUserData(otherUserId, authentication);

        assertTrue(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCanAccessUserData_withMultipleRoles_returnTrue() {
        Collection<GrantedAuthority> multipleAuthorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        when(authentication.getAuthorities()).thenReturn((Collection) multipleAuthorities);

        boolean result = userPermissionService.canAccessUserData(testUserId, authentication);

        assertTrue(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCanAccessUserData_withNoRolesAndDifferentUserId_returnFalse() {
        UUID differentUserId = UUID.randomUUID();
        Collection<GrantedAuthority> noAuthorities = Arrays.asList();
        when(authentication.getAuthorities()).thenReturn((Collection) noAuthorities);
        when(authentication.getPrincipal()).thenReturn(testUser);

        boolean result = userPermissionService.canAccessUserData(differentUserId, authentication);

        assertFalse(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCanAccessUserData_withNoAdminRoleButSameUserId_returnTrue() {
        Collection<GrantedAuthority> noAdminAuthorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) noAdminAuthorities);
        when(authentication.getPrincipal()).thenReturn(testUser);

        boolean result = userPermissionService.canAccessUserData(testUser.getId(), authentication);

        assertTrue(result);
    }
}
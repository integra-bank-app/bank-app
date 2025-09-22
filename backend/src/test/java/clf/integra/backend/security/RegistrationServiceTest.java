package clf.integra.backend.security;

import clf.integra.backend.model.Branch;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.BranchRepository;
import clf.integra.backend.repository.UserRepository;
import clf.integra.backend.security.DTO.RegisterRequest;
import clf.integra.backend.security.model.AuthUser;
import clf.integra.backend.security.repository.AuthUserRepository;
import clf.integra.backend.security.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    private RegisterRequest registerRequest;
    private Branch mockBranch;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setBranchId(UUID.randomUUID());
        registerRequest.setRequestedRole(AuthUser.Role.USER);

        mockBranch = new Branch();

        mockUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .branch(mockBranch)
                .build();
    }

    @Test
    void registerUser_WithValidData_ShouldCreateUserAndAuthUser() {
        when(authUserRepository.existsByUsername("testuser")).thenReturn(false);
        when(authUserRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(branchRepository.findById(registerRequest.getBranchId())).thenReturn(Optional.of(mockBranch));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(authUserRepository.save(any(AuthUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthUser result = registrationService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encoded-password", result.getPassword());
        assertEquals(AuthUser.Role.USER, result.getRole());
        assertEquals(mockUser.getId(), result.getUserId());

        verify(userRepository).save(any(User.class));
        verify(authUserRepository).save(any(AuthUser.class));
    }

    @Test
    void registerUser_WithExistingUsername_ShouldThrowException() {
        when(authUserRepository.existsByUsername("testuser")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerUser(registerRequest));

        assertEquals("Username is already taken!", exception.getMessage());
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        when(authUserRepository.existsByUsername("testuser")).thenReturn(false);
        when(authUserRepository.existsByEmail("test@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerUser(registerRequest));

        assertEquals("Email is already in use!", exception.getMessage());
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void registerUser_WithInvalidBranch_ShouldThrowException() {
        when(authUserRepository.existsByUsername("testuser")).thenReturn(false);
        when(authUserRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(branchRepository.findById(registerRequest.getBranchId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerUser(registerRequest));

        assertTrue(exception.getMessage().contains("Branch not found"));
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void registerUser_WithIntegraBankEmail_ShouldAssignAdminRole() {
        registerRequest.setEmail("admin@integrabank.com");
        registerRequest.setRequestedRole(AuthUser.Role.ADMIN);

        when(authUserRepository.existsByUsername("testuser")).thenReturn(false);
        when(authUserRepository.existsByEmail("admin@integrabank.com")).thenReturn(false);
        when(branchRepository.findById(registerRequest.getBranchId())).thenReturn(Optional.of(mockBranch));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(authUserRepository.save(any(AuthUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthUser result = registrationService.registerUser(registerRequest);

        assertEquals(AuthUser.Role.ADMIN, result.getRole());
    }

    @Test
    void registerUser_WithNonIntegraBankEmailRequestingAdmin_ShouldAssignUserRole() {
        registerRequest.setRequestedRole(AuthUser.Role.ADMIN);

        when(authUserRepository.existsByUsername("testuser")).thenReturn(false);
        when(authUserRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(branchRepository.findById(registerRequest.getBranchId())).thenReturn(Optional.of(mockBranch));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(authUserRepository.save(any(AuthUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthUser result = registrationService.registerUser(registerRequest);

        assertEquals(AuthUser.Role.USER, result.getRole());
    }
}
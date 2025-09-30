package clf.integra.backend.security;

import clf.integra.backend.model.Branch;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.BranchRepository;
import clf.integra.backend.repository.UserRepository;
import clf.integra.backend.security.DTO.RegisterRequestDTO;
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
    private BranchRepository branchRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    private RegisterRequestDTO registerRequestDTO;
    private Branch mockBranch;
    private User mockUser;

    @BeforeEach
    void setUp() {
        registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setEmail("test@example.com");
        registerRequestDTO.setPassword("password123");
        registerRequestDTO.setFirstName("John");
        registerRequestDTO.setLastName("Doe");
        registerRequestDTO.setBranchId(UUID.randomUUID());
        registerRequestDTO.setRequestedRole(User.Role.USER);

        mockBranch = new Branch();

        mockUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .branch(mockBranch)
                .email("test@example.com")
                .password("password123")
                .role(User.Role.USER)
                .build();
    }

    @Test
    void registerUser_WithValidData_ShouldCreateUserAndAuthUser() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(branchRepository.findById(registerRequestDTO.getBranchId())).thenReturn(Optional.of(mockBranch));
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = registrationService.registerUser(registerRequestDTO);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encoded-password", result.getPassword());
        assertEquals(User.Role.USER, result.getRole());

        verify(userRepository).save(any(User.class));
    }


    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerUser(registerRequestDTO));

        assertEquals("Email is already in use!", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_WithInvalidBranch_ShouldThrowException() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(branchRepository.findById(registerRequestDTO.getBranchId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.registerUser(registerRequestDTO));

        assertTrue(exception.getMessage().contains("Branch not found"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_WithIntegraBankEmail_ShouldAssignAdminRole() {
        registerRequestDTO.setEmail("admin@integrabank.com");
        registerRequestDTO.setRequestedRole(User.Role.ADMIN);

        when(userRepository.existsByEmail("admin@integrabank.com")).thenReturn(false);
        when(branchRepository.findById(registerRequestDTO.getBranchId())).thenReturn(Optional.of(mockBranch));
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = registrationService.registerUser(registerRequestDTO);

        assertEquals(User.Role.ADMIN, result.getRole());
    }

    @Test
    void registerUser_WithNonIntegraBankEmailRequestingAdmin_ShouldAssignUserRole() {
        registerRequestDTO.setRequestedRole(User.Role.ADMIN);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(branchRepository.findById(registerRequestDTO.getBranchId())).thenReturn(Optional.of(mockBranch));
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = registrationService.registerUser(registerRequestDTO);

        assertEquals(User.Role.USER, result.getRole());
    }
}
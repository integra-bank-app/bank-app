package clf.integra.backend.security;

import clf.integra.backend.security.DTO.LoginRequest;
import clf.integra.backend.security.DTO.RegisterRequest;
import clf.integra.backend.security.controller.AuthController;
import clf.integra.backend.security.model.AuthUser;
import clf.integra.backend.security.service.RegistrationService;
import clf.integra.backend.security.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private LoginRequest validLoginRequest;
    private RegisterRequest validRegisterRequest;
    private AuthUser mockAuthUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        validLoginRequest = new LoginRequest();
        validLoginRequest.setUsernameOrEmail("testuser");
        validLoginRequest.setPassword("password123");

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("newuser");
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setFirstName("John");
        validRegisterRequest.setLastName("Doe");
        validRegisterRequest.setBranchId(UUID.randomUUID());

        mockAuthUser = AuthUser.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .role(AuthUser.Role.USER)
                .userId(UUID.randomUUID())
                .build();
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnJwtResponse() throws Exception {
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockAuthUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtUtils.generateToken(mockAuthUser)).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.type").value("Bearer"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateToken(mockAuthUser);
    }

    @Test
    void authenticateUser_WithInvalidCredentials_ShouldReturnBadRequest() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Invalid username/email or password!"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void authenticateUser_WithGeneralException_ShouldReturnBadRequest() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Authentication failed - Database connection failed"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void registerUser_WithValidData_ShouldReturnSuccessResponse() throws Exception {
        when(registrationService.registerUser(any(RegisterRequest.class))).thenReturn(mockAuthUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").value(mockAuthUser.getId().toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.assignedRole").value("USER"))
                .andExpect(jsonPath("$.roleNote").doesNotExist());

        verify(registrationService).registerUser(any(RegisterRequest.class));
    }

    @Test
    void registerUser_WithExistingUsername_ShouldReturnBadRequest() throws Exception {
        when(registrationService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username is already taken!"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Username is already taken!"));

        verify(registrationService).registerUser(any(RegisterRequest.class));
    }

    @Test
    void registerUser_WithAdminRoleRequest_ShouldReturnRoleNote() throws Exception {
        validRegisterRequest.setRequestedRole(AuthUser.Role.ADMIN);

        AuthUser userWithUserRole = AuthUser.builder()
                .id(UUID.randomUUID())
                .username("newuser")
                .email("test@example.com")
                .role(AuthUser.Role.USER)
                .userId(UUID.randomUUID())
                .build();

        when(registrationService.registerUser(any(RegisterRequest.class))).thenReturn(userWithUserRole);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.assignedRole").value("USER"))
                .andExpect(jsonPath("$.roleNote").value("Admin role was requested but only IntegraBank employees (@integrabank.com) can be admins. USER role assigned instead."));

        verify(registrationService).registerUser(any(RegisterRequest.class));
    }

    @Test
    void registerUser_WithIntegraBankEmail_ShouldAssignAdminRole() throws Exception {
        validRegisterRequest.setEmail("admin@integrabank.com");
        validRegisterRequest.setRequestedRole(AuthUser.Role.ADMIN);

        AuthUser adminUser = AuthUser.builder()
                .id(UUID.randomUUID())
                .username("newuser")
                .email("admin@integrabank.com")
                .role(AuthUser.Role.ADMIN)
                .userId(UUID.randomUUID())
                .build();

        when(registrationService.registerUser(any(RegisterRequest.class))).thenReturn(adminUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("admin@integrabank.com"))
                .andExpect(jsonPath("$.assignedRole").value("ADMIN"))
                .andExpect(jsonPath("$.roleNote").doesNotExist());

        verify(registrationService).registerUser(any(RegisterRequest.class));
    }

    @Test
    void registerUser_WithRuntimeException_ShouldReturnBadRequest() throws Exception {
        when(registrationService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error occurred"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Unexpected error occurred"));

        verify(registrationService).registerUser(any(RegisterRequest.class));
    }

    @Test
    void registerUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        when(registrationService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Email is already in use!"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already in use!"));

        verify(registrationService).registerUser(any(RegisterRequest.class));
    }

    @Test
    void registerUser_WithInvalidBranch_ShouldReturnBadRequest() throws Exception {
        when(registrationService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Branch not found with ID: " + validRegisterRequest.getBranchId()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Branch not found with ID: " + validRegisterRequest.getBranchId()));

        verify(registrationService).registerUser(any(RegisterRequest.class));
    }
}
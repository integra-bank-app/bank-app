package clf.integra.backend.security;

import clf.integra.backend.model.Branch;
import clf.integra.backend.model.User;
import clf.integra.backend.security.DTO.LoginRequestDTO;
import clf.integra.backend.security.DTO.RegisterRequestDTO;
import clf.integra.backend.security.controller.AuthController;
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

    private LoginRequestDTO validLoginRequestDTO;
    private RegisterRequestDTO validRegisterRequestDTO;
    private User mockUser;

    private static final String loginPath = "/login";
    private static final String registerPath = "/register";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        validLoginRequestDTO = new LoginRequestDTO();
        validLoginRequestDTO.setEmail("testuser@gmail.com");
        validLoginRequestDTO.setPassword("password123");

        validRegisterRequestDTO = new RegisterRequestDTO();
        validRegisterRequestDTO.setEmail("test@example.com");
        validRegisterRequestDTO.setPassword("password123");
        validRegisterRequestDTO.setFirstName("John");
        validRegisterRequestDTO.setLastName("Doe");
        validRegisterRequestDTO.setBranchId(UUID.randomUUID());

        mockUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .branch(Branch.builder().id(validRegisterRequestDTO.getBranchId()).build())
                .email("test@example.com")
                .password("password123")
                .role(User.Role.USER)
                .build();
    }

    @Test
    void testAuthenticateUser_withValidCredentials_returnJwtResponse() throws Exception {
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtUtils.generateToken(mockUser)).thenReturn("mock-jwt-token");

        mockMvc.perform(post(loginPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.type").value("Bearer"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateToken(mockUser);
    }

    @Test
    void testAuthenticateUser_withInvalidCredentials_returnBadRequest() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post(loginPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Invalid email or password!"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void testAuthenticateUser_withGeneralException_returnBadRequest() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(post(loginPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Authentication failed - Database connection failed"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void testRegisterUser_withValidData_returnSuccessResponse() throws Exception {
        when(registrationService.registerUser(any(RegisterRequestDTO.class))).thenReturn(mockUser);

        mockMvc.perform(post(registerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").value(mockUser.getId().toString()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.assignedRole").value("USER"))
                .andExpect(jsonPath("$.roleNote").doesNotExist());

        verify(registrationService).registerUser(any(RegisterRequestDTO.class));
    }

    @Test
    void testRegisterUser_withAdminRoleRequest_returnRoleNote() throws Exception {
        validRegisterRequestDTO.setRequestedRole(User.Role.ADMIN);

        User userWithUserRole = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.USER)
                .branch(Branch.builder().id(validRegisterRequestDTO.getBranchId()).build())
                .build();

        when(registrationService.registerUser(any(RegisterRequestDTO.class))).thenReturn(userWithUserRole);

        mockMvc.perform(post(registerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.assignedRole").value("USER"))
                .andExpect(jsonPath("$.roleNote").value("Admin role was requested but only IntegraBank employees (@integrabank.com) can be admins. USER role assigned instead."));

        verify(registrationService).registerUser(any(RegisterRequestDTO.class));
    }

    @Test
    void testRegisterUser_withIntegraBankEmail_returnAssignAdminRole() throws Exception {
        validRegisterRequestDTO.setEmail("admin@integrabank.com");
        validRegisterRequestDTO.setRequestedRole(User.Role.ADMIN);

        User adminUser = User.builder()
                .id(UUID.randomUUID())
                .email("admin@integrabank.com")
                .password("password123")
                .firstName("Admin")
                .lastName("User")
                .role(User.Role.ADMIN)
                .branch(Branch.builder().id(validRegisterRequestDTO.getBranchId()).build())
                .build();

        when(registrationService.registerUser(any(RegisterRequestDTO.class))).thenReturn(adminUser);

        mockMvc.perform(post(registerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.email").value("admin@integrabank.com"))
                .andExpect(jsonPath("$.assignedRole").value("ADMIN"))
                .andExpect(jsonPath("$.roleNote").doesNotExist());

        verify(registrationService).registerUser(any(RegisterRequestDTO.class));
    }

    @Test
    void testRegisterUser_withRuntimeException_returnBadRequest() throws Exception {
        when(registrationService.registerUser(any(RegisterRequestDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error occurred"));

        mockMvc.perform(post(registerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Unexpected error occurred"));

        verify(registrationService).registerUser(any(RegisterRequestDTO.class));
    }

    @Test
    void testRegisterUser_withInvalidEmail_returnBadRequest() throws Exception {
        when(registrationService.registerUser(any(RegisterRequestDTO.class)))
                .thenThrow(new RuntimeException("Email is already in use!"));

        mockMvc.perform(post(registerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Email is already in use!"));

        verify(registrationService).registerUser(any(RegisterRequestDTO.class));
    }

    @Test
    void testRegisterUser_withInvalidBranch_returnBadRequest() throws Exception {
        when(registrationService.registerUser(any(RegisterRequestDTO.class)))
                .thenThrow(new RuntimeException("Branch not found with ID: " + validRegisterRequestDTO.getBranchId()));

        mockMvc.perform(post(registerPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Branch not found with ID: " + validRegisterRequestDTO.getBranchId()));

        verify(registrationService).registerUser(any(RegisterRequestDTO.class));
    }
}
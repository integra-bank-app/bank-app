package clf.integra.backend.security.controller;

import clf.integra.backend.model.User;
import clf.integra.backend.security.DTO.JwtResponseDTO;
import clf.integra.backend.security.DTO.LoginRequestDTO;
import clf.integra.backend.security.DTO.RegisterRequestDTO;
import clf.integra.backend.security.utils.JwtUtils;
import clf.integra.backend.security.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j


public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RegistrationService registrationService;
    private final JwtUtils jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        log.info("Login attempt for user with email: {}", loginRequestDTO.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(user);

            log.info("Login successful for user: {} with role: {}", user.getUsername(), user.getRole());

            return ResponseEntity.ok(new JwtResponseDTO(
                    jwt,
                    user.getEmail(),
                    user.getRole().name()
            ));
        } catch (BadCredentialsException e) {
            log.error("Bad credentials for user: {}", loginRequestDTO.getEmail());
            return ResponseEntity.badRequest()
                    .body("Error: Invalid username/email or password!");
        } catch (Exception e) {
            log.error("Login error for user: {}", loginRequestDTO.getEmail(), e);
            return ResponseEntity.badRequest()
                    .body("Error: Authentication failed - " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        log.info("Registration attempt for user with email: {} requesting role: {}",
                registerRequestDTO.getEmail(), registerRequestDTO.getRequestedRole());

        try {
            User user = registrationService.registerUser(registerRequestDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("assignedRole", user.getRole());

            if (registerRequestDTO.getRequestedRole() == User.Role.ADMIN &&
                    user.getRole() == User.Role.USER) {
                response.put("roleNote", "Admin role was requested but only IntegraBank employees (@integrabank.com) can be admins. USER role assigned instead.");
            }

            log.info("Registration successful for user: {} with final role: {}", user.getUsername(), user.getRole());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected registration error: ", e);
            return ResponseEntity.badRequest()
                    .body("Error: Registration failed - " + e.getMessage());
        }
    }
}
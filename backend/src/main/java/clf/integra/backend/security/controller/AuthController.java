package clf.integra.backend.security.controller;

import clf.integra.backend.security.DTO.JwtResponse;
import clf.integra.backend.security.DTO.LoginRequest;
import clf.integra.backend.security.DTO.RegisterRequest;
import clf.integra.backend.security.model.AuthUser;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RegistrationService registrationService;
    private final JwtUtils jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsernameOrEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsernameOrEmail(),
                            loginRequest.getPassword()
                    )
            );

            AuthUser authUser = (AuthUser) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(authUser);

            log.info("Login successful for user: {} with role: {}", authUser.getUsername(), authUser.getRole());

            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    authUser.getUsername(),
                    authUser.getEmail(),
                    authUser.getRole().name()
            ));
        } catch (BadCredentialsException e) {
            log.error("Bad credentials for user: {}", loginRequest.getUsernameOrEmail());
            return ResponseEntity.badRequest()
                    .body("Error: Invalid username/email or password!");
        } catch (Exception e) {
            log.error("Login error for user: {}", loginRequest.getUsernameOrEmail(), e);
            return ResponseEntity.badRequest()
                    .body("Error: Authentication failed - " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration attempt for user: {} with email: {} requesting role: {}",
                registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getRequestedRole());

        try {
            AuthUser authUser = registrationService.registerUser(registerRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", authUser.getId());
            response.put("username", authUser.getUsername());
            response.put("email", authUser.getEmail());
            response.put("assignedRole", authUser.getRole());

            if (registerRequest.getRequestedRole() == AuthUser.Role.ADMIN &&
                    authUser.getRole() == AuthUser.Role.USER) {
                response.put("roleNote", "Admin role was requested but only IntegraBank employees (@integrabank.com) can be admins. USER role assigned instead.");
            }

            log.info("Registration successful for user: {} with final role: {}", authUser.getUsername(), authUser.getRole());
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
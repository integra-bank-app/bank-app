package clf.integra.backend.security.service;

import clf.integra.backend.security.DTO.RegisterRequest;
import clf.integra.backend.security.model.AuthUser;
import clf.integra.backend.model.Branch;
import clf.integra.backend.model.User;
import clf.integra.backend.security.repository.AuthUserRepository;
import clf.integra.backend.repository.BranchRepository;
import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private final UserRepository userRepository;
    private final AuthUserRepository authUserRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL_DOMAIN = "@integrabank.com";

    @Transactional
    public AuthUser registerUser(RegisterRequest registerRequest) {
        log.info("Starting registration for user: {}", registerRequest.getUsername());

        if (authUserRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (authUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        Branch branch = branchRepository.findById(registerRequest.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + registerRequest.getBranchId()));

        try {
            User user = User.builder()
                    .firstName(registerRequest.getFirstName())
                    .middleName(registerRequest.getMiddleName())
                    .lastName(registerRequest.getLastName())
                    .branch(branch)
                    .build();

            user = userRepository.save(user);
            log.info("User created with ID: {}", user.getId());

            AuthUser.Role userRole = determineUserRole(registerRequest.getEmail(), registerRequest.getRequestedRole());

            AuthUser authUser = AuthUser.builder()
                    .username(registerRequest.getUsername())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(userRole)
                    .userId(user.getId())
                    .build();

            authUser = authUserRepository.save(authUser);
            log.info("AuthUser created with ID: {} and role: {}", authUser.getId(), authUser.getRole());

            return authUser;
        } catch (Exception e) {
            log.error("Error during registration: ", e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    private AuthUser.Role determineUserRole(String email, AuthUser.Role requestedRole) {
        boolean isIntegraBankEmail = email.toLowerCase().endsWith(ADMIN_EMAIL_DOMAIN.toLowerCase());

        log.info("Email: {}, isIntegraBankEmail: {}, requestedRole: {}", email, isIntegraBankEmail, requestedRole);

        if (isIntegraBankEmail) {
            log.info("Admin role granted for IntegraBank email: {}", email);
            return AuthUser.Role.ADMIN;
        }

        if (requestedRole == AuthUser.Role.ADMIN) {
            log.warn("Admin role requested but email {} is not from IntegraBank domain. Assigning USER role.", email);
            return AuthUser.Role.USER;
        }

        log.info("USER role assigned for email: {}", email);
        return AuthUser.Role.USER;
    }
}
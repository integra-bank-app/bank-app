package clf.integra.backend.security.service;

import clf.integra.backend.security.DTO.RegisterRequestDTO;
import clf.integra.backend.model.Branch;
import clf.integra.backend.model.User;
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
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL_DOMAIN = "@integrabank.com";

    @Transactional
    public User registerUser(RegisterRequestDTO registerRequestDTO) {
        log.info("Starting registration for user with email: {}", registerRequestDTO.getEmail());

        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        Branch branch = branchRepository.findById(registerRequestDTO.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + registerRequestDTO.getBranchId()));

        try {
            User.Role userRole = determineUserRole(registerRequestDTO.getEmail(), registerRequestDTO.getRequestedRole());

            User user = User.builder()
                    .firstName(registerRequestDTO.getFirstName())
                    .middleName(registerRequestDTO.getMiddleName())
                    .lastName(registerRequestDTO.getLastName())
                    .email(registerRequestDTO.getEmail())
                    .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                    .role(userRole)
                    .branch(branch)
                    .build();

            user = userRepository.save(user);
            log.info("User created with ID: {}", user.getId());

            return user;
        } catch (Exception e) {
            log.error("Error during registration: ", e);
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    private User.Role determineUserRole(String email, User.Role requestedRole) {
        boolean isIntegraBankEmail = email.toLowerCase().endsWith(ADMIN_EMAIL_DOMAIN.toLowerCase());

        log.info("Email: {}, isIntegraBankEmail: {}, requestedRole: {}", email, isIntegraBankEmail, requestedRole);

        if (isIntegraBankEmail) {
            log.info("Admin role granted for IntegraBank email: {}", email);
            return User.Role.ADMIN;
        }

        if (requestedRole == User.Role.ADMIN) {
            log.warn("Admin role requested but email {} is not from IntegraBank domain. Assigning USER role.", email);
            return User.Role.USER;
        }

        log.info("USER role assigned for email: {}", email);
        return User.Role.USER;
    }
}
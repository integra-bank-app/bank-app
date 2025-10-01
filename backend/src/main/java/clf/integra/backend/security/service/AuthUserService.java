package clf.integra.backend.security.service;

import clf.integra.backend.model.User;
import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Attempting to load user by email: {}", email);

        User authUser = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email : {}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });

        log.info("User found: {}", authUser.getEmail());
        return authUser;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
package clf.integra.backend.security.repository;

import clf.integra.backend.security.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    Optional<AuthUser> findByUsername(String username);

    Optional<AuthUser> findByEmail(String email);

    Optional<AuthUser> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
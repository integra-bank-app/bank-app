package clf.integra.backend.repository;

import clf.integra.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findByBranchId(UUID branchId);

    Double getBalanceById(UUID id);
}



package clf.integra.backend.repository;

import clf.integra.backend.model.Deposits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DepositsRepository extends JpaRepository<Deposits, UUID> {
    List<Deposits> findByUserId(UUID userId);
}

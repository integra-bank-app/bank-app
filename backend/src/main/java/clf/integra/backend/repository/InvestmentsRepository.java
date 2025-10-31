package clf.integra.backend.repository;

import clf.integra.backend.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InvestmentsRepository extends JpaRepository<Investment, UUID> {
    List<Investment> findAllByUserId(UUID userId);
}

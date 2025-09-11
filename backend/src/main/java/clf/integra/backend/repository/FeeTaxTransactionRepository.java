package clf.integra.backend.repository;

import clf.integra.backend.model.FeeTaxTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FeeTaxTransactionRepository extends JpaRepository<FeeTaxTransaction, UUID> {
    List<FeeTaxTransaction> findByCreatedAtAfter(LocalDateTime createdAtAfter);
}

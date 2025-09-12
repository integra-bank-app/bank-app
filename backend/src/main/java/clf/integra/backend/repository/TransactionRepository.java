package clf.integra.backend.repository;

import clf.integra.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserIdOrderByTimestampDesc(UUID userId);

    List<Transaction> findByUserIdAndTransactionType(UUID userId, String transactionType);

}

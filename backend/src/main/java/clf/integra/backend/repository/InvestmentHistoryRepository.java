package clf.integra.backend.repository;

import clf.integra.backend.model.InvestmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvestmentHistoryRepository extends JpaRepository<InvestmentHistory, Long> {
    List<InvestmentHistory> findAllByInvestment_User_Id(UUID userId);
    List<InvestmentHistory> findAllByInvestment_IdOrderByDateAsc(UUID investmentId);
}

package clf.integra.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record InvestmentHistoryDTO (
        String investmentId,
        Double balance,
        LocalDateTime date) {}

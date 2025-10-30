package clf.integra.backend.dto;

import java.time.LocalDate;

public record InvestmentHistoryDTO (
        String investmentId,
        Double balance,
        LocalDate date) {}

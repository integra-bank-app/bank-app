package clf.integra.backend.mapper;

import clf.integra.backend.dto.InvestmentDTO;
import clf.integra.backend.model.Investment;


public class InvestmentMapper {
    public static InvestmentDTO toDTO(Investment investment) {
        if (investment == null) return null;

        return InvestmentDTO.builder()
                .risk(investment.getRisk())
                .balance(investment.getBalance())
                .build();
    }
}

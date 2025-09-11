package clf.integra.backend.mapper;

import clf.integra.backend.dto.FeeTaxTransactionDTO;
import clf.integra.backend.model.FeeTaxTransaction;

public class FeeTaxTransactionMapper {

    public static FeeTaxTransactionDTO toDTO(FeeTaxTransaction entity) {
        if (entity == null) return null;

        return new FeeTaxTransactionDTO(
                UserMapper.toDTO(entity.getUser()),
                entity.getAmount(),
                entity.getCreatedAt()
        );
    }
}
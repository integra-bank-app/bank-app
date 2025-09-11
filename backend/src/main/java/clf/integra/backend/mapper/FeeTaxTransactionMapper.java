package clf.integra.backend.mapper;

import clf.integra.backend.dto.FeeTaxTransactionDTO;
import clf.integra.backend.model.FeeTaxTransaction;

public class FeeTaxTransactionMapper {
    public static FeeTaxTransactionDTO toDTO(FeeTaxTransaction entity) {
        if (entity == null) return null;

        return FeeTaxTransactionDTO.builder()
                .user(UserMapper.toDTO(entity.getUser()))
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .build();

    }
}
package clf.integra.backend.mapper;

import clf.integra.backend.dto.TransactionDTO;
import clf.integra.backend.model.Transaction;

public class TransactionMapper {

    public static TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return new TransactionDTO(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTimestamp(),
                transaction.getDescription(),
                transaction.getReferenceTransactionId()
        );
    }
}
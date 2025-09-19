package clf.integra.backend.mapper;

import clf.integra.backend.dto.UserTransactionDTO;
import clf.integra.backend.model.FeeTaxTransaction;
import clf.integra.backend.model.Transaction;
import clf.integra.backend.model.TransactionType;

import java.util.UUID;

public class UserTransactionHistoryMapper {
    public static UserTransactionDTO fromTransfers(Transaction transaction){
        return UserTransactionDTO.builder()
                .transactionId(transaction.getId())
                .transactionType(transaction.getType())
                .amount(transaction.getAmount())
                .timestamp(transaction.getTimestamp())
                .description(transaction.getDescription())
                .fromUserId(transaction.getType() == TransactionType.TRANSFER_OUT ? transaction.getUser().getId() : null)
                .toUserId(transaction.getType() == TransactionType.TRANSFER_IN ? transaction.getUser().getId() : null)
                .build();
    }
    public static UserTransactionDTO fromFeeTaxTransaction(FeeTaxTransaction feeTaxTransaction,UUID userId){
        return UserTransactionDTO.builder()
                .transactionId(feeTaxTransaction.getId())
                .transactionType(TransactionType.FEE)
                .amount(feeTaxTransaction.getAmount())
                .timestamp(feeTaxTransaction.getCreatedAt())
                .description("Fee/Tax deducted")
                .fromUserId(userId)
                .toUserId(null)
                .build();
    }
}

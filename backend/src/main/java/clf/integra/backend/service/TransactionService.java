package clf.integra.backend.service;

import clf.integra.backend.dto.UserTransactionDTO;
import clf.integra.backend.exceptions.InvalidAmountException;
import clf.integra.backend.exceptions.InvalidTransactionType;
import clf.integra.backend.model.Transaction;
import clf.integra.backend.model.TransactionType;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    Transaction createTransaction(User user, Double amount, TransactionType type, String description){
        if (amount <= 0) {
            throw new InvalidAmountException("Transaction amount must be positive");
        }

        if (type == null) {
            throw new InvalidTransactionType("Transaction type is invalid");
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(amount)
                .type(type)
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByUser(UUID userId) {
        return transactionRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + transactionId));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<UserTransactionDTO> getUserTransaction(UUID userId) {
        return transactionRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(transaction -> UserTransactionDTO.builder()
                        .transactionId(transaction.getId())
                        .transactionType(transaction.getType())
                        .amount(transaction.getAmount())
                        .timestamp(transaction.getTimestamp())
                        .description(transaction.getDescription())
                        .fromUserId(transaction.getType() == TransactionType.TRANSFER_OUT ? transaction.getUser().getId() : null)
                        .toUserId(transaction.getType() == TransactionType.TRANSFER_IN ? transaction.getUser().getId() : null)
                        .build()
                ).toList();
    }
}
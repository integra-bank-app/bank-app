package clf.integra.backend.service;

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
    public Transaction createTransaction(User user, Double amount, TransactionType type, String description) {
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
}
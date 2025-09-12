package clf.integra.backend.service;

import clf.integra.backend.dto.TransactionDTO;
import clf.integra.backend.exceptions.InvalidAmountException;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.exceptions.SelfTransferException;
import clf.integra.backend.mapper.TransactionMapper;
import clf.integra.backend.model.Transaction;
import clf.integra.backend.repository.TransactionRepository;
import clf.integra.backend.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public TransactionDTO saveTopUpTransaction(UUID userId, double amount, String description) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .amount(Math.abs(amount))
                .transactionType("TOP-UP")
                .timestamp(LocalDateTime.now())
                .description(description != null ? description : "Account top-up")
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionMapper.toDTO(savedTransaction);
    }

    @Transactional
    public void saveTransferTransactions(UUID senderId, UUID receiverId, double amount, String description) {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        if (senderId.equals(receiverId)) {
            throw new SelfTransferException("Cannot transfer to the same user");
        }

        if (!userRepository.existsById(senderId)) {
            throw new NotFoundException("Sender not found");
        }

        if (!userRepository.existsById(receiverId)) {
            throw new NotFoundException("Receiver not found");
        }

        Transaction senderTransaction = Transaction.builder()
                .userId(senderId)
                .amount(-Math.abs(amount))
                .transactionType("TRANSFER-OUT")
                .timestamp(LocalDateTime.now())
                .description(description != null ? description : "Money transfer sent")
                .build();

        Transaction receiverTransaction = Transaction.builder()
                .userId(receiverId)
                .amount(Math.abs(amount))
                .transactionType("TRANSFER-IN")
                .timestamp(LocalDateTime.now())
                .description(description != null ? description : "Money transfer received")
                .build();

        Transaction savedSender = transactionRepository.save(senderTransaction);
        Transaction savedReceiver = transactionRepository.save(receiverTransaction);

        savedSender.setReferenceTransactionId(savedReceiver.getId());
        savedReceiver.setReferenceTransactionId(savedSender.getId());

        transactionRepository.save(savedSender);
        transactionRepository.save(savedReceiver);
    }

    public List<TransactionDTO> getUserTransactionHistory(UUID userId) {
        if(!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTimestampDesc(userId);
        return transactions.stream()
                .map(TransactionMapper::toDTO)
                .toList();
    }

    public List<TransactionDTO> getUserTransactionsByType(UUID userId, String transactionType) {
        if(!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionType(userId, transactionType);
        return transactions.stream()
                .map(TransactionMapper::toDTO)
                .toList();
    }
}
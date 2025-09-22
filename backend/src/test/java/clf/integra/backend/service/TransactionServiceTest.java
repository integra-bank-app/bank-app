package clf.integra.backend.service;

import clf.integra.backend.dto.UserTransactionDTO;
import clf.integra.backend.model.Transaction;
import clf.integra.backend.model.TransactionType;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionService transactionService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        user = User.builder().id(userId).build();
    }

    @Test
    void testGetUserTransaction_existingTransactions_returnTransactionList(){
        Transaction t1 = Transaction.builder()
                .id(UUID.randomUUID())
                .user(user)
                .amount(100.0)
                .type(TransactionType.TOP_UP)
                .timestamp(LocalDateTime.now())
                .description("Top-up")
                .build();

        when(transactionRepository.findByUserIdOrderByTimestampDesc(userId)).thenReturn(List.of(t1));

        List<UserTransactionDTO> result = transactionService.getUserTransaction(userId);

        assertEquals(1, result.size());
        assertEquals(t1.getId(), result.get(0).transactionId());
        verify(transactionRepository).findByUserIdOrderByTimestampDesc(userId);
    }

    @Test
    void testGetUserTransaction_withNoTransactions_returnEmptyList() {
        when(transactionRepository.findByUserIdOrderByTimestampDesc(userId)).thenReturn(List.of());

        List<UserTransactionDTO> result = transactionService.getUserTransaction(userId);

        assertTrue(result.isEmpty());
    }
}

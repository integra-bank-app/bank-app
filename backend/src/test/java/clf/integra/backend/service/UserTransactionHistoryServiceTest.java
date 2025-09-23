package clf.integra.backend.service;

import clf.integra.backend.dto.UserTransactionDTO;
import clf.integra.backend.model.TransactionType;
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
import static org.mockito.Mockito.when;

public class UserTransactionHistoryServiceTest {
    @Mock
    private TransactionService transferService;
    @Mock
    private FeeTaxService feeTaxService;
    @InjectMocks
    private UserTransactionHistoryService userTransactionHistoryService;

    private UUID userId;
    private UserTransactionDTO tr1;
    private UserTransactionDTO tr2;
    private UserTransactionDTO feetax;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();

        tr1 = UserTransactionDTO.builder()
                .transactionId(UUID.randomUUID())
                .transactionType(TransactionType.TRANSFER_IN)
                .amount(100.0)
                .timestamp(LocalDateTime.now().minusDays(2))
                .description("transfer in")
                .fromUserId(userId)
                .build();

        tr2 = UserTransactionDTO.builder()
                .transactionId(UUID.randomUUID())
                .transactionType(TransactionType.TRANSFER_OUT)
                .amount(50.0)
                .timestamp(LocalDateTime.now().minusDays(1))
                .description("transfer out")
                .fromUserId(userId)
                .build();

        feetax = UserTransactionDTO.builder()
                .transactionId(UUID.randomUUID())
                .transactionType(TransactionType.FEE)
                .amount(10.0)
                .timestamp(LocalDateTime.now())
                .description("Fee deducted")
                .fromUserId(userId)
                .build();
    }

    @Test
    void testGetTransactionHistory_withBothServices_returnMergedAndSorted() {
        when(transferService.getUserTransaction(userId)).thenReturn(List.of(tr1, tr2));
        when(feeTaxService.getUserFeeTaxesTransaction(userId)).thenReturn(List.of(feetax));

        List<UserTransactionDTO> result = userTransactionHistoryService.getTransactionHistory(userId);

        assertEquals(3, result.size());
        assertEquals(feetax.transactionId(), result.get(0).transactionId(), "Newest transaction should come first");
        assertEquals(tr2.transactionId(), result.get(1).transactionId());
        assertEquals(tr1.transactionId(), result.get(2).transactionId());
    }


    @Test
    void testGetTransactionHistory_withOnlyRegularTransactions_returnSortedList() {
        when(transferService.getUserTransaction(userId)).thenReturn(List.of(tr1, tr2));
        when(feeTaxService.getUserFeeTaxesTransaction(userId)).thenReturn(List.of());

        List<UserTransactionDTO> result = userTransactionHistoryService.getTransactionHistory(userId);

        assertEquals(2, result.size());
        assertEquals(tr2.transactionId(), result.get(0).transactionId());
        assertEquals(tr1.transactionId(), result.get(1).transactionId());
    }

    @Test
    void testGetTransactionHistory_withOnlyFeeTransactions_returnSortedList() {
        when(transferService.getUserTransaction(userId)).thenReturn(List.of());
        when(feeTaxService.getUserFeeTaxesTransaction(userId)).thenReturn(List.of(feetax));

        List<UserTransactionDTO> result = userTransactionHistoryService.getTransactionHistory(userId);

        assertEquals(1, result.size());
        assertEquals(feetax.transactionId(), result.get(0).transactionId());
    }

    @Test
    void testGetTransactionHistory_withNoTransactions_returnEmptyList() {
        when(transferService.getUserTransaction(userId)).thenReturn(List.of());
        when(feeTaxService.getUserFeeTaxesTransaction(userId)).thenReturn(List.of());

        List<UserTransactionDTO> result = userTransactionHistoryService.getTransactionHistory(userId);

        assertTrue(result.isEmpty());
    }
}

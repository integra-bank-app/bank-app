package clf.integra.backend.controller;

import clf.integra.backend.dto.UserTransactionDTO;
import clf.integra.backend.model.TransactionType;
import clf.integra.backend.security.utils.JwtUtils;
import clf.integra.backend.service.UserTransactionHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserTransactionHistoryControlller.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserTransactionHistoryTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserTransactionHistoryService userTransactionHistoryService;

    @MockitoBean
    JwtUtils utils;

    private UUID userId;
    private UserTransactionDTO tr1;
    private UserTransactionDTO feetax;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        tr1 = UserTransactionDTO.builder()
                .transactionId(UUID.randomUUID())
                .transactionType(TransactionType.TRANSFER_IN)
                .amount(200.0)
                .timestamp(LocalDateTime.now().minusDays(1))
                .description("Deposit")
                .fromUserId(userId)
                .toUserId(null)
                .build();

        feetax = UserTransactionDTO.builder()
                .transactionId(UUID.randomUUID())
                .transactionType(TransactionType.FEE)
                .amount(15.0)
                .timestamp(LocalDateTime.now())
                .description("Fee")
                .fromUserId(userId)
                .toUserId(null)
                .build();
    }

    @Test
    void testGetUserTransactionHistory_withTransactions_returnOkAndList() throws Exception {
        when(userTransactionHistoryService.getTransactionHistory(userId))
                .thenReturn(List.of(tr1, feetax));

        mockMvc.perform(get("/users/" +userId +"/transactions" ))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].transactionId").value(tr1.transactionId().toString()))
                .andExpect(jsonPath("$[0].amount").value(200.0))
                .andExpect(jsonPath("$[0].transactionType").value("TRANSFER_IN"))
                .andExpect(jsonPath("$[1].transactionId").value(feetax.transactionId().toString()))
                .andExpect(jsonPath("$[1].transactionType").value("FEE"));
    }


    @Test
    void testGetUserTransactionHistory_withNoTransactions_returnNotFound() throws Exception {
        when(userTransactionHistoryService.getTransactionHistory(userId))
                .thenReturn(List.of());

        mockMvc.perform(get("/users/{userId}/transactions", userId))
                .andExpect(status().isNotFound());
    }
}

package clf.integra.backend.controller;

import clf.integra.backend.dto.DepositsDTO;
import clf.integra.backend.dto.UserWithBranchDTO;
import clf.integra.backend.exceptions.InsufficientFundsException;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.service.DepositsService;
import clf.integra.backend.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @MockitoBean
    DepositsService depositsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testAddUser_validData_returnsSuccess() throws Exception {
        UUID newUserId = UUID.randomUUID();

        when(userService.addUserWithName(anyString(), anyString(), anyString(), any(UUID.class)))
                .thenReturn(newUserId);

        UserWithBranchDTO newUser = new UserWithBranchDTO(
                "John",
                "M",
                "Doe",
                UUID.fromString("4a5cf1e2-3b6d-4c5e-8f1e-1234567890ab")
        );

        String requestJson = objectMapper.writeValueAsString(newUser);

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        UUID returnedId = objectMapper.readValue(responseBody, UUID.class);

        assert returnedId.equals(newUserId);
    }

    @Test
    void testAddUser_branchDoesNotExist_returnsNotFound() throws Exception {
        when(userService.addUserWithName(anyString(), anyString(), anyString(), any(UUID.class)))
                .thenThrow(new NotFoundException("Branch not found"));

        UserWithBranchDTO newUser = new UserWithBranchDTO(
                "John",
                "M",
                "Doe",
                UUID.fromString("4a5cf1e2-3b6d-4c5e-8f1e-1234567890ab")
        );

        String requestJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAddBalance_validData_returnsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.addBalance(eq(userId), anyDouble())).thenReturn(150.0);

        mockMvc.perform(post("/users/" + userId + "/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\": 50.0}"))
                .andExpect(status().isOk())
                .andExpect(content().string("150.0"));
    }

    @Test
    void testAddBalance_invalidBalance_returnsBadRequest() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/users/" + userId + "/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\": -10.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddBalance_invalidUser_returnsNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.addBalance(eq(userId), anyDouble())).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/users/" + userId + "/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\": 20.0}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserBalance_validData_returnsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.getUserTotalBalanceById(eq(userId))).thenReturn(200.0);

        mockMvc.perform(get("/users/" + userId + "/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("200.0"));
    }

    @Test
    void testGetUserBalance_invalidUser_returnsNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.getUserTotalBalanceById(eq(userId))).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/" + userId + "/balance"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testTransferMoney_validData_returnsSuccess() throws Exception {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        when(userService.transferMoney(eq(fromId), eq(toId), eq(50.0))).thenReturn(100.0);

        mockMvc.perform(post("/users/transfer")
                        .param("fromUserId", fromId.toString())
                        .param("toUserId", toId.toString())
                        .param("amount", "50.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("100.0"));
    }

    @Test
    void testTransferMoney_insufficientFunds_returnsPaymentRequired() throws Exception {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        when(userService.transferMoney(eq(fromId), eq(toId), eq(500.0)))
                .thenThrow(new InsufficientFundsException("Not enough funds"));

        mockMvc.perform(post("/users/transfer")
                        .param("fromUserId", fromId.toString())
                        .param("toUserId", toId.toString())
                        .param("amount", "500.0"))
                .andExpect(status().isPaymentRequired());
    }

    @Test
    void testTransferMoney_invalidUser_returnsNotFound() throws Exception {
        UUID fromId = UUID.randomUUID();
        UUID toId = UUID.randomUUID();
        when(userService.transferMoney(eq(fromId), eq(toId), anyDouble()))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/users/transfer")
                        .param("fromUserId", fromId.toString())
                        .param("toUserId", toId.toString())
                        .param("amount", "50.0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserAccounts_validData_returnsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID accountId1 = UUID.randomUUID();
        UUID accountId2 = UUID.randomUUID();
        when(userService.getUserAccounts(eq(userId))).thenReturn(
                java.util.List.of(accountId1, accountId2)
        );

        mockMvc.perform(get("/users/" + userId + "/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"" + accountId1 + "\",\"" + accountId2 + "\"]"));
    }

    @Test
    void testGetUserAccounts_invalidUser_returnsNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.getUserAccounts(eq(userId))).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/" + userId + "/accounts"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserAccountBalance_validData_returnsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        when(userService.getUserAccountBalance(eq(userId), eq(accountId))).thenReturn(300.0);

        mockMvc.perform(get("/users/" + userId + "/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(content().string("300.0"));
    }

    @Test
    void testGetUserAccountBalance_invalidUserOrAccount_returnsNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        when(userService.getUserAccountBalance(eq(userId), eq(accountId))).thenThrow(new NotFoundException("User or account not found"));

        mockMvc.perform(get("/users/" + userId + "/accounts/" + accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserDeposits_validData_returnsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();

        DepositsDTO deposit1 = new DepositsDTO(UUID.randomUUID(), 5.0, 1000.0);
        DepositsDTO deposit2 = new DepositsDTO(UUID.randomUUID(), 3.0, 2000.0);

        when(depositsService.getUserDeposits(eq(userId))).thenReturn(
                java.util.List.of(deposit1, deposit2));

        MvcResult result = mockMvc.perform(get("/users/" + userId + "/deposits"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        List<DepositsDTO> deposits = objectMapper.readValue(responseBody, new TypeReference<>() {
        });

        assert deposits.size() == 2;
        assert (deposits.get(0)).equals(deposit1);
        assert (deposits.get(1)).equals(deposit2);
    }

    @Test
    void testGetUserDeposits_noDeposits_returnsNotFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(depositsService.getUserDeposits(eq(userId))).thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/users/" + userId + "/deposits"))
                .andExpect(status().isNotFound());
    }
}

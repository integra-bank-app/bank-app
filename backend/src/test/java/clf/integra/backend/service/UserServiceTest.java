package clf.integra.backend.service;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.exceptions.BalanceUpdateFailedException;
import clf.integra.backend.exceptions.InsufficientFundsException;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.model.Account;
import clf.integra.backend.model.Branch;
import clf.integra.backend.model.TransactionType;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.BranchRepository;
import clf.integra.backend.repository.UserRepository;
import clf.integra.backend.utils.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RandomUtils randomUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private Account account;
    private UUID userId;
    private Branch branch;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");

        userId = UUID.randomUUID();
        branch = Branch.builder().id(UUID.randomUUID()).build();

        account = Account.builder()
                .id(UUID.randomUUID())
                .balance(100.0)
                .build();

        user = User.builder()
                .id(userId)
                .firstName("John")
                .middleName("Mike")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .password("password123")
                .role(User.Role.USER)
                .branch(branch)
                .accounts(new ArrayList<>())
                .build();
        user.getAccounts().add(account);
    }

    @Test
    void testAddUserWithName_validData_returnSuccess() {
        UUID branchId = UUID.randomUUID();
        Branch branch = Branch.builder().id(branchId).build();

        when(branchRepository.findById(branchId)).thenReturn(Optional.of(branch));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.addUserWithName(
                "John", "Mike", "Doe", branchId,
                "john.doe@email.com", "parola123", User.Role.USER
        );

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testAddUserWithName_noBranch_returnNoSuchElement() {
        UUID branchId = UUID.randomUUID();

        when(branchRepository.findById(branchId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                userService.addUserWithName(
                        "John", "Mike", "Doe", branchId,
                        "john.doe@email.com", "parola123", User.Role.USER
                ));    }

    @Test
    void testAddBalance_whenRandomBelow_returnSuccess() throws IOException {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(randomUtils.random()).thenReturn(0.5);
        doNothing().when(notificationService).sendNotificationToUser(any(), anyString(), any(UUID.class));

        double result = userService.addBalance(userId, 100.0);

        assertEquals(200.0, result);
        verify(userRepository).save(user);
        verify(transactionService).createTransaction(user, 100.0, TransactionType.TOP_UP, "Top-up of 100.0");
    }


    @Test
    void testAddBalance_whenRandomAbove_returnFailure() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(randomUtils.random()).thenReturn(0.9);

        assertThrows(BalanceUpdateFailedException.class, () ->
                userService.addBalance(userId, 100.0));
        verify(transactionService, never()).createTransaction(any(), anyDouble(), any(), any());
    }

    @Test
    void testAddBalance_userNotFound_returnNotFound() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                userService.addBalance(userId, 100.0));
    }

    @Test
    void testGetUserBalanaceByID_validData_returnSuccess() {
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        double totalBalance = userService.getUserTotalBalanceById(userId);

        assertEquals(100.0, totalBalance);
    }

    @Test
    void testGetUserBalanaceByID_idNull_returnFailure() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.getUserTotalBalanceById(null));
    }

    @Test
    void testGetAllUserByBranch() {
        UUID branchId = UUID.randomUUID();
        when(userRepository.findByBranchId(branchId)).thenReturn(List.of(user));

        List<UserDTO> result = userService.getAllUsersByBranch(branchId);

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).firstName());
    }

    @Test
    void testCollectTaxesAndFeesFromBranch_validData_returnSuccess() throws IOException {
        UUID branchId = UUID.randomUUID();
        when(userRepository.findByBranchId(branchId)).thenReturn(List.of(user));

        double revenue = userService.collectTaxesAndFeesFromBranch(branchId);
        double expectedFee = userService.calculateFee(user.getAccounts().getFirst().getBalance() + revenue);

        assertTrue(revenue > 0);
        verify(userRepository).saveAll(anyList());
        verify(transactionService).createTransaction(
                eq(user),
                anyDouble(),
                eq(TransactionType.FEE),
                anyString()
        );
    }

    @Test
    void testCollectTaxesAndFeesFromBranch_noBranch_returnFailure() throws IOException {
        UUID branchId = UUID.randomUUID();
        when(userRepository.findByBranchId(branchId)).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> userService.collectTaxesAndFeesFromBranch(branchId));
    }

    @Test
    void testCalculateFee_below100() {
        double balance = 50.0;
        double expectedFee = balance * 0.1;

        double actualFee = userService.calculateFee(balance);

        assertEquals(expectedFee, actualFee);
    }

    @Test
    void testCalculateFee_equal100() {
        double balance = 100.0;
        double expectedFee = 10.0;

        double actualFee = userService.calculateFee(balance);

        assertEquals(expectedFee, actualFee);
    }

    @Test
    void testCalculateFee_above100() {
        double balance = 150.0;
        double expectedFee = 10.0;

        double actualFee = userService.calculateFee(balance);

        assertEquals(expectedFee, actualFee);
    }

    @Test
    void testTransferMoney_validData_returnSuccess() throws NotFoundException, InsufficientFundsException, IOException {
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();

        Account fromAccount = Account.builder().id(UUID.randomUUID()).balance(200.0).build();
        Account toAccount = Account.builder().id(UUID.randomUUID()).balance(50.0).build();

        User fromUser = User.builder().id(fromUserId).accounts(new ArrayList<>()).build();
        User toUser = User.builder().id(toUserId).accounts(new ArrayList<>()).build();

        fromUser.getAccounts().add(fromAccount);
        toUser.getAccounts().add(toAccount);

        when(userRepository.getReferenceById(fromUserId)).thenReturn(fromUser);
        when(userRepository.getReferenceById(toUserId)).thenReturn(toUser);

        double remainingBalance = userService.transferMoney(fromUserId, toUserId, 100.0);

        assertEquals(100.0, remainingBalance);
        assertEquals(150.0, toUser.getAccounts().get(0).getBalance());
        verify(userRepository).save(fromUser);
        verify(userRepository).save(toUser);

        verify(transactionService).createTransaction(eq(fromUser), eq(-100.0), eq(TransactionType.TRANSFER_OUT), anyString());
        verify(transactionService).createTransaction(eq(toUser), eq(100.0), eq(TransactionType.TRANSFER_IN), anyString());
    }

    @Test
    void testTransferMoney_insufficientFunds_returnFailure() {
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();

        Account fromAccount = Account.builder().id(UUID.randomUUID()).balance(50.0).build();
        Account toAccount = Account.builder().id(UUID.randomUUID()).balance(50.0).build();

        User fromUser = User.builder().id(fromUserId).accounts(new ArrayList<>()).build();
        User toUser = User.builder().id(toUserId).accounts(new ArrayList<>()).build();

        fromUser.getAccounts().add(fromAccount);
        toUser.getAccounts().add(toAccount);

        when(userRepository.getReferenceById(fromUserId)).thenReturn(fromUser);
        when(userRepository.getReferenceById(toUserId)).thenReturn(toUser);

        assertThrows(InsufficientFundsException.class, () ->
                userService.transferMoney(fromUserId, toUserId, 100.0)
        );
        verify(transactionService, never()).createTransaction(any(), anyDouble(), any(), any());
    }

    @Test
    void testTransferMoney_userNotFound_returnNotFound() {
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();

        when(userRepository.getReferenceById(fromUserId)).thenReturn(null);
        when(userRepository.getReferenceById(toUserId)).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                userService.transferMoney(fromUserId, toUserId, 50.0)
        );
        verify(transactionService, never()).createTransaction(any(), anyDouble(), any(), any());
    }

    @Test
    void testGetUserAccounts_validData_returnSuccess() {
        UUID userId = user.getId();
        Account account2 = Account.builder().id(UUID.randomUUID()).build();
        user.getAccounts().add(account2);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        List<UUID> accountIds = userService.getUserAccounts(userId);

        assertEquals(2, accountIds.size());
        assertTrue(accountIds.contains(user.getAccounts().get(0).getId()));
        assertTrue(accountIds.contains(account2.getId()));
    }

    @Test
    void testGetUserAccounts_userNotFound_returnNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        when(userRepository.existsById(unknownUserId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                userService.getUserAccounts(unknownUserId));
    }

    @Test
    void testGetUserAccounts_noAccounts_returnEmptyList() {
        UUID userId = user.getId();
        user.getAccounts().clear();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        List<UUID> accountIds = userService.getUserAccounts(userId);

        assertEquals(0, accountIds.size());
    }

    @Test
    void testGetUserAccountBalance_validData_returnSuccess() {
        UUID userId = user.getId();
        UUID accountId = user.getAccounts().get(0).getId();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        Double balance = userService.getUserAccountBalance(userId, accountId);

        assertEquals(user.getAccounts().get(0).getBalance(), balance);
    }

    @Test
    void testGetUserAccountBalance_userNotFound_returnNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        when(userRepository.existsById(unknownUserId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                userService.getUserAccountBalance(unknownUserId, accountId)
        );
    }

    @Test
    void testGetUserAccountBalance_accountNotFound_returnNotFound() {
        UUID userId = user.getId();
        UUID unknownAccountId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        assertThrows(NotFoundException.class, () ->
                userService.getUserAccountBalance(userId, unknownAccountId)
        );
    }

}
package clf.integra.backend.service;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.exceptions.BalanceUpdateFailedException;
import clf.integra.backend.exceptions.InsufficientFundsException;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.model.Account;
import clf.integra.backend.model.Branch;
import clf.integra.backend.model.RandomUtils;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.BranchRepository;
import clf.integra.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private RandomUtils randomUtils;

    private User user;
    private Account account;
    private UUID userId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        account = Account.builder()
                .id(UUID.randomUUID())
                .balance(100.0)
                .build();
        user=User.builder()
                .id(userId)
                .firstName("John")
                .middleName("Mike")
                .lastName("Doe")
                .accounts(new ArrayList<>())
                .build();
        user.getAccounts().add(account);
    }

    @Test
    void TestAddUserWithName_ValidData_returnSuccess(){
        UUID branchId = UUID.randomUUID();
        Branch branch=Branch.builder().id(branchId).build();

        when(branchRepository.findById(branchId)).thenReturn(Optional.of(branch));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        UUID newUserId = userService.addUserWithName("John", "Mike", "Doe", branchId);

        assertNotNull(newUserId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void TestAddUserWithName_NoBranch_returnNoSuchElement(){
        UUID branchId = UUID.randomUUID();

        when(branchRepository.findById(branchId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, ()->
                userService.addUserWithName("John","Mike","Doe", branchId));
    }

    @Test
    void TestAddBalance_WhenRandomBelow_ReturnSuccess(){
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(randomUtils.random()).thenReturn(0.5);

        double result = userService.addBalance(userId, 100.0);

        assertEquals(200.0, result);
        verify(userRepository).save(user);
    }

    @Test
    void TestAddBalance_WhenRandomAbove_ReturnFailure(){
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(randomUtils.random()).thenReturn(0.9);

        assertThrows(BalanceUpdateFailedException.class, ()->
                    userService.addBalance(userId, 100.0));
    }

    @Test
    void TestAddBalance_UserNotFound_ReturnNotFound(){
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, ()->
                userService.addBalance(userId,100.0));
    }

    @Test
    void TestgetUserBalanaceByID_ValidData_ReturnSuccess(){
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        double totalBalance = userService.getUserTotalBalanceById(userId);

        assertEquals(100.0,totalBalance);
    }

    @Test
    void TestgetUserBalanaceByID_IdNull_ReturnFailure(){
        assertThrows(IllegalArgumentException.class, ()->
                userService.getUserTotalBalanceById(null));
    }

    @Test
    void TestgetAllUserByBranch(){
        UUID branchId = UUID.randomUUID();
        when(userRepository.findByBranchId(branchId)).thenReturn(List.of(user));

        List<UserDTO> result= userService.getAllUsersByBranch(branchId);

        assertEquals(1,result.size());
        assertEquals("John",result.get(0).firstName());
    }

    @Test
    void TestCollectTaxesAndFeesFromBranch_ValidData_ReturnSuccess(){
        UUID branchId = UUID.randomUUID();
        when(userRepository.findByBranchId(branchId)).thenReturn(List.of(user));

        double revenue = userService.collectTaxesAndFeesFromBranch(branchId);

        assertTrue(revenue > 0);
        verify(userRepository).saveAll(anyList());
    }

    @Test
    void TestCollectTaxesAndFeesFromBranch_NoBranch_ReturnFailure(){
        UUID branchId = UUID.randomUUID();
        when(userRepository.findByBranchId(branchId)).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, ()-> userService.collectTaxesAndFeesFromBranch(branchId));
    }

    @Test
    void testCalculateFee_Below100() {
        double balance = 50.0;
        double expectedFee = balance * 0.1;

        double actualFee = userService.calculateFee(balance);

        assertEquals(expectedFee, actualFee);
    }

    @Test
    void testCalculateFee_Equal100() {
        double balance = 100.0;
        double expectedFee = 10.0;

        double actualFee = userService.calculateFee(balance);

        assertEquals(expectedFee, actualFee);
    }

    @Test
    void testCalculateFee_Above100() {
        double balance = 150.0;
        double expectedFee = 10.0;

        double actualFee = userService.calculateFee(balance);

        assertEquals(expectedFee, actualFee);
    }

    @Test
    void TestTransferMoney_ValidData_ReturnSuccess() throws NotFoundException, InsufficientFundsException {
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
    }

    @Test
    void TestTransferMoney_InsufficientFunds_ReturnFailure() {
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
    }

    @Test
    void TestTransferMoney_UserNotFound_ReturnNotFound() {
        UUID fromUserId = UUID.randomUUID();
        UUID toUserId = UUID.randomUUID();

        when(userRepository.getReferenceById(fromUserId)).thenReturn(null);
        when(userRepository.getReferenceById(toUserId)).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                userService.transferMoney(fromUserId, toUserId, 50.0)
        );
    }

    @Test
    void TestGetUserAccounts_ValidData_ReturnSuccess() {
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
    void TestGetUserAccounts_UserNotFound_ReturnNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        when(userRepository.existsById(unknownUserId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                userService.getUserAccounts(unknownUserId));
    }

    @Test
    void TestGetUserAccounts_NoAccounts() {
        UUID userId = user.getId();
        user.getAccounts().clear();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        List<UUID> accountIds = userService.getUserAccounts(userId);

        assertEquals(0, accountIds.size());
    }

    @Test
    void TestGetUserAccountBalance_ValidData_ReturnSuccess() {
        UUID userId = user.getId();
        UUID accountId = user.getAccounts().get(0).getId();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        Double balance = userService.getUserAccountBalance(userId, accountId);

        assertEquals(user.getAccounts().get(0).getBalance(), balance);
    }

    @Test
    void TestGetUserAccountBalance_UserNotFound_ReturnNotFound() {
        UUID unknownUserId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        when(userRepository.existsById(unknownUserId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                userService.getUserAccountBalance(unknownUserId, accountId)
        );
    }

    @Test
    void TestGetUserAccountBalance_AccountNotFound_ReturnNotFound() {
        UUID userId = user.getId();
        UUID unknownAccountId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        assertThrows(NotFoundException.class, () ->
                userService.getUserAccountBalance(userId, unknownAccountId)
        );
    }

}

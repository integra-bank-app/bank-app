package clf.integra.backend.service;

import clf.integra.backend.dto.FeeTaxTransactionDTO;
import clf.integra.backend.dto.UserTransactionDTO;
import clf.integra.backend.model.Account;
import clf.integra.backend.model.FeeTaxTransaction;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.FeeTaxTransactionRepository;
import clf.integra.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FeeTaxServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FeeTaxTransactionRepository taxRepository;

    @InjectMocks
    private FeeTaxService feeTaxService;

    private User testUser;
    private Account testAccount;
    private FeeTaxTransaction testTransaction;
    private UUID userId;

    @BeforeEach
    void setUp() throws Exception {
        testAccount = new Account();
        testAccount.setBalance(150.0);
        userId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .firstName("John")
                .middleName("Mike")
                .lastName("Doe")
                .accounts(List.of(testAccount))
                .build();

        testUser.setAccounts(List.of(testAccount));

        testTransaction = FeeTaxTransaction.builder()
                .user(testUser)
                .amount(10.0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCalculateFee_BalanceNegative_ShouldReturnZero() {
        double negativeBalance = -50.0;

        double result = feeTaxService.calculateFee(negativeBalance);

        assertEquals(0.0, result);
    }

    @Test
    void testCalculateFee_BalanceLessThan100_ShouldReturnTenPercent() {
        double balance = 50.0;

        double result = feeTaxService.calculateFee(balance);

        assertEquals(5.0, result);
    }

    @Test
    void testCalculateFee_BalanceOver100_ShouldReturnTen() {
        double balance = 200.0;

        double result = feeTaxService.calculateFee(balance);

        assertEquals(10.0, result);
    }

    @Test
    void testDeductFeeAndTax_ShouldUpdateBalanceAndSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        double deductedAmount = feeTaxService.deductFeeAndTax(testUser);

        assertEquals(10.0, deductedAmount);
        assertEquals(140.0, testAccount.getBalance());

        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testGetFeeTaxesFromLastNDays_ShouldReturnFilteredTransactions() {
        List<FeeTaxTransaction> mockTransactions = Arrays.asList(testTransaction);

        when(taxRepository.findByCreatedAtAfter(any(LocalDateTime.class)))
                .thenReturn(mockTransactions);

        List<FeeTaxTransactionDTO> result = feeTaxService.getFeeTaxesFromLastNDays(7);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(taxRepository, times(1))
                .findByCreatedAtAfter(any(LocalDateTime.class));
    }

    @Test
    void testFeeAndTaxUsers_ShouldProcessAllUsers() {
        UUID idUser1 = UUID.randomUUID();
        UUID idUser2 = UUID.randomUUID();
        User user1 = createUserWithBalance(idUser1, "John", "A", "Doe", 50.0);
        User user2 = createUserWithBalance(idUser2, "Jane", "B", "Smith", 200.0);


        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(userRepository.findById(idUser1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(idUser2)).thenReturn(Optional.of(user2));
        when(taxRepository.save(any(FeeTaxTransaction.class)))
                .thenReturn(new FeeTaxTransaction());

        feeTaxService.feeAndTaxUsers();

        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(2)).save(any(User.class));
        verify(taxRepository, times(2)).save(any(FeeTaxTransaction.class));

        assertEquals(45.0, userRepository.findById(idUser1).get().getAccounts().getFirst().getBalance());
        assertEquals(190.0, userRepository.findById(idUser2).get().getAccounts().getFirst().getBalance());
    }

    private User createUserWithBalance(UUID id, String firstName, String middleName, String lastName, double balance) {
        User user = User.builder()
                .id(id)
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .build();

        Account account = Account.builder()
                .id(UUID.randomUUID())
                .balance(balance)
                .user(user)
                .build();

        user.getAccounts().add(account);
        return user;
    }

    @Test
    void testGetUserFeeTaxesTransaction_existingTransaction_returnTransactionList(){
        FeeTaxTransaction f1 = FeeTaxTransaction.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .amount(10.0)
                .createdAt(LocalDateTime.now())
                .build();
        FeeTaxTransaction fOther = FeeTaxTransaction.builder()
                .id(UUID.randomUUID())
                .user(User.builder().id(UUID.randomUUID()).build())
                .amount(5.0)
                .createdAt(LocalDateTime.now())
                .build();

        when(taxRepository.findAll()).thenReturn(List.of(f1, fOther));

        List<UserTransactionDTO> result = feeTaxService.getUserFeeTaxesTransaction(userId);

        assertEquals(1, result.size());
        assertEquals(f1.getId(), result.get(0).transactionId());
        assertEquals(userId, result.get(0).fromUserId());
        assertNull(result.get(0).toUserId());
    }

    @Test
    void testGetUserFeeTaxesTransaction_noTransactions_returnEmptyList(){
        when(taxRepository.findAll()).thenReturn(List.of());

        List<UserTransactionDTO> result = feeTaxService.getUserFeeTaxesTransaction(userId);

        assertTrue(result.isEmpty());
    }
}
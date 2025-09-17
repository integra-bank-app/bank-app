package clf.integra.backend.service;

import clf.integra.backend.dto.FeeTaxTransactionDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setBalance(150.0);

        testUser = new User();
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
        User user1 = new User();
        Account account1 = new Account();
        account1.setBalance(50.0);
        user1.setAccounts(List.of(account1));

        User user2 = new User();
        Account account2 = new Account();
        account2.setBalance(200.0);
        user2.setAccounts(List.of(account2));

        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(taxRepository.save(any(FeeTaxTransaction.class)))
                .thenReturn(new FeeTaxTransaction());

        feeTaxService.feeAndTaxUsers();

        verify(userRepository, times(1)).findAll();
        verify(userRepository, times(2)).save(any(User.class));
        verify(taxRepository, times(2)).save(any(FeeTaxTransaction.class));

        assertEquals(45.0, account1.getBalance());
        assertEquals(190.0, account2.getBalance());
    }
}
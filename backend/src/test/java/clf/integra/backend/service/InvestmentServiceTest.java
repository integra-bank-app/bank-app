package clf.integra.backend.service;

import clf.integra.backend.dto.InvestmentDTO;
import clf.integra.backend.model.Branch;
import clf.integra.backend.model.Investment;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.InvestmentsRepository;
import clf.integra.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @Mock
    private InvestmentsRepository investmentsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InvestmentService investmentService;

    private UUID userId;
    private User user;
    private UUID investmentId;
    private Investment investment;

    @BeforeEach
    void setUp() {
        Branch branch = Branch.builder().id(UUID.randomUUID()).build();
        userId = UUID.randomUUID();
        investmentId = UUID.randomUUID();

        investment = Investment.builder()
                .id(investmentId)
                .risk(3)
                .balance(200.0)
                .build();

        user = User.builder()
                .id(userId)
                .branch(branch)
                .investments(Set.of(investment))
                .build();

    }

    //creatInvestment

    @Test
    void createInvestment_validData_returnsGeneratedId() {
        UUID generatedId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(investmentsRepository.save(any(Investment.class)))
                .thenAnswer(invocation -> {
                    Investment inv = invocation.getArgument(0);
                    inv.setId(generatedId);
                    return inv;
                });

        UUID result = investmentService.createInvestment(3, 200.0, userId);
        assertNotNull(result);
        assertEquals(generatedId, result);
    }

    @Test
    void createInvestment_nullBalance_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class,
                () -> investmentService.createInvestment(5, null, userId));
    }

    @Test
    void createInvestment_invalidRisk_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class,
                () -> investmentService.createInvestment(0, 100.0, userId));
        assertThrows(IllegalArgumentException.class,
                () -> investmentService.createInvestment(11, 100.0, userId));
    }

    @Test
    void createInvestment_nullUserId_throwsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class,
                () -> investmentService.createInvestment(5, 100.0, null));
    }

    @Test
    void createInvestment_userNotFound_throwsException() {
        UUID nonExistentUserId = UUID.randomUUID();
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> investmentService.createInvestment(5, 100.0, nonExistentUserId));
    }

    //getInvestmentByUserId

    @Test
    void getInvestmentByUserId_investmentFound_returnsDTO() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        InvestmentDTO dto = investmentService.getInvestmentByUserId(userId, investmentId);

        assertNotNull(dto);
        assertEquals(investment.getRisk(), dto.risk());
        assertEquals(investment.getBalance(), dto.balance());
    }

    @Test
    void getInvestmentByUserId_userNotFound_throwsException() {
        UUID unknownUserId = UUID.randomUUID();
        when(userRepository.findById(unknownUserId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> investmentService.getInvestmentByUserId(unknownUserId, investmentId));
    }

    @Test
    void getInvestmentByUserId_investmentNotFound_throwsException() {
        UUID missingInvestmentId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> investmentService.getInvestmentByUserId(userId, missingInvestmentId));
    }


}

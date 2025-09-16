package clf.integra.backend.service;

import clf.integra.backend.dto.DepositsDTO;
import clf.integra.backend.model.Branch;
import clf.integra.backend.model.Deposits;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.DepositsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class DepositsServiceTest {

    @InjectMocks
    DepositsService depositsService;

    @Mock
    DepositsRepository depositsRepository;

    @BeforeEach
    public void init() {
    }

    @Test
    void testGetUserDeposits() {
        Branch branch = Branch.builder()
                .id(UUID.randomUUID())
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .branch(branch)
                .build();

        Deposits deposit1 = Deposits.builder()
                .user(user)
                .amount(1000.0)
                .interest_rate(3.5)
                .id(UUID.randomUUID())
                .build();

        Deposits deposit2 = Deposits.builder()
                .user(user)
                .amount(2000.0)
                .interest_rate(4.0)
                .id(UUID.randomUUID())
                .build();

        when(depositsRepository.findByUserId(user.getId()))
                .thenReturn(List.of(deposit1, deposit2));

        List<DepositsDTO> result = depositsService.getUserDeposits(user.getId());

        assertEquals(2, result.size());

        DepositsDTO dto1 = result.getFirst();
        assertEquals(deposit1.getId(), dto1.id());
        assertEquals(1000.0, dto1.amount());
        assertEquals(3.5, dto1.interest_rate());

        DepositsDTO dto2 = result.get(1);
        assertEquals(deposit2.getId(), dto2.id());
        assertEquals(2000.0, dto2.amount());
        assertEquals(4.0, dto2.interest_rate());

        verify(depositsRepository, times(1)).findByUserId(user.getId());
    }

}

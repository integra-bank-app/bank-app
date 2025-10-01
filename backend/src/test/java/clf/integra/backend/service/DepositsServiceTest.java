package clf.integra.backend.service;

import clf.integra.backend.dto.DepositImportDTO;
import clf.integra.backend.dto.DepositsDTO;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.model.Branch;
import clf.integra.backend.model.Deposits;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.DepositsRepository;
import clf.integra.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class DepositsServiceTest {

    @InjectMocks
    DepositsService depositsService;

    @Mock
    DepositsRepository depositsRepository;

    @Mock
    UserRepository userRepository;

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

    @Test
    void testBulkImport_userNotFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        DepositImportDTO dto = new DepositImportDTO(1000.0, 3.5, id);

        assertThrows(NotFoundException.class, () -> {
            depositsService.bulkImport(List.of(dto));
        });

        verifyNoInteractions(depositsRepository);
    }

    @Test
    void testBulkImport_successfulImport() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        DepositImportDTO dto1 = new DepositImportDTO(1000.0, 3.5, id);
        DepositImportDTO dto2 = new DepositImportDTO(2000.0, 4.0, id);

        depositsService.bulkImport(List.of(dto1, dto2));

        verify(userRepository, times(2)).findById(id);
        verify(depositsRepository, times(2)).save(org.mockito.ArgumentMatchers.any(Deposits.class));
    }
}

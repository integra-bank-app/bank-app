package clf.integra.backend.service;

import clf.integra.backend.dto.DepositImportDTO;
import clf.integra.backend.dto.DepositsDTO;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.model.Deposits;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.DepositsRepository;
import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepositsService {
    private final DepositsRepository depositsRepository;
    private final UserRepository userRepository;

    public List<DepositsDTO> getUserDeposits(UUID id) {
        return depositsRepository.findByUserId(id)
                .stream()
                .map(deposit -> new DepositsDTO(deposit.getId(),deposit.getInterest_rate(),deposit.getAmount()))
                .toList();
    }

    public void bulkImport(List<DepositImportDTO> depositsDTOs) {
        for (DepositImportDTO depositImportDTO : depositsDTOs) {
            User user = userRepository.findById(depositImportDTO.userId()).orElseThrow(() -> new NotFoundException("User not found"));
            Deposits deposit = Deposits.builder()
                    .user(user)
                    .interest_rate(depositImportDTO.interest_rate())
                    .amount(depositImportDTO.amount())
                    .build();
            depositsRepository.save(deposit);
        }
    }
}

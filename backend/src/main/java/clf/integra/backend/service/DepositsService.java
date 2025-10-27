package clf.integra.backend.service;

import clf.integra.backend.dto.DepositImportDTO;
import clf.integra.backend.dto.DepositsDTO;
import clf.integra.backend.dto.DepositsExportDTO;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.mapper.DepositsExportMapper;
import clf.integra.backend.model.Deposits;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.DepositsRepository;
import clf.integra.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepositsService {
    private final DepositsRepository depositsRepository;
    private final UserRepository userRepository;

    @Transactional
    public UUID createDeposits(DepositsDTO depositsDTO, UUID userId) {
        if (depositsDTO.amount() == null || depositsDTO.amount() <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        if (depositsDTO.interest_rate() == null || depositsDTO.interest_rate() <= 0) {
            throw new IllegalArgumentException("Interest rate must be positive.");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));
        Deposits deposits = Deposits
                .builder()
                .user(user)
                .amount(depositsDTO.amount())
                .interest_rate(depositsDTO.interest_rate()).build();
        return depositsRepository.save(deposits).getId();
    }

    public List<DepositsDTO> getUserDeposits(UUID id) {
        return depositsRepository.findByUserId(id)
                .stream()
                .map(deposit -> new DepositsDTO(deposit.getId(), deposit.getInterest_rate(), deposit.getAmount()))
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

    public String bulkExport() {
        List<Deposits> deposits = depositsRepository.findAll();

        List<DepositsExportDTO> depositDTOs = deposits.stream()
                .map(DepositsExportMapper::toDTO)
                .collect(Collectors.toList());
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            return mapper.writeValueAsString(depositDTOs);
        } catch (Exception e) {
            throw new RuntimeException("Error generating JSON", e);
        }
    }

}

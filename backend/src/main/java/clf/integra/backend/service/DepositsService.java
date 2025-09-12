package clf.integra.backend.service;

import clf.integra.backend.dto.DepositsDTO;
import clf.integra.backend.repository.DepositsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepositsService {
    private final DepositsRepository depositsRepository;

    public List<DepositsDTO> getUserDeposits(UUID id) {
        return depositsRepository.findByUserId(id)
                .stream()
                .map(deposit -> new DepositsDTO(deposit.getId(),deposit.getInterest_rate(),deposit.getAmount()))
                .toList();
    }
}

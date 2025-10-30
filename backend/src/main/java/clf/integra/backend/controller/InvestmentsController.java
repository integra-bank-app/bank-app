package clf.integra.backend.controller;

import clf.integra.backend.dto.InvestmentDTO;

import clf.integra.backend.dto.InvestmentHistoryDTO;
import clf.integra.backend.service.InvestmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class InvestmentsController {
    private InvestmentService investmentService;

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userPermissionService.canAccessUserData(#userId, authentication))")
    @PostMapping("users/{userId}/investments")
    public ResponseEntity<UUID> createInvestment(@PathVariable UUID userId, @RequestBody InvestmentDTO investmentDTO) {
        UUID uuid = investmentService.createInvestment(investmentDTO.risk(), investmentDTO.balance(), userId);
        return ResponseEntity.ok(uuid);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userPermissionService.canAccessUserData(#userId, authentication))")
    @GetMapping("users/{userId}/investments/{investmentsId}")
    public ResponseEntity<InvestmentDTO> getInvestmentByUserId(@PathVariable("userId") UUID userId, @PathVariable("investmentsId") UUID investmentsId) {
        InvestmentDTO investmentDTO = investmentService.getInvestmentByUserId(userId, investmentsId);
        return ResponseEntity.ok(investmentDTO);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userPermissionService.canAccessUserData(#userId, authentication))")
    @GetMapping("users/{userId}/investment")
    public ResponseEntity<List<InvestmentDTO>> getAllInvestmentsByUserId(@PathVariable("userId") UUID userId) {
        List<InvestmentDTO> investments = investmentService.getAllInvestmentsByUser(userId);
        return ResponseEntity.ok(investments);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userPermissionService.canAccessUserData(#userId, authentication))")
    @GetMapping("users/{userId}/investments/history")
    public ResponseEntity<List<InvestmentHistoryDTO>> getInvestmentHistoryByUser(@PathVariable UUID userId) {
        List<InvestmentHistoryDTO> history = investmentService.getInvestmentHistoryByUser(userId);
        return ResponseEntity.ok(history);
    }
}

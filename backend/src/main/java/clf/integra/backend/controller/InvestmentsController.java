package clf.integra.backend.controller;

import clf.integra.backend.dto.InvestmentDTO;
import clf.integra.backend.service.InvestmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class InvestmentsController {
    private InvestmentService investmentService;

    @PostMapping("users/{userId}/investments")
    public ResponseEntity<UUID> createInvestment(@PathVariable UUID userId, @RequestBody InvestmentDTO investmentDTO) {
        UUID uuid = investmentService.createInvestment(investmentDTO.risk(), investmentDTO.balance(), userId);
        return ResponseEntity.ok(uuid);
    }

    @GetMapping("users/{userId}/investments/{investmentsId}")
    public ResponseEntity<InvestmentDTO> getInvestmentByUserId(@PathVariable("userId") UUID userId, @PathVariable("investmentsId") UUID investmentsId) {
        InvestmentDTO investmentDTO = investmentService.getInvestmentByUserId(userId, investmentsId);
        return ResponseEntity.ok(investmentDTO);
    }
}

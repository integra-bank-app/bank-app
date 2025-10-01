package clf.integra.backend.controller;

import clf.integra.backend.dto.FeeTaxTransactionDTO;
import clf.integra.backend.service.FeeTaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FeeTaxController {
    private final FeeTaxService feeTaxService;

    @GetMapping("/fee-tax-transactions")
    public ResponseEntity<List<FeeTaxTransactionDTO>> getFeeTaxesFromLastNDays(@RequestParam int lastNDays) {
        if (lastNDays <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(feeTaxService.getFeeTaxesFromLastNDays(lastNDays));
    }
}

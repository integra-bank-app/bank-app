package clf.integra.backend.controller;

import clf.integra.backend.dto.DepositImportRequest;
import clf.integra.backend.dto.DepositsDTO;
import clf.integra.backend.service.DepositsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class DepositController {
    private DepositsService depositsService;

    @PostMapping("/deposits/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @userPermissionService.canAccessUserData(#userId, authentication)")
    public ResponseEntity<UUID> createDeposit(
            @PathVariable UUID userId,
            @RequestBody @Valid DepositsDTO depositsDTO
    ) {
        UUID depositId = depositsService.createDeposits(depositsDTO, userId);
        return ResponseEntity.ok(depositId);
    }


    @PostMapping("/deposits/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity importDeposits(@Valid @RequestBody DepositImportRequest request) {
        depositsService.bulkImport(request.depositImports());
        return ResponseEntity.ok("Deposits imported successfully");
    }

}

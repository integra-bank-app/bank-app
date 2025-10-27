package clf.integra.backend.controller;

import clf.integra.backend.dto.DepositImportRequest;
import clf.integra.backend.dto.DepositsDTO;
import clf.integra.backend.service.DepositsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.nio.charset.StandardCharsets;

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

    @GetMapping("/deposits/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportDeposits() {
        try {
            String json = depositsService.bulkExport(); // Should return JSON string
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"deposits_export.json\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(jsonBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Error exporting deposits: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }
}

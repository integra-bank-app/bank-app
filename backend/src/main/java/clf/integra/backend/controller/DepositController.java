package clf.integra.backend.controller;

import clf.integra.backend.dto.DepositImportRequest;
import clf.integra.backend.service.DepositsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.nio.charset.StandardCharsets;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class DepositController {
    private DepositsService depositsService;

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

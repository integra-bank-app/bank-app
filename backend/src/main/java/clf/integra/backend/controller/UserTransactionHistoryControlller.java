package clf.integra.backend.controller;

import clf.integra.backend.dto.UserTransactionDTO;
import clf.integra.backend.service.UserTransactionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserTransactionHistoryControlller {
    private final UserTransactionHistoryService userTransactionHistoryService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') and @userPermissionService.canAccessUserData(#userId, authentication)")
    @GetMapping("/users/{userId}/transactions")
    public ResponseEntity<List<UserTransactionDTO>> getUserTransactionHistory(@PathVariable UUID userId) {
        List<UserTransactionDTO> transations = userTransactionHistoryService.getTransactionHistory(userId);
        if (transations.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transations);
    }
}

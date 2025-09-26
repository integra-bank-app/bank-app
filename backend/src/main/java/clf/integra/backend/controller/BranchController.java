package clf.integra.backend.controller;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BranchController {
    private final UserService userService;

    @PostMapping("branches/{branchId}/collect-taxes-and-fees")
    public ResponseEntity<Double> collectTaxesAndFeesFromBranch(@PathVariable UUID branchId) {
        try {
            double revenue = userService.
                    collectTaxesAndFeesFromBranch(branchId);
            if (revenue == 0) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
            }

            return ResponseEntity.ok(revenue);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("branches/{branchId}/users")
    public ResponseEntity<List<UserDTO>> getUsersByBranch(@PathVariable UUID branchId) {
        List<UserDTO> users = userService.getAllUsersByBranch(branchId);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(users);
    }
}

package clf.integra.backend.controller;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.io.IOException;
import java.util.UUID;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin("*")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class BranchController {
    private final UserService userService;

    @PostMapping("branches/{branchId}/collect-taxes-and-fees")
    public ResponseEntity<Double> collectTaxesAndFeesFromBranch(@PathVariable UUID branchId) throws IOException {
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
    public ResponseEntity<Page<UserDTO>> getUsersByBranch(
            @PathVariable UUID branchId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        int[] allowedSizes = {5, 10, 20, 50, 100, 1000, 10000, 100000};
        int pageSize = Arrays.stream(allowedSizes)
                .filter(s -> s == size)
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Invalid page size. Allowed values: " + Arrays.toString(allowedSizes))
                );
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<UserDTO> users = userService.getAllUsersByBranchAndPage(branchId, pageable);
        if (users.getTotalElements() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}

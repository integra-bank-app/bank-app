package clf.integra.backend.controller;

import clf.integra.backend.dto.BalanceDTO;
import clf.integra.backend.dto.DepositDTO;
import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.exceptions.BalanceUpdateFailedException;
import clf.integra.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public UUID addUser(@RequestBody UserDTO user) {
        return userService.addUserWithName(user.firstName(), user.middleName(), user.lastName());
    }

    @PostMapping("/users/{userId}/balance")
    public ResponseEntity<Double> addUserBalance(@PathVariable("userId") UUID userId, @RequestBody BalanceDTO balance) {
        double value = balance.value();
        if (value < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            double finalBalance = userService.addBalance(userId, value);
            return new ResponseEntity<>(finalBalance, HttpStatus.OK);
        } catch (BalanceUpdateFailedException e) {
            return new ResponseEntity<>(HttpStatus.PAYMENT_REQUIRED);
        }

    }

    /**
     * Endpoint to get users by branchId
     *
     */
    @GetMapping("branches/{branchId}/users")
    public ResponseEntity<List<UserDTO>> getUsersByBranch(@PathVariable UUID branchId) {
        List<UserDTO> users = userService.getAllUsersByBranch(branchId);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}/balance")
    public ResponseEntity<Double> getUserBalanceById(@PathVariable UUID id) {
        Double balance = userService.getUserBalanceById(id);
        if (balance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(balance);
    }

    @GetMapping("users/{id}/deposit")
    public ResponseEntity<List<DepositDTO>> getUserDeposits (@PathVariable UUID id) {
        List<DepositDTO> deposits = userService.getUserDeposits(id);
        if (deposits == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(deposits);
    }
}

package clf.integra.backend.controller;

import clf.integra.backend.dto.BalanceDTO;
import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.exceptions.InsufficientFundsException;
import clf.integra.backend.exceptions.UserNotFoundException;
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
        double value  = balance.value();
        if (value < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        double finalBalance = userService.addBalance(userId, value);
        return new ResponseEntity<>(finalBalance, HttpStatus.OK);

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

    @PostMapping("/user/transfer")
    public ResponseEntity<Double> transferMoney(@RequestParam UUID fromUserId, @RequestParam UUID toUserId, @RequestParam double amount) throws InsufficientFundsException, UserNotFoundException {
        double newBalance = userService.transferMoney(fromUserId, toUserId, amount);
        return ResponseEntity.ok(newBalance);
    }
}
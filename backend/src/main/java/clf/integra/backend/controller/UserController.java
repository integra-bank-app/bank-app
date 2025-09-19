package clf.integra.backend.controller;

import clf.integra.backend.dto.BalanceDTO;
import clf.integra.backend.dto.DepositsDTO;
import clf.integra.backend.service.DepositsService;
import clf.integra.backend.dto.UserWithBranchDTO;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.exceptions.InsufficientFundsException;
import clf.integra.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final DepositsService depositsService;

    @PostMapping("/users")
    public UUID addUser(@RequestBody UserWithBranchDTO user) {
        return userService.addUserWithName(user.firstName(), user.middleName(), user.lastName(), user.branchId());
    }

    @PostMapping("/users/{userId}/balance")
    public ResponseEntity<Double> addUserBalance(@PathVariable("userId") UUID userId, @RequestBody BalanceDTO balance) throws IOException {
        double value = balance.value();
        if (value < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        double finalBalance = userService.addBalance(userId, value);
        return new ResponseEntity<>(finalBalance, HttpStatus.OK);
    }

    @GetMapping("/users/{id}/balance")
    public ResponseEntity<Double> getUserTotalBalanceById(@PathVariable UUID id) throws NotFoundException {
        Double balance = userService.getUserTotalBalanceById(id);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/users/transfer")
    public ResponseEntity<Double> transferMoney(@RequestParam UUID fromUserId, @RequestParam UUID toUserId, @RequestParam double amount) throws InsufficientFundsException, NotFoundException {
        double newBalance = userService.transferMoney(fromUserId, toUserId, amount);
        return ResponseEntity.ok(newBalance);
    }

    @GetMapping("/users/{id}/accounts")
    public ResponseEntity<List<UUID>> getUserAccounts(@PathVariable UUID id) throws NotFoundException {
        List<UUID> accounts = userService.getUserAccounts(id);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/users/{id}/accounts/{accountId}")
    public ResponseEntity<Double> getUserAccountBalance(@PathVariable UUID id, @PathVariable UUID accountId) throws NotFoundException {
        Double balance = userService.getUserAccountBalance(id, accountId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("users/{id}/deposits")
    public ResponseEntity<List<DepositsDTO>> getUserDeposits(@PathVariable UUID id) {
        List<DepositsDTO> deposits = depositsService.getUserDeposits(id);
        if (deposits.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(deposits);
    }

}
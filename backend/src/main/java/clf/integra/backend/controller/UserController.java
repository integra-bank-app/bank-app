package clf.integra.backend.controller;

import clf.integra.backend.dto.BalanceDTO;
import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

        //Simulate a random chance of 20% (in the issue is 10%, but I had bad luck) for the operation to fail
        double randomValue = Math.random();
        if (randomValue >= 0.8) {
            return new ResponseEntity<>(HttpStatus.PAYMENT_REQUIRED);
        }

        double finalBalance = userService.addBalance(userId, value);
        return new ResponseEntity<>(finalBalance, HttpStatus.OK);

    }
}

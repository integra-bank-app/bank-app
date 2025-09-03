package clf.integra.backend.controller;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    public UUID addUser(@RequestBody UserDTO user) {
        return userService.addUserByName(user.firstName(), user.middleName(), user.lastName());
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint to get users by branchId
    @GetMapping("branches/{branchId}/users")
    public ResponseEntity<List<String>> getUsersByBranch(@PathVariable UUID branchId) {
        List<String> users = userService.getAllUsersByBranch(branchId);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(users);
    }
}

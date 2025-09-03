package clf.integra.backend.controller;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.service.UserService;
import lombok.RequiredArgsConstructor;
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
    /* Endpoint to get users by branchId */
    @GetMapping("branches/{branchId}/users")
    public ResponseEntity<List<String>> getUsersByBranch(@PathVariable UUID branchId) {
        List<String> users = userService.getAllUsersByBranch(branchId);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(users);
    }
}

package clf.integra.backend.controller;


import clf.integra.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import clf.integra.backend.repository.UserRepository;


import java.util.List;
import java.util.UUID;
@RestController
public class TestController {
    private final UserRepository userRepository;
    private final UserService userService;

    public TestController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

    //Testing creation of the repository
    @GetMapping("/test-repo")
    public String testRepo() {
        userService.addUserByName("A", "B", "C");
        return userRepository.getAllUsers().toString();
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

    // Test endpoint to get users by branchId
    @GetMapping("/test/users")
    public ResponseEntity<List<String>> testFirstBranchUsers() {
        UUID existingBranchId = userRepository.getAllUsers().getFirst().getBranchId();
        return getUsersByBranch(existingBranchId);
    }
}

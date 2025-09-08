package clf.integra.backend.controller;


import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import clf.integra.backend.repository.UserRepository;
import java.util.UUID;


@RestController
public class TestController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserController userController;

    public TestController(UserRepository userRepository, UserService userService, UserController userController) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userController = userController;
    }

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }

    //Testing creation of the repository
    @GetMapping("/test-repo")
    public String testRepo() {
        userService.addUserWithName("A", "B", "C");
        return userRepository.getAllUsers().toString();
    }

    /**
    * Endpoint to collect taxes and fees from a branch
    * If the branchId is null or the branch has no customers, returns 404 NOT FOUND
    * If the revenue collected is 0, returns 417 EXPECTATION FAILED
    * Otherwise, returns 200 OK with the revenue amount
     */
    @PostMapping("branches/{branchId}/collect-taxes-and-fees")
    public ResponseEntity<Double> collectTaxesAndFeesFromBranch(@PathVariable UUID branchId) {
        try {
            double revenue = userService.collectTaxesAndFeesFromBranch(branchId);
            if (revenue == 0) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
            }

            return ResponseEntity.ok(revenue);

        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}

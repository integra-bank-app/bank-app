package clf.integra.backend.controller;


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
        return userRepository.getAllUsers().toString();
    }

    //Testing the taxes and fees system
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

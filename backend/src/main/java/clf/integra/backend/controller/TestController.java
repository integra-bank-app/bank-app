package clf.integra.backend.controller;


import clf.integra.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.UserRepository;
import clf.integra.backend.service.UserService;

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
}

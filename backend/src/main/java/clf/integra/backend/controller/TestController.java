package clf.integra.backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.UserRepository;


@RestController
public class TestController {

    private final UserRepository userRepository;

    public TestController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}

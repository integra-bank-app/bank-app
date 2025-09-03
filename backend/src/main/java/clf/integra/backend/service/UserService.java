package clf.integra.backend.service;

import clf.integra.backend.model.User;
import clf.integra.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /* Input: Full name of the User. Output: UUID of the newly added User*/
    public UUID addUserByName(String firstName, String middleName, String lastName) {
        UUID uuid = generateUUID();
        User newUser = new User(uuid, firstName, middleName, lastName, 0);
        userRepository.addUser(newUser);
        return uuid;
    }

    /* Added a generateUUID in case we want to change how the UUID is generated */
    public UUID generateUUID() {
        return UUID.randomUUID();
    }
}


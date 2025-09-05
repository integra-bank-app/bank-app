package clf.integra.backend.service;

import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UUID addUserWithName(String firstName, String middleName, String lastName) {
        UUID uuid = generateUUID();
        User newUser = new User(uuid, firstName, middleName, lastName, 0);
        userRepository.addUser(newUser);
        return uuid;
    }

    public double addBalance(UUID uuid, double amount) {
        if (!userRepository.userExists(uuid)) {
            throw new NotFoundException("User not found");
        }

        User user = userRepository.getUserById(uuid);
        user.setBalance(user.getBalance() + amount);

        return user.getBalance();
    }

    public UUID generateUUID() {
        return UUID.randomUUID();
    }
}

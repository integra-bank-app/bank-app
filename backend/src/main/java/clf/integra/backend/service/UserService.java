package clf.integra.backend.service;

import clf.integra.backend.model.User;
import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UUID addUserWithName(String firstName, String middleName, String lastName) {
        UUID uuid = generateUUID();
        User newUser = new User(uuid, firstName, middleName, lastName, 0, null);
        userRepository.addUser(newUser);
        return uuid;
    }

    public UUID generateUUID() {
        return UUID.randomUUID();
    }

    public Double getUserBalanceById(UUID id) {
        return userRepository.getUserBalanceById(id);

    }

    public List<String> getAllUsersByBranch(UUID branchId) {
        return userRepository.getAllUsers().stream()
                .filter(user -> branchId.equals(user.getBranchId()))
                .map(user -> {
                    if (user.getMiddleName() == null || user.getMiddleName().isBlank()) {
                        return user.getFirstName() + " " + user.getLastName();
                    }
                    return user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName();
                })
                .collect(Collectors.toList());
    }
}
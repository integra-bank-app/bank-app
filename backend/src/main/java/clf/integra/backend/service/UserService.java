package clf.integra.backend.service;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.exceptions.BalanceUpdateFailedException;
import clf.integra.backend.exceptions.NotFoundException;
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

    public double addBalance(UUID uuid, double amount) {
        if (!userRepository.userExists(uuid)) {
            throw new NotFoundException("User not found");
        }

        //Simulate a random chance of 20% (in the issue is 10%, but I had bad luck) for the operation to fail
        double randomValue = Math.random();
        if (randomValue >= 0.8) {
            throw new BalanceUpdateFailedException("An unknown error has occurred intentionally");
        }

        User user = userRepository.getUserById(uuid);
        user.setBalance(user.getBalance() + amount);

        return user.getBalance();
    }

    public UUID generateUUID() {
        return UUID.randomUUID();
    }

    public Double getUserBalanceById(UUID id) {
        return userRepository.getUserBalanceById(id);
    }

    public List<UserDTO> getAllUsersByBranch(UUID branchId) {
        return userRepository.getAllUsers().stream()
                .filter(user -> branchId.equals(user.getBranchId()))
                .map(user -> {
                    if (user.getMiddleName() == null || user.getMiddleName().isBlank()) {
                        return new UserDTO(user.getFirstName(), "", user.getLastName());
                    }
                    return new UserDTO(user.getFirstName(), user.getMiddleName(), user.getLastName());
                })
                .collect(Collectors.toList());
    }
}

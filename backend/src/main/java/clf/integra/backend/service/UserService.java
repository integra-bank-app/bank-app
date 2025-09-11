package clf.integra.backend.service;

import clf.integra.backend.dto.DepositDTO;
import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.exceptions.BalanceUpdateFailedException;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.model.Deposit;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UUID addUserWithName(String firstName, String middleName, String lastName) {
        UUID uuid = generateUUID();
        User newUser = new User(uuid, firstName, middleName, lastName, 0, null, null);
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

    public double collectTaxesAndFeesFromBranch(UUID branchId) {
        if (branchId == null) {
            throw new IllegalArgumentException("Branch ID can not be null!");
        }

        List<User> usersBranch = userRepository.getAllUsers().stream()
                .filter(user -> branchId.equals(user.getBranchId()))
                .toList();

        if (usersBranch.isEmpty()) {
            throw  new IllegalArgumentException("The branch does not have any customer!");
        }

        double revenue = 0;
        for (User user : usersBranch) {
            double userBalance = user.getBalance();
            double fee = calculateFee(userBalance);

            if (fee > 0) {
                user.setBalance(userBalance - fee);
                revenue += fee;
            }
        }
        return revenue;
    }

    public UUID generateUUID() {
        return UUID.randomUUID();
    }

    public double calculateFee(double balance) {
        return balance < 100 ? balance * 0.1 : 10;
    }

    public List<DepositDTO> getUserDeposits(UUID id) {
        return userRepository.getUserDeposits(id);
    }
}

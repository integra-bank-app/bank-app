package clf.integra.backend.service;

import clf.integra.backend.dto.UserDTO;
import clf.integra.backend.exceptions.BalanceUpdateFailedException;
import clf.integra.backend.exceptions.InsufficientFundsException;
import clf.integra.backend.exceptions.NotFoundException;
import clf.integra.backend.exceptions.UserNotFoundException;
import clf.integra.backend.model.User;
import clf.integra.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UUID addUserWithName(String firstName, String middleName, String lastName) {
        UUID uuid = generateUUID();
        User newUser = User.builder()
                .id(uuid)
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .balance(0)
                .build();
        userRepository.save(newUser);
        return uuid;
    }

    @Transactional
    public double addBalance(UUID uuid, double amount) {
        if (!userRepository.existsById(uuid)) {
            throw new NotFoundException("User not found");
        }

        //Simulate a random chance of 20% (in the issue is 10%, but I had bad luck) for the operation to fail
        double randomValue = Math.random();
        if (randomValue >= 0.8) {
            throw new BalanceUpdateFailedException("An unknown error has occurred intentionally");
        }

        User user = userRepository.findById(uuid).orElseThrow(() -> new NotFoundException("User not found"));
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);

        return user.getBalance();
    }

    public Double getUserBalanceById(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException("User ID can not be null!");
        }

        Double balance = userRepository.getBalanceById(id);

        if (balance == null) {
            throw new NotFoundException(String.format("User with the id %s not found", id));
        }

        return balance;
    }

    public List<UserDTO> getAllUsersByBranch(UUID branchId) {
        return userRepository.findByBranchId(branchId).stream()
                .map(user -> new UserDTO(user.getFirstName(), user.getMiddleName(), user.getLastName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public double collectTaxesAndFeesFromBranch(UUID branchId) {
        if (branchId == null) {
            throw new IllegalArgumentException("Branch ID can not be null!");
        }

        List<User> usersBranch = userRepository.findByBranchId(branchId);

        if (usersBranch.isEmpty()) {
            throw new IllegalArgumentException("The branch does not have any customer!");
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

        userRepository.saveAll(usersBranch);
        return revenue;
    }

    public UUID generateUUID() {
        return UUID.randomUUID();
    }

    public double calculateFee(double balance) {
        return balance < 100 ? balance * 0.1 : 10;
    }

    public double transferMoney(UUID fromUserId, UUID toUserId, double amount) throws UserNotFoundException, InsufficientFundsException {
        User fromUser = userRepository.getUserById(fromUserId);
        User toUser = userRepository.getUserById(toUserId);

        if (fromUser == null || toUser == null) {
            throw new UserNotFoundException("One or both users not found");
        }

        if (fromUser.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        fromUser.setBalance(fromUser.getBalance() - amount);
        toUser.setBalance(toUser.getBalance() + amount);

        userRepository.updateUser(fromUser);
        userRepository.updateUser(toUser);

        return fromUser.getBalance();
    }
}

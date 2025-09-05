package clf.integra.backend.service;

import clf.integra.backend.model.User;
import clf.integra.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public double collectTaxesAndFeesFromBranch(UUID branchId) {
        if (branchId == null) {
            throw new IllegalArgumentException("Branch ID can not be null!");
        }

        // We will be able to use the method of get clients for a branch implemented by Madalina
        // after merging the develop branch with the branch she created.
        List<User> allUsers = userRepository.getAllUsers();
        List<User> usersBranch = new ArrayList<>();
        double revenue = 0;

        for (User user: allUsers) {
            if (branchId.equals(user.getId())) {
                usersBranch.add(user);
            }
        }

        if (usersBranch.isEmpty()) {
            throw  new IllegalArgumentException("The branch doesnt not have any customer!");
        }

        for (User user : usersBranch) {
            double fee = user.getBalance() < 100 ? user.getBalance() * 0.1 : 10;
            if (fee > 0) {
                user.setBalance(user.getBalance() - fee);
                revenue += fee;
            }
        }

        return revenue;
    }

    public UUID generateUUID() {
        return UUID.randomUUID();
    }
}

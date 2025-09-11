package clf.integra.backend.repository;

import clf.integra.backend.dto.DepositDTO;
import clf.integra.backend.model.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    void addUser(User newUser);
    User getUserById(UUID id);
    List<User> getAllUsers();
    void deleteUserById(UUID id);
    void updateUser(User updatedUser);
    boolean userExists(UUID id);
    Double getUserBalanceById(UUID id);
    List<DepositDTO> getUserDeposits(UUID id);
}

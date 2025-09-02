package clf.integra.backend.repository;

import clf.integra.backend.model.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    public void addUser(User newUser);
    public User getUserById(UUID id);
    public List<User> getAllUsers();
    public void deleteUserById(UUID id);
    public void updateUser(User updatedUser);
    public boolean userExists(UUID id);
}

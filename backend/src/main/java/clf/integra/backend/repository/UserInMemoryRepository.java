package clf.integra.backend.repository;
import clf.integra.backend.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class UserInMemoryRepository implements UserRepository
{
    private HashMap<UUID, User> userRepository;

    public UserInMemoryRepository() {
        this.userRepository = new HashMap<>();
        User newUser = new User(UUID.randomUUID(), "John", "", "Doe", 1000.55);
        this.userRepository.put(newUser.getId(), newUser);
        this.userRepository.put(UUID.randomUUID(), new User(UUID.randomUUID(), "Andrei", "Mihai", "Popescu", 2500.00));
        this.userRepository.put(UUID.randomUUID(), new User(UUID.randomUUID(), "Jane", "", "Smith", 1500.25));
    }

    public void addUser(User newUser)
    {
        this.userRepository.put(newUser.getId(), newUser);
    }

    public User getUserById(UUID id)
    {
        return this.userRepository.get(id);
    }

    public List<User> getAllUsers()
    {
        return new ArrayList<>(this.userRepository.values());
    }

    public void deleteUserById(UUID id)
    {
        this.userRepository.remove(id);
    }

    public void updateUser(User updatedUser)
    {
        this.userRepository.put(updatedUser.getId(), updatedUser);
    }

    public boolean userExists(UUID id)
    {
        return this.userRepository.containsKey(id);
    }


}

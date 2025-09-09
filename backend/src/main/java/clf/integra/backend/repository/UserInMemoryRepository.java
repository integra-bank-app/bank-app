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

        UUID user1Id = UUID.randomUUID();
        this.userRepository.put(user1Id, new User(user1Id, "John", "", "Doe", 1000.55, UUID.randomUUID()));

        UUID user2Id = UUID.randomUUID();
        this.userRepository.put(user2Id, new User(user2Id, "Andrei", "Mihai", "Popescu", 2500.00, UUID.randomUUID()));

        UUID user3Id = UUID.randomUUID();
        this.userRepository.put(user3Id, new User(user3Id, "Jane", "", "Smith", 1500.25, UUID.randomUUID()));
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

    public Double getUserBalanceById(UUID id) {
        if (this.userRepository.get(id) == null) {
            return null;
        }
        return this.userRepository.get(id).getBalance();
    }
}

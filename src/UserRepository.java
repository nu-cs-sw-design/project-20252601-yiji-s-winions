
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Replaces the UserRepository interface
public class UserRepository {
    private final Map<String, User> userStorage = new HashMap<>();

    public Optional<User> findByUsername(String username) {
        return userStorage.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }

    public void save(User user) {
        userStorage.put(user.getUsername(), user);
        System.out.println("User saved: " + user.getUsername());
    }

    public void delete(String id) {
        userStorage.remove(id);
    }
}
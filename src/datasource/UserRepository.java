package datasource;
import domain.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Replaces the UserRepository interface
public class UserRepository {
    private final Map<String, User> userStorage = new HashMap<>();

    public Optional<User> findByEmail(String email) {
        return userStorage.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    public void save(User user) {
        userStorage.put(user.getEmail(), user);
        System.out.println("User saved: " + user.getEmail());
    }

    public void delete(String id) {
        userStorage.remove(id);
    }
}
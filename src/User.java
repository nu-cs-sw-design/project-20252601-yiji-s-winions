
import java.util.UUID;

// Replaces the abstract class structure by making it concrete
public class User {
    private final String userId;
    private final String username;
    private String passwordHash;
    private final UserRepository userRepository; // Direct dependency on concrete repo

    public User(String username, String passwordHash, UserRepository userRepository) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.passwordHash = passwordHash;
        this.userRepository = userRepository;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }

    public Boolean authenticate(String credentials) {
        return this.passwordHash.equals(credentials);
    }

    public void changePassword(String newPassHash) {
        this.passwordHash = newPassHash;
        userRepository.save(this);
    }

    // Replaced viewDashboard() with a simple return value since Dashboard model is complex
    public String viewDashboard() {
        return "Dashboard data summary for " + username;
    }
}
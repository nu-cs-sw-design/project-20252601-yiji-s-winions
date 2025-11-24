package domain;

import datasource.UserRepository;
import java.util.UUID;

public class User {
    // Making userId final and removing the 'private final String username' field
    // to align with your previous 'email' field and CSV structure.
    private final String userId;
    private final String email;
    private String passwordHash;
    private final UserRepository userRepository;

    // Constructor 1: For NEW user creation (generates new UUID)
    public User(String email, String passwordHash, UserRepository userRepository) {
        this.userId = UUID.randomUUID().toString();
        this.email = email;
        this.passwordHash = passwordHash;
        this.userRepository = userRepository;
    }

    // Constructor 2: For LOADING from CSV (accepts existing ID)
    public User(String userId, String email, String passwordHash, UserRepository userRepository) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.userRepository = userRepository;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; } // Added for CSV output

    public Boolean authenticate(String credentials) {
        return this.passwordHash.equals(credentials);
    }

    public void changePassword(String newPassHash) {
        this.passwordHash = newPassHash;
        userRepository.save(this);
    }

    /**
     * Serializes the User object into a CSV line format.
     */
    public String toCsvString() {
        return String.format("%s,%s,%s", this.userId, this.email, this.passwordHash);
    }

    public String viewDashboard() {
        return "Dashboard data summary for " + email;
    }
}
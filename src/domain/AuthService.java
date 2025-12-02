package domain;

import datasource.UserRepository;
import java.util.Optional;

public class AuthService {

    // Direct dependency on the concrete UserRepository (Datasource layer)
    private final UserRepository userRepository;

    public AuthService(UserRepository repo) {
        this.userRepository = repo;
    }


    public Optional<User> login(String email, String password) {
        System.out.println("SERVICE: Attempting login for: " + email);

        // 1. Check if the User exists in the Datasource
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 2. Delegate authentication logic to the Domain entity (User)
            if (user.authenticate(password)) {
                // Success: In a real app, session tokens/JWTs would be generated here.
                System.out.println("SERVICE: Login successful. Session started.");
                return Optional.of(user);
            }
        }

        // Failure: User not found OR password incorrect
        System.out.println("SERVICE: Authentication failed.");
        return Optional.empty();
    }


    public User register(String email, String password) throws IllegalArgumentException {
        // 1. Check if the user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Registration failed: Email address already in use.");
        }

        // 2. Create the new Domain entity (User)
        // Note: For simplicity, we use the raw password as the hash input here.
        User newUser = new User(email, password, userRepository);

        // 3. Persist the new entity using the repository (which saves to CSV)
        userRepository.save(newUser);

        System.out.println("SERVICE: Registration successful for: " + email);
        return newUser;
    }


    public void changePassword(User user, String newPassHash) {
        // Delegate password change and persistence to the User entity itself
        user.changePassword(newPassHash);
        System.out.println("SERVICE: Password successfully changed for: " + user.getEmail());
    }
}
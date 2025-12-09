package datasource;

import domain.User;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.Collectors;

public class UserRepository {
    private static final String FILE_PATH = "users.csv";
    private final Map<String, User> userCache = new HashMap<>(); // Cache for quick lookup

    public UserRepository() {
        loadDataFromCsv();
    }

    private void loadDataFromCsv() {
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            // Create file and write header if it doesn't exist
            try {
                Files.writeString(path, "userId,email,passwordHash\n", StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                System.err.println("Error creating users.csv: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            // Skip header line
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    // Note: We need a special User constructor or setter for loading
                    User loadedUser = new User(parts[0], parts[1], parts[2], this);
                    userCache.put(parts[1], loadedUser); // Key by email
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users from CSV: " + e.getMessage());
        }
    }

    private void writeDataToCsv() {
        List<String> lines = userCache.values().stream()
                .map(User::toCsvString) // Requires a toCsvString() method in User class
                .collect(Collectors.toList());

        lines.add(0, "userId,email,passwordHash"); // Add header back

        try {
            Files.write(Paths.get(FILE_PATH), lines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error writing users to CSV: " + e.getMessage());
        }
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userCache.get(email));
    }

    public void save(User user) {
        userCache.put(user.getEmail(), user);
        writeDataToCsv();
    }

    public void delete(String id) {
        String emailKeyToRemove = null;

        for (User user : userCache.values()) {
            if (user.getUserId().equals(id)) {
                emailKeyToRemove = user.getEmail();
                break;
            }
        }

        if (emailKeyToRemove != null) {
            userCache.remove(emailKeyToRemove);
            writeDataToCsv(); // persist changes to users.csv
        } else {
            System.err.println("No user found with id: " + id);
        }
    }

}
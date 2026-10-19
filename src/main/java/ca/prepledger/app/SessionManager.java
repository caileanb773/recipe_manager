
package ca.prepledger.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SessionManager {

	// XXX will need to be changed to support other OS
    private final Path sessionPath = Path.of(
        System.getenv("LOCALAPPDATA"),
        "PrepLedger",
        "session.cfg"
    );

    public void save(UserSession session) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("token", session.getToken());
        properties.setProperty("userId", session.getUserId().toString());
        properties.setProperty("email", session.getEmail());

        Files.createDirectories(sessionPath.getParent());

        try (var output = Files.newOutputStream(sessionPath)) {
            properties.store(output, "PrepLedger session");
        }
    }

    public UserSession load() throws IOException {
        if (!Files.exists(sessionPath)) {
            return null;
        }

        Properties properties = new Properties();

        try (var input = Files.newInputStream(sessionPath)) {
            properties.load(input);
        }

        String token = properties.getProperty("token");
        String userId = properties.getProperty("userId");
        String email = properties.getProperty("email");

        if (token == null || userId == null || email == null) {
            clear();
            return null;
        }

        try {
            return new UserSession(token, Long.parseLong(userId), email);
        } catch (NumberFormatException e) {
            clear();
            return null;
        }
    }

    public void clear() throws IOException {
        Files.deleteIfExists(sessionPath);
    }
}

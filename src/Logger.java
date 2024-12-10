import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE = "ticketing_system.log"; // Log file name
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Synchronized log method to ensure thread-safe access
    public static synchronized void log(String message) {
        String timestampedMessage = "[" + LocalDateTime.now().format(FORMATTER) + "] " + message;

        // Log to console
        System.out.println(timestampedMessage);

        // Log to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(timestampedMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write log to file: " + e.getMessage());
        }
    }
}

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserRegistration {
    private static Map<String, String> users = new HashMap<>();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("Enter username (or exit to quit):");
                String username = scanner.nextLine();
                if (username.equals("exit")) {
                    break;
                }

                if (users.containsKey(username)) {
                    System.out.println("Username already exists.");
                    continue;
                }

                System.out.println("Enter password:");
                String password = scanner.nextLine();

                String hashedPassword = hashPassword(password);
                users.put(username, hashedPassword);

                System.out.println("User registered.");
            }
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not hash password", e);
        }
    }
}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SSLServerWithPasswordFromFile {
    
    private static final int SERVER_PORT = 8080;
    private static final String KEYSTORE_FILE = "Juan.jks";
    private static final String PASSWORD_FILE = "password.txt";

    public static void main(String[] args) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(SSLServerWithPasswordFromFile.class.getResourceAsStream(KEYSTORE_FILE), getPassword());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, getPassword());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(SERVER_PORT);

            while (true) {
                System.out.println("Waiting for client connection...");
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = in.readLine();
                System.out.println("Received message from client: " + message);

                clientSocket.close();
                System.out.println("Connection closed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static char[] getPassword() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(PASSWORD_FILE));
        char[] password = reader.readLine().toCharArray();
        reader.close();
        return password;
    }
}

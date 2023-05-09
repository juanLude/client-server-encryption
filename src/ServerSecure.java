import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class ServerSecure {
    public static void main(String[] args) {
        int portNumber = 1234;

        // Set up the SSL context
        System.setProperty("javax.net.ssl.keyStore", "server.keystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");

        try {
            // Create the SSL server socket factory
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

            // Create the SSL server socket
            ServerSocket serverSocket = sslServerSocketFactory.createServerSocket(portNumber);
            System.out.println("Server started on port " + portNumber);

            while (true) {
                // Wait for a client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Start a new thread to handle the client
                new ClientHandler((SSLSocket) clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class to handle client connections
    private static class ClientHandler extends Thread {
        private SSLSocket clientSocket;

        public ClientHandler(SSLSocket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                // Get the input and output streams for the socket
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Read the client's message
                String clientMessage = in.readLine();
                System.out.println("Received message from client: " + clientMessage);

                // Send a response back to the client
                out.println("Hello from the server!");

                // Close the connections
                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ClientSecure {
    public static void main(String[] args) {
        String serverName = "localhost";
        int portNumber = 1234;

        // Set up the SSL context
        System.setProperty("javax.net.ssl.trustStore", "client.truststore");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");

        try {
            // Create the SSL socket factory
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

            // Create the SSL socket and connect to the server
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverName, portNumber);
            System.out.println("Connected to server: " + socket);

            // Get the input and output streams for the socket
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send a message to the server
            String message = "Hello from the client!";
            out.println(message);
            System.out.println("Sent message to server: " + message);

            // Wait for a response from the server and print it out
            String serverResponse = in.readLine();
            System.out.println("Received response from server: " + serverResponse);

            // Close the connections
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

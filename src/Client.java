import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws IOException {
       // server address and port
       String serverAddress = "localhost";
       int port = 8080;
  
        try {
            // create a socket to connect to the server
            Socket client = new Socket(serverAddress, port);

            // set up input and output streams for the socket
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            // set up an input stream to read user input
            BufferedReader readUserInput = new BufferedReader(new InputStreamReader(System.in));

            String username = "";
            String password = "";
            Boolean validPassword = false;
            boolean clientConnected = false;

            while(!clientConnected){
                 // Prompt the user for their username and password
                System.out.print("Enter your username: ");
                username = readUserInput.readLine();
                out.println(username);
                while(!validPassword){
                    System.out.print("Enter your password: ");
                    password = readUserInput.readLine();
                    if (password.length() < 8) {
                        System.out.println("Password must be at least 8 characters long.");
                     } else if (password.equals(password.toLowerCase())) {
                        System.out.println("Password must contain at least 1 uppercase character.");
                     } else {
                        validPassword = true;
                        System.out.println("Password is valid!");
                        
                        break;
                     }
                }
                // System.out.print("Enter your password: ");
                // password = readUserInput.readLine();

                // Send the username and password to the server
           
                out.println(password);

                // Read the server's response
                String serverResponse = in.readLine();
                System.out.println(serverResponse);
                

                if(serverResponse.contains("You have been registered as") || serverResponse.contains("Welcome back")){
                    clientConnected = true;
                    // display connection status
                    System.out.println("Connected to server");
                    break;
                }
                
            }
           
            // client ID
            String clientId = username;

           

            // main loop to handle user input and server responses
            while (true) {
                // prompt user for input
                System.out.print("Enter message: ");
                String input = readUserInput.readLine();

                // handle user input
                if (input.trim().equals("DISCONNECT") && clientConnected) {
                    // send disconnect message with client ID
                    out.println(input + " " + "%%%" + clientId);
                } else if (input.split(" ")[0].equals("PUT") && clientConnected) {
                    // send put message with client ID
                    out.println(input + "%%%" + clientId);
                    // prompt user for additional input
                    System.out.print("Enter message: ");
                    input = readUserInput.readLine();
                    // send additional input
                    out.println(input);
                } else if (input.split(" ")[0].equals("GET") && clientConnected) {
                    // send get message with client ID
                    out.println(input + "%%%" + clientId);
                } else if (input.split(" ")[0].equals("DELETE") && clientConnected) {
                    // send delete message with client ID
                    out.println(input + "%%%" + clientId);
                } else if (input.split(" ")[0].equals("CONNECT")) {
                    // extract client ID from input
                    String[] result = Arrays.stream(input.split(" "), 1, input.split(" ").length)
                            .toArray(String[]::new);
                    clientId = String.join(" ", result);
                    // send connect message with client ID
                    out.println(input);
                } else {

                    // disconnect client due to invalid requests
                    System.out.println("Closing connection due to Invalid message");
                    client.close();
                    readUserInput.close();
                    System.exit(0);
                }
                // read server messages
                String serverMessage = in.readLine();
                System.out.println("Server says: " + serverMessage.trim());

                // set connection to true if connection is okay
                if (serverMessage.trim().equals("CONNECT: OK")) {
                    clientConnected = true;
                }
                // close socket and stream and release any resources associated with it
                if (serverMessage.trim().equals("DISCONNECT: OK")
                        || serverMessage.trim().equals("CONNECT: ERROR")
                        || serverMessage.trim().equals("PUT: ERROR")
                        || serverMessage.trim().equals("ERROR: Invalid command")) {
                    client.close();
                    readUserInput.close();
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.err.println("Error: Could not connect to server at " + serverAddress + ":" + port);
            System.exit(1);
        }
    }
}

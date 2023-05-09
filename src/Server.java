import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
public class Server {
    // store key-value pairs
    private static Map<String, Map<String, String>> keyValueStore;
    // keep track of client connections
    private static Map<String, Boolean> clientConnected;
     // keep track of credentials
    private static Map<String, String> userCredentials;

    // constructor to initialise fields to empty hashmaps
    public Server() {
        keyValueStore = new HashMap<>();
        clientConnected = new HashMap<>();
        userCredentials = new HashMap<>();
    }

    // create a new ServerSocket object with the specified port number and listen
    // for incoming connections
    public void start(int port) {
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                // Create a new thread for each client once connected
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        }
        // server unable to be started
        catch (IOException e) {
            System.err.println("Error: Port " + port + " is already in use.");
            System.exit(1);
        }
        
    }

    // handle communication with individual clients
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {


                // Add some dummy user credentials
                userCredentials.put("Juan", "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f");
                userCredentials.put("Marcos", "1c8bfe8f801d79745c4631d09fff36c82aa37fc4cce4fc946683d7b336b63032");
                System.out.println(userCredentials.toString());
                // Read data from the client and send responses back
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
               
              
               // Check if the username and password are valid

               while(true){
           

                 // Read the username and password from the client
                 String username = in.readLine();
                 String password= in.readLine();
                
                 String hashedPassword = hashPassword(password);               
                if (!userCredentials.containsKey(username) && !clientConnected.containsKey(username)) {
        
                    userCredentials.put(username, hashedPassword);
                    out.println("You have been registered as " + username);
                    clientConnected.put(username, true);
                     // initialise an empty HashMap for storing the client's key-value pairs
                     keyValueStore.put(username, new HashMap<>());
                    System.out.println(userCredentials.toString());
                    break;
                        
                    }else if (userCredentials.containsKey(username) && userCredentials.containsValue(hashPassword(password)) && !clientConnected.isEmpty() & clientConnected.containsKey(username)){
                        out.println("You are already logged in. ");
                    } 
                    else if (userCredentials.containsKey(username) && userCredentials.containsValue(hashPassword(password))){
                        clientConnected.put(username, true);
                        out.println("Welcome back " + username + ". You are logged in now.");
                        System.out.println(userCredentials.toString());
                        System.out.println(clientConnected.toString());
                        // add the client to the clientConnected map
                        clientConnected.put(username, true);
                        // initialise an empty HashMap for storing the client's key-value pairs
                        keyValueStore.put(username, new HashMap<>());
                        break;
                    }
                    
                    else{
                        out.println("Invalid username or password. Please try again.");
                    }   

                }
                

                // read a one line text from a given input source
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // capture initial string
                    String[] tokens = inputLine.split(" ");
                    String command = tokens[0];
                    // branch off into different paths depending on what the initial string was sent
                    // by the Client
                    switch (command) {

                        // case "CONNECT":
                        //     // parse client input to get client id
                        //     String[] result = Arrays.stream(tokens, 1, tokens.length).toArray(String[]::new);
                        //     String client = String.join(" ", result);
                        //     // no client id provided hence close the client socket after break
                        //     if (tokens.length < 2) {
                        //         System.out.println("CONNECT: ERROR");
                        //         out.println("CONNECT: ERROR");
                        //         break;
                        //     }
                        //     // client already connected hence close the client socket after break
                        //     if (clientConnected.containsKey(client)) {
                        //         System.out.println("CONNECT: ERROR");
                        //         out.println("CONNECT: ERROR");
                        //         break;
                        //     }
                        //     // store client id
                        //     String clientId = client;
                        //     // add the client to the clientConnected map
                        //     clientConnected.put(clientId, true);
                        //     // initialise an empty HashMap for storing the client's key-value pairs
                        //     keyValueStore.put(clientId, new HashMap<>());
                        //     out.println("CONNECT: OK");
                        //     System.out.println("CONNECT: OK");
                        //     break;

                        case "PUT":
                            // parse client input to separate key value from client id
                            String[] separateStrings = inputLine.split("%%%");
                            String clientPut = separateStrings[1];
                            String putMessage = String.join("", separateStrings[0].split("PUT")).trim();
                            // check if client is connected
                            if (clientConnected.getOrDefault(clientPut, false)) {
                                // if client is connected allow to provide value to key
                                String inputLine2;
                                inputLine2 = in.readLine();
                                // attach value to key
                                keyValueStore.get(clientPut).put(putMessage, inputLine2);
                                System.out.println("PUT: OK");
                                out.println("PUT: OK");
                            } else {
                                System.out.println("PUT: ERROR");
                                out.println("PUT: ERROR");
                            }
                            break;

                        case "GET":
                           // parse Client input to separate key from client id
                           String[] splitStrings = inputLine.split("%%%");
                           String clientGet = splitStrings[1];
                           String getMessage = String.join("", splitStrings[0].split("GET")).trim();
                           // check if client is connected
                           if (clientConnected.getOrDefault(clientGet, false)) {
                               String value = keyValueStore.get(clientGet).get(getMessage);
                               // no value linked to key provided
                               if (value == null) {
                                   System.out.println("GET: ERROR");
                                   out.println("GET: ERROR");
                               }
                               // return value
                               else {
                                   System.out.println(value);
                                   out.println(value);
                               }
                           } else {
                               System.out.println("ERROR: Client not connected");
                           }
                           break;
                        case "DELETE":
                            // parse Client input to separate key from client id
                            String[] breakStrings = inputLine.split("%%%");
                            String clientDelete = breakStrings[1];
                            String message = String.join("", breakStrings[0].split("DELETE")).trim();

                            // deleted key value pair is already null
                            if (keyValueStore.get(clientDelete).get(message) == null) {
                                System.out.println("DELETE: OK");
                                out.println("DELETE: OK");
                                break;
                            }
                            // delete key value pair
                            if (clientConnected.getOrDefault(clientDelete, false)) {

                                keyValueStore.get(clientDelete).remove(message);
                                System.out.println("DELETE: OK");
                                out.println("DELETE: OK");
                                break;

                            } else {
                                System.out.println("DELETE: ERROR");
                                out.println("DELETE: ERROR");
                            }
                            break;

                        case "DISCONNECT":

                            // close connection if DISCONNECT message is received when no client is actually
                            // connected
                            if (clientConnected.size() == 0) {
                                System.out.println("ERROR: Invalid command");
                                out.println("ERROR: Invalid command");
                                in.close();
                                out.close();
                                clientSocket.close();
                                System.exit(0);

                            }
                            // get client id message from client
                            String[] smashStrings = inputLine.split("%%%");
                            String shutClient = smashStrings[1];

                            // remove all traces of the session
                            if (clientConnected.containsKey(shutClient)) {
                                clientConnected.remove(shutClient);
                                keyValueStore.remove(shutClient);

                                System.out.println("DISCONNECT: OK");
                                out.println("DISCONNECT: OK");

                                // disconnect when client has not stored a key-value pair
                            } else {
                                System.out.println("DISCONNECT: OK");
                                out.println("DISCONNECT: OK");
                            }
                            break;

                        default:
                            // If the switch case doesn't match any of the valid commands, print an error
                            // message to the console.
                            System.out.println("ERROR: Invalid command");
                            // Send an error message back to the client.
                            out.println("ERROR: Invalid command");
                            // Close the input and output streams and the client socket.
                            in.close();
                            out.close();
                            clientSocket.close();
                            // Terminate the program.
                            System.exit(0);
                            break;
                    }
                }

                // Close the client socket when the client disconnects
                clientSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        // hash the password using the SHA-256 algorithm before getting stored in the map
        private String hashPassword(String password) {
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

    // create a new KeyValueServer object and start the server
    public static void main(String[] args) throws Exception {
        
        //int port = Integer.parseInt(args[0]);
       
        
        Server server = new Server();
        server.start(8080);
       
    }

}

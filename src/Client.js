const net = require('net');
const readline = require('readline');

// server address and port
const serverAddress = process.argv[2];
const port = parseInt(process.argv[3]);

if (!serverAddress || !port) {
  console.error("Error: Please provide the server address and port number.");
  process.exit(1);
}

// create a socket to connect to the server
const client = net.createConnection(port, serverAddress, () => {
  console.log('Connected to server');
  rl.setPrompt('Enter message: ');
  rl.prompt();
});

// set up input and output streams for the socket
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

// client ID
let clientId = '';
let clientConnected = false;

// main loop to handle user input and server responses
rl.on('line', (input) => {
  // handle user input
  if (input.trim() === 'DISCONNECT' && clientConnected) {
    // send disconnect message with client ID
    client.write(input + ' %%%' + clientId + '\n');
  } else if (input.split(' ')[0] === 'PUT' && clientConnected) {
    // send put message with client ID
    client.write(input + '%%%' + clientId + '\n');
    // prompt user for additional input
    rl.question('Enter message: ', (input) => {
      // send additional input
      client.write(input + '\n');
    });
  } else if (input.split(' ')[0] === 'GET' && clientConnected) {
    // send get message with client ID
    client.write(input + '%%%' + clientId + '\n');
  } else if (input.split(' ')[0] === 'DELETE' && clientConnected) {
    // send delete message with client ID
    client.write(input + '%%%' + clientId + '\n');
  } else if (input.split(' ')[0] === 'CONNECT' && !clientConnected) {
    // extract client ID from input
    const extract = input.split(' ').slice(1).join(' ');
    clientId = extract;

    // send connect message with client ID
    client.write(input + '\n');
  } else {
    // disconnect client due to invalid requests
    console.log('Closing connection due to invalid message');
    client.destroy();
    process.exit();
  }

});

// read server messages
client.on('data', (data) => {
  const serverMessage = data.toString().trim();
  console.log(`Server says: ${serverMessage}`);

  // set connection to true if connection is okay
  if (serverMessage === 'CONNECT: OK') {
    clientConnected = true;

  }

  // close socket and stream and release any resources associated with it
  if (serverMessage === 'DISCONNECT: OK'
    || serverMessage === 'CONNECT: ERROR'
    || serverMessage === 'PUT: ERROR'
    || serverMessage === 'ERROR: Invalid command') {
    client.destroy();
    process.exit();
  }
  rl.prompt();
});

// handle errors
client.on('error', (err) => {
  console.error(`Error: Could not connect to server at ${serverAddress}:${port}`);
  console.error(err);
  process.exit(1);
});

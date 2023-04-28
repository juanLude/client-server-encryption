if [ "$#" -ne 2 ]; then
  echo "Usage: $0 <hostname> <port>"
  exit 1
fi

HOSTNAME=$1
PORT=$2

node Client.js $HOSTNAME $PORT

# Attempt to connect to the server
if ! nc -z $HOSTNAME $PORT; then
  echo "Error: Could not connect to server at $HOSTNAME:$PORT"
  exit 1
else
  echo "Successfully connected to server at $HOSTNAME:$PORT"
fi
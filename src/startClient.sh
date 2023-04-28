#!/bin/bash

# Check if the correct number of arguments were provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 hostname port" >&2
    exit 1
fi

# Set variables for the hostname and port number
HOSTNAME=$1
PORT=$2

# Compile the Client.java program
javac Client.java

# Check if the compilation was successful
if [ $? -ne 0 ]; then
    echo "Compilation failed" >&2
    exit 1
fi

# Run the Client program with the given hostname and port number
java Client $HOSTNAME $PORT

# Check if the connection was successful
if [ $? -ne 0 ]; then
    echo "Connection failed" >&2
    exit 1
fi

echo "Connection successful"

#!/bin/bash

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <port>"
  exit 1
fi

port="$1"

# Compile the Java server
javac Server.java

# Start the server on the specified port
java Server "$port"
![TextMeAppLogoLarge](https://github.com/user-attachments/assets/d2fce27d-6c33-418c-a434-5c7155470e98)

Copyright 2024 Caleb Lanphere, Faz Creation - All Rights Reserved.

## Purpose of creation
This program was created in my spare time during my first semester at St. Bonaventure University. The application was created to build practical applications and push my skills further than a classroom

## Technical Basics
The program uses Java as it's programming language, Java's built-in GUI system, and Java's built-in internet interface

## Technical Details
The application uses Peer-To-Peer (P2P) to connect the server with clients. The system uses the `Java.io` package to implement `PrintStream`, `BufferedReader`, `Socket`, and `Server Socket`.\
\
The server application does the heavy lifting, as it does all:
1. Message Parsing - Checks for messages that have commands like `getmessagehistory;`, or `quit;`.
2. Message Sending - Forwards all messages received to all other connected users
3. Message History - Keeps the message history from all sent users if enabled
4. User connections - Adds new users if enabled

The client application does does the rest
1. Message Indentifer - Adds the username picked by the user into the message before sending it to the server
2. Message Showing - Adding the message to the GUI

Both work together to make a rather simple P2P messaging application that works over the web.\
\
The application is split into two different packages, one for the Server files and the other for the Client files

## How To Create A Server
To host a server:
1. Open the file `app.exec`
2. Enter the port you want to host the server on (Type `0` for the machine to find an already open port)
3. Make sure your router is port-forwarded for the port that is lised under "Server Details". If the router is not port-forwarded, it will not function over the web
4. Wait for a client to join based on the information listed under "Server Details"
> [!NOTE]
> The server application does not show messages that are sent by users; To join the talk yourself, you must also launch the client program as well

## To Join A Server
To join a hosted server:
1. Open the file `app.exec`
2. Set a username
3. Input the provided IP Address and port from the server hoster
4. The client will attempt to connect to the server. If it does not connect, an error message will be provided


Copyright 2024 Caleb Lanphere, Faz Creation - All Rights Reserved.

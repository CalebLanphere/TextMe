/**
 * @author Caleb Lanphere
 * 
 * TextMe Application server netcode
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package appTextMe;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class netCommServer {
	
	static ArrayList<Socket> sockets = new ArrayList<Socket>();
	static ArrayList<BufferedReader> bufferedReaders = new ArrayList<BufferedReader>();
	static ArrayList<PrintStream> printStreams = new ArrayList<PrintStream>();
	static ServerSocket serSocket;
	static int usersOnServer = 0;
	static boolean firstUserConnected = false;
	
	
	/**
	 * Sends message received by a user to all clients connected to server
	 * 
	 * @param String message to send to clients
	 */
	public void sendMessageNet(String message) {
		for(int i = 0; i < sockets.size(); i++) {
			printStreams.get(i).print(message + "\n");
			printStreams.get(i).flush();
		}
	}
	
	/**
	 * Receives message from client and parses it before sending it back out
	 * 
	 * @param String message received by client
	 */
	public void recieveMessageNet(String message, int userIndex) {
			if(message.toLowerCase().equals("quit")) {
				closeSocket(userIndex);
			} else {
				for(int i = 0; i < sockets.size(); i++) { // Goes through entirety of sockets size
					printStreams.get(i).print(message + "\n"); // Gets every PrintStream variable then tells them to print
					printStreams.get(i).flush(); // Gets every PrintStream variable and sends the message
				}
			}
	}
	
	/**
	 * Check constantly for new messages sent to the server
	 */
	public void watchForMessages() {
		Thread messageCheckLoop = new Thread(new Runnable() { // Creates a new Thread
		
		public void run() {
		while(true) { // Infinite loop
			try {
				for(int i = 0; i < sockets.size(); i++) { // Iterates through all instances inside "sockets"
					if( bufferedReaders.get(i) != null) { // Checks to see if the bufferReader at "i" is valid
						if(bufferedReaders.get(i).ready()) { // Checks to see if the message at BufferReader[i] is ready
							recieveMessageNet(bufferedReaders.get(i).readLine(), i); // Sends message to parser
						}
					} else {
						closeSocket(i); // If the BufferedReader[i] is invalid, close the socket
					}
				}
				} catch (IOException IOE) {
					throwError("IOE reading message");
				}
			// Make the infinite loop sleep for 50 milliseconds
			try { 
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throwError("Sleep interrupted");
			}
			}
		}
		});
		messageCheckLoop.start(); // Starts the message checker loop
		
	}
	
	/**
	 * Close the socket that is associated to "user"
	 * 
	 * @param int user to remove
	 */
	public void closeSocket(int user) {
		try {
			bufferedReaders.get(user).close();
			bufferedReaders.remove(user);
			printStreams.get(user).close();
			printStreams.remove(user);
			sockets.get(user).close();
			sockets.remove(user);
		} catch (IOException e) {
			throwError("IOE at closing buffered reader");
		}
	}
	
	/**
	 * Attempts to start the server
	 * 
	 * @return boolean if starting the server is successful
	 */
	public boolean startServer() { 
		try { // Tries to start a new ServerSocket at the localhost address with port "0"
			serSocket = new ServerSocket(0, 2, InetAddress.getLocalHost()); // cannot use port 80, 21, 443
		} catch (IOException IOE) {
			throwError("IOE at socket creation" + IOE.getMessage()); // ServerSocket could not be set
			return false;
		}
		
		System.out.println("IP address: " + serSocket.getLocalSocketAddress()); // Outputs the SocketAddress
		System.out.println("port: " + serSocket.getLocalPort());// Outputs the Socket port
		
		allowNewUser(); // Adds a new user
		allowNewUser(); // Adds a new user
		
		watchForMessages(); // Start watching for messages
		
		return true; // Returns true for server creation and two user connected successfully
	} 
	
	/**
	 * Adds a new BufferedReader and PrintStream into the bufferedReaders and printStreams ArrayLists
	 * 
	 * @param int user index
	 */
	public void createInputsAndOutputs(int user) {
		// Try to create a new BufferedReader and add it to bufferedReaders
		try {
			bufferedReaders.add(new BufferedReader(new InputStreamReader(sockets.get(user).getInputStream())));
		} catch (IOException e) {
			throwError("IOE at adding BufferedReader");
		}
		// Try to create a new PrintStream and add it to printStreams
		try {
			printStreams.add(new PrintStream(sockets.get(user).getOutputStream()));
		} catch (IOException e) {
			throwError("IOE at adding BufferedReader");
		}
		usersOnServer++;
	}
	
	/**
	 * Closes the server and all associated variables
	 */
	public void closeServer() {
		if(!serSocket.isClosed()) {
			try {
			for(int i = 0; i < sockets.size(); i++) { // Iterates though all sockets
				sockets.get(i).close(); // Closes the socket at "i"
				bufferedReaders.get(i).close(); // Closes the BufferedReader at "i"
				printStreams.get(i).close(); // Closes the PrintStream at "i"
			}
			serSocket.close(); // Closes the server socket
			} catch (IOException IOE) {
				throwError("IOE at closing server");
			}
		}
	}
	
	/**
	 * Creates a new socket in the sockets ArrayList
	 */
	public void allowNewUser() {
		try {
			sockets.add(serSocket.accept()); // Adds a new socket in "sockets" and sets it to the connected user
		} catch (IOException e) {
			throwError("IOE at socket creation");
		}
		createInputsAndOutputs(usersOnServer);
	}
	
	/**
	 * Outputs an error message to the user
	 * 
	 * @param String Error message to print
	 */
	public void throwError(String err) {
		System.out.println(err);
	}
}
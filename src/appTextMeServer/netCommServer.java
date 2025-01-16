/**
 * @author Caleb Lanphere
 * 
 * TextMe Application server netcode
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package appTextMeServer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.net.InetAddress;

public class netCommServer {
	
	private static ArrayList<Socket> sockets = new ArrayList<Socket>();
	private static ArrayList<BufferedReader> bufferedReaders = new ArrayList<BufferedReader>();
	private static ArrayList<PrintStream> printStreams = new ArrayList<PrintStream>();
	private static ServerSocket serSocket;
	private static int usersOnServer = 0;
	private static boolean allowMessageHistory = true;
	private static boolean clearMessageHistory = false;
	private static boolean newUsersAllowed = true;
	private static appUIS appUI;
	private static ArrayList<String> messageHistory = new ArrayList<String>();
	
	/**
	 * Sets a reference to the owning application
	 * 
	 * @param appUIS
	 */
	public void setUIRef(appUIS ui) {
		appUI = ui;
	}
	
	/**
	 * Gets the current users on the server
	 * 
	 * @return String usersOnServer
	 */
	public String getUsersOnServer() {
		return Integer.toString(usersOnServer);
	}
	
	/**
	 * Sends message received by a user to all clients connected to server
	 * 
	 * @param String message to send to clients
	 */
	public void sendMessageNet(String message) {
		for(int i = 0; i < sockets.size(); i++) { // Iterates through all connected users
			printStreams.get(i).print(message + "\n"); // Prints the message with a new line to buffer
			printStreams.get(i).flush(); // Push messages out to connected clients
		}
	}
	
	
	/**
	 * Sends message received by a user to all clients connected to server
	 * 
	 * @param String message to send to clients
	 */
	public void sendMessageToUserNet(String message, int user) {
			printStreams.get(user).print(message + "\n"); // Prints the message with a new line to buffer
			printStreams.get(user).flush(); // Push messages out to connected clients
	}
	/**
	 * Send a specific user the message history
	 * 
	 * @param int user to send message history to
	 */
	private void sendMessageHistory(int user) {
		for(int i = 0; i < messageHistory.size(); i++) {
			printStreams.get(user).print(messageHistory.get(i) + "\n");
			printStreams.get(user).flush();
		}
		printStreams.get(user).print("endofhistory;" + "\n");
		printStreams.get(user).flush();
	}
	
	/**
	 * Receives message from client and parses it before sending it back out
	 * 
	 * @param String message received by client
	 */
	private void recieveMessageNet(String message, int userIndex) {
			// Checks to see if the user has sent a critical command
			if(!parseMessageForCriticalCommands(message, userIndex)) {
				if(allowMessageHistory) {
					messageHistory.add(message);
				}
				sendMessageNet(message); // Sends the message to all connected clients
			}
	}
	
	/**
	 * Checks the message received by users for critical commands
	 * 
	 * @param String message to parse
	 * @return boolean returns is message contains a critical command
	 */
	private boolean parseMessageForCriticalCommands(String message, int userIndex) {
		if((message.toLowerCase().substring(message.indexOf(':') + 1, message.length())).contains("quit;")) {
			sendMessageNet(message.toLowerCase().substring(0, message.indexOf(':') + 1) + "left the chat.");
			closeSocket(userIndex);
			return true;
		} else if(message.toLowerCase().substring(message.indexOf(':') + 1, message.length()).contains("getmessagehistory;")) {
			sendMessageHistory(userIndex);
			return true;
		} else if (message.toLowerCase().substring(message.indexOf(':') + 1, message.length()).contains("messagehistorycleared;")) {
			sendMessageToUserNet("Server: Cleared message history" + "\n", userIndex);
			return true;
		} else {
			return false;
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
	private void closeSocket(int user) {
		try {
			// Closes the BufferedReader assigned to "user"
			bufferedReaders.get(user).close();
			bufferedReaders.remove(user); // Removes the BufferedReader object from bufferedReaders ArrayList
			
			// Closes the PrintStream assigned to "user"
			printStreams.get(user).close();
			printStreams.remove(user); // Removes the PrintStream object from printStreams ArrayList
			
			// Closes the Socket assigned to "user"
			sockets.get(user).close();
			sockets.remove(user); // Removes the Socket object from sockets ArrayList
			usersOnServer--;
			appUI.updateUserCountUI();
		} catch (IOException e) {
			throwError("IOE at closing buffered reader");
		}
	}
	
	/**
	 * Attempts to start the server
	 * 
	 * @return boolean if starting the server is successful
	 */
	public boolean startServer(int port) {
		String ip;
		try { // Tries to start a new ServerSocket at the localhost address with port "0"
			
			// Creates and sets inetA to a InetAddress bounded to the local host IP of the hosting server
			InetAddress inetA;
			inetA = InetAddress.getLocalHost();
			
			// Parses the IP address grabbed above to just the IP, instead of computer name and IP.
			ip = new String(inetA.getLocalHost().toString().substring(inetA.getLocalHost().toString().indexOf("/") + 1));
			
			// Creates the server
			serSocket = new ServerSocket(port, 2, inetA); // cannot use port 80, 21, 443
			serSocket.setSoTimeout(5000);
		} catch (IOException IOE) {
			throwError("IOE at socket creation" + " " + IOE.getMessage()); // ServerSocket could not be set
			return false;
		}
		
		appUI.setServerConnectionInfo(ip, serSocket.getLocalPort());
		
		allowNewUser(); // Adds a new user
		
		watchForMessages(); // Start watching for messages
		return true; // Returns true for server creation and two user connected successfully
	} 
	
	/**
	 * Adds a new BufferedReader and PrintStream into the bufferedReaders and printStreams ArrayLists
	 * 
	 * @param int user index
	 */
	private void createInputsAndOutputs(int user) {
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
		appUI.updateUserCountUI();
	}
	
	/**
	 * Closes the server and all associated variables
	 */
	public boolean closeServer() {
		if(serSocket != null) {
			if(!serSocket.isClosed()) {
				sendMessageNet("Server: Shutting down..."); // Sends message to users connected that server is closing
				try {
					for(int i = 0; i < sockets.size(); i++) { // Iterates though all sockets
						sockets.get(i).close(); // Closes the socket at "i"
						bufferedReaders.get(i).close(); // Closes the BufferedReader at "i"
						printStreams.get(i).close(); // Closes the PrintStream at "i"
				
						serSocket.close(); // Closes the server socket
					}
				} catch (IOException IOE) {
					throwError("IOE at closing server");
					return false;
				}
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	/**
	 * Set the usage of messagehistory on the server
	 * 
	 * @param boolean newBool
	 */
	public void setMessageHistory(boolean newBool) {
		allowMessageHistory = newBool;
		if(clearMessageHistory) {
			messageHistory.clear();
			clearMessageHistory = false;
			throwMessage("Message History Cleared");
		}
	}
	
	/**
	 * Sets clearMessageHistory
	 * @param boolean sets clearMessageHistory
	 */
	public void setClearMessageHistory(boolean newVal) {
		clearMessageHistory = newVal;
	}
	
	/**
	 * Sets if users can join the server
	 * 
	 * @param boolean new value of newUsersAllowed
	 */
	public void setNewUsersAllowed(boolean newBool) {
		newUsersAllowed = newBool;
	}
	
	/**
	 * Creates a new socket in the sockets ArrayList
	 */
	private void allowNewUser() { 
		Thread userAddLoop = new Thread(new Runnable() {
			
			public void run() {
				while(true) {
					if(newUsersAllowed == true) { // Checks to see if a new user can join
						try {
							sockets.add(serSocket.accept()); // Adds a new socket in "sockets" and sets it to the connected user
						} catch (SocketTimeoutException e) {
							continue;
						} catch (IOException e) {
							throwError("IOE at socket creation");
						} 
						createInputsAndOutputs(usersOnServer);
					} else { // If a new user cannot join
						try {
							Thread.sleep(100); // Sleep to avoid resource strain
						} catch (InterruptedException e) {
							throwError("Sleep interrupted at allowNewUser");
						}
					}
				}
			}
		});
		userAddLoop.start();
	}
	
	/**
	 * Outputs an error message to the user
	 * 
	 * @param String Error message to print
	 */
	private void throwError(String err) {
		appUI.throwError(err, true);
	}
	
	/**
	 * Outputs a message to user
	 * 
	 * @param String message to send
	 */
	private void throwMessage(String text) {
		appUI.throwError(text, false);
	}
}
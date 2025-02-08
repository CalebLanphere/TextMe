/**
 * @author Caleb Lanphere
 * 
 * TextMe Application Server Network Manager
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package com.creativitystudios.textmeserver;

import javafx.scene.layout.Pane;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.net.InetAddress;
import javax.crypto.*;
import java.util.HashMap;

public class TextMeServerNetworkManager {
	
	private static ArrayList<Socket> userSockets = new ArrayList<Socket>(); // Users connected associated sockets
	// Reader for connected users to receive and parse incoming messages
	private static ArrayList<BufferedReader> usersBufferedReaders = new ArrayList<BufferedReader>();
	// Sender for connected users to forward received messages to all users
	private static ArrayList<PrintStream> usersPrintStreams = new ArrayList<PrintStream>();
	private static ServerSocket serSocket; // Server socket
	private static int usersOnServer = 0; // Number of users on the server
	private static boolean allowMessageHistory = true; // Determines if message history can be saved
	private static boolean newUsersAllowed = true; // Determines if new users can connect to the server
	private static Pane appUI; // Reference to server GUI
	private static TextMeServerController uiController;
	private static ArrayList<String> messageHistory = new ArrayList<String>(); // Holds sent messages from users
	private static ArrayList<String> usersUsernames = new ArrayList<String>();
	private static int maxUsers = 2147000000;
	protected static final HashMap<Integer, String> CMD_MSG_MAP = new HashMap<Integer, String>();
	private static int messagesSentOnServer = 0;

	/**
	 * Set's up the server's error list that can be sent to users
	 */
	private static void setupCommandHashMap() {
		// Sent if the server is past the maxUsers number at the users time of connection
		CMD_MSG_MAP.put(0, ":_svr/err_server_full;");
		// Send if the server has disabled new users from joining
		CMD_MSG_MAP.put(1, ":_svr/err_joining_closed;");
		// Sent if a user is kicked from the server
		CMD_MSG_MAP.put(13, ":_svr/err_kicked_from_server_reason");
		
		// Messages from server/client to recognize as commands
		CMD_MSG_MAP.put(2, "svr/msg_servershutdown;");
		CMD_MSG_MAP.put(3, "usr/msg_messagehistorycleared;");
		CMD_MSG_MAP.put(4, "svr/msg_getmessagehistory;");
		CMD_MSG_MAP.put(5, "usr/msg_quit;");
		CMD_MSG_MAP.put(6, "svr/msg_clearmessagehistory;");
		CMD_MSG_MAP.put(7, "svr/msg_endofhistory;");
		CMD_MSG_MAP.put(8, "usr/msg_joined;");
		CMD_MSG_MAP.put(9, "usr/msg_getservername;");
		CMD_MSG_MAP.put(10, "svr/msg_name-");
		CMD_MSG_MAP.put(11,"usr/msg_usernamechangeto_");
		CMD_MSG_MAP.put(12,"usr/msg_usernameis_");
		CMD_MSG_MAP.put(14, "svr/msg_getmessagehistory;");
		CMD_MSG_MAP.put(15, "svr/msg_endofhistory;");
		CMD_MSG_MAP.put(16, "svr/msg_priority_;");
		CMD_MSG_MAP.put(17, "svr/msg_warn_;");
		
	}
	
	/**
	 * Sets a reference to the owning application
	 * 
	 * @param appUIS
	 */
	public TextMeServerNetworkManager(Pane ui, TextMeServerController controller) {
		appUI = ui;
		uiController = controller;
		setupCommandHashMap();
	}

	protected long getMessagesSentOnServer() {
		if(messagesSentOnServer == 0) {
			return -1;
		} else {
			return messagesSentOnServer;
		}
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
		for(int i = 0; i < userSockets.size(); i++) { // Iterates through all connected users
			usersPrintStreams.get(i).print(message + "\n"); // Prints the message with a new line to buffer
			usersPrintStreams.get(i).flush(); // Push messages out to connected clients
		}
	}
	
	
	/**
	 * Sends message received by a user to all clients connected to server
	 * 
	 * @param String message to send to clients
	 */
	public void sendMessageToUserNet(String message, int user) {
		usersPrintStreams.get(user).print(message + "\n"); // Prints the message with a new line to buffer
		usersPrintStreams.get(user).flush(); // Push messages out to connected clients
	}
	/**
	 * Send a specific user the message history
	 * 
	 * @param int user to send message history to
	 */
	private void sendMessageHistory(int user) {
		for(int i = 0; i < messageHistory.size(); i++) {
			usersPrintStreams.get(user).print(messageHistory.get(i) + "\n");
			usersPrintStreams.get(user).flush();
		}
		usersPrintStreams.get(user).print(CMD_MSG_MAP.get(7) + "\n");
		usersPrintStreams.get(user).flush();
	}

	private boolean allowMessageHistory() {
		if(!(messageHistory.size() >= 2147000000) && allowMessageHistory == true) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Receives message from client and parses it before sending it back out
	 * 
	 * @param String message received by client
	 */
	private void recieveMessageNet(String message, int userIndex) {
			// Checks to see if the user has sent a critical command
			if(!parseMessageForCriticalCommands(message, userIndex)) {
				if(allowMessageHistory()) {
					messageHistory.add(message);
					messagesSentOnServer++;
					fillServerLogBox();
				}
				uiController.updateMessageCountUI();
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
		for(int i = 0; i < CMD_MSG_MAP.size(); i++) {
			if((message.toLowerCase().substring(message.indexOf(':') + 1, message.length())).contains(CMD_MSG_MAP.get(i))) {
				switch(i) {
					case 4:
						sendMessageHistory(userIndex);
						return true;
					case 5:
						sendMessageNet(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " left the chat.");
						if(allowMessageHistory()) {
							messageHistory.add(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " left the chat.");
						}
						closeSocket(userIndex);
						return true;
					case 8:
						sendMessageNet(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " joined the chat.");
						if(allowMessageHistory()) {
							messageHistory.add(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " joined the chat.");
						}
						return true;
					case 9:
						sendMessageToUserNet(CMD_MSG_MAP.get(10) + getServerName(), userIndex);
						return true;
					case 11:
						sendMessageNet(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " changed their username to " + message.substring(message.indexOf("o") + 2, message.length()));
						if(allowMessageHistory()) {
							messageHistory.add(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " changed their username to " + message.substring(message.indexOf("o") + 2, message.length()));
						}
						return true;
					case 12:
						usersUsernames.add(message.substring(message.indexOf("i") + 3, message.length()));
						fillUserControlBox();
						return true;
					default:
						return false;
				}
			}
		}
		return false;
	}

	private String getServerName() {
		return uiController.serverName;
	}
	
	/**
	 * Check constantly for new messages sent to the server
	 */
	public void watchForMessages() {
		Thread messageCheckLoop = new Thread(new Runnable() { // Creates a new Thread
		
		public void run() {
		while(true) { // Infinite loop
			try {
				for(int i = 0; i < userSockets.size(); i++) { // Iterates through all instances inside "sockets"
					if( usersBufferedReaders.get(i) != null) { // Checks to see if the bufferReader at "i" is valid
						if(usersBufferedReaders.get(i).ready()) { // Checks to see if the message at BufferReader[i] is ready
							recieveMessageNet(usersBufferedReaders.get(i).readLine(), i); // Sends message to parser
						}
					} else {
						closeSocket(i); // If the BufferedReader[i] is invalid, close the socket
					}
				}
				} catch (IOException IOE) {
					throwMessage("IOE reading message", true);
				}
			// Make the infinite loop sleep for 50 milliseconds
			try { 
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throwMessage("Sleep interrupted", true);
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
			usersBufferedReaders.get(user).close();
			usersBufferedReaders.remove(user); // Removes the BufferedReader object from bufferedReaders ArrayList
			
			// Closes the PrintStream assigned to "user"
			usersPrintStreams.get(user).close();
			usersPrintStreams.remove(user); // Removes the PrintStream object from printStreams ArrayList
			
			// Closes the Socket assigned to "user"
			userSockets.get(user).close();
			userSockets.remove(user); // Removes the Socket object from sockets ArrayList
			usersUsernames.remove(user);
			usersOnServer--;
			uiController.updateUserCountUI();
			fillUserControlBox();
		} catch (IOException e) {
			throwMessage("IOE at closing buffered reader", true);
		}
	}

	private void closeSocket(int user, boolean isConnectionClosed) {
		try {
			// Closes the BufferedReader assigned to "user"
			usersBufferedReaders.get(user).close();
			usersBufferedReaders.remove(user); // Removes the BufferedReader object from bufferedReaders ArrayList

			// Closes the PrintStream assigned to "user"
			usersPrintStreams.get(user).close();
			usersPrintStreams.remove(user); // Removes the PrintStream object from printStreams ArrayList

			// Closes the Socket assigned to "user"
			userSockets.get(user).close();
			userSockets.remove(user); // Removes the Socket object from sockets ArrayList
			usersOnServer--;
			uiController.updateUserCountUI();
			fillUserControlBox();
		} catch (IOException e) {
			throwMessage("IOE at closing buffered reader", true);
		}
	}
	
	/**
	 * Attempts to start the server
	 * 
	 * @return boolean if starting the server is successful
	 */
	public void startServer(int port, String serverName) {
		String ip = "";
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
			throwMessage("IOE at socket creation" + " " + IOE.getMessage(), true); // ServerSocket could not be set
		}

		uiController.setServerConnectionInfo(ip, serSocket.getLocalPort());
		
		allowNewUser(); // Adds a new user
		
		watchForMessages(); // Start watching for messages
	} 
	
	/**
	 * Adds a new BufferedReader and PrintStream into the bufferedReaders and printStreams ArrayLists
	 * 
	 * @param int user index
	 */
	private void createInputsAndOutputs(int user) {
		// Try to create a new BufferedReader and add it to bufferedReaders
		try {
			usersBufferedReaders.add(new BufferedReader(new InputStreamReader(userSockets.get(user).getInputStream())));
		} catch (IOException e) {
			throwMessage("IOE at adding BufferedReader", true);
		}
		// Try to create a new PrintStream and add it to printStreams
		try {
			usersPrintStreams.add(new PrintStream(userSockets.get(user).getOutputStream()));
		} catch (IOException e) {
			throwMessage("IOE at adding BufferedReader", true);
		}
		usersOnServer++;
		uiController.updateUserCountUI();
	}
	
	/**
	 * Closes the server and all associated variables
	 */
	public boolean closeServer() {
		if(serSocket != null) {
			if(!serSocket.isClosed()) {
				sendMessageNet("Server: " + CMD_MSG_MAP.get(2));
				sendMessageNet("Shutting down..."); // Sends message to users connected that server is closing
				try {
					for(int i = 0; i < userSockets.size(); i++) { // Iterates though all sockets
						userSockets.get(i).close(); // Closes the socket at "i"
						usersBufferedReaders.get(i).close(); // Closes the BufferedReader at "i"
						usersPrintStreams.get(i).close(); // Closes the PrintStream at "i"
				
						serSocket.close(); // Closes the server socket
					}
				} catch (IOException IOE) {
					throwMessage("IOE at closing server", true);
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
	}

	public void clearMessageHistory() {
		sendMessageNet("Server: " + CMD_MSG_MAP.get(6));
		messageHistory.clear();
		messagesSentOnServer = 0;
		throwMessage("Message History Cleared", false);
	}

	public void kickUser(int userIndex, String reason) {
		sendMessageToUserNet(CMD_MSG_MAP.get(13) + reason, userIndex);
		sendMessageNet(usersUsernames.get(userIndex) + " was kicked from the server");
		if(allowMessageHistory()) {
			messageHistory.add(usersUsernames.get(userIndex) + " was kicked from the server");
		}
		closeSocket(userIndex);
	}

	protected void fillServerLogBox() {
		uiController.clearServerMessageLogList();
		uiController.addMessageLogBox("test");
		if(allowMessageHistory()) {
			for(int i = 0; i < messageHistory.size(); i++) {
				uiController.addMessageLogBox(messageHistory.get(i));
			}
		} else {
			uiController.disableMessageLogBox();
		}
	}

	protected void fillUserControlBox() {
		uiController.clearServerUserControlList();
		uiController.addUserToUserControlList("test", 1);
		for(int i = 0; i < userSockets.size(); i++) {
			uiController.addUserToUserControlList(usersUsernames.get(i), i);
		}
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
						if(userSockets.size() < maxUsers) {
							try {
								userSockets.add(serSocket.accept()); // Adds a new socket in "sockets" and sets it to the connected user
							} catch (SocketTimeoutException e) {
								continue;
							} catch (IOException e) {
								throwMessage("Could not add socket", true);
							} 
							createInputsAndOutputs(usersOnServer);
							} else {
								try {
									userSockets.add(serSocket.accept()); // Adds a new socket in "sockets" and sets it to the connected user
								} catch (SocketTimeoutException e) {
									continue;
								} catch (IOException e) {
									throwMessage("Could not add socket", true);
								} 
								createInputsAndOutputs(usersOnServer);
								sendMessageToUserNet(CMD_MSG_MAP.get(0), usersOnServer - 1);
								closeSocket(usersOnServer - 1, true);
							}
					} else { // If a new user cannot join
						try {
							userSockets.add(serSocket.accept()); // Adds a new socket in "sockets" and sets it to the connected user
						} catch (SocketTimeoutException e) {
							continue;
						} catch (IOException e) {
							throwMessage("Could not add socket", true);
						} 
						createInputsAndOutputs(usersOnServer);
						sendMessageToUserNet(CMD_MSG_MAP.get(1), usersOnServer - 1);
						closeSocket(usersOnServer - 1, true);
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
	private void throwMessage(String err, boolean isError) {
		uiController.throwMessage(err, isError);
	}

}
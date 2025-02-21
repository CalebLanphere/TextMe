/**
 * @author Caleb Lanphere
 *
 * TextMe Application Server Network Manager
 *
 * Copyright 2024-2025 | Caleb Lanphere | All Rights Reserved
 *
 * TODO make work on a phone
 */


package com.creativitystudios.textmeserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.scene.layout.Pane;
import org.glassfish.tyrus.server.Server;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.net.InetAddress;
import java.util.Base64;
import java.util.HashMap;

public class TextMeServerNetworkManager {

	private static ArrayList<TextMeClientUser> userArrayList = new ArrayList<TextMeClientUser>();
	private static ServerSocket serSocket; // Server socket
	private static int usersOnServer = 0; // Number of users on the server
	private static boolean allowMessageHistory = true; // Determines if message history can be saved
	private static boolean newUsersAllowed = true; // Determines if new users can connect to the server
	private static Pane appUI; // Reference to server GUI
	private static TextMeServerController uiController; // UI Controller
	private static ArrayList<String> messageHistory = new ArrayList<String>(); // Holds sent messages from users
	// TODO allow server hosters to change MAX_USERS in ui up to 2147000000
	private static final int MAX_USERS = 2147000000; // Max amount of users allowed on the server
	protected static final HashMap<Integer, String> CMD_MSG_MAP = new HashMap<Integer, String>();
	protected static final HashMap<Integer, String> CMD_WEB_MAP = new HashMap<Integer, String>();
	private static int messagesSentOnServer = 0; // Tracks the amount of messages sent on the server
	//private static TextMeServerRSAEncryption encryption = new TextMeServerRSAEncryption();

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
		CMD_MSG_MAP.put(18, "usr/msg_getrsaenckey;");
		CMD_MSG_MAP.put(19, "svr/msg_rsaenckey_;");
		CMD_MSG_MAP.put(20, "svr/msg_getrsaenckey;");
		CMD_MSG_MAP.put(21, "usr/msg_rsaenckey_;");
		CMD_MSG_MAP.put(22, "svr/msg_aeskey_;");
		CMD_MSG_MAP.put(23, "svr/msg_aesiv_;");
		CMD_MSG_MAP.put(24, "GET / HTTP/1.1");

		//Web Commands
		CMD_WEB_MAP.put(0, "GET / HTTP/1.1");
		CMD_WEB_MAP.put(1, "Host:");
		CMD_WEB_MAP.put(2, "Upgrade: websocket");
		CMD_WEB_MAP.put(3, "Sec-WebSocket-Key:");
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

	/**
	 * Sends the number of messages on the server
	 * @return long amount of messages recorded on the server
	 */
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
		for(int i = 0; i < userArrayList.size(); i++) {// Iterates through all connected users
			if(userArrayList.get(i).getReadyForMessages()) { // Checks if the user is ready for user messages
				switch (userArrayList.get(i).getCurrentEncryptionMethod()) {
					case AES: // Sends message using the AES encryption protocol
						try {
							userArrayList.get(i).getUserPrintStream().print(userArrayList.get(i).getEncryption().encryptMessageAES(message) + "\n");
							userArrayList.get(i).getUserPrintStream().flush();
							break;
						} catch (Exception e) {
							throwMessage("Error in AES encrypting message", true);
							break;
						}
					case RSA: // Sends messages using the RSA encryption protcol
						// Should only be used for setting up AES
						try {
							userArrayList.get(i).getUserPrintStream().print(userArrayList.get(i).getEncryption().encryptMessageRSA(message) + "\n");
							userArrayList.get(i).getUserPrintStream().flush();
							break;
						} catch (Exception e) {
							throwMessage("Error in RSA encrypting message", true);
							break;
						}
					case NONE: // Sends messages without any encryption
						// Should only be used if the server disables encryption or setting up encryption
						userArrayList.get(i).getUserPrintStream().print(message + "\n"); // Prints the message with a new line to buffer
						userArrayList.get(i).getUserPrintStream().flush(); // Push messages out to connected clients
						break;
					default:// Sends messages without any encryption
						// Should only be used if the server disables encryption or setting up encryption
						userArrayList.get(i).getUserPrintStream().print(message + "\n"); // Prints the message with a new line to buffer
						userArrayList.get(i).getUserPrintStream().flush(); // Push messages out to connected clients
						break;
				}
			}
		}
	}
	
	
	/**
	 * Sends message received by a user to all clients connected to server
	 * 
	 * @param String message to send to clients
	 */
	public void sendMessageNet(String message, int user) {
		switch(userArrayList.get(user).getCurrentEncryptionMethod()) {
			case AES: // Sends message using the AES encryption protocal
				try {
					userArrayList.get(user).getUserPrintStream().print(userArrayList.get(user).getEncryption().encryptMessageAES(message) + "\n");
					userArrayList.get(user).getUserPrintStream().flush();
					break;
				} catch (Exception e) {
					throwMessage("Error in AES encrypting message", true);
					break;
				}
			case RSA:// Sends messages using the RSA encryption protcol
				// Should only be used for setting up AES
				try {
					userArrayList.get(user).getUserPrintStream().print(userArrayList.get(user).getEncryption().encryptMessageRSA(message) + "\n");
					userArrayList.get(user).getUserPrintStream().flush();
					break;
				} catch (Exception e) {
					throwMessage("Error in RSA encrypting message", true);
					break;
				}
			case NONE:// Sends messages without any encryption
				// Should only be used if the server disables encryption or setting up encryption
				userArrayList.get(user).getUserPrintStream().print(message + "\n"); // Prints the message with a new line to buffer
				userArrayList.get(user).getUserPrintStream().flush(); // Push messages out to connected clients
				break;
			default:// Sends messages without any encryption
				// Should only be used if the server disables encryption or setting up encryption
				userArrayList.get(user).getUserPrintStream().print(message + "\n"); // Prints the message with a new line to buffer
				userArrayList.get(user).getUserPrintStream().flush(); // Push messages out to connected clients
				break;
		}
	}

	/**
	 * Send a specific user the message history
	 * 
	 * @param int user to send message history to
	 */
	private void sendMessageHistory(int user) {
		switch(userArrayList.get(user).getCurrentEncryptionMethod()) {
			case AES: // Sends message using the AES encryption protocal
				try {
					for(int i = 0; i < messageHistory.size(); i++) {
						userArrayList.get(user).getUserPrintStream().print(userArrayList.get(user).getEncryption().encryptMessageAES(messageHistory.get(i)) + "\n");
						userArrayList.get(user).getUserPrintStream().flush();
					}
					userArrayList.get(user).getUserPrintStream().print(userArrayList.get(user).getEncryption().encryptMessageAES(CMD_MSG_MAP.get(7)) + "\n");
					userArrayList.get(user).getUserPrintStream().flush();
					break;
				} catch (Exception e) {
					throwMessage("Error in AES encrypting message", true);
					break;
				}
			case RSA:// Sends messages using the RSA encryption protcol
				// Should only be used for setting up AES
				try {
					for(int i = 0; i < messageHistory.size(); i++) {
						userArrayList.get(user).getUserPrintStream().print(userArrayList.get(user).getEncryption().encryptMessageRSA(messageHistory.get(i)) + "\n");
						userArrayList.get(user).getUserPrintStream().flush();
					}
					userArrayList.get(user).getUserPrintStream().print(userArrayList.get(user).getEncryption().encryptMessageRSA(CMD_MSG_MAP.get(7)) + "\n");
					userArrayList.get(user).getUserPrintStream().flush();
				} catch (Exception e) {
					throwMessage("Error in RSA encrypting message", true);
					break;
				}
			case NONE:// Sends messages without any encryption
				// Should only be used if the server disables encryption or setting up encryption
				for(int i = 0; i < messageHistory.size(); i++) {
					userArrayList.get(user).getUserPrintStream().print(messageHistory.get(i) + "\n"); // Prints the message with a new line to buffer
					userArrayList.get(user).getUserPrintStream().flush(); // Push messages out to connected clients
				}
				userArrayList.get(user).getUserPrintStream().print(CMD_MSG_MAP.get(7) + "\n");
				userArrayList.get(user).getUserPrintStream().flush();
				break;
			default:// Sends messages without any encryption
				// Should only be used if the server disables encryption or setting up encryption
				for(int i = 0; i < messageHistory.size(); i++) {
					userArrayList.get(user).getUserPrintStream().print(messageHistory.get(i) + "\n"); // Prints the message with a new line to buffer
					userArrayList.get(user).getUserPrintStream().flush(); // Push messages out to connected clients
				}
				userArrayList.get(user).getUserPrintStream().print(CMD_MSG_MAP.get(7) + "\n");
				userArrayList.get(user).getUserPrintStream().flush();
				break;
		}
	}

	/**
	 * Checks if the server allows message recording and has the room for new messages to be recorded
	 * @return
	 */
	private boolean allowMessageHistory() {
		if(!(messageHistory.size() >= 2147000000) && allowMessageHistory == true) {
			return true;
		} else {
			if(messageHistory.size() >= 2147000000) { // If it is greater than maximum allowed
				messageHistory.removeFirst(); // Remove the message first added and allow it
				return true;
			}
			return false;
		}
	}
	
	/**
	 * Receives message from client and parses it before sending it back out
	 * 
	 * @param String message received by client
	 */
	private void recieveMessageNet(String message, int userIndex) {
		String decryptedMessage = "err";
		switch(userArrayList.get(userIndex).getCurrentEncryptionMethod()) {
			case AES: // Decrypts the message received from clients using the AES protocol
				try {
					decryptedMessage = userArrayList.get(userIndex).getEncryption().decryptMessageAES(message);
					break;
				} catch(Exception e) {
					throwMessage("Error at decrypting message AES", true);
				}
			case RSA: // Decrypts the message received from clients using the RSA protocol
				// Should only be used when setting up AES encryption
				try {
					decryptedMessage = userArrayList.get(userIndex).getEncryption().decryptMessageRSA(message);
					break;
				} catch(Exception e) {
					throwMessage("Error at decrypting message RSA", true);
				}
			case NONE: // Reads message sent without encryption
				// Should only be used if server disables encryption or client is setting up encryption
				decryptedMessage = message;
				break;
			default: // Reads message sent without encryption
				// Should only be used if server disables encryption or client is setting up encryption
				decryptedMessage = message;
				break;
		}
			if(!parseMessageForCriticalCommands(decryptedMessage, userIndex)) {
				if(allowMessageHistory()) {
					messageHistory.add(decryptedMessage);
					messagesSentOnServer++;
					fillServerLogBox();
				}
				uiController.updateMessageCountUI();
				sendMessageNet(decryptedMessage); // Sends the message to all connected clients
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
					case 4: // Requested to get the server message history
						sendMessageHistory(userIndex);
						return true;
					case 5: // User quits the server
						sendMessageNet(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " left the chat.");
						if(allowMessageHistory()) {
							messageHistory.add(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " left the chat.");
						}
						closeSocket(userIndex, false);
						return true;
					case 8: // User joined the server
						sendMessageNet(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " joined the chat.");
						if(allowMessageHistory()) {
							messageHistory.add(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " joined the chat.");
						}
						return true;
					case 9: // User requesting the server name
						sendMessageNet(CMD_MSG_MAP.get(10) + getServerName(), userIndex);
						return true;
					case 11: // User changed their username
						sendMessageNet(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " changed their username to " + message.substring(message.indexOf("o") + 2, message.length()));
						if(allowMessageHistory()) {
							messageHistory.add(message.toLowerCase().substring(0, message.indexOf(':') + 1) + " changed their username to " + message.substring(message.indexOf("o") + 2, message.length()));
						}
						userArrayList.get(userIndex).setUserUsername(message.substring(message.indexOf("o") + 2, message.length()));
						return true;
					case 12: // User sending their username to the server
						userArrayList.get(userIndex).setUserUsername(message.substring(message.indexOf("i") + 3, message.length()));
						fillUserControlBox();
						return true;
					case 18: // User requests the public key for RSA encryption
						try {
							userArrayList.get(userIndex).getEncryption().createRSAKeyPair();
							sendMessageNet(CMD_MSG_MAP.get(19) + userArrayList.get(userIndex).getEncryption().getRSAPublicKey(), userIndex);
							sendMessageNet(CMD_MSG_MAP.get(20), userIndex);
							return true;
						} catch(Exception e) {
							throwMessage(e.getMessage(), true);
							return true;
						}
					case 21: // Receving the user's RSA public key for encryption
						try {
							userArrayList.get(userIndex).getEncryption().recreateRSAKey(message.substring(message.indexOf("y") + 3));
							userArrayList.get(userIndex).setCurrentEncryptionMethod(TextMeServerEncryption.EncryptionStatuses.RSA);
							userArrayList.get(userIndex).getEncryption().createAESKey();
							sendMessageNet(CMD_MSG_MAP.get(22) + userArrayList.get(userIndex).getEncryption().getAESKey(), userIndex);
							sendMessageNet(CMD_MSG_MAP.get(23) + userArrayList.get(userIndex).getEncryption().getIv(), userIndex);
							userArrayList.get(userIndex).setCurrentEncryptionMethod(TextMeServerEncryption.EncryptionStatuses.AES);
							userArrayList.get(userIndex).setReadyForMessages(true);
							return true;
						} catch(Exception e) {
							throwMessage(e.getMessage(), true);
							return true;
						}
					default:
						return false;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the server name thats set in the uiController
	 * @return String serverName
	 */
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
				for(int i = 0; i < userArrayList.size(); i++) { // Iterates through all instances inside "sockets"
					if( userArrayList.get(i).getUserBufferedReader() != null) { // Checks to see if the bufferReader at "i" is valid
						if(userArrayList.get(i).getUserBufferedReader().ready()) { // Checks to see if the message at BufferReader[i] is ready
							recieveMessageNet(userArrayList.get(i).getUserBufferedReader().readLine(), i); // Sends message to parser
						}
					} else {
						closeSocket(i, false); // If the BufferedReader[i] is invalid, close the socket
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
	 * @param user int user to remove
	 * @param isConnectionClosedUponStart boolean determines if the connection was closed upon start
	 */
	private void closeSocket(int user, boolean isConnectionClosedUponStart) {
		try {
			// Closes the BufferedReader assigned to "user"
			userArrayList.get(user).close();
			userArrayList.remove(user);
			usersOnServer--;
			uiController.updateUserCountUI();

			fillUserControlBox();
		} catch (Exception e) {
			throwMessage("IOE at closing client socket", true);
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
			//encryption.createKeyPair();
		} catch (Exception e) {
			throwMessage("Exception at socket creation" + " " + e.getMessage(), true); // ServerSocket could not be set
		}

		uiController.setServerConnectionInfo(ip, serSocket.getLocalPort());
		
		allowNewUser(); // Adds a new user
		
		watchForMessages(); // Start watching for messages

		Server webServer = new Server("localhost", (port + 1), "/", WebServerEndpoint.class);
		// TODO REFACTOR SERVER CODE TO ONLY USE WEB ENDPOINT AS MAIN NETWORK MANAGER
		// TODO MAY CREATE ISSUES WITH KICKING AND USER DIFFERENTIATION SINCE CREATING
		// TODO A ARRAYLIST DOES NOT WORK AS FAR AS I AM AWARE
		try {
			webServer.start();
		} catch (Exception e) {
			throwMessage(e.getMessage(), true);
		}

    }
	
	/**
	 * Adds a new BufferedReader and PrintStream into the bufferedReaders and printStreams ArrayLists
	 * 
	 * @param int user index
	 */
	private void createInputsAndOutputs(int user) {
		// Try to create a new BufferedReader and add it to bufferedReaders
		try {
			userArrayList.get(user).setUserBufferedReader(new BufferedReader(new InputStreamReader(userArrayList.get(user).getClientSocket().getInputStream())));
			System.out.print("added buffered reader");
		} catch (IOException e) {
			throwMessage("IOE at adding BufferedReader", true);
		}
		// Try to create a new PrintStream and add it to printStreams
		try {
			userArrayList.get(user).setUserPrintStream(new PrintStream(userArrayList.get(user).getClientSocket().getOutputStream()));
			System.out.print("added print stream");
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
					userArrayList.clear();
					serSocket.close(); // Closes the server socket
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

	/**
	 * Clears the message history recorded on the server and tells all connected clients to do the same
	 */
	public void clearMessageHistory() {
		sendMessageNet("Server: " + CMD_MSG_MAP.get(6)); // Clears clients messages
		messageHistory.clear(); // Clears local messages
		messagesSentOnServer = 0; // Resets messages sent counter
		throwMessage("Message History Cleared", false); // Notifies user of successful clear
	}

	/**
	 * Kicks the user at userindex for the reason listed
	 * @param userIndex int user to kick
	 * @param reason String reason for kicking a user
	 */
	public void kickUser(int userIndex, String reason) {
		sendMessageNet(CMD_MSG_MAP.get(13) + reason, userIndex);
		sendMessageNet(userArrayList.get(userIndex).getUserUsername() + " was kicked from the server");
		if(allowMessageHistory()) {
			messageHistory.add(userArrayList.get(userIndex).getUserUsername() + " was kicked from the server");
		}
		closeSocket(userIndex, false);
	}

	/**
	 * Fills the server's log box with all messages sent on the server at the time of executing
	 */
	protected void fillServerLogBox() {
		uiController.clearServerMessageLogList();
		if(allowMessageHistory()) {
			for(int i = 0; i < messageHistory.size(); i++) {
				uiController.addMessageLogBox(messageHistory.get(i));
			}
		} else {
			uiController.disableMessageLogBox();
		}
	}

	/**
	 * Fills the user control box with all users connected at the time of executing
	 */
	protected void fillUserControlBox() {
		uiController.clearServerUserControlList();
		for(int i = 0; i < userArrayList.size(); i++) {
			uiController.addUserToUserControlList(userArrayList.get(i).getUserUsername(), i);
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
						if(userArrayList.size() < MAX_USERS) {
							try {
								userArrayList.add(new TextMeClientUser(serSocket.accept(), usersOnServer)); // Adds a new socket in "sockets" and sets it to the connected user
							} catch (SocketTimeoutException e) {
								continue;
							} catch (IOException e) {
								if(!serSocket.isClosed()) {
									throwMessage("Could not add socket", true);
								}
							}
							if(!userArrayList.isEmpty()) {
								createInputsAndOutputs(usersOnServer);
							}
							} else {
								try {
									userArrayList.add(new TextMeClientUser(serSocket.accept(), usersOnServer)); // Adds a new socket in "sockets" and sets it to the connected user
								} catch (SocketTimeoutException e) {
									continue;
								} catch (IOException e) {
									throwMessage("Could not add socket", true);
								}
								if (!userArrayList.isEmpty()) {
									createInputsAndOutputs(usersOnServer);
									sendMessageNet(CMD_MSG_MAP.get(0), usersOnServer - 1);
									closeSocket(usersOnServer - 1, true);
								}
							}
					} else { // If a new user cannot join
						try {
							userArrayList.add(new TextMeClientUser(serSocket.accept(), usersOnServer)); // Adds a new socket in "sockets" and sets it to the connected user
						} catch (SocketTimeoutException e) {
							continue;
						} catch (IOException e) {
							throwMessage("Could not add socket", true);
						}
						if(!userArrayList.isEmpty()) {
							createInputsAndOutputs(usersOnServer);
							sendMessageNet(CMD_MSG_MAP.get(1), usersOnServer - 1);
							closeSocket(usersOnServer - 1, true);
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
	private void throwMessage(String err, boolean isError) {
		uiController.throwMessage(err, isError);
	}

	private String createWebSocketAcceptKey(String keyReceived) {
		String responseKey = keyReceived.strip();
		responseKey += "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        byte[] responseKeyByte;
        try {
            responseKeyByte = MessageDigest.getInstance("SHA-1").digest(responseKey.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(responseKeyByte);
    }

}
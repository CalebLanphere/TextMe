/**
 * @author Caleb Lanphere
 * 
 * TextMe Application Client Network Manager
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package com.creativitystudios.textme;

import javafx.scene.layout.Pane;
import java.io.*;
import java.net.*;
import java.util.HashMap;

public class TextMeClientNetManager {
	
	private static Socket socket = new Socket(); // Initializes client socket
	private static PrintStream out; // Initializes client PrintStream
	private static BufferedReader in; // Initializes client BufferedReader
	private static boolean messageHistoryStatus; // Determines if the application has received server message history
	private static Pane appUI; // UI reference of the application GUI
	private static TextMeAppController uiController;
	private static boolean receivedError = false; // States if app received error from server
	// Stores all commands for application to check for
	protected static final HashMap<Integer, String> CMD_MAP = new HashMap<Integer, String>();
	protected static String serverName;
	
	
	/** 
	 * Attempts to connect socket to server at given IP and port
	 * 
	 * @param ip the IPv4 address to use for connection
	 * @param port the port in int form
	 * @return boolean if connection was successful
	 */
	public boolean attemptConnection(String ip, int port) {
		// Translate IP into socketAddress format
		InetAddress ipNet;
		
		// Try to make InetAddress with ip provided
		try {
		ipNet = InetAddress.ofLiteral(ip); 
		} catch (IllegalArgumentException IAE) {
			throwMessage("IP address is invalid\nPlease use only numbers\n0-9", true); // IP is not valid
			return false; // return that the connection failed to connect
		}
		
		// Create SocketAddress based off created InetAddress
		SocketAddress ipFiltered = new InetSocketAddress(ipNet, port);
		receivedError = false;
		
		// Try to run the connection with the timeout limit 5000ms
		try {
			socket.connect(ipFiltered, 5000);
		} catch (SocketTimeoutException timeOut) { // Socket timed out exception
			throwMessage("Connection timed out", true);
			return false; // return that the connection failed to connect
		} catch (IllegalArgumentException IAE) { // Argument is invalid
			throwMessage(IAE.getMessage(), true);
			return false; // return that the connection failed to connect
		} catch (IOException IOE) { // IO is not what was expected
			if(!(IOE.getMessage().equals("already connected"))) {
				socket = new Socket();
				throwMessage(IOE.getMessage(), true);
				return false; // return that the connection failed to connect
			} else { // Always called when connecting to multiple servers
				return false;
			}
		}
		
		// If connection was successful, try to set the BufferedReader and PrintStream to the sockets input/output streams
		
		try {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException IOE) {
			throwMessage("Failed to connect\nBufferedReader", true); // Improper argument to set BufferedReader
			return false; // return that the connection failed to connect
		}
		try {
		out = new PrintStream(socket.getOutputStream());
		} catch (IOException IOE) {
			throwMessage("Failed to connect\nPrintStream", true); // Improper argument to set PrintStream
			return false; // return that the connection failed to connect
		}
		watchForMessages();
		requestServerNameNet();
		requestMessageHistoryNet();
		sendUsernameToServer();
		return true;
	}

	private static void requestServerNameNet() {
		sendMessageNet("usr/msg_getservername;");
	}

	private static void sendUsernameToServer() {
		sendMessageNet(CMD_MAP.get(8) + uiController.getUsername());
	}
	
	private static void setupCommandHashMap() {
		// Messages sent from server to recognize as errors
		CMD_MAP.put(0, "svr/err_joining_closed;");
		CMD_MAP.put(1, "svr/err_server_full;");
		CMD_MAP.put(9, "svr/err_kicked_from_server");
		
		// Messages from server/client to recognize as commands
		CMD_MAP.put(2, "svr/msg_servershutdown;");
		CMD_MAP.put(3, "svr/msg_clearmessagehistory;");
		CMD_MAP.put(4, "svr/msg_getmessagehistory;");
		CMD_MAP.put(5, "svr/msg_endofhistory;");
		CMD_MAP.put(6, "svr/msg_name-");
		CMD_MAP.put(7, "usr/msg_usernamechangeto_");
		CMD_MAP.put(8,"usr/msg_usernameis_");
		CMD_MAP.put(10, "usr/msg_messagehistorycleared;");
		CMD_MAP.put(11, "usr/msg_quit;");
		CMD_MAP.put(12, "usr/msg_joined;");
		CMD_MAP.put(13, "usr/msg_getservername;");
	}
	
	/**
	 * Sets the UI reference
	 * @param Pane application reference
	 */
	public  TextMeClientNetManager(TextMeAppController uiOwner, Pane ui) {
		appUI = ui;
		uiController = uiOwner;
		setupCommandHashMap();
	}
	
	/**
	 * Returns if the socket associated to the client is connected to a server
	 * 
	 * @return boolean is socket connected to a server
	 */
	public boolean isConnected() {
		return socket.isConnected();
	}
	
	/**
	 * Sends message provided by the appUI to the server
	 * 
	 * @param message string to send to server
	 */
	public static void sendMessageNet(String message) {

		if(socket.isConnected()) { // Checks to see if the socket is connected to a server
			if(receivedError != true) {
				try {
					out.print(message + "\n"); // Sends the message to the buffer and adds "\n" to indicate message end
					out.flush(); // Pushes message to server
				} catch (NullPointerException e) {
					throwMessage("Error sending message\n" + e.getMessage(), true);
				}
			} else {
				// Do nothing
			}
		} else {
			if(receivedError != true) {
				throwMessage("Socket is not connected to a server", true); // If socket is not connected, throw error
			}
		}
	}
	
	/**
	 * Creates a Thread so Thread.sleep can be used to delay when the application clears the error received
	 */
	private static void resetReceivedError() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					throwMessage(e.getMessage(), true);
				}
				receivedError = false;
			}
		});
		thread.run();
	}
	
	/**
	 * Clear both in/out variables and reinitialize socket variable
	 */
	public static void resetConnection() {
		resetReceivedError();
		socket = new Socket();
		in = null;
		out = null;
		resetReceivedError();
	}
	
	/**
	 * Send received message to parser and determine if it gets sent to the receivedMessageBox GUI
	 * 
	 * @param message message received from server
	 */
	private static void receiveMessageNet(String message) {
		if(!isMessageCriticalCommand(message)) {
			uiController.addMessageToUI(message); // Calls the addMessage function in appUI and sends the message received
		}
	}
	
	/**
	 * Checks to see if the message contains a critical command
	 * 
	 * @param String message to check
	 * @return boolean if message contains critical command
	 */
	private static boolean isMessageCriticalCommand(String message) {
		for(int i = 0; i < CMD_MAP.size(); i++) {
			if(message.toLowerCase().substring(message.indexOf(':') + 1, message.length()).contains(CMD_MAP.get(i))) {
				switch(i) {
					case 0:
						receivedError = true;
						uiController.resetForReconnection();
						throwMessage("Error connecting to server \nServer is not allowing new users at this time", true);
						return true;
					case 1:
						receivedError = true;
						uiController.resetForReconnection();
						throwMessage("Error connecting to server \nServer is full", false);
						return true;
					case 2:
						uiController.resetForReconnection();
						throwMessage("Disconnected from server \nServer shutting down", false);
						return true;
					case 3:
						uiController.clearMessageHistory();
						return true;
					case 4:
						return true;
					case 5:
						messageHistoryStatus = true;
						uiController.sendMessageToNetManager("usr/msg_joined;");
						return true;
					case 6:
						serverName = message.substring(message.indexOf("-") + 1);
						uiController.setServerName(serverName);
						return true;
					case 9:
						receivedError = true;
						uiController.resetForReconnection();
						throwMessage("Disconnected from server \nYou were kicked from the server", false);
						return true;
					default:
						return false;
				}
			}
		}
		return false;
	}
	
	/**
	 * Requests server message history be forwarded to the client
	 * 
	 * @return 
	 */
	public boolean requestMessageHistoryNet() {
		messageHistoryStatus = false;
		sendMessageNet(": " + CMD_MAP.get(4));
		return true;
	}
	
	/**
	 * Constantly checks for new messages from server
	 */
	public void watchForMessages() { 
		
		// Creates a new thread that contains the message checking code; application hangs without new thread
		Thread messageLoop = new Thread(new Runnable() {
			
			public void run() {
				
				while(true) { // Creates the infinite loop
					try { // Try to check if the BufferedReader has a new message
						
						// Checks to see that variable "in" is valid and is ready to read
						if(in != null && in.ready()) {
							receiveMessageNet(in.readLine()); // calls receiveMessageNet and gives it the new message
						} else { // If variable "in" is valid and not ready, try to have the thread sleep
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) { // Sleep interrupted
								throwMessage(e.getMessage(), true);
							}
						}
					} catch (IOException IOE) { // "in.ready()" function fails
						throwMessage(IOE.getMessage(), true);
					}
					
					// Check to see if variables "in" or "out" are null
					if(in == null || out == null) {
						try { 
							Thread.sleep(100); // If the values are null, wait for values to be assigned
						} catch (InterruptedException e) { // Sleep interrupted
							throwMessage(e.getMessage(), true);
						}
					}
				}
			}
			});
		
		messageLoop.start(); // Starts the thread loop
		
	}

	/**
	 * Prints any error to the screen
	 * 
	 * @param err string that has error message
	 */
	private static void throwMessage(String err, boolean isError) {
		uiController.throwMessage(err, isError);
	}

	/**
	 * Closes the socket and clearing all references set upon connection
	 */
	public void closeConnection() {
		try { // Try to close socket
			if(in != null && out != null) {
				socket.close();
				in.close();
				out.close();
			} else {
				socket.close();
			}
		} catch (IOException e) { // IO does not match to close socket
			throwMessage(e.getMessage(), true);
		}
	}
}

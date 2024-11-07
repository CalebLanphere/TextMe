/**
 * @author Caleb Lanphere
 * 
 * TextMe Application client netcode
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package appTextMe;

import java.io.*;
import java.net.*;

public class netCommClient {
	
	private static Socket socket = new Socket(); // Initializes client socket
	private static PrintStream out; // Initializes client PrintStream
	private static BufferedReader in; // Initializes client BufferedReader
	private static boolean messageHistoryStatus;
	
	
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
			throwError("IP provided is invalid"); // IP is not valid
			return false; // return that the connection failed to connect
		}
		
		// Create SocketAddress based off created InetAddress
		SocketAddress ipFiltered = new InetSocketAddress(ipNet, port);
		
		// Try to run the connection with the timeout limit 5000ms
		try {
			socket.connect(ipFiltered, 5000);
		} catch (SocketTimeoutException timeOut) { // Socket timed out exception
			throwError("Timed out");
			return false; // return that the connection failed to connect
		} catch (IllegalArgumentException IAE) { // Argument is invalid
			throwError("Argument is invalid at connection");
			return false; // return that the connection failed to connect
		} catch (IOException IOE) { // IO is not what was expected
			throwError("IOException");
			return false; // return that the connection failed to connect
		}
		
		// If connection was successful, try to set the BufferedReader and PrintStream to the sockets input/output streams
		
		try {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException IOE) {
			throwError("IOE at setting input stream"); // Improper argument to set BufferedReader
			return false; // return that the connection failed to connect
		}
		try {
		out = new PrintStream(socket.getOutputStream());
		} catch (IOException IOE) {
			throwError("IOE at setting output stream"); // Improper argument to set PrintStream
			return false; // return that the connection failed to connect
		}
		return true;
	}
	
	/**
	 * Sends message provided by the appUI to the server
	 * 
	 * @param message string to send to server
	 */
	public static void sendMessageNet(String message) {

		if(socket.isConnected()) { // Checks to see if the socket is connected to a server
			out.print(message + "\n"); // Sends the message to the buffer and adds "\n" to indicate message end
			out.flush(); // Pushes message to server
			
			// Try to receive message from server after sending
			//try {
			//	receiveMessageNet(in.readLine()); // Calls recieveMessageNet function with the value of what was received
			//} catch (IOException e) { // Message received is invalid
			//	throwError("Failed to recieve message");
			//}
		} else {
			throwError("socket closed or server closed"); // If socket is not connected, throw error
		}
	}
	
	/**
	 * 
	 * @param message message received from server
	 */
	private static void receiveMessageNet(String message) {
		if(!isMessageCriticalCommand(message)) {
			appUIC.addMessage(message); // Calls the addMessage function in appUI and sends the message received
		}
	}
	
	/**
	 * Checks to see if the message contains a critical command
	 * 
	 * @param String message to check
	 * @return boolean if message contains critical command
	 */
	private static boolean isMessageCriticalCommand(String message) {
		if(message.toLowerCase().substring(message.indexOf(':') + 1, message.length()).contains("endofhistory;")) {
			messageHistoryStatus = true;
			sendMessageNet("user has connected");
			return true;
		} else if(message.toLowerCase().substring(message.indexOf(':') + 1, message.length()).contains("getmessagehistory;")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean getMessageHistoryNet() {
		messageHistoryStatus = false;
		sendMessageNet("getmessagehistory;");
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
						} 
						// If variable "in" is null, close the connection
						else if (in == null) { // 
							closeConnection();
							appUIC.addMessage("disconnected from server");
						} else { // If variable "in" is valid and not ready, try to have the thread sleep
							try { 
								Thread.sleep(100);
							} catch (InterruptedException e) { // Sleep interrupted
								throwError("error sleeping");
							}
						}
					} catch (IOException IOE) { // "in.ready()" function fails
						throwError("Error detecting messages");
					}
					
					// Check to see if variables "in" or "out" are null
					if(in == null || out == null) {
						throwError("Server connection lost");  // If variables "in" or "out" are invalid, throw error
					}
				}
			}
			});
		
		messageLoop.start(); // Starts the thread loop
		
	}

	/**
	 * Prints any error to the console
	 * 
	 * @param err string that has error message
	 */
	private static void throwError(String err) {
		System.out.println(err);
	}
	
	/**
	 * Closes the socket and clearing all references set upon connection
	 */
	public void closeConnection() {
		try { // Try to close socket
			socket.close();
		} catch (IOException e) { // IO does not match to close socket
			throwError("failed to close socket");
		}
	}
}

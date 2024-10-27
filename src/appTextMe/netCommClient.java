package appTextMe;

import java.io.*;
import java.net.*;

public class netCommClient {
	
	static Socket socket = new Socket();
	static PrintStream out;
	static BufferedReader in;
	
	public static void startSocket() {
		
	}

	public static boolean attemptConnection(String ip, int port) {
		// Filter IP into socketAddress format
		InetAddress ipNet;
		try {
		ipNet = InetAddress.ofLiteral(ip);
		} catch (IllegalArgumentException IAE) {
			System.out.println("Illegal IP");
			return false;
		}

		SocketAddress ipFiltered = new InetSocketAddress(ipNet, port);
		try {
			socket.connect(ipFiltered, 5000);
		} catch (SocketTimeoutException timeOut) {
			System.out.println("Timed out");
			return false;
		} catch (IllegalArgumentException IAE) {
			System.out.println("Argument is invalid at connection");
			return false;
		} catch (IOException IOE) {
			System.out.println("IOException");
			return false;
		}
		try {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException IOE) {
			throwError("IOE at setting input stream");
			return false;
		}
		try {
		out = new PrintStream(socket.getOutputStream());
		} catch (IOException IOE) {
			throwError("IOE at setting output stream");
			return false;
		}
		return true;
	}
	
	public static void sendMessageNet(String message) {
		if(socket.isConnected()) {
			out.print(message + "\n");
			out.flush();
			try {
				recieveMessageNet(in.readLine());
			} catch (IOException e) {
				throwError("Failed to recieve message");
			}
		} else {
			throwError("socket closed or server closed");
		}
	}
	
	public static void recieveMessageNet(String message) {
		appUI.addMessage(message);
	}
	
	public static void watchForMessages() { // Hangs application, needs to properly detect messages when they are recieved
		// when triggered it causes the app to hang
		Thread messageLoop = new Thread(new Runnable() {
			public void run() {
				while(true) {
					try {
						if(in != null && in.ready()) {
							recieveMessageNet(in.readLine());
						} else if (in == null) {
							closeConnection();
							appUI.addMessage("disconnected from server");
						} else {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								throwError("error sleeping");
							}
						}
					} catch (IOException IOE) {
						throwError("Error detecting messages");
					}
					if(in == null || out == null) {
						System.out.println("Server connection lost");
					}
				}
			}
			});
		messageLoop.start();
	}

	public static void throwError(String err) {
		System.out.println(err);
	}
	
	public static String getUserID() {
		out.print("getID" + "\n");
		out.flush();
		out.print("-1" + "\n");
		out.flush();
		try {
			return in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "-1";
		}
	}
	
	public static void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			throwError("failed to close socket");
		}
	}
}

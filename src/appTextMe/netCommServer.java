package appTextMe;

import java.io.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.*;
import javax.swing.*;

public class netCommServer extends JFrame implements WindowListener{
	
	static Socket socket1 = new Socket();
	static Socket socket2 = new Socket();
	static ServerSocket serSocket;
	static OutputStream out1;
	static InputStream in1;
	static OutputStream out2;
	static InputStream in2;
	static BufferedReader inRead1;
	static PrintStream outWrite1;
	static BufferedReader inRead2;
	static PrintStream outWrite2;
	static int usersOnServer = 0;
	static boolean firstUserConnected = false;
	static netCommServer baseUI;
	static WindowListener ui;
	
	public static void main(String[] args) {
		JFrame base = new JFrame("Server");
		base.setSize(new Dimension(5,5));
		base.addWindowListener(baseUI);
		base.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		base.setVisible(true);
		
		hostServer();
		watchForMessages();
	}
	
	public static void sendMessageNet(String message) {
		outWrite1.print(message + "\n");
		outWrite1.flush();
		outWrite2.print(message + "\n");
		outWrite2.flush();
	}
	
	public static void recieveMessageNet(String message ) {
			if(message.equals("getID") == true) {
				if(!firstUserConnected) {
					outWrite1.print(usersOnServer + "\n");
					outWrite1.flush();
					firstUserConnected = true;
				} else if(message.toLowerCase().equals("quit")) {
				
				}else {
					outWrite2.print(usersOnServer + "\n");
					outWrite2.flush();
				}
			} else {
				outWrite1.print(message + "\n");
				outWrite1.flush();
				outWrite2.print(message + "\n");
				outWrite2.flush();
			}
	}
	
	public static void watchForMessages() { // Hangs application, needs to properly detect messages when they are received
		//InputStreamReader inpReader1 = new InputStreamReader(in1);
		//InputStreamReader inpReader2 = new InputStreamReader(in2);// Needs to have separate server app to chat app
		
		while(true) {
			try {
				if(inRead1 != null) {
					if(inRead1.ready()) {
						recieveMessageNet(inRead1.readLine());
					}
				} if(inRead2 != null) {
				 	if (inRead2.ready()) {
						recieveMessageNet(inRead2.readLine());
					}
				}
				} catch (IOException IOE) {
					throwError("IOE reading message");
				}
		}
		
	}
	
	public static void closeSocket(int user) {
		if(user == 0) {
			socket1 = new Socket();
			try {
				in1 = socket1.getInputStream();
			} catch (IOException IOE) {
				throwError("IOE at clearing in1");
			}
			try {
				out1 = socket1.getOutputStream();
			} catch (IOException IOE) {
				throwError("IOE at clearing out1");
			}
		} else {
			socket2 = new Socket();
			try {
				in2 = socket2.getInputStream();
			} catch (IOException IOE) {
				throwError("IOE at clearing in1");
			}
			try {
				out2 = socket2.getOutputStream();
			} catch (IOException IOE) {
				throwError("IOE at clearing out1");
			}
		}
	}
	
	public static boolean hostServer() { // Continue here, server gets hosted but cannot receive connections
		try {
			serSocket = new ServerSocket(0, 2, InetAddress.getLocalHost()); // cannot use port 80, 21, 443
		} catch (IOException IOE) {
			throwError("IOE at socket creation" + IOE.getMessage());
			return false;
		}
		
		System.out.println("IP address: " + serSocket.getLocalSocketAddress());
		System.out.println("port: " + serSocket.getLocalPort());
		
		try {
			socket1 = serSocket.accept();
		} catch (IOException IOE) {
			throwError("IOE at accepting socket");
			return false;
		}
		createInputsAndOutputs(usersOnServer);
		
		try {
			socket2 = serSocket.accept();
		} catch (IOException IOE) {
			throwError("IOE at accepting socket");
			return false;
		}
		createInputsAndOutputs(usersOnServer);
		
		return true;
	}
	
	public static void createInputsAndOutputs(int user) {
		if(user == 0) {
			try {
				inRead1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
			} catch (IOException e) {
				throwError("IOE at creating reader in");
			}
			try {
				outWrite1 = new PrintStream(socket1.getOutputStream());
			} catch (IOException e) {
				throwError("IOE at creating reader in");
			}
			usersOnServer++;
			System.out.print("user connected");
			
		} else if (user == 1) {
			try {
				inRead2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
			} catch (IOException e) {
				throwError("IOE at creating reader in");
			}
			try {
				outWrite2 = new PrintStream(socket2.getOutputStream());
			} catch (IOException e) {
				throwError("IOE at creating reader in");
			}
			usersOnServer++;
			System.out.print("user connected");
		}
	}
	
	public static void closeServer() {
		if(!serSocket.isClosed()) {
			try {
			socket1.close();
			socket2.close();
			serSocket.close();
			} catch (IOException IOE) {
				throwError("IOE at closing server");
			}
		}
	}
	
	public static void throwError(String err) {
		System.out.println(err);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		recieveMessageNet("server closing");
		closeServer();
		
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

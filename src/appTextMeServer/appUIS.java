/**
 * @author Caleb Lanphere
 * 
 * TextMe Application Server GUI
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 * @TODO
 * Add server name option during setup
 * Allow users to grab server information like IP and Port
 * Add options menu that shows creator information
 * 
 */

package appTextMeServer;

import java.awt.*;
import java.awt.event.*;
import java.net.SocketAddress;

import javax.swing.*;

public class appUIS extends JFrame implements WindowListener, ActionListener, KeyListener {
	
	private static netCommServer netS = new netCommServer(); // Sets up the network manager
	private static JFrame uiWin; // Main window
	private static JLabel userCountGUI = new JLabel(); // Creates user amount on server widget
	// Creates button widgets
	private static JButton sendMessageButton, allowNewUsersButton, allowMessageHistoryButton, portSelectorButton;
	// Creates boolean values for new users allowed and 
	private static boolean allowNewUsers, allowMessageHistory = true;
	private static JTextField sendMessageBox, portBox;
	private static JPanel portPanel;
	private static JLabel portLabel;
	appUIS self;
	
	/**
	 * Creates the window and sets all subsequent values
	 */
	public void init(appUIS uiRef) {
		// Sets reference to window
		self = uiRef;
		netS.initalizeNetworkManager(self);
		
		// Sets up the window frame
		uiWin = new JFrame("Server");
		uiWin.setLayout(new BoxLayout(uiWin.getContentPane(), BoxLayout.Y_AXIS)); // Sets new layout manager
		
		addWindowListener(self); // Adds window listener to this application
		uiWin.addWindowListener(self); // Adds window listener to this window
		uiWin.setResizable(false); // Sets window to not allow resizing
		
		//Sets up the server port selector
		portPanel = new JPanel();
		portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
		portPanel.setAlignmentX(CENTER_ALIGNMENT);
		
		portBox = new JTextField("0");
		portBox.setSize(portBox.getX() - 25, portBox.getY() + 5);
		
		portLabel = new JLabel("Enter port for server to use (Use zero to pick any open port): ");
		portLabel.setLabelFor(portBox);
		portLabel.setAlignmentX((float)1.0);
		
		
		portSelectorButton = new JButton("Set port");
		portSelectorButton.addActionListener(self);
		
		portPanel.add(portBox);
		portPanel.add(portSelectorButton);
		
		// Adds created objects to "uiWin"
		uiWin.add(portLabel);
		uiWin.add(portPanel);
		uiWin.pack(); // Packs them into the main window
		
		// Sets window visible and starts server
		uiWin.setSize(new Dimension(uiWin.getWidth() + 15, uiWin.getHeight() + 15)); // Sets window size
		uiWin.setVisible(true);
	}

	/**
	 * Takes a String and sends it to network manager
	 * 
	 * @param String message to send
	 */
	public void sendMessage(String message) {
		netS.sendMessageNet("Server:" + message + "\n"); // Sends message with user tag Server
		sendMessageBox.setText(""); // Clears message textbox
	}
	
	/**
	 * Adds the server interactables to the screen
	 */
	private void addServerDetailsToScreen() {
		uiWin.remove(portPanel);
		uiWin.remove(portLabel);
		
		// Sets up the button that sends server messages
		sendMessageButton = new JButton("Send Message");
		sendMessageButton.addActionListener(self);
		sendMessageButton.setAlignmentX((float)0.5);
		
		// Sets up the userAllowOnServer toggle
		allowNewUsersButton = new JButton("Disable New Users To Join");
		allowNewUsersButton.addActionListener(self);
		allowNewUsersButton.setAlignmentX((float)0.5);
		
		// Sets up the messageHistoryEnable toggle
		allowMessageHistoryButton = new JButton("Disable message history");
		allowMessageHistoryButton.addActionListener(self);
		allowMessageHistoryButton.setAlignmentX((float)0.5);
		
		// Sets up a label for messageBox
		JLabel messageLabel = new JLabel("Message Box: ");
		messageLabel.setLabelFor(sendMessageBox);
		messageLabel.setAlignmentX((float)1.0);
		
		//Sets up the textField for messageBox
		sendMessageBox = new JTextField();
		sendMessageBox.setSize(getPreferredSize());
		sendMessageBox.addKeyListener(self);
		
		// Sets up the panel that will contain the user count
		JPanel userCountPanel = new JPanel();
		userCountPanel.setLayout(new BoxLayout(userCountPanel, BoxLayout.X_AXIS));
		userCountPanel.setAlignmentX((float)1.0);
		
		//Sets up the textField for the user count
		JLabel userCountLabel = new JLabel();
		userCountLabel.setText("Connected user count: ");
		
		//Sets up the textField for the user count
		userCountGUI.setText(netS.getUsersOnServer());
		
		// Adds components to the userCountPanel
		userCountPanel.add(userCountLabel);
		userCountPanel.add(userCountGUI);
		
		uiWin.add(allowMessageHistoryButton);
		uiWin.add(Box.createVerticalStrut(15));
		uiWin.add(allowNewUsersButton);
		uiWin.add(Box.createVerticalStrut(15));
		uiWin.add(userCountPanel);
		uiWin.add(Box.createVerticalStrut(15));
		uiWin.add(messageLabel);
		uiWin.add(sendMessageBox);
		uiWin.add(sendMessageButton);
		
		uiWin.pack();
	}
	
	/**
	 * Sets up the server IP and port that will be displayed on the screen
	 * 
	 * @param SocketAddress ip
	 * @param int port
	 */
	public void setServerConnectionInfo(String ip, int port) {
		// Creates a panel to store the IP and the Port text
		JPanel serverInfo = new JPanel();
		JLabel ipA = new JLabel("IP Address: " + ip); // Adds the ip text
		JLabel portA = new JLabel("Port: " + Integer.toString(port)); // Adds the port text
		
		serverInfo.add(ipA); // Adds the ip to the panel
		serverInfo.add(portA); // Adds the port to the panel
		
		uiWin.add(serverInfo); // Adds the panel to the buffer to wait for the other components
	}
	
	/**
	 * Updates the userCount
	 */
	public void updateUserCountUI() {
		userCountGUI.setText(netS.getUsersOnServer()); // Sets user number
		uiWin.pack(); // Updates the UI
	}
	public void setUsersOnServerText() {
		
	}
	
	/**
	 * Creates error box
	 */
	public void throwError(String text, boolean isError) {
		if(isError) {
			JOptionPane.showMessageDialog(self, text, "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(self, text, "Message", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if(netS.closeServer()) { // Check if server shutdown properly
			// Close the opening app and this app window
			TextMeServer.close();
			System.exit(0);
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendMessageButton) {
			if(sendMessageBox.getText().length() > 0) {
				sendMessage(sendMessageBox.getText());
			}
		} else if (e.getSource() == portSelectorButton) {
			try {
				if(netS.startServer(Integer.parseInt(portBox.getText()))) {
					addServerDetailsToScreen();
				} else {
					throwError("Failure to start server", true);
				}
			} catch (NumberFormatException err) {
				throwError(err.getMessage(), true);
			}
		} else if (e.getSource() == allowNewUsersButton) { // Sets flip flop up for allowing new users
			if(allowNewUsers) { // If the flipflop is set to true
				allowNewUsers = false; // Sets value to false
				netS.setNewUsersAllowed(false); // Sets allowUsers to false
				
				// Sets button text to flipflop and updates the UI
				allowNewUsersButton.setText("Enable New Users To Join");
				uiWin.pack();
			} else { // If the flipflop is set to false
				allowNewUsers = true; // Sets value to true
				netS.setNewUsersAllowed(true); // Sets allowUsers to true
				
				// Sets button text to flipflop and updates the UI
				allowNewUsersButton.setText("Disable New Users To Join");
				uiWin.pack();
			}
		} else if(e.getSource() == allowMessageHistoryButton) { // Sets flip flop up for allowing new users
			if(allowMessageHistory) { // If the flipflop is set to true
				int option = JOptionPane.showConfirmDialog(uiWin, "Do you want to clear the message history recorded?", "Clear Message History", JOptionPane.YES_NO_CANCEL_OPTION);
				
				switch (option) {
					case 0: // If option is equal to "yes"
						allowMessageHistory = false; // Sets value to false
						netS.setClearMessageHistory(true); // Clears message history recorded
						netS.setMessageHistory(false); // Sets message history collection to false
						
						netS.sendMessageNet("clearmessagehistory" + "\n");
					
						// Sets button text to flipflop and updates the UI
						allowMessageHistoryButton.setText("Enable message history");
						uiWin.pack();
						break;
					case 1: // If option is equal to "no"
						allowMessageHistory = false; // Sets value to false
						netS.setMessageHistory(true); // Sets message history collection to true
					
						// Sets button text to flipflop and updates the UI
						allowMessageHistoryButton.setText("Enable message history");
						uiWin.pack();
						break;
					default: // If option is not equal to yes, no, or is invalid
						break;
				}
				
			} else { // If the flipflop is set to false
				allowMessageHistory = true; // Sets value to true
				netS.setMessageHistory(true); // Sets allowUsers to true
				
				// Sets button text to flipflop and updates the UI
				allowMessageHistoryButton.setText("Disable message history");
				uiWin.pack();
			}
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == e.VK_ENTER && e.getSource() == sendMessageBox) {
			sendMessage(sendMessageBox.getText());
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}

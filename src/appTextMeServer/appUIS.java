/**
 * @author Caleb Lanphere
 * 
 * TextMe Application server application
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package appTextMeServer;

import java.awt.*;
import java.awt.event.*;
import java.net.SocketAddress;

import javax.swing.*;

public class appUIS extends JFrame implements WindowListener, ActionListener, KeyListener {
	
	private static netCommServer netS = new netCommServer();
	private static JFrame base;
	private static JLabel userCountNum = new JLabel();
	private static JButton buttonSSM, buttonTAUOS, buttonMHE, buttonPS;
	private static boolean buttonTAUOSValue, buttonMHEValue = true;
	private static JTextField messageBox, portBox;
	private static JPanel portPanel;
	private static JLabel portLabel;
	appUIS self;
	
	/**
	 * Creates the window and sets all subsequent values
	 */
	public void init(appUIS uiRef) {
		// Sets reference to window
		self = uiRef;
		netS.setUIRef(self);
		
		// Sets up the window frame
		base = new JFrame("Server");
		base.setLayout(new BoxLayout(base.getContentPane(), BoxLayout.Y_AXIS)); // Sets new layout manager
		
		addWindowListener(self); // Adds window listener to this application
		base.addWindowListener(self); // Adds window listener to this window
		base.setResizable(false); // Sets window to not allow resizing
		
		//Sets up the server port selector
		portPanel = new JPanel();
		portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
		portPanel.setAlignmentX(CENTER_ALIGNMENT);
		
		portBox = new JTextField("0");
		portBox.setSize(portBox.getX() - 25, portBox.getY() + 5);
		
		portLabel = new JLabel("Enter port for server to use (Use zero to pick any open port): ");
		portLabel.setLabelFor(portBox);
		portLabel.setAlignmentX((float)1.0);
		
		
		buttonPS = new JButton("Set port");
		buttonPS.addActionListener(self);
		
		portPanel.add(portBox);
		portPanel.add(buttonPS);
		
		// Adds created objects to "base"
		base.add(portLabel);
		base.add(portPanel);
		base.pack(); // Packs them into the main window
		
		// Sets window visible and starts server
		base.setSize(new Dimension(base.getWidth() + 15, base.getHeight() + 15)); // Sets window size
		base.setVisible(true);
	}

	/**
	 * Takes a String and sends it to net manager
	 * 
	 * @param String message to send
	 */
	public void sendMessage(String message) {
		netS.sendMessageNet("Server:" + message + "\n"); // Sends message with user tag Server
		messageBox.setText(""); // Clears message textbox
	}
	
	/**
	 * Adds the server functionalities to the screen
	 */
	private void addServerDetailsToScreen() {
		base.remove(portPanel);
		base.remove(portLabel);
		
		// Sets up the button that sends server messages
		buttonSSM = new JButton("Send Message");
		buttonSSM.addActionListener(self);
		buttonSSM.setAlignmentX((float)0.5);
		
		// Sets up the userAllowOnServer toggle
		buttonTAUOS = new JButton("Disable New Users To Join");
		buttonTAUOS.addActionListener(self);
		buttonTAUOS.setAlignmentX((float)0.5);
		
		// Sets up the messageHistoryEnable toggle
		buttonMHE = new JButton("Disable message history");
		buttonMHE.addActionListener(self);
		buttonMHE.setAlignmentX((float)0.5);
		
		// Sets up a label for messageBox
		JLabel messageLabel = new JLabel("Message Box: ");
		messageLabel.setLabelFor(messageBox);
		messageLabel.setAlignmentX((float)1.0);
		
		//Sets up the textField for messageBox
		messageBox = new JTextField();
		messageBox.setSize(getPreferredSize());
		messageBox.addKeyListener(self);
		
		// Sets up the panel that will contain the user count
		JPanel userCountPanel = new JPanel();
		userCountPanel.setLayout(new BoxLayout(userCountPanel, BoxLayout.X_AXIS));
		userCountPanel.setAlignmentX((float)1.0);
		
		//Sets up the textField for the user count
		JLabel userCountLabel = new JLabel();
		userCountLabel.setText("Connected user count: ");
		
		//Sets up the textField for the user count
		userCountNum.setText(netS.getUsersOnServer());
		
		// Adds components to the userCountPanel
		userCountPanel.add(userCountLabel);
		userCountPanel.add(userCountNum);
		
		base.add(buttonMHE);
		base.add(Box.createVerticalStrut(15));
		base.add(buttonTAUOS);
		base.add(Box.createVerticalStrut(15));
		base.add(userCountPanel);
		base.add(Box.createVerticalStrut(15));
		base.add(messageLabel);
		base.add(messageBox);
		base.add(buttonSSM);
		
		base.pack();
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
		
		base.add(serverInfo); // Adds the panel to the buffer to wait for the other components
	}
	
	/**
	 * Updates the userCount
	 */
	public void updateUserCountUI() {
		userCountNum.setText(netS.getUsersOnServer()); // Sets user number
		base.pack(); // Updates the UI
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
		if(e.getSource() == buttonSSM) {
			if(messageBox.getText().length() > 0) {
				sendMessage(messageBox.getText());
			}
		} else if (e.getSource() == buttonPS) {
			try {
				if(netS.startServer(Integer.parseInt(portBox.getText()))) {
					addServerDetailsToScreen();
				} else {
					throwError("Failure to start server", true);
				}
			} catch (NumberFormatException err) {
				throwError(err.getMessage(), true);
			}
		} else if (e.getSource() == buttonTAUOS) { // Sets flip flop up for allowing new users
			if(buttonTAUOSValue) { // If the flipflop is set to true
				buttonTAUOSValue = false; // Sets value to false
				netS.setNewUsersAllowed(false); // Sets allowUsers to false
				
				// Sets button text to flipflop and updates the UI
				buttonTAUOS.setText("Enable New Users To Join");
				base.pack();
			} else { // If the flipflop is set to false
				buttonTAUOSValue = true; // Sets value to true
				netS.setNewUsersAllowed(true); // Sets allowUsers to true
				
				// Sets button text to flipflop and updates the UI
				buttonTAUOS.setText("Disable New Users To Join");
				base.pack();
			}
		} else if(e.getSource() == buttonMHE) { // Sets flip flop up for allowing new users
			if(buttonMHEValue) { // If the flipflop is set to true
				int option = JOptionPane.showConfirmDialog(base, "Do you want to clear the message history recorded?", "Clear Message History", JOptionPane.YES_NO_CANCEL_OPTION);
				
				switch (option) {
					case 0: // If option is equal to "yes"
						buttonMHEValue = false; // Sets value to false
						netS.setClearMessageHistory(true); // Clears message history recorded
						netS.setMessageHistory(false); // Sets message history collection to false
						
						netS.sendMessageNet("clearmessagehistory" + "\n");
					
						// Sets button text to flipflop and updates the UI
						buttonMHE.setText("Enable message history");
						base.pack();
						break;
					case 1: // If option is equal to "no"
						buttonMHEValue = false; // Sets value to false
						netS.setMessageHistory(true); // Sets message history collection to true
					
						// Sets button text to flipflop and updates the UI
						buttonMHE.setText("Enable message history");
						base.pack();
						break;
					default: // If option is not equal to yes, no, or is invalid
						break;
				}
				
			} else { // If the flipflop is set to false
				buttonMHEValue = true; // Sets value to true
				netS.setMessageHistory(true); // Sets allowUsers to true
				
				// Sets button text to flipflop and updates the UI
				buttonMHE.setText("Disable message history");
				base.pack();
			}
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == e.VK_ENTER && e.getSource() == messageBox) {
			sendMessage(messageBox.getText());
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}

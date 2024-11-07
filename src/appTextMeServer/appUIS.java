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
import javax.swing.*;

public class appUIS extends JFrame implements WindowListener, ActionListener, KeyListener {
	
	private static netCommServer netS = new netCommServer();
	private static JFrame base;
	private static JLabel userCountNum = new JLabel();
	private static JButton buttonSSM, buttonTAUOS, buttonMHE;
	private static boolean buttonTAUOSValue, buttonMHEValue = true;
	private static JTextField messageBox;
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
		JLabel messageLabel = new JLabel();
		messageLabel.setText("Message Box: ");
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
		
		// Adds created objects to "base"
		base.add(buttonMHE);
		base.add(Box.createVerticalStrut(15));
		base.add(buttonTAUOS);
		base.add(Box.createVerticalStrut(15));
		base.add(userCountPanel);
		base.add(Box.createVerticalStrut(15));
		base.add(messageLabel);
		base.add(messageBox);
		base.add(buttonSSM);
		base.pack(); // Packs them into the main window
		
		// Sets window visible and starts server
		base.setSize(new Dimension(base.getWidth() + 15, base.getHeight() + 15)); // Sets window size
		base.setVisible(true);
		netS.startServer(); // Starts server
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
	 * Updates the userCount
	 */
	public void updateUserCountUI() {
		userCountNum.setText(netS.getUsersOnServer()); // Sets user number
		base.pack(); // Updates the UI
	}
	public void setUsersOnServerText() {
		
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
				buttonMHEValue = false; // Sets value to false
				netS.setMessageHistory(false); // Sets allowUsers to false
				
				//TODO Add a message prompt that asks if user wants to clear message history that exists on server
				
				
				// Sets button text to flipflop and updates the UI
				buttonMHE.setText("Enable message history");
				base.pack();
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

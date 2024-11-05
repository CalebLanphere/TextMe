/**
 * @author Caleb Lanphere
 * 
 * TextMe Application server application
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package appTextMe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class appUIS extends JFrame implements WindowListener, ActionListener, KeyListener {
	
	static WindowListener ui;
	static netCommServer netS = new netCommServer();
	static JFrame base;
	static JButton buttonSSM;
	static JTextField messageBox;
	appUIS self;
	
	/**
	 * Creates the window and sets all subsequent values
	 */
	public void init(appUIS uiRef) {
		// Sets reference to window
		self = uiRef;
		
		// Sets up the window frame
		base = new JFrame("Server");
		base.setLayout(new BoxLayout(base.getContentPane(), BoxLayout.Y_AXIS)); // Sets new layout manager
		
		addWindowListener(self); // Adds window listener to this application
		base.addWindowListener(self); // Adds window listener to this window
		base.setMaximumSize(base.getPreferredSize()); // Sets maximum window size
		base.setMinimumSize(base.getPreferredSize()); // Sets minimum window size
		
		// Sets up the button that sends server messages
		buttonSSM = new JButton("Send Message");
		buttonSSM.addActionListener(self);
		buttonSSM.setAlignmentX((float)0.5);
		
		//Sets up the textField
		messageBox = new JTextField();
		messageBox.setSize(getPreferredSize());
		messageBox.addKeyListener(self);
		
		// Adds created objects to "base"
		base.add(messageBox);
		base.add(buttonSSM);
		base.pack(); // Packs them into the main window
		
		// Sets window visible and starts server
		base.setVisible(true);
		netS.startServer();
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
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == e.VK_ENTER && e.getSource() == messageBox) {
			sendMessage(messageBox.getText());
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

/**
 * @author Caleb Lanphere
 * 
 * TextMe Application UI
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package appTextMe;

import java.awt.*;
import java.awt.event.*;
import java.net.Socket;

import javax.swing.*;

public class appUIC extends Frame implements WindowListener, ActionListener, KeyListener {
	
		//Create the components that need to be accessed by outside functions
		private static JPanel sentMessageBox = new JPanel();
		private static JFrame uiWin = new JFrame("TextMe");
		private static JTextField messageTextBox = new JTextField(0);
		
		//Send message widgets
		private JButton buttonSM = new JButton("Send message");
		
		//Username widgets
		private static JPanel usernameField = new JPanel();
		private static JButton buttonUN = new JButton("Set Username");
		private static JTextField usernameTextBox = new JTextField(0);
		
		//Connection widgets
		private static JPanel connectionPanel = new JPanel();
		private static JButton buttonC = new JButton("Attempt Connection");
		private static JTextField ipTextBox = new JTextField("127.0.0.1");
		private static JTextField portTextBox = new JTextField(0);
		
		// UI references
		private static appUIC ui;
		private static netCommClient netC = new netCommClient();
		public static int user;
		//static JPopupMenu serverPopup = new JPopupMenu("Server Started - Open Application Again");
		
		
		public static String username = "";
		private static boolean isClosing = false;
		
	/**
	 *  Creates all the important window widgets and applies it to the screen
	 * 
	 * @param ClientUI gets reference to window to allow for key presses
	 */
	public void initialize(appUIC ui, boolean setUsernameInUI, String username) {
		// Sets overall appUI reference based on one provided
		appUIC.ui = ui;
		netC.setUIRef(ui);
		
		uiWin.setLayout(new BoxLayout(uiWin.getContentPane(), BoxLayout.Y_AXIS));
		addWindowListener(ui);
		uiWin.addWindowListener(ui);
		uiWin.setPreferredSize(new Dimension(500,500));
		uiWin.setMaximumSize(new Dimension(500,500));
		
		//Sets the text-box UI styling
		messageTextBox.setMaximumSize(new Dimension(450, 100));
		messageTextBox.setPreferredSize(new Dimension(350, 25));
		messageTextBox.setAlignmentX((float)0.5); // is ignored
		messageTextBox.addKeyListener(ui);
		
		//Sets the text-box label styling
		JLabel textLabel = new JLabel();
		textLabel.setText("Message Box: ");
		textLabel.setLabelFor(messageTextBox);
		textLabel.setAlignmentX((float)0.5); // applies to all objects
		
		//Sets the button for sending messages styling
		buttonSM.addActionListener(ui);
		buttonSM.setAlignmentX((float)0.5);
		
		//Sets the scroll box's styling
		JScrollPane scrollBox = new JScrollPane(sentMessageBox);
		scrollBox.setPreferredSize(new Dimension(450,450));
		scrollBox.setMaximumSize(new Dimension(450,400));
		scrollBox.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		//Sets the text box that displays messages style
		sentMessageBox.setLayout(new BoxLayout(sentMessageBox, BoxLayout.Y_AXIS));
		
		//Adds the UI elements to the spawn list on screen in order
		if(setUsernameInUI) {
			addUsernameBoxToScreen(uiWin, ui, usernameField, buttonUN, usernameTextBox);
		} else {
			addConnectBoxToScreen(uiWin, ui, connectionPanel, ipTextBox, buttonC, portTextBox);
		}
		uiWin.add(scrollBox);
		uiWin.add(Box.createVerticalGlue()); // Adds a spacer in-between the above and below add statement
		uiWin.getContentPane().add(textLabel); // Add text label to the window	
		uiWin.getContentPane().add(messageTextBox); // Add text-box to the window
		uiWin.getContentPane().add(buttonSM); // Add buttonSM to the window
		
		// Sets the screen size and makes it visible
		uiWin.setSize(500, 500); // Sets the window size
		uiWin.setVisible(true); // Show UI window
		
	}
	
	/**
	 *  adds the set username box onto the screen
	 * 
	 * @return void
	 */
	public static void addUsernameBoxToScreen(JFrame uiWin, appUIC ui, JPanel usernameField, JButton buttonUN, JTextField usernameTextBox) {
		// Styles usernameField
		usernameField.setLayout(new BoxLayout(usernameField, (BoxLayout.X_AXIS)));
		
		// Styles usernameTextBox
		usernameTextBox.setMinimumSize(new Dimension(150, 20));
		usernameTextBox.addKeyListener(ui);
		
		// Styles buttonUN
		buttonUN.addActionListener(ui);
		buttonUN.setAlignmentX((float)0.5);
		
		// Add items created to the screen
		usernameField.add(usernameTextBox);
		usernameField.add(buttonUN);
		uiWin.add(usernameField);
	}
	
	/**
	 *  Adds the connect box to the screen
	 * 
	 * @return void 
	 * @param uiWin takes the UI main reference
	 * @param appUIC takes in the instance reference
	 * @param connectionPanel takes it's JPanel reference
	 * @param ip takes it's JTextField reference
	 * @param buttonC takes it's JButton reference
	 */
	
	public static void addConnectBoxToScreen(JFrame uiWin, appUIC ui, JPanel connectionPanel, JTextField ip, JButton buttonC, JTextField portTextBox) {
		//Styles connectionPanel
		connectionPanel.setLayout(new BoxLayout(connectionPanel, (BoxLayout.X_AXIS)));
		
		//Styles ip
		ip.setMinimumSize(new Dimension(100, 20));
		ip.addKeyListener(ui);
		
		//Styles buttonC
		buttonC.addActionListener(ui);
		
		connectionPanel.add(ip);
		connectionPanel.add(portTextBox);
		connectionPanel.add(buttonC);
		
		uiWin.add(connectionPanel, 0);
	}
	
	/**
	 *  Sends a message to the chat from what was typed into the textBox to the users sentMessageBox
	 * 
	 * @return void
	 * @param message the string message to be applied in the text
	 * @param sentMessageBox the box that the new text will be applied
	 * @param textBox the box that contains the message typed
	 * 
	 */
	public static void parseAndSendMessage(String message) {
			String messageWithIdent = getUsername(); // Sets beginning of messageWithIdent as the username
		
			messageWithIdent += message; // Adds the rest of messageWithIdent with the message
		
			netC.sendMessageNet(messageWithIdent);
			
			if(message.equals("quit;") && !isClosing) {
				clearTextBox(messageTextBox);
				resetForReconnection();
			}
	}
	
	public static void resetForReconnection() {
		netC.resetConnection(); // Resets socket and associated variables
		
		// Clear the onscreen message history
		clearMessageHistory(true);
		
		addConnectBoxToScreen(uiWin, ui, connectionPanel, ipTextBox, buttonC, portTextBox);
	}
	
	public static void addMessage(String message) {
		if(message != null && message.length() > 0) { // Checks to see if text is in the box
			JTextArea newText = new JTextArea(); // create new textArea
			
			// Set preferences
			newText.setText(message);
			newText.setLineWrap(true);
			newText.setMinimumSize(new Dimension(450, (int)(newText.getPreferredSize().getHeight() + 5))); // Required for having multiple lines
			newText.setEditable(false);
			
			// Adds the text to the screen
			sentMessageBox.add(newText);
			clearTextBox(messageTextBox);
			updateUI(uiWin);
		}
	}
	
	private static void clearTextBox(JTextField messageTextBox) {
		messageTextBox.setText(""); // Clear previous text in box
	}
	
	/**
	 *  Applies all new additions to the main window
	 * 
	 * @param uiWin takes the UI main reference in and repacks it to show new changes in sentMessageBox
	 */
	private static void updateUI(JFrame uiWin) {
		uiWin.pack();
	}
	
	/**
	 * Sets username
	 * @return void
	 * @param usernameGiven requires a string input
	 */
	public void setUsername(String usernameGiven) {
		username = usernameGiven + ": "; // Takes the parameter usernameGiven and adds ": " before setting username
	}
	
	public static String getUsername() {
		return username;
	}
	
	private static void removeConnectionPanel(JFrame uiWin) {
		connectionPanel.removeAll();
		uiWin.remove(connectionPanel);
	}
	
	private static void removeUsernamePanel(JFrame uiWin) {
		usernameField.removeAll();
		uiWin.remove(usernameField);
	}
	
	public void attemptConnection(netCommClient netC, String ip, int port) {
		if(netC.attemptConnection(ip, port)) {
			removeConnectionPanel(uiWin);
			updateUI(uiWin);
			
			netC.watchForMessages();
			netC.getMessageHistoryNet();
		}
	}
	
	/**
	 * Clears message history from the client
	 */
	public static void clearMessageHistory() {
		sentMessageBox.removeAll();
		
		parseAndSendMessage("messagehistorycleared;");
	}
	
	/**
	 * Clears message history from the client and does not send completion to server
	 */
	public static void clearMessageHistory(boolean sendMessageHistory) {
		sentMessageBox.removeAll();
	}
	
	/**
	 * Creates and shows an error to the user through a dialog box
	 * @param String message to send
	 */
	public static void throwError(String err) {
		JOptionPane.showMessageDialog(uiWin, err, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Creates and shows an message to the user through a dialog box
	 * @param String message to send
	 */
	public static void throwMessage(String message, String title) {
		JOptionPane.showMessageDialog(uiWin, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	/**
	 * When button runs, run specified action
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttonSM) { // If the button pressed was the send message button
			if(messageTextBox.getText().length() > 0) {
				parseAndSendMessage(messageTextBox.getText()); // Send message to parser and sends to server
				updateUI(uiWin); // Reload UI
			}
		} 
		else if (e.getSource() == buttonUN) { // If the button pressed was the username button
			if(usernameTextBox.getText().length() > 0) {
				setUsername(usernameTextBox.getText()); // Sets username
				removeUsernamePanel(uiWin); // Remove username panel
				addConnectBoxToScreen(uiWin, ui, connectionPanel, ipTextBox, buttonC, portTextBox); // Adds connection panel
				updateUI(uiWin); // Reload UI
			}
		} 
		else if (e.getSource() == buttonC) { // If the button pressed was the connect to server button
			// Checks to see if the length of the ip is greater than zero and the port length is greater than zero
			if((ipTextBox.getText().length() != 0) && (portTextBox.getText().length() != 0)) { 
				
				// Attempt connection
				try {
					attemptConnection(netC, ipTextBox.getText(), Integer.parseInt(portTextBox.getText()));
				} catch (NumberFormatException err) {
					throwError(err.getMessage());
				}
			}
		}
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		
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
	
	/*
	 * FIX OR IGNORE
	 */
	public void keyTyped(KeyEvent e) {
		if(messageTextBox.getText().length() > 2000) { // Checks to see if message box
			// is over 2000 characters
			
			// Sets the text box's text to the typed characters minus the last character
			messageTextBox.setText(messageTextBox.getText().substring(0, messageTextBox.getText().length() - 1));
		} else {

		}
	}

	@Override
	
	/*
	 * Checks to see if the enter key was pressed inside of the textBox; 
	 * If so, send the message and apply the message to the screen
	 */
	public void keyPressed(KeyEvent e) {
		if(e.getSource() == messageTextBox && e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(messageTextBox.getText().length() > 0) { // Checks if the message has content
				parseAndSendMessage(messageTextBox.getText()); // Send message to parser and sends to server
				updateUI(uiWin); // Reloads UI
			}
		} else if (e.getSource() == usernameTextBox && e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(usernameTextBox.getText().length() > 0) {
				setUsername(usernameTextBox.getText()); // Sets the username
				removeUsernamePanel(uiWin); // Clears the username panel
				addConnectBoxToScreen(uiWin, ui, connectionPanel, ipTextBox, buttonC, portTextBox); // Creates the connect panel
				updateUI(uiWin); // Reloads UI
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	/**
	 * Catches the window before closing, sends a disconnect message to server, then closes the socket and window
	 */
	public void windowClosing(WindowEvent e) {
		if(netC.isConnected()) {
			isClosing = true;
			parseAndSendMessage("quit;"); // Send message to parser and sends to server
			netC.closeConnection(); // Close socket and associated variables
		
			TextMe.close();
			System.exit(0);
		} else {
			isClosing = true;
			netC.closeConnection(); // Close socket and associated variables

			TextMe.close();
			System.exit(0);
		}
	}

}

package appTextMe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class appUI extends Frame implements WindowListener, ActionListener, KeyListener {
	

	//Create the components that need to be accessed by outside functions
	static JPanel sentMessageBox = new JPanel();
	static JFrame uiWin = new JFrame("TextMe");
	static JTextField messageTextBox = new JTextField(0);
	static WindowListener mainUI;
	
	//Send message widgets
	JButton buttonSM = new JButton("Send message");
	
	//Username widgets
	static JPanel usernameField = new JPanel();
	static JButton buttonUN = new JButton("Set Username");
	static JTextField usernameTextBox = new JTextField(0);
	
	//Connection widgets
	static JPanel connectionPanel = new JPanel();
	static JButton buttonC = new JButton("Attempt Connection");
	static JTextField ipTextBox = new JTextField("127.0.0.1");
	static JTextField portTextBox = new JTextField(0);
	
	// UI references
	static appUI ui;
	static netCommClient netC = new netCommClient();
	static boolean isConnected = false;
	public static int user;
	//static JPopupMenu serverPopup = new JPopupMenu("Server Started - Open Application Again");
	
	
	public static String username = "";
	
	/* Creates all the important window widgets and applies it to the screen
	 * 
	 * @param ClientUI gets reference to window to allow for key presses
	 */
	public void initialize(appUI ui, boolean setUsernameInUI, String username) {
		// Sets overall appUI reference based on one provided
		appUI.ui = ui;
		
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
		scrollBox.setHorizontalScrollBarPolicy(scrollBox.HORIZONTAL_SCROLLBAR_NEVER);
		
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
	
	/* adds the set username box onto the screen
	 * 
	 * @return void
	 */
	
	public static void addUsernameBoxToScreen(JFrame uiWin, appUI ui, JPanel usernameField, JButton buttonUN, JTextField usernameTextBox) {
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
	
	/* Adds the connect box to the screen
	 * 
	 * @return void 
	 * @param uiWin takes the UI main reference
	 * @param appUI takes in the instance reference
	 * @param connectionPanel takes it's JPanel reference
	 * @param ip takes it's JTextField reference
	 * @param buttonC takes it's JButton reference
	 */
	
	public static void addConnectBoxToScreen(JFrame uiWin, appUI ui, JPanel connectionPanel, JTextField ip, JButton buttonC, JTextField portTextBox) {
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
	
	/* Sends a message to the chat from what was typed into the textBox to the users sentMessageBox
	 * 
	 * @return void
	 * @param message the string message to be applied in the text
	 * @param sentMessageBox the box that the new text will be applied
	 * @param textBox the box that contains the message typed
	 * 
	 */
	
	public static void sendMessage(String message) {
			//addMessage(message);
			String messageWithIdent = getUsername(); // Sets beginning of messageWithIdent as the username
		
			messageWithIdent += message; // Adds the rest of messageWithIdent with the message
		
			netC.sendMessageNet(messageWithIdent);
			
			System.out.println("Message sent to net manager"); // Add success text to console
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
	
	public static void clearTextBox(JTextField messageTextBox) {
		messageTextBox.setText(""); // Clear previous text in box
	}
	
	/* Applies all new additions to the main window
	 * 
	 * @param uiWin takes the UI main reference in and repacks it to show new changes in sentMessageBox
	 */
	
	public static void updateUI(JFrame uiWin) {
		uiWin.pack();
	}
	
	/*
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
	
	public static void removeConnectionPanel(JFrame uiWin) {
		connectionPanel.removeAll();
		uiWin.remove(connectionPanel);
	}
	
	public static void removeUsernamePanel(JFrame uiWin) {
		usernameField.removeAll();
		uiWin.remove(usernameField);
	}
	
	public void attemptConnection(netCommClient netC, String ip, int port) {
		System.out.println("Attempting Connection");
		if(!netC.attemptConnection(ip, port)) {
			System.out.println("error connecting");
			isConnected = false;
		} else {
			System.out.println("Connected");
			removeConnectionPanel(uiWin);
			updateUI(uiWin);
			isConnected = true;
			netC.watchForMessages();
			sendMessage("has connected");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == buttonSM) {
			sendMessage(messageTextBox.getText());
			updateUI(uiWin);
		} else if (e.getSource() == buttonUN) {
			setUsername(usernameTextBox.getText());
			removeUsernamePanel(uiWin);
			addConnectBoxToScreen(uiWin, ui, connectionPanel, ipTextBox, buttonC, portTextBox);
			updateUI(uiWin);
		} else if (e.getSource() == buttonC) {
			if((ipTextBox.getText().length() != 0) && (portTextBox.getText().length() != 0)) {
				attemptConnection(netC, ipTextBox.getText(), Integer.parseInt(portTextBox.getText()));
			}
		}
	}
	
	public static JPanel getSentMessageBox() {
		return sentMessageBox;
	}
	
	public static JTextField getMessageTextBox() {
		return messageTextBox;
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
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

	}

	@Override
	
	/*
	 * FIX OR IGNORE
	 */
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		if(messageTextBox.getText().length() > 2000) {
			
		}
	}

	@Override
	
	/*
	 * Checks to see if the enter key was pressed inside of the textBox; 
	 * If so, send the message and apply the message to the screen
	 */
	public void keyPressed(KeyEvent e) {
		if(e.getSource() == messageTextBox && e.getKeyCode() == KeyEvent.VK_ENTER) {
			sendMessage(messageTextBox.getText());
			updateUI(uiWin);
		} else if (e.getSource() == usernameTextBox && e.getKeyCode() == KeyEvent.VK_ENTER) {
			setUsername(usernameTextBox.getText());
			removeUsernamePanel(uiWin);
			addConnectBoxToScreen(uiWin, ui, connectionPanel, ipTextBox, buttonC, portTextBox);
			updateUI(uiWin);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		sendMessage("left the chat");
		netC.closeConnection();
		
		TextMe.close();
		System.exit(0);
	}

}

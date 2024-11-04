/**
 * @author Caleb Lanphere
 * 
 * TextMe Application
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package appTextMe;

public class TextMe {
	
	public static void main(String[] args) {
		createApp(true);
	}
	
	/**
	 * Constructs the application window with username being set in the construction
	 * 
	 * @param boolean Do we set username in the UI
	 * @param String username
	 */
	public static void createApp(boolean addUsernameInUI, String username) {
		// Create a new appUI and set it to initialize
		appUIC ui = new appUIC();
		ui.initialize(ui, addUsernameInUI, username);
	}
	
	/**
	 * Constructs the application window with username being set in the application
	 * 
	 * @param boolean Do we set username in the UI
	 */
	public static void createApp(boolean addUsernameInUI) {
		// Create a new appUI and set it to initialize
		appUIC ui = new appUIC();
		ui.initialize(ui, addUsernameInUI, "");
	}
	
	public static void close() {
		System.exit(0);
	}
}
/**
 * @author Caleb Lanphere
 * 
 * TextMe Application Client
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package appTextMe;

import javax.swing.JFrame;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TextMe extends JFrame {
	
	public static void main(String[] args) {
		TextMe self = new TextMe();
		
		try {
		self.setIconImage(ImageIO.read(self.getClass().getResource("/AppIcons/TextMeAppIcon.png")));
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
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
/**
 * @author Caleb Lanphere
 * 
 * TextMe Application Server
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package appTextMeServer;

import javax.swing.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TextMeServer extends JFrame {
	public static void main(String[] args) {
		TextMeServer self = new TextMeServer();
		
		try {
		self.setIconImage(ImageIO.read(self.getClass().getResource("/AppIcons/TextMeAppIcon.png")));
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
		createApp();
	}
	
	/**
	 * Constructs the application window
	 */
	public static void createApp() {
		// Create a new appUIS and tell it to initialize
		appUIS ui = new appUIS();
		ui.init(ui);
	}
	
	/**
	 * Closes the app
	 */
	public static void close() {
		System.exit(0);
	}
}

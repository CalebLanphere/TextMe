package appTextMeServer;

public class TextMeServer {
	public static void main(String[] args) {
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

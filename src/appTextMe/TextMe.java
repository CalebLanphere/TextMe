package appTextMe;

public class TextMe {
	
	public static void main(String[] args) {
		createApp(true, "");
	}
	
	public static void createApp(boolean addUsernameInUI, String username) {
		// Create a new appUI and set it to initialize
		appUI ui = new appUI();
		ui.initialize(ui, addUsernameInUI, username);
	}
	
	public static void close() {
		System.exit(0);
	}
}
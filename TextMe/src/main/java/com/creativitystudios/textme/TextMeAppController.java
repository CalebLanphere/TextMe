/**
 * @author Caleb Lanphere
 *
 * TextMe Application Client Controller
 *
 * Copyright 2025 | Caleb Lanphere | All Rights Reserved
 *
 */

package com.creativitystudios.textme;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TextMeAppController {
    @FXML private Pane mainUI;
    @FXML private Pane usernameSelectorPane;
    @FXML private Pane connectionSelectorPane;
    @FXML private Pane serverInformationPane;
    @FXML private ScrollPane messageReceivedPane;
    @FXML private TextField usernameTextBox;
    @FXML private TextField ipAddressTextBox;
    @FXML private TextField portTextBox;
    @FXML private VBox messageVBox;
    @FXML private TextField messageTextBox;
    @FXML private Label serverIdentName;
    @FXML private Label serverIdentDetails;
    @FXML private Pane settingsPane;
    @FXML private Button sendMessageButton;
    @FXML private ChoiceBox themeSelectorDropdown;

    // Sets the network manager, which handles all network communications
    protected final TextMeClientNetManager netC = new TextMeClientNetManager(this, mainUI);
    private String username = "Unnamed User"; // Picked username
    private boolean isSettingsOpen = false; // Checks if the settings pane is ope
    // Checks if the username panel has been set to a value at least once
    private boolean isThisUsernameInitialSetting = true;
    // Determines which pane was affected by setting a new username
    private Object resetUsernameAffectedPane;
    private final String appVersion = "1.0.0"; // App version
    private final String appCreator = "Caleb Lanphere"; // App creator
    private final int MAX_USERNAME_LENGTH = 32; // Max username length
    private final int MAX_MESSAGE_LENGTH = 2000; // Max message length to send over network
    private Stage mainStage; // Reference to owning window
    private int selectedTheme = 0; // Theme that has been selected in the settings menu

    /**
     * Checks if the enter key was pressed when in the TextField;
     * ONLY TO BE USED WHEN USING KEYPRESSED EVENT
     * @param e KeyEvent created from the calling object
     */
    @FXML
    private void onSetUsernameKey(KeyEvent e) {
        if(e.getCode().equals(KeyCode.ENTER)) {
            onSetUsername();
        }
    }

    /**
     * Used to set a specific username for the user; Uses usernameTextBox to determine username
     * @return boolean Used to determine success on setting username
     */
    @FXML
    private boolean onSetUsername() {
        if (!usernameTextBox.getText().isEmpty()) { // Makes sure an empty username cannot be selected
            for(int i = 0; i < netC.CMD_MAP.size(); i++) { // Iterates through all application commands
                // Checks if the username is a command listed
                if (usernameTextBox.getText().equals(netC.CMD_MAP.get(i))) {
                    // If so, throw an error and return a failure to set username
                    throwMessage("Cannot set username that is a command", true);
                    return false;
                }
            }
            if(isThisUsernameInitialSetting) { // If this is the first time setting the username
                username = usernameTextBox.getText(); // Sets username
                removeUsernameSelectorPane(); // Removes the username pane and adds the next one
                addConnectionPane();
                isThisUsernameInitialSetting = false; // Makes isThisUsernameInitialSetting false
            } else { // If this is not the first time setting the username
                if(netC.isConnected()) { // Checks if the app is connected to a server
                    // If it is, send the username change message to the server
                    sendMessageToNetManager(netC.CMD_MAP.get(7) + usernameTextBox.getText());
                }
                username = usernameTextBox.getText(); // Sets username
                removeUsernameSelectorPane(); // Removes the username pane

                // Checks what the previous pane that was active was, then adds it back to the screen
                if (resetUsernameAffectedPane.equals(serverInformationPane)) {
                    addServerInformationPane();
                }
                if (resetUsernameAffectedPane.equals(connectionSelectorPane)) {
                    addConnectionPane();
                }
            }
            return true; // Throw success on setting username
        }
        return false; // Throws failure to set username
    }

    /**
     * Checks if the port box is greater than 5 characters, removing the new characters if it is
     */
    @FXML
    protected void isMaxPortLength() {
        if(portTextBox.getText().length() > 5) { // if it is greater than 5 characters
            portTextBox.setText(portTextBox.getText(0, 5));// Sets to first 5 chars selected
            // Puts cursor at end of textbox
            portTextBox.positionCaret(portTextBox.getText().length());
        }
    }

    /**
     * Checks if the IP box is greater than 15 characters, removing the new characters if it is
     */
    @FXML
    protected void isMaxIPLength() {
        if(ipAddressTextBox.getText().length() > 15) { // Checks if length is greater than 15 characters
            // Sets to first 15 chars selected
            ipAddressTextBox.setText(ipAddressTextBox.getText(0, 15));
            // Puts cursor at end of textbox
            ipAddressTextBox.positionCaret(ipAddressTextBox.getText().length());
        }
    }

    /**
     * Used to determine if enter was pressed to attempt joining a server
     * ONLY TO BE USED BY CALLING TEXTBOX
     * @param e KeyEvent used to see what key was pressed
     */
    @FXML
    private void attemptConnectionKey(KeyEvent e) {
        if(e.getCode().equals(KeyCode.ENTER)) {
            attemptConnection();
        }
    }

    /**
     * Attempts to join a server based on the information provided in the IP box and Port box
     */
    @FXML private void attemptConnection() {
        String ipTemp = ipAddressTextBox.getText(); // Creates a temporary value for the IP
        int portTemp = -1; // Creates a temporary value for the Port
        try { // Attempt to parse the port into an int
            portTemp = Integer.parseInt(portTextBox.getText());
        } catch (NumberFormatException error) { // Throw error if port cannot be parsed into int
            throwMessage("Port entered is not valid\n Please only use numbers 0-9", true);
        }
        // If the tempIP string is not empty and the tempPort is set
        if(!ipTemp.isEmpty() && portTemp != -1) {
            // Tell the network manager to attempt a connection
            if(netC.attemptConnection(ipTemp, portTemp)) { // If successful at joining
                isConnectedToServer(true); // Enable the messagebox pane
                removeConnectionSelectorPane(); // Remove the connection selector pane
                // Sets up the server identification details
                serverIdentDetails.setText("IP: " + ipTemp + " | Port: " + portTemp);
                addServerInformationPane(); // Adds the server identification pane
            }
        }
    }

    /**
     * Enables and disables the messagebox by flipping the inputted boolean
     * @param isConnected boolean determines if the message pane should be enabled or not
     */
    @FXML
    protected void isConnectedToServer(boolean isConnected) {
        sendMessageButton.setDisable(!isConnected);
        messageTextBox.setDisable(!isConnected);
    }

    /**
     * Checks if the username is too long and sets it to the value inputted upto the max char length
     */
    @FXML
    private void isMaxUsernameLength() {
        if(usernameTextBox.getLength() > MAX_USERNAME_LENGTH) { // If username is too long
            usernameTextBox.setText(usernameTextBox.getText(0, MAX_USERNAME_LENGTH));
            usernameTextBox.positionCaret(usernameTextBox.getLength()); // Move cursor to end
        }
    }

    /**
     * Removes the username pane from the screen
     */
    private void removeUsernameSelectorPane() {
        usernameSelectorPane.setVisible(false);
        usernameTextBox.setText("");
    }

    /**
     * Adds the username pane to the screen
     */
    private void addUsernameSelectorPane() {
        usernameSelectorPane.setVisible(true);
    }

    /**
     * Closes the settings menu, hides the currently active top pane, and adds the username pane back
     */
    @FXML
    private void resetUsername() {
        closeSettings(); // Closes settings pane
        if(resetUsernameAffectedPane.equals(serverInformationPane)) {
            removeServerInformationPane(); // Removes server info pane if that is the active pane
        }
        if(resetUsernameAffectedPane.equals(connectionSelectorPane)) {
            removeConnectionSelectorPane(); // Removes connection selector pane if that is the active pane
        }
        addUsernameSelectorPane(); // re-adds the username pane to the screen
    }

    /**
     * Removes the connection selector pane from the screen and clears its associated values
     */
    private void removeConnectionSelectorPane() {
        connectionSelectorPane.setVisible(false);
        ipAddressTextBox.setText("");
        portTextBox.setText("");
    }

    /**
     * Adds the connection selector pane to the screen and sets it as the resetUsernameAffectedPane
     */
    protected void addConnectionPane() {
        connectionSelectorPane.setVisible(true);
        resetUsernameAffectedPane = connectionSelectorPane;
    }

    /**
     * Adds the server info pane to the screen and sets it as the resetUsernameAffectedPane
     */
    protected void addServerInformationPane() {
        serverInformationPane.setVisible(true);
        resetUsernameAffectedPane = serverInformationPane;
    }

    /**
     * Adds the settings pane to the screen, shrinks the message received pane to account for it,
     * and sets isSettingsOpen accordingly
     */
    protected void addSettingsPane() {
        messageReceivedPane.setPrefWidth(336);
        messageVBox.setPrefWidth(335);
        settingsPane.setVisible(true);
        isSettingsOpen = true; // Set value isSettingsOpen to true
    }

    /**
     * Removes the server info pane from the screen;
     */
    protected void removeServerInformationPane() {
        Platform.runLater(new Runnable() {
            public void run() {
                serverInformationPane.setVisible(false);
            }
        });
    }

    /**
     * Removes the settings pane to the screen and grows the message received pane to account for it
     */
    protected void removeSettingsPane() {
        messageReceivedPane.setPrefWidth(500);
        messageVBox.setPrefWidth(498);
        settingsPane.setVisible(false);
    }

    /**
     * Sends a message to the network manager with the selected username attached
     * Uses the message textbox to determine the message
     */
    @FXML
    protected void sendMessageToNetManager() {
        netC.sendMessageNet(username + ": " + messageTextBox.getText());
        messageTextBox.setText("");
    }

    /**
     * Determines if the enter key was pressed to send a message
     * ONLY TO BE USED WITH TEXTBOX
     * @param e KeyEvent key used to trigger event
     */
    @FXML
    protected void sendMessageToNetManagerKey(KeyEvent e) {
        if(e.getCode().equals(KeyCode.ENTER)) {
            sendMessageToNetManager();
        }
    }

    /**
     * Sends a message to the network manager with the selected username attached
     * Uses provided String to determine the message to send
     * @param message String message to be sent
     */
    protected void sendMessageToNetManager(String message) {
        netC.sendMessageNet(username + ": " + message);
    }

    /**
     * Checks if the message is too long and sets it to the value inputted upto the max char length
     */
    @FXML
    protected void isMaxCharactersReached() {
        if(messageTextBox.getLength() > MAX_MESSAGE_LENGTH) { // If username is too long
            // Select all characters upto MAX_MESSAGE_LENGTH
            messageTextBox.setText(messageTextBox.getText(0, MAX_MESSAGE_LENGTH));
            messageTextBox.positionCaret(messageTextBox.getLength()); // Put cursor at end of textbox
        }
    }

    /**
     * Adds a message to the screen inside the message received pane
     * @param message String message received from network manager
     */
    public void addMessageToUI(String message) {
        Platform.runLater(new Runnable() {
            public void run() {
                HBox messageSeparator = new HBox(); // Creates a new horizontal box
                Label newMessage = new Label(message); // Creates the message
                newMessage.setWrapText(true); // Allows the message to wrap to a new line if it's too long
                // Sets the smallest size of the message horizontal box to the width of 498
                // And the height the message wants plus 15 extra
                messageSeparator.setMinSize(498, newMessage.getPrefHeight() + 15);
                messageSeparator.setId("sentMessage"); // Adds a ID to the HBox for styling purposes
                newMessage.setId("sentMessageText"); // Adds a ID to the message for styling purposes

                messageSeparator.getChildren().add(newMessage); // Adds message to horizontal box
                messageVBox.getChildren().add(messageSeparator); // Adds message to the screen
            }
        });
    }

    /**
     * Sets up the Theme dropdown menu by adding it's values and setups what to do upon a value change
     */
    protected void setupThemeSelectorDropdown() {
        // Sets the items to add into the dropdown
        themeSelectorDropdown.getItems().addAll("Light Mode", "Dark Mode");
        // Selects the first value in the dropdown as the default value
        themeSelectorDropdown.setValue(themeSelectorDropdown.getItems().getFirst());
        // Adds the listener to determine if the value selected has changed
        themeSelectorDropdown.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newValue) {
                selectedTheme = newValue.intValue(); // Sets global value of selected theme
                switch(newValue.intValue()) { // Converts the values to a String representation
                    case 0:
                        changeTheme("Light Mode", mainStage); // Sets theme to Light Mode
                        break;
                    case 1:
                        changeTheme("Dark Mode", mainStage); // Sets theme to Dark Mode
                        break;
                    default:
                        changeTheme("Light Mode", mainStage); // Sets Theme to Light Mode
                        break;
                }
            }
        });
    }

    /**
     * Sets the servername inside the server info pane to what is inside the string
     * @param serverName String servername
     */
    protected void setServerName(String serverName) {
        Platform.runLater(new Runnable() {
            public void run() {
                serverIdentName.setText("Server: " + serverName); // Sets server name
            }
        });
    }

    /**
     * Disables the message textbox, clears message history, tells the network manager to reset,
     * removes the server info pane, and re-adds the connection pane to the screen
     */
    protected void resetForReconnection() {
        isConnectedToServer(false); // Disables message textbox
        netC.resetConnection(); // Resets network manager
        clearMessageHistory(); // Clears message history
        removeServerInformationPane(); // Removes server info pane from screen
        addConnectionPane(); // Adds connection pane back to screen
    }

    /**
     * Clears the message received pane of all messages
     */
    protected void clearMessageHistory() {
        Platform.runLater(new Runnable() {
            public void run() {
                messageVBox.getChildren().clear();
            }
        });
    }

    /**
     * Determines if the settings menu should be opened and changes isSettingsOpen accordingly
     */
    @FXML
    protected void openCloseAppSettings() {
        if(!isSettingsOpen) { // If the settings pane is not open, open the settings pane
            addSettingsPane();
        } else { // Otherwise, close the settings menu
            closeSettings();
        }
    }

    /**
     * Remove the settings pane from the screen and sets isSettingsOpen accordingly
     */
    private void closeSettings() {
        removeSettingsPane();
        isSettingsOpen = false;
    }

    /**
     * Sends the disconnect message to the server and resets the UI for reconnection
     */
    @FXML
    protected void disconnectFromServer() {
        sendMessageToNetManager("usr/msg_quit;"); // Quit command
        resetForReconnection();
    }

    /**
     * Creates and adds the about menu onto the screen in a new window
     */
    @FXML
    private void openAboutMenu() {
        Stage aboutMenu = new Stage();
        Pane aboutMainUI = new Pane();
        // Image viewer that shows the image below
        ImageView logoViewer = new ImageView();
        // App logo
        Image logoImg = new Image("file:src/main/java/com/creativitystudios/textme/AppIcons/TextMeAppLogoSmall.png");
        // App name
        Label appName = new Label("TextMe Client");
        // App Version
        Label appVersion = new Label("App Version: " + this.appVersion);
        // Copyright Information
        Label appCopyright = new Label("© 2025 Caleb Lanphere. All Rights Reserved");
        // App creators
        Label appCreator = new Label("Creators: " + this.appCreator);
        // Close Button
        Button closeMenu = new Button("Close");
        // Horizontal spacer
        HBox horizontalSpacer = new HBox();
        // Vertical box to hold all Labels
        VBox appDetails = new VBox();

        appCopyright.setWrapText(true); // Allows copyright text to wrap to a new line
        closeMenu.setDefaultButton(true); // Sets the closeMenu to be pressed with the enter key
        // Sets up the image shown for the next three lines
        logoViewer.setImage(logoImg);
        logoViewer.setFitWidth(125);
        logoViewer.setFitHeight(100);
        // Sets the Vertical box's area it uses
        appDetails.setPrefWidth(150);
        appDetails.setPrefHeight(250);
        // Positions the logoViewer
        logoViewer.setLayoutX(0);
        // Places the horizontal spacer
        horizontalSpacer.setLayoutX(140);
        horizontalSpacer.setLayoutY(-50);
        // Centers the vertical box's contents
        appDetails.setAlignment(Pos.CENTER);
        appDetails.setSpacing(10);
        // Positions the copyright information
        appCopyright.setLayoutX(5);
        appCopyright.setLayoutY(140);

        // Sets up what pressing enter executes
        closeMenu.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                if(e.getCode().equals(KeyCode.ENTER)) {
                    e.consume(); // Negates the KeyEvent
                    aboutMenu.close(); // Closes the popup
                }
            }
        });
        // Sets up what pressing the button executes
        closeMenu.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                e.consume(); // Negates the KeyEvent
                aboutMenu.close(); // Closes the popup
            }
        });

        // Sets up the popup window with the created information above
        aboutMainUI.getChildren().add(logoViewer);
        appDetails.getChildren().add(appName);
        appDetails.getChildren().add(appVersion);
        appDetails.getChildren().add(appCreator);
        appDetails.getChildren().add(closeMenu);
        horizontalSpacer.getChildren().add(appDetails);
        aboutMainUI.getChildren().add(horizontalSpacer);
        aboutMainUI.getChildren().add(appCopyright);
        aboutMainUI.setId("aboutScene"); // Sets the window's ID for styling purposes

        aboutMenu.setResizable(false); // Sets the window to not allow resizing
        aboutMenu.setTitle("About | TextMe"); // Sets the title of the window
        aboutMenu.setScene(new Scene(aboutMainUI, 300, 160)); // Sets the window and its size
        // Sets the window's theme to follow
        changeTheme(themeSelectorDropdown.getItems().get(selectedTheme).toString(), aboutMenu);
        aboutMenu.show(); // Shows the window
    }

    /**
     * Throws a message or error based on  isError's value on a popup
     * @param message String message to show on the popup
     * @param isError boolean determines if message is an error or not
     */
    protected void throwMessage(String message, boolean isError) {
        Platform.runLater(new Runnable() {
            public void run() {
                Label newMessage = new Label(message); // Message to show
                Stage messageStage = new Stage();
                Pane pane = new Pane();
                VBox vbox = new VBox(); // Vertical box to store message and other details
                VBox closeButtonCenter = new VBox(); // Vertical box used to center the close button
                HBox hbox = new HBox(); // Horizontal box that aligns image with error text
                Button closeMenu = new Button("Close"); // Close button
                ImageView IconViewer = new ImageView(); // Viewer that will show the image

                closeMenu.setDefaultButton(true); // Sets closeMenu button to close when enter is pressed
                // Sets up the image viewer in the next three lines
                IconViewer.setFitWidth(100);
                IconViewer.setFitHeight(100);
                IconViewer.setCache(true);
                // Sets up the message received label
                newMessage.setPrefWidth(150);
                newMessage.setPrefHeight(75);
                newMessage.setWrapText(true);
                // Tells the closeButton's VBox to center objects
                closeButtonCenter.setAlignment(Pos.CENTER);

                // Sets what to do upon the enter key being pressed
                closeMenu.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        if(e.getCode().equals(KeyCode.ENTER)) {
                            e.consume(); // Negates key press
                            messageStage.close(); // Closes popup
                        }
                    }
                });
                // Sets what to do upon closeButton being pressed
                closeMenu.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        e.consume(); // Negates value
                        messageStage.close(); // Closes popup
                    }
                });

                // Sets up the layout using the above objects
                hbox.getChildren().add(IconViewer);
                vbox.getChildren().add(newMessage);
                closeButtonCenter.getChildren().add(closeMenu);
                vbox.getChildren().add(closeButtonCenter);
                hbox.getChildren().add(vbox);
                pane.getChildren().add(hbox);
                pane.setId("messageScene"); // Sets the windows ID for styling purposes

                // Checks if the message received is an error, setting the title and image accordingly
                if(isError) {
                    messageStage.setTitle("Error | TextMe");
                    Image icon = new Image("file:src/main/java/com/creativitystudios/textme/AppIcons/TextMeAppError.png");
                    IconViewer.setImage(icon); // Sets the image viewer to show the image created above
                } else {
                    messageStage.setTitle("Message | TextMe");
                    Image icon = new Image("file:src/main/java/com/creativitystudios/textme/AppIcons/TextMeAppMessage.png");
                    IconViewer.setImage(icon); // Sets the image viewer to show the image created above
                }
                messageStage.setResizable(false); // Makes window not resizable
                // Creates a popup with the associated size
                messageStage.setScene(new Scene(pane, 300, 115));
                // Sets the theme for the popup to follow
                changeTheme(themeSelectorDropdown.getItems().get(selectedTheme).toString(), messageStage);
                messageStage.show(); // Shows popup
            }
        });
    }

    /**
     * Gets the username set by the user
     * @return String username that was set by user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets mainStage to the received value
     * @param stage Stage item to set as the owning stage
     */
    public void setStageReference(Stage stage) {
        mainStage = stage;
    }

    /**
     * Sets the Stage received theme
     * @param theme String theme to set the ui to follow
     * @param stage Stage window to set the stylesheet of
     */
    private void changeTheme(String theme, Stage stage) {
        // If the mainStage doesn't have an empty stylesheet, clear all stylesheets it has
        if(!stage.getScene().getStylesheets().isEmpty()) {
            stage.getScene().getStylesheets().clear();
        } switch (theme) { // Creates a switch statement to determine what style to use
            case "Dark Mode": // Sets the stylesheet to the dark mode sheet
                try { // Attempts to set stylesheet
                    stage.getScene().getStylesheets().add(getClass().getResource("TextMeThemeDark.css").toExternalForm());
                } catch(Exception e) { // Throws error if it fails
                    throwMessage(e.getMessage(), true);
                }
                break;
            case "Light Mode": // Sets the stylesheet to the light mode sheet
                try { // Attempts to set stylesheet
                    stage.getScene().getStylesheets().add(getClass().getResource("TextMeThemeLight.css").toExternalForm());
                } catch(Exception e) { // Throws error if it fails
                    throwMessage(e.getMessage(), true);
                }
                break;
            default: // If it does not have an associated stylesheet, do nothing
                break;
        }
    }

}
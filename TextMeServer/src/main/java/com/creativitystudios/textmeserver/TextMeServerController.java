/**
 * @author Caleb Lanphere
 *
 * TextMe Application Server Controller
 *
 * Copyright 2025 | Caleb Lanphere | All Rights Reserved
 *
 */

package com.creativitystudios.textmeserver;

//import com.creativitystudios.textmewebtranslator.TextMeWebTranslatorApplication;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TextMeServerController {
    @FXML private Pane mainUI;
    @FXML private Pane serverSetupPane;
    @FXML private Pane serverHostingPane;
    @FXML private VBox serverStatisticsVBox;
    @FXML private VBox messageHistoryVBox;
    @FXML private VBox serverMessagingVBox;
    @FXML private VBox serverChatLogsVBox;
    @FXML private VBox serverMessageLogVBox;
    @FXML private VBox serverUserControlsVBox;
    @FXML private VBox serverUserListVBox;
    @FXML private TextField serverNameTextBox;
    @FXML private TextField serverPortTextBox;
    @FXML private TextArea serverMessageTextArea;
    @FXML private Label portInfoLabel;
    @FXML private Label ipInfoLabel;
    @FXML private Label nameInfoLabel;
    @FXML private Label serverUserCountLabel;
    @FXML private Label messageCountLabel;
    @FXML private Button serverStaticticsButton;
    @FXML private Button serverMessageHistoryButton;
    @FXML private Button serverSendMessage;
    @FXML private Button serverMessagingButton;
    @FXML private Button serverMessageLogs;
    @FXML private Button serverUserControlsButton;
    @FXML private ToggleButton messageHistorySavingToggle;
    @FXML private ChoiceBox themeSelectorDropdown;
    @FXML private ToggleButton disableNewUsersToggle;

    // Sets up the network manager
    protected TextMeServerNetworkManager netS = new TextMeServerNetworkManager(mainUI, this);
    //private TextMeWebTranslatorApplication webTranslator;
    protected String serverName = "Unnamed Server"; // Name of the server
    private final int MAX_SERVER_NAME_LENGTH = 50; // Max length of the server name
    private final String appVersion = "1.0.0"; // App version
    private final String appCreator = "Caleb Lanphere"; // App creator
    private EventHandler<MouseEvent> KICK_EVENT_HANDLER; // Event handler for kicking a user
    private EventHandler<MouseEvent> WARN_EVENT_HANDLER; // Event handler for warning a user
    private ArrayList<String> tempUsernameForKickOrWarn = new ArrayList<String>(); // Used when creating the kick/warn windows
    private int selectedTheme; // Determines the selected theme
    private Stage mainStage; // Owning window

    /**
     * Toggles if the server is allowed to have new users join
     */
    @FXML
    protected void toggleAllowNewUsers() {
        if(disableNewUsersToggle.isSelected()) { // If the toggle is pressed, disable joining
            disableNewUsersToggle.setText("Enable Joining");
            netS.setNewUsersAllowed(false);
        } else { // Otherwise, enable joining
            disableNewUsersToggle.setText("Disable Joining");
            netS.setNewUsersAllowed(true);
        }
    }

    /**
     * Updates the user count from the network manager's count when called
     */
    protected void updateUserCountUI() {
        Platform.runLater(new Runnable() {
            public void run() {
                serverUserCountLabel.setText(netS.getUsersOnServer());
            }
        });
    }

    /**
     * Adds a message to the message log using the String provided
     * @param message String message to add
     */
    protected void addMessageLogBox(String message) {
        Platform.runLater(new Runnable() {
            public void run() {
                Label newMessage = new Label(message); // Creates the message
                newMessage.setWrapText(true); // Allows the message to wrap to a new line
                // Sets the minimum size of the message
                newMessage.setMinSize(serverMessageLogVBox.getWidth(), newMessage.getPrefHeight() + 25);
                newMessage.setId("newLogEntry"); // Sets the message's ID for styling purposes

                serverMessageLogVBox.getChildren().add(newMessage); // Adds message to log
            }
        });
    }

    /**
     * Disables the server's log box
     * Used in the network manager is message history is disabled
     */
    protected void disableMessageLogBox() {
        Platform.runLater(new Runnable() {
            public void run() {
                serverMessageLogVBox.setDisable(true);
            }
        });
    }

    /**
     * Updates the on-screen message count; Will display a message if the message history is disabled
     */
    protected void updateMessageCountUI() {
        Platform.runLater(new Runnable() {
            public void run() {
                if(netS.getMessagesSentOnServer() == -1) { // Checks if message history is disabled
                    messageCountLabel.setText("Message Recording Disabled");
                } else { // If not, show count of messages
                    messageCountLabel.setText(Long.toString(netS.getMessagesSentOnServer()));
                }
            }
        });
    }

    /**
     * Sends a server message or a server priority message using the message put into the server's textarea
     * @param e MouseEvent event that triggered the function
     */
    @FXML
    protected void sendServerMessage(MouseEvent e) {
        if(e.getSource().equals(serverSendMessage)) {
            netS.sendMessageNet("Server: " + serverMessageTextArea.getText());
        } else {
            netS.sendMessageNet("Server: " + netS.CMD_MSG_MAP.get(16) + serverMessageTextArea.getText());
        }
    }

    /**
     * Clears the server's recorded message history
     */
    @FXML
    protected void clearMessageHistory() {
        netS.clearMessageHistory();
    }

    /**
     * Determines if messages are allowed to be recorded based on the toggles value
     */
    @FXML
    protected void toggleMessageHistorySaving() {
        if(messageHistorySavingToggle.isSelected()) { // If pressed, disable message history
            netS.setMessageHistory(false);
            messageHistorySavingToggle.setText("Enable Message Saving");
        } else { // Otherwise, enable message history
            netS.setMessageHistory(true);
            messageHistorySavingToggle.setText("Disable Message Saving");
        }
    }

    /**
     * Sets what the active action pane is based off the mouseevent provided
     * @param e MouseEvent event that triggered the function
     */
    @FXML
    protected void setServerActionPane(MouseEvent e) {
        // Sets all action pane's false to start
        serverStatisticsVBox.setVisible(false);
        messageHistoryVBox.setVisible(false);
        serverMessagingVBox.setVisible(false);
        serverChatLogsVBox.setVisible(false);
        serverUserControlsVBox.setVisible(false);

        // Then, determines which pane to show based on which button triggered the function
        if(e.getSource().equals(serverStaticticsButton)) {
            serverStatisticsVBox.setVisible(true); // Shows the statistics pane
        }
        if(e.getSource().equals(serverMessageHistoryButton)) {
            messageHistoryVBox.setVisible(true); // Shows the message history pane
        }
        if(e.getSource().equals(serverMessagingButton)) {
            serverMessagingVBox.setVisible(true); // Shows the server messaging pane
        }
        if(e.getSource().equals(serverMessageLogs)) {
            serverMessageLogVBox.setDisable(false); // Enables the message log box
            serverChatLogsVBox.setVisible(true); // Shows the message log pane
            netS.fillServerLogBox(); // Fills the message log box
        }
        if(e.getSource().equals(serverUserControlsButton)) {
            serverUserControlsVBox.setVisible(true); // Shows the user control pane
            netS.fillUserControlBox(); // Fills the user control pane
        }
    }

    /**
     * Clears the users shown on the users control list
     */
    protected void clearServerUserControlList() {
        Platform.runLater(new Runnable() {
            public void run() {
                serverUserListVBox.getChildren().clear();
            }
        });
    }

    /**
     * Clears the message log of its messages
     */
    protected void clearServerMessageLogList() {
        Platform.runLater(new Runnable() {
            public void run() {
                serverMessageLogVBox.getChildren().clear();
            }
        });
    }

    /**
     * Adds a new user to the user control list
     * @param username String username of the user to add to the user control list
     * @param userIndex int index of the user for kicking/warning purposes
     */
    protected void addUserToUserControlList(String username, int userIndex) {
        Platform.runLater(new Runnable() {
            public void run() {
                tempUsernameForKickOrWarn.add(username); // Adds the user to a ArrayList of all users
                HBox userActionsHBox = new HBox();
                HBox userHBox = new HBox();
                Label usernameLabel = new Label(username + ": "); // Username of the user sent over
                Button userKick = new Button("Kick user"); // Button for kicking a user
                Button userWarn = new Button("Warn user"); // Button for warning a user

                userKick.setOnMouseClicked(KICK_EVENT_HANDLER); // Sets the button's onClick function
                userKick.setId(Integer.toString(userIndex)); // Sets the button ID to the index of the user
                userWarn.setOnMouseClicked(WARN_EVENT_HANDLER); // Sets the button's onClick function
                userWarn.setId(Integer.toString(userIndex)); // Sets the button ID to the index of the user
                userActionsHBox.setAlignment(Pos.CENTER_RIGHT);
                // Sets up the user's HBox
                userHBox.setLayoutX(200);
                userHBox.setLayoutY(50);
                userHBox.setPadding(new Insets(5));
                userHBox.setAlignment(Pos.CENTER_LEFT);
                userHBox.setId("newUserKickWarn"); // Sets the ID for styling purposes
                // Sets the font to allow for increasing the font size
                usernameLabel.setFont(new Font(usernameLabel.getFont().getSize() + 4));

                // Adds the objects above to HBoxes
                userActionsHBox.getChildren().add(userWarn);
                userActionsHBox.getChildren().add(userKick);
                userHBox.getChildren().add(usernameLabel);
                userHBox.getChildren().add(userActionsHBox);

                // Adds the set HBoxes above to the list
                serverUserListVBox.getChildren().add(userHBox);
            }
        });
    }

    /**
     * Removes the server setup pane from the screen
     */
    private void removeServerSetupPane() {
        mainUI.getChildren().remove(serverSetupPane);
        serverUserCountLabel.setText("0");
    }

    /**
     * Adds the server hosting pane to the screen
     */
    private void addServerHostingPane() {
        serverHostingPane.setVisible(true);
    }

    /**
     * Sets the IP, Port, and Server name variables that are shown after server startup
     * @param ip String used for showing the server's IP to connect to
     * @param port int used for showing the server's Port to connect to
     */
    protected void setServerConnectionInfo(String ip, int port) {
        removeServerSetupPane(); // Removes the server setup pane
        addServerHostingPane(); // Adds the server hosting pane

        Platform.runLater(new Runnable() {
            public void run() {
                ipInfoLabel.setText("IP: " + ip); // Sets the server's IP
                portInfoLabel.setText("Port: " + port); // Sets the server's Port
                nameInfoLabel.setText("Name: " + serverName); // Sets the server's name
            }
        });
    }

    /**
     * Throws a message as a popup to the screen
     * @param message String message to show
     * @param isError boolean determines if the message is an error
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
                    messageStage.setTitle("Error | TextMe Server");
                    Image icon = new Image("file:src/main/java/com/creativitystudios/textmeserver/AppIcons/TextMeAppError.png");
                    IconViewer.setImage(icon); // Sets the image viewer to show the image created above
                } else {
                    messageStage.setTitle("Message | TextMe Server");
                    Image icon = new Image("file:src/main/java/com/creativitystudios/textmeserver/AppIcons/TextMeAppMessage.png");
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
     * Starts the server
     * @return boolean determines if the server start was successful
     */
    @FXML
    public boolean onStartServer() {
        try {
            if(!serverNameTextBox.getText().isEmpty()) { // If the server name is not empty,
                for(int i = 0; i < netS.CMD_MSG_MAP.size(); i++) { // Check if the name is a command
                    // If it is a command, throw an error and fail the server start
                    if(serverNameTextBox.getText().equals(netS.CMD_MSG_MAP.get(i))) {
                        throwMessage("Cannot set username that is a command", true);
                        return false;
                    }
                }
                serverName = serverNameTextBox.getText(); // Sets the server name
            }
            if(!serverPortTextBox.getText().isEmpty()) { // Checks if the server Port box is empty
                // If it isn't, try to start the server with the provided port
                netS.startServer(Integer.parseInt(serverPortTextBox.getText()), serverName);
                //webTranslator.getWebTranslatorController().getNetworkManager().startServer(Integer.parseInt(serverPortTextBox.getText()), serverName);
                return true;
            } else { // If it is empty, start and make the server decide a port
                netS.startServer(0, serverName);
                //webTranslator.getWebTranslatorController().getNetworkManager().startServer(0, serverName);
                return true;
            }
        } catch (NumberFormatException e) { // If it fails, throw an error
            throwMessage("Incorrect format for port \nPlease try again", true);
            return false;
        }
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
        Image logoImg = new Image("file:src/main/java/com/creativitystudios/textmeserver/AppIcons/TextMeAppLogoSmall.png");
        // App name
        Label appName = new Label("TextMe Server");
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
        aboutMainUI.setId("newPopup"); // Sets the window's ID for styling purposes

        aboutMenu.setResizable(false); // Sets the window to not allow resizing
        aboutMenu.setTitle("About | TextMe Server"); // Sets the title of the window
        aboutMenu.setScene(new Scene(aboutMainUI, 300, 160)); // Sets the window and its size
        // Sets the window's theme to follow
        changeTheme(themeSelectorDropdown.getItems().get(selectedTheme).toString(), aboutMenu);
        aboutMenu.show(); // Shows the window
    }

    /**
     * Checks if the server name box or the port box have passed their max word count
     * @param e KeyEvent used to determine the owner and the key pressed
     */
    @FXML
    private void onKeyPressed(KeyEvent e) {
        if(e.getSource() == serverNameTextBox) { // If the server name box is the source
            if(serverNameTextBox.getText().length() > MAX_SERVER_NAME_LENGTH) { // And is too long
                // Set the name to the characters upto the MAX_SERVER_NAME_LENGTH
                serverNameTextBox.setText(serverNameTextBox.getText().substring(0, MAX_SERVER_NAME_LENGTH));
                // Set the cursor to the end
                serverNameTextBox.positionCaret(serverNameTextBox.getText().length());
            }
        } else if(e.getSource() == serverPortTextBox) { // If the server port box is the source
            if(serverPortTextBox.getText().length() > 5) { // And is too long
                // Set the port to the characters upto the 5
                serverPortTextBox.setText(serverPortTextBox.getText().substring(0, 5));
                // Set the cursor to the end
                serverPortTextBox.positionCaret(serverPortTextBox.getText().length());
            }
        }
    }

    /**
     * Sets up the Warn event handler;
     * SHOULD NOT BE CALLED BESIDES ON STARTUP
     */
    protected void setWARN_EVENT_HANDLER() {
        WARN_EVENT_HANDLER = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent){
                // Gets the values of the owning object
                String tempOwningButton = mouseEvent.getSource().toString();
                // Grabs the index from the calling button's ID
                int tempUserIndex = Integer.parseInt(tempOwningButton.substring(tempOwningButton.indexOf("d") + 2, tempOwningButton.indexOf(",")));
                Stage stage = new Stage();
                Pane mainPopup = new Pane();
                VBox mainVBox = new VBox();
                Button cancelWarn = new Button("Cancel"); // Cancel button
                Button confirmWarn = new Button("Send"); // Send button
                TextArea reasonForWarn = new TextArea(); // Text Area
                Label warnLabel = new Label("Warn Message: ");
                HBox WarnButtons = new HBox();

                // Sets up all the objects above
                mainVBox.setAlignment(Pos.TOP_CENTER);
                warnLabel.setFont(new Font(18));
                warnLabel.setAlignment(Pos.CENTER);
                mainVBox.setLayoutX(10);
                mainVBox.setMaxWidth(280);
                mainVBox.setMaxHeight(230);
                mainVBox.setSpacing(5);
                WarnButtons.setAlignment(Pos.CENTER);
                WarnButtons.setSpacing(25);
                reasonForWarn.setWrapText(true);
                reasonForWarn.setOpaqueInsets(new Insets(5));
                cancelWarn.setCancelButton(true);
                confirmWarn.setDefaultButton(true);

                // Sets up what to do when clicking to warn a user
                confirmWarn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (!reasonForWarn.getText().isEmpty()) {
                            netS.sendMessageNet("Server: " + netS.CMD_MSG_MAP.get(17) + reasonForWarn.getText(), tempUserIndex);
                        } else {
                            netS.sendMessageNet("Server: " + netS.CMD_MSG_MAP.get(17) + "No reason specified", tempUserIndex);
                        }

                        stage.close();
                    }
                });
                // Sets up what to do when clicking to cancel
                cancelWarn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        stage.close();
                    }
                });
                // Checks the length of the message
                reasonForWarn.setPromptText("Type message that will be sent to user upon kick");
                reasonForWarn.setOnKeyTyped(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        if (reasonForWarn.getText().length() > 50) {
                            reasonForWarn.setText(reasonForWarn.getText().substring(0, 50));
                            reasonForWarn.positionCaret(reasonForWarn.getText().length());
                        }
                    }
                });

                // Lays out all objects created above
                mainVBox.getChildren().add(warnLabel);
                mainVBox.getChildren().add(reasonForWarn);
                WarnButtons.getChildren().add(cancelWarn);
                WarnButtons.getChildren().add(confirmWarn);
                mainVBox.getChildren().add(WarnButtons);
                mainPopup.getChildren().add(mainVBox);
                mainPopup.setId("newPopup"); // Sets the ID for styling purposes

                // Sets the windows title
                stage.setTitle("Warn User: " + tempUsernameForKickOrWarn.get(tempUserIndex));
                stage.setScene(new Scene(mainPopup, 300, 250)); // Sets the windows size
                // Sets the window's theming
                changeTheme(themeSelectorDropdown.getItems().get(selectedTheme).toString(),stage);
                stage.setResizable(false); // Disables resizing the window
                stage.show(); // Shows the window on the screen
            }
        };
    }

    /**
     * Sets up the Kick event handler;
     * SHOULD NOT BE CALLED BESIDES ON STARTUP
     */
    protected void setKICK_EVENT_HANDLER() {
        KICK_EVENT_HANDLER = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent){
                // Gets the values of the owning object
                String tempOwningButton = mouseEvent.getSource().toString();
                // Grabs the index from the calling button's ID
                int tempUserIndex = Integer.parseInt(tempOwningButton.substring(tempOwningButton.indexOf("d") + 2, tempOwningButton.indexOf(",")));
                Stage stage = new Stage();
                Pane mainPopup = new Pane();
                VBox mainVBox = new VBox();
                Button cancelKick = new Button("Cancel"); // Button to cancel
                Button confirmKick = new Button("Kick"); // Button to kick
                TextArea reasonForKick = new TextArea(); // Text area
                Label kickLabel = new Label("Kick Message: ");
                HBox kickButtons = new HBox();

                // Sets up all the objects above
                mainVBox.setAlignment(Pos.TOP_CENTER);
                kickLabel.setFont(new Font(18));
                kickLabel.setAlignment(Pos.CENTER);
                mainVBox.setLayoutX(10);
                mainVBox.setMaxWidth(280);
                mainVBox.setMaxHeight(230);
                mainVBox.setSpacing(5);
                kickButtons.setAlignment(Pos.CENTER);
                kickButtons.setSpacing(25);
                reasonForKick.setWrapText(true);
                reasonForKick.setOpaqueInsets(new Insets(5));
                cancelKick.setCancelButton(true);
                confirmKick.setDefaultButton(true);

                // Sets up what to do when clicking to kick a user
                confirmKick.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (!reasonForKick.getText().isEmpty()) {
                            netS.kickUser(tempUserIndex, reasonForKick.getText());
                        } else {
                            netS.kickUser(tempUserIndex, "No reason specified");
                        }

                        stage.close();
                    }
                });
                // Sets up what to do when clicking to cancel
                cancelKick.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        stage.close();
                    }
                });
                // Checks the length of the message
                reasonForKick.setPromptText("Type message that will be sent to user upon kick");
                reasonForKick.setOnKeyTyped(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        if (reasonForKick.getText().length() > 50) {
                            reasonForKick.setText(reasonForKick.getText().substring(0, reasonForKick.getText().length() - 1));
                            reasonForKick.positionCaret(reasonForKick.getText().length());
                        }
                    }
                });

                // Lays out the objects created above
                mainVBox.getChildren().add(kickLabel);
                mainVBox.getChildren().add(reasonForKick);
                kickButtons.getChildren().add(cancelKick);
                kickButtons.getChildren().add(confirmKick);
                mainVBox.getChildren().add(kickButtons);
                mainPopup.getChildren().add(mainVBox);
                mainPopup.setId("newPopup"); // Sets an ID for styling purposes

                // Sets the windows title
                stage.setTitle("Kick User: " + tempUsernameForKickOrWarn.get(tempUserIndex));
                stage.setScene(new Scene(mainPopup, 300, 250)); // Sets the windows size
                // Sets the window's theming
                changeTheme(themeSelectorDropdown.getItems().get(selectedTheme).toString(),stage);
                stage.setResizable(false); // Disables resizing the window
                stage.show(); // Shows the window on the screen
            }
        };
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
                    stage.getScene().getStylesheets().add(getClass().getResource("TextMeServerThemeDark.css").toExternalForm());
                } catch(Exception e) { // Throws error if it fails
                    throwMessage(e.getMessage(), true);
                }
                break;
            case "Light Mode": // Sets the stylesheet to the light mode sheet
                try { // Attempts to set stylesheet
                    stage.getScene().getStylesheets().add(getClass().getResource("TextMeServerThemeLight.css").toExternalForm());
                } catch(Exception e) { // Throws error if it fails
                    throwMessage(e.getMessage(), true);
                }
                break;
            default: // If it does not have an associated stylesheet, do nothing
                break;
        }
    }

    /**
     * Sets mainStage to the supplied stage
     * @param stage owning stage
     */
    public void setStageReference(Stage stage) {
        mainStage = stage;
    }

    //public void setWebTranslator(TextMeWebTranslatorApplication appReference) {
    //    webTranslator = appReference;
    //}
}
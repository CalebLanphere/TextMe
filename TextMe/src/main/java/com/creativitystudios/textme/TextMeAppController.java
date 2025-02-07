package com.creativitystudios.textme;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;

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

    private final TextMeClientNetManager netC = new TextMeClientNetManager(this, mainUI);
    private static String username;
    private boolean isSettingsOpen = false;
    private boolean isInitialSet = true;
    private Object resetUsernameAffectedPane;
    private final String appVersion = "1.0.0";
    private final String appCreator = "Caleb Lanphere";
    private final int MAX_USERNAME_LENGTH = 32;
    private final int MAX_MESSAGE_LENGTH = 2000;

    @FXML
    private void onSetUsernameKey(KeyEvent e) {
        if(e.getCode().equals(KeyCode.ENTER)) {
            onSetUsername();
        }
    }

    @FXML
    private boolean onSetUsername() {
        if (!usernameTextBox.getText().isEmpty()) {
            for(int i = 0; i < netC.CMD_MAP.size(); i++) {
                if (usernameTextBox.getText().equals(netC.CMD_MAP.get(i))) {
                    throwMessage("Cannot set username that is a command", true);
                    return false;
                }
            }
            if(isInitialSet) {
                username = usernameTextBox.getText();
                removeUsernameSelectorPane();
                addConnectionPane();
                isInitialSet = false;
            } else {
                sendMessageToNetManager(netC.CMD_MAP.get(7) + usernameTextBox.getText());
                username = usernameTextBox.getText();
                removeUsernameSelectorPane();

                if (resetUsernameAffectedPane.equals(serverInformationPane)) {
                    addServerInformationPane();
                }
                if (resetUsernameAffectedPane.equals(connectionSelectorPane)) {
                    addConnectionPane();
                }
            }
            return true;
        }
        return false;
    }

    @FXML
    protected void isMaxPortLength() {
        if(portTextBox.getText().length() > 5) {
            portTextBox.setText(portTextBox.getText(0, portTextBox.getText().length() - 1));
            portTextBox.positionCaret(portTextBox.getText().length());
        }
    }

    @FXML
    protected void isMaxIPLength() {
        if(ipAddressTextBox.getText().length() > 15) {
            ipAddressTextBox.setText(ipAddressTextBox.getText(0, ipAddressTextBox.getText().length() - 1));
            ipAddressTextBox.positionCaret(ipAddressTextBox.getText().length());
        }
    }

    @FXML
    private void attemptConnectionKey(KeyEvent e) {
        if(e.getCode().equals(KeyCode.ENTER)) {
            attemptConnection();
        }
    }

    @FXML private void attemptConnection() {
        String ipTemp = ipAddressTextBox.getText();
        int portTemp = 0;
        try {
            portTemp = Integer.parseInt(portTextBox.getText());
        } catch (NumberFormatException error) {
            throwMessage("Port entered is not valid\n Please only use numbers 0-9", true);
        }
        if(!ipTemp.isEmpty() && !portTextBox.getText().isEmpty()) {
            if(netC.attemptConnection(ipTemp, portTemp)) {
                isConnectedToServer(true);
                removeConnectionSelectorPane();
                serverIdentDetails.setText("IP: " + ipTemp + " | Port: " + portTemp);
                addServerInformationPane();
            }
        }
    }

    @FXML
    protected void isConnectedToServer(boolean isConnected) {
        messageReceivedPane.setDisable(!isConnected);
        messageVBox.setDisable(!isConnected);
        sendMessageButton.setDisable(!isConnected);
        messageTextBox.setDisable(!isConnected);
    }

    @FXML
    protected void isMaxUsernameLength() {
        if(usernameTextBox.getLength() > MAX_USERNAME_LENGTH) {
            usernameTextBox.setText(usernameTextBox.getText(0, usernameTextBox.getLength() - 1));
            usernameTextBox.positionCaret(usernameTextBox.getLength());
        }
    }

    private void removeUsernameSelectorPane() {
        usernameSelectorPane.setVisible(false);
        usernameTextBox.setText("");
    }

    private void addUsernameSelectorPane() {
        usernameSelectorPane.setVisible(true);
    }

    @FXML
    private void resetUsername() {
        closeSettings();
        if(resetUsernameAffectedPane.equals(serverInformationPane)) {
            removeServerInformationPane();
        }
        if(resetUsernameAffectedPane.equals(connectionSelectorPane)) {
            removeConnectionSelectorPane();
        }
        addUsernameSelectorPane();
    }

    private void removeConnectionSelectorPane() {
        connectionSelectorPane.setVisible(false);
        ipAddressTextBox.setText("");
        portTextBox.setText("");
    }

    protected void addConnectionPane() {
        connectionSelectorPane.setVisible(true);
        resetUsernameAffectedPane = connectionSelectorPane;
    }

    protected void addServerInformationPane() {
        serverInformationPane.setVisible(true);
        resetUsernameAffectedPane = serverInformationPane;
    }

    protected void addSettingsPane() {
        messageReceivedPane.setPrefWidth(330);
        messageVBox.setPrefWidth(328);
        settingsPane.setVisible(true);
    }

    protected void removeServerInformationPane() {
        Platform.runLater(new Runnable() {
            public void run() {
                serverInformationPane.setVisible(false);
                serverIdentDetails.setText("IP: | Port: ");
            }
        });
    }

    protected void removeSettingsPane() {
        messageReceivedPane.setPrefWidth(500);
        messageVBox.setPrefWidth(498);
        settingsPane.setVisible(false);
    }

    @FXML
    protected void sendMessageToNetManager() {
        netC.sendMessageNet(username + ": " + messageTextBox.getText());
        messageTextBox.setText("");
    }

    @FXML
    protected void sendMessageToNetManagerKey(KeyEvent e) {
        if(e.getCode().equals(KeyCode.ENTER)) {
            sendMessageToNetManager();
        }
    }

    protected void sendMessageToNetManager(String message) {
        netC.sendMessageNet(username + ": " + message);
    }

    @FXML
    protected void isMaxCharactersReached() {
        if(messageTextBox.getLength() > MAX_MESSAGE_LENGTH) {
            messageTextBox.setText(messageTextBox.getText(0, messageTextBox.getLength() - 1));
            messageTextBox.positionCaret(messageTextBox.getLength());
        }
    }

    public void addMessageToUI(String message) {
        Platform.runLater(new Runnable() {
            public void run() {
                Label newMessage = new Label(message);
                newMessage.setWrapText(true);
                newMessage.setMinSize(newMessage.getPrefWidth() + 15, newMessage.getPrefHeight() + 15);

                messageVBox.getChildren().add(newMessage);
            }
        });
    }

    protected void setServerName(String serverName) {
        Platform.runLater(new Runnable() {
            public void run() {
                serverIdentName.setText("Server: " + serverName);
            }
        });
    }

    protected void resetForReconnection() {
        isConnectedToServer(false);
        netC.resetConnection();
        clearMessageHistory();
        removeServerInformationPane();
        addConnectionPane();
    }

    protected void clearMessageHistory() {
        Platform.runLater(new Runnable() {
            public void run() {
                messageVBox.getChildren().clear();
                System.out.print("cleared");
            }
        });
    }

    @FXML
    protected void openCloseAppSettings() {
        if(!isSettingsOpen) {
            addSettingsPane();
            isSettingsOpen = true;
        } else {
            closeSettings();
        }
    }

    private void closeSettings() {
        removeSettingsPane();
        isSettingsOpen = false;
    }

    @FXML
    protected void disconnectFromServer() {
        sendMessageToNetManager("usr/msg_quit;");
        resetForReconnection();
    }

    @FXML
    private void openAboutMenu() {
        Stage aboutMenu = new Stage();
        Pane aboutMainUI = new Pane();
        ImageView logoViewer = new ImageView();
        Image logoImg = new Image("file:src/main/java/com/creativitystudios/textme/AppIcons/TextMeAppLogoSmall.png");
        Label appName = new Label("TextMe Client");
        Label appVersion = new Label("App Version: " + this.appVersion);
        Label appCopyright = new Label("© 2025 Caleb Lanphere. All Rights Reserved");
        Label appCreator = new Label("Creators: " + this.appCreator);
        Button closeMenu = new Button("Close");
        HBox horizontalSpacer = new HBox();
        VBox appDetails = new VBox();

        appCopyright.setWrapText(true);
        closeMenu.setDefaultButton(true);
        logoViewer.setImage(logoImg);
        logoViewer.setFitWidth(125);
        logoViewer.setFitHeight(100);
        appDetails.setPrefWidth(150);
        appDetails.setPrefHeight(250);
        logoViewer.setLayoutX(0);
        horizontalSpacer.setLayoutX(140);
        horizontalSpacer.setLayoutY(-50);
        appDetails.setAlignment(Pos.CENTER);
        appDetails.setSpacing(10);
        appCopyright.setLayoutX(5);
        appCopyright.setLayoutY(140);

        closeMenu.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                e.consume();
                aboutMenu.close();
            }
        });
        closeMenu.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                e.consume();
                aboutMenu.close();
            }
        });

        aboutMainUI.getChildren().add(logoViewer);
        appDetails.getChildren().add(appName);
        appDetails.getChildren().add(appVersion);
        appDetails.getChildren().add(appCreator);
        appDetails.getChildren().add(closeMenu);
        horizontalSpacer.getChildren().add(appDetails);
        aboutMainUI.getChildren().add(horizontalSpacer);
        aboutMainUI.getChildren().add(appCopyright);

        aboutMenu.setResizable(false);
        aboutMenu.setTitle("About | TextMe");
        aboutMenu.setScene(new Scene(aboutMainUI, 300, 160));
        aboutMenu.show();
    }

    protected void throwMessage(String message, boolean isError) {
        Platform.runLater(new Runnable() {
            public void run() {
                Label newMessage = new Label(message);
                Stage stage = new Stage();
                Pane pane = new Pane();
                VBox vbox = new VBox();
                VBox closeButtonCenter = new VBox();
                HBox hbox = new HBox();
                Button closeMenu = new Button("Close");
                ImageView IconViewer = new ImageView();
                Image icon = new Image("file:src/main/java/com/creativitystudios/textme/AppIcons/TextMeAppIcon.png");

                closeMenu.setDefaultButton(true);
                IconViewer.setImage(icon);
                IconViewer.setFitWidth(100);
                IconViewer.setFitHeight(100);
                IconViewer.setCache(true);
                newMessage.setPrefWidth(150);
                newMessage.setPrefHeight(75);
                newMessage.setWrapText(true);
                closeButtonCenter.setAlignment(Pos.CENTER);

                closeMenu.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        e.consume();
                        stage.close();
                    }
                });
                closeMenu.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        e.consume();
                        stage.close();
                    }
                });

                hbox.getChildren().add(IconViewer);
                vbox.getChildren().add(newMessage);
                closeButtonCenter.getChildren().add(closeMenu);
                vbox.getChildren().add(closeButtonCenter);
                hbox.getChildren().add(vbox);
                pane.getChildren().add(hbox);

                if(isError) {
                    stage.setTitle("Error | TextMe");
                } else {
                    stage.setTitle("Message | TextMe");
                }
                stage.setResizable(false);
                stage.setScene(new Scene(pane, 250, 115));
                stage.show();
            }
        });
    }

    public String getUsername() {
        return username;
    }

}
package com.creativitystudios.textmeserver;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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
    @FXML private Button serverSendMessagePopup;
    @FXML private Button serverMessageLogs;
    @FXML private Button serverUserControlsButton;
    @FXML private ToggleButton messageHistorySavingToggle;

    protected TextMeServerNetworkManager netS = new TextMeServerNetworkManager(mainUI, this);
    protected String serverName = "Unnamed Server";
    private final int maxServerNameLength = 50;


    protected void updateUserCountUI() {
        Platform.runLater(new Runnable() {
            public void run() {
                serverUserCountLabel.setText(netS.getUsersOnServer());
            }
        });
    }

    protected void addMessageLogBox(String message) {
        Platform.runLater(new Runnable() {
            public void run() {
                Label newMessage = new Label(message);
                newMessage.setWrapText(true);
                newMessage.setMinSize(newMessage.getPrefWidth() + 15, newMessage.getPrefHeight() + 15);

                serverMessageLogVBox.getChildren().add(newMessage);
            }
        });
    }

    protected void disableMessageLogBox() {
        Platform.runLater(new Runnable() {
            public void run() {
                serverMessageLogVBox.setDisable(true);
            }
        });
    }

    protected void updateMessageCountUI() {
        Platform.runLater(new Runnable() {
            public void run() {
                if(netS.getMessagesSentOnServer() == -1) {
                    messageCountLabel.setText("Message Recording Disabled");
                } else {
                    messageCountLabel.setText(Long.toString(netS.getMessagesSentOnServer()));
                }
            }
        });
    }

    @FXML
    protected void sendServerMessage(MouseEvent e) {
        if(e.getSource().equals(serverSendMessage)) {
            netS.sendMessageNet("Server: " + serverMessageTextArea.getText());
        } else {
            netS.sendMessageNet("Server: " + serverMessageTextArea.getText());
        }
    }

    @FXML
    protected void clearMessageHistory() {
        netS.clearMessageHistory();
    }

    @FXML
    protected void toggleMessageHistorySaving() {
        if(messageHistorySavingToggle.isSelected()) {
            netS.setMessageHistory(false);
            messageHistorySavingToggle.setText("Enable Message Saving");
        } else {
            netS.setMessageHistory(true);
            messageHistorySavingToggle.setText("Disable Message Saving");
        }
    }

    @FXML
    protected void setServerActionPane(MouseEvent e) {
        serverStatisticsVBox.setVisible(false);
        messageHistoryVBox.setVisible(false);
        serverMessagingVBox.setVisible(false);
        serverChatLogsVBox.setVisible(false);
        serverUserControlsVBox.setVisible(false);

        if(e.getSource().equals(serverStaticticsButton)) {
            serverStatisticsVBox.setVisible(true);
        }
        if(e.getSource().equals(serverMessageHistoryButton)) {
            messageHistoryVBox.setVisible(true);
        }
        if(e.getSource().equals(serverMessagingButton)) {
            serverMessagingVBox.setVisible(true);
        }
        if(e.getSource().equals(serverMessageLogs)) {
            serverMessageLogVBox.setDisable(false);
            serverChatLogsVBox.setVisible(true);
            netS.fillServerLogBox();
        }
        if(e.getSource().equals(serverUserControlsButton)) {
            serverUserControlsVBox.setVisible(true);
            netS.fillUserControlBox();
        }
    }

    protected void clearServerUserControlList() {
        Platform.runLater(new Runnable() {
            public void run() {
                serverUserListVBox.getChildren().clear();
            }
        });
    }

    protected void clearServerMessageLogList() {
        Platform.runLater(new Runnable() {
            public void run() {
                serverMessageLogVBox.getChildren().clear();
            }
        });
    }

    protected void addUserToUserControlList(String username, int userIndex) {
        Platform.runLater(new Runnable() {
            public void run() {
                int user = userIndex;
                HBox userActionsHBox = new HBox();
                HBox userHBox = new HBox();
                Label usernameLabel = new Label(username + ": ");
                Button userKick = new Button("Kick user");
                Button userWarn = new Button("Warn user");

                userKick.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        netS.kickUser(user);
                    }
                });
                userActionsHBox.setAlignment(Pos.CENTER_RIGHT);
                userHBox.setLayoutX(200);
                userHBox.setLayoutY(50);
                userHBox.setPadding(new Insets(5));
                userHBox.setAlignment(Pos.CENTER_LEFT);
                usernameLabel.setFont(new Font(usernameLabel.getFont().getSize() + 4));

                userActionsHBox.getChildren().add(userWarn);
                userActionsHBox.getChildren().add(userKick);
                userHBox.getChildren().add(usernameLabel);
                userHBox.getChildren().add(userActionsHBox);

                serverUserListVBox.getChildren().add(userHBox);
            }
        });
    }

    private void removeServerSetupPane() {
        mainUI.getChildren().remove(serverSetupPane);
        serverUserCountLabel.setText("0");
    }

    private void addServerHostingPane() {
        serverHostingPane.setVisible(true);
    }

    protected void setServerConnectionInfo(String ip, int port) {
        removeServerSetupPane();
        addServerHostingPane();

        Platform.runLater(new Runnable() {
            public void run() {
                ipInfoLabel.setText("IP: " + ip);
                portInfoLabel.setText("Port: " + port);
                nameInfoLabel.setText("Name: " + serverName);
            }
        });
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
                Image icon = new Image("file:src/main/java/com/creativitystudios/textmeserver/AppIcons/TextMeAppIcon.png");

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

    @FXML
    public boolean onStartServer() {
        try {
            if(!serverNameTextBox.getText().isEmpty()) {
                for(int i = 0; i < netS.CMD_MSG_MAP.size(); i++) {
                    if(serverNameTextBox.getText().equals(netS.CMD_MSG_MAP.get(i))) {
                        throwMessage("Cannot set username that is a command", true);
                        return false;
                    }
                }
                serverName = serverNameTextBox.getText();
            }
            if(!serverPortTextBox.getText().isEmpty()) {
                netS.startServer(Integer.parseInt(serverPortTextBox.getText()), serverName);
                return true;
            } else {
                netS.startServer(0, serverName);
                return true;
            }
        } catch (NumberFormatException e) {
            throwMessage("Incorrect format for port \nPlease try again", true);
            return false;
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent e) {
        if(e.getSource() == serverNameTextBox) {
            if(serverNameTextBox.getText().length() > maxServerNameLength) {
                serverNameTextBox.setText(serverNameTextBox.getText().substring(0, maxServerNameLength));
                serverNameTextBox.positionCaret(serverNameTextBox.getText().length());
            }
        } else if(e.getSource() == serverPortTextBox) {
            if(serverPortTextBox.getText().length() > 5) {
                serverPortTextBox.setText(serverPortTextBox.getText().substring(0, 5));
                serverPortTextBox.positionCaret(serverPortTextBox.getText().length());
            }
        }
    }
}
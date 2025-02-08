package com.creativitystudios.textmeserver;

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
    @FXML private ChoiceBox themeSelectorDropdown;
    @FXML private ToggleButton disableNewUsersToggle;

    protected TextMeServerNetworkManager netS = new TextMeServerNetworkManager(mainUI, this);
    protected String serverName = "Unnamed Server";
    private final int maxServerNameLength = 50;
    private final String appVersion = "1.0.0";
    private final String appCreator = "Caleb Lanphere";
    private EventHandler<MouseEvent> KICK_EVENT_HANDLER;
    private EventHandler<MouseEvent> WARN_EVENT_HANDLER;
    private String tempUsername;
    private int tempUserIndex;
    private int selectedTheme;
    private Stage mainStage;

    @FXML
    protected void toggleAllowNewUsers() {
        if(disableNewUsersToggle.isSelected()) {
            disableNewUsersToggle.setText("Enable Joining");
            netS.setNewUsersAllowed(false);
        } else {
            disableNewUsersToggle.setText("Disable Joining");
            netS.setNewUsersAllowed(true);
        }
    }

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
                newMessage.setMinSize(serverMessageLogVBox.getWidth(), newMessage.getPrefHeight() + 15);
                newMessage.setId("newLogEntry");

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
            netS.sendMessageNet("Server: " + netS.CMD_MSG_MAP.get(16) + serverMessageTextArea.getText());
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
                tempUsername = username;
                tempUserIndex = userIndex;
                HBox userActionsHBox = new HBox();
                HBox userHBox = new HBox();
                Label usernameLabel = new Label(username + ": ");
                Button userKick = new Button("Kick user");
                Button userWarn = new Button("Warn user");

                userKick.setOnMouseClicked(KICK_EVENT_HANDLER);
                userWarn.setOnMouseClicked(WARN_EVENT_HANDLER);
                userActionsHBox.setAlignment(Pos.CENTER_RIGHT);
                userHBox.setLayoutX(200);
                userHBox.setLayoutY(50);
                userHBox.setPadding(new Insets(5));
                userHBox.setAlignment(Pos.CENTER_LEFT);
                userHBox.setId("newUserKickWarn");
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
                pane.setId("newPopup");

                if(isError) {
                    stage.setTitle("Error | TextMe");
                } else {
                    stage.setTitle("Message | TextMe");
                }
                stage.setResizable(false);
                stage.setScene(new Scene(pane, 250, 115));
                changeTheme(themeSelectorDropdown.getItems().get(selectedTheme).toString(),stage);
                stage.setResizable(false);
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
    private void openAboutMenu() {
        Stage aboutMenu = new Stage();
        Pane aboutMainUI = new Pane();
        ImageView logoViewer = new ImageView();
        Image logoImg = new Image("file:src/main/java/com/creativitystudios/textmeserver/AppIcons/TextMeAppLogoSmall.png");
        Label appName = new Label("TextMe Server");
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
        aboutMainUI.setId("newPopup");

        aboutMenu.setResizable(false);
        aboutMenu.setTitle("About | TextMe Server");
        aboutMenu.setScene(new Scene(aboutMainUI, 300, 160));
        changeTheme(themeSelectorDropdown.getItems().get(selectedTheme).toString(),aboutMenu);
        aboutMenu.setResizable(false);
        aboutMenu.show();
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

    protected void setWARN_EVENT_HANDLER() {
        WARN_EVENT_HANDLER = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent){
                Stage stage = new Stage();
                Pane mainPopup = new Pane();
                VBox mainVBox = new VBox();
                Button cancelWarn = new Button("Cancel");
                Button confirmWarn = new Button("Send");
                TextArea reasonForWarn = new TextArea();
                Label warnLabel = new Label("Warn Message: ");
                HBox WarnButtons = new HBox();

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
                confirmWarn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (!reasonForWarn.getText().isEmpty()) {
                            netS.sendMessageToUserNet("Server: " + netS.CMD_MSG_MAP.get(17) + reasonForWarn.getText(), tempUserIndex);
                        } else {
                            netS.sendMessageToUserNet("Server: " + netS.CMD_MSG_MAP.get(17) + "No reason specified", tempUserIndex);
                        }

                        stage.close();
                    }
                });
                cancelWarn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        stage.close();
                    }
                });
                reasonForWarn.setPromptText("Type message that will be sent to user upon kick");
                reasonForWarn.setOnKeyTyped(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        if (reasonForWarn.getText().length() > 50) {
                            reasonForWarn.setText(reasonForWarn.getText().substring(0, reasonForWarn.getText().length() - 1));
                            reasonForWarn.positionCaret(reasonForWarn.getText().length());
                        }
                    }
                });

                mainVBox.getChildren().add(warnLabel);
                mainVBox.getChildren().add(reasonForWarn);
                WarnButtons.getChildren().add(cancelWarn);
                WarnButtons.getChildren().add(confirmWarn);
                mainVBox.getChildren().add(WarnButtons);
                mainPopup.getChildren().add(mainVBox);
                mainPopup.setId("newPopup");

                stage.setTitle("Warn User: " + tempUsername);
                stage.setScene(new Scene(mainPopup, 300, 250));
                changeTheme(themeSelectorDropdown.getItems().get(selectedTheme).toString(),stage);
                stage.setResizable(false);
                stage.show();
            }
        };
    }

    protected void setKICK_EVENT_HANDLER() {
        KICK_EVENT_HANDLER = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent){
                Stage stage = new Stage();
                Pane mainPopup = new Pane();
                VBox mainVBox = new VBox();
                Button cancelKick = new Button("Cancel");
                Button confirmKick = new Button("Kick");
                TextArea reasonForKick = new TextArea();
                Label kickLabel = new Label("Kick Message: ");
                HBox kickButtons = new HBox();

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
                cancelKick.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        stage.close();
                    }
                });
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

                mainVBox.getChildren().add(kickLabel);
                mainVBox.getChildren().add(reasonForKick);
                kickButtons.getChildren().add(cancelKick);
                kickButtons.getChildren().add(confirmKick);
                mainVBox.getChildren().add(kickButtons);
                mainPopup.getChildren().add(mainVBox);
                mainPopup.setId("newPopup");

                stage.setTitle("Kick User: " + tempUsername);
                stage.setScene(new Scene(mainPopup, 300, 250));
                changeTheme(themeSelectorDropdown.getItems().get(selectedTheme).toString(),stage);
                stage.setResizable(false);
                stage.show();
            }
        };
    }

    protected void setupThemeSelectorDropdown() {
        themeSelectorDropdown.getItems().addAll("Light Mode", "Dark Mode");
        themeSelectorDropdown.setValue(themeSelectorDropdown.getItems().getFirst());
        themeSelectorDropdown.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newValue) {
                selectedTheme = newValue.intValue();
                switch(newValue.intValue()) {
                    case 0:
                        changeMainUITheme("Light Mode");
                        break;
                    case 1:
                        changeMainUITheme("Dark Mode");
                        break;
                    default:
                        changeMainUITheme("Light Mode");
                        break;
                }
            }
        });
    }

    private void changeMainUITheme(String theme) {
        if(!mainStage.getScene().getStylesheets().isEmpty()) {
            mainStage.getScene().getStylesheets().clear();
        } switch (theme) {
            case "Dark Mode":
                try {
                    mainStage.getScene().getStylesheets().add(getClass().getResource("TextMeServerThemeDark.css").toExternalForm());
                } catch(Exception e) {
                    throwMessage(e.getMessage(), true);
                }
                break;
            case "Light Mode":
                try {
                    mainStage.getScene().getStylesheets().add(getClass().getResource("TextMeServerThemeLight.css").toExternalForm());
                } catch(Exception e) {
                    throwMessage(e.getMessage(), true);
                }
                break;
            default:
                break;
        }
    }

    private void changeTheme(String theme, Stage stage) {
        if(!stage.getScene().getStylesheets().isEmpty()) {
            stage.getScene().getStylesheets().clear();
        } switch (theme) {
            case "Dark Mode":
                try {
                    stage.getScene().getStylesheets().add(getClass().getResource("TextMeServerThemeDark.css").toExternalForm());
                } catch(Exception e) {
                    throwMessage(e.getMessage(), true);
                }
                break;
            case "Light Mode":
                try {
                    mainStage.getScene().getStylesheets().add(getClass().getResource("TextMeServerThemeLight.css").toExternalForm());
                } catch(Exception e) {
                    throwMessage(e.getMessage(), true);
                }
                break;
            default:
                break;
        }
    }

    public void sendStageReference(Stage stage) {
        mainStage = stage;
    }
}
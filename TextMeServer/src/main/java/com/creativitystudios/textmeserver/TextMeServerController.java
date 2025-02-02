package com.creativitystudios.textmeserver;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TextMeServerController {
    @FXML
    private Pane mainUI;
    @FXML
    private Pane serverSetupPane;
    @FXML
    private Pane serverHostingPane;
    @FXML
    private TextField serverNameTextBox;
    @FXML
    private TextField serverPortTextBox;
    @FXML
    private Label portInfoLabel;
    @FXML
    private Label ipInfoLabel;
    @FXML
    private Label nameInfoLabel;
    @FXML
    private Label serverUserCountLabel;

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
                Image icon;
                Label messageLabel;
                Label newMessage = new Label(message);
                Stage stage = new Stage();
                Pane pane = new Pane();
                VBox vbox = new VBox();
                HBox hbox = new HBox();
                ImageView messageIcon = new ImageView();
                if(isError == true) {
                    icon = new Image("file:src/main/java/com/creativitystudios/textmeserver/AppIcons/TextMeAppIcon.png");
                    stage.setTitle("Error!");
                    messageLabel = new Label("Error was sent at runtime");

                } else {
                    icon = new Image("file:src/main/java/com/creativitystudios/textmeserver/AppIcons/TextMeAppIcon.png");
                    stage.setTitle("Message sent from server");
                    messageLabel = new Label("Message received from server");
                }

                messageIcon.setImage(icon);
                messageIcon.setFitWidth(50);
                messageIcon.setFitHeight(50);
                messageIcon.setCache(true);
                newMessage.setMinWidth(25);
                newMessage.setMinHeight(50);

                hbox.getChildren().add(messageIcon);
                vbox.getChildren().add(messageLabel);
                vbox.getChildren().add(newMessage);
                hbox.getChildren().add(vbox);
                pane.getChildren().add(hbox);

                stage.setScene(new Scene(pane, 250, 100));
                stage.show();
            }
        });
    }

    @FXML
    public void onStartServer() {
        try {
            if(!serverNameTextBox.getText().isEmpty()) {
                serverName = serverNameTextBox.getText();
            }
            if(!serverPortTextBox.getText().isEmpty()) {
                netS.startServer(Integer.parseInt(serverPortTextBox.getText()), serverName);
            } else {
                netS.startServer(0, serverName);
            }
        } catch (NumberFormatException e) {
            throwMessage("Incorrect format for port \nPlease try again", true);
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
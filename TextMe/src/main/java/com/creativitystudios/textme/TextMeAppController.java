package com.creativitystudios.textme;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;

import java.io.File;

public class TextMeAppController {
    private static String username;

    @FXML
    private Pane mainUI;
    @FXML
    private Pane usernameSelectorPane;
    @FXML
    private Pane connectionSelectorPane;
    @FXML
    private Pane serverInformationPane;
    @FXML
    private TextField usernameTextBox;
    @FXML
    private TextField ipAddressTextBox;
    @FXML
    private TextField portTextBox;
    @FXML
    private VBox messageVBox;
    @FXML
    private TextField messageTextBox;
    @FXML
    private Label serverIdentName;
    @FXML
    private Label serverIdentDetails;

    private final TextMeClientNetManager netC = new TextMeClientNetManager(this, mainUI);


    @FXML
    private void onSetUsername() {
        if(!usernameTextBox.getText().isEmpty()) {
            username = usernameTextBox.getText();
            removeUsernameSelectorPane();
            addConnectionPane();
        }
    }
    @FXML
    private void attemptConnection() {
        String ipTemp = ipAddressTextBox.getText();
        int portTemp = Integer.parseInt(portTextBox.getText());
        if(!ipTemp.isEmpty() && !portTextBox.getText().isEmpty()) {
            if(netC.attemptConnection(ipTemp, portTemp)) {
                removeConnectionSelectorPane();
                serverIdentDetails.setText("IP: " + ipTemp + " | Port: " + portTemp);
                addServerInformationPane();
            }
        }
    }

    private void removeUsernameSelectorPane() {
        usernameSelectorPane.setVisible(false);
    }

    private void removeConnectionSelectorPane() {
        connectionSelectorPane.setVisible(false);
    }

    protected void addConnectionPane() {
        connectionSelectorPane.setVisible(true);
    }

    protected void addServerInformationPane() {
        serverInformationPane.setVisible(true);
    }

    protected void removeServerInformationPane() {
        serverInformationPane.setVisible(false);
    }

    @FXML
    protected void sendMessageToNetManager() {
        netC.sendMessageNet(username + ": " + messageTextBox.getText());
        messageTextBox.setText("");
    }

    protected void sendMessageToNetManager(String message) {
        netC.sendMessageNet(username + ": " + message);
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

    protected void throwMessage(String message, String title) {
        Platform.runLater(new Runnable() {
            public void run() {
                Label newMessage = new Label(message);
                Stage stage = new Stage();
                Pane pane = new Pane();
                VBox vbox = new VBox();
                HBox hbox = new HBox();
                ImageView IconViewer = new ImageView();
                Image icon = new Image("file:src/main/java/com/creativitystudios/textme/AppIcons/TextMeAppIcon.png");

                IconViewer.setImage(icon);
                IconViewer.setFitWidth(50);
                IconViewer.setFitHeight(50);
                IconViewer.setCache(true);
                stage.setTitle(title);
                newMessage.setMinWidth(25);
                newMessage.setMinHeight(50);

                hbox.getChildren().add(IconViewer);
                vbox.getChildren().add(newMessage);
                hbox.getChildren().add(vbox);
                pane.getChildren().add(hbox);

                stage.setScene(new Scene(pane, 250, 100));
                stage.show();
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

    protected void throwError(String message) {
        Platform.runLater(new Runnable() {
            public void run() {
                Label errorMessage = new Label(message);
                Stage stage = new Stage();
                Pane pane = new Pane();
                VBox vbox = new VBox();
                HBox hbox = new HBox();
                ImageView errorImage = new ImageView();
                Image icon = new Image("file:src/main/java/com/creativitystudios/textme/AppIcons/TextMeAppIcon.png");

                errorImage.setImage(icon);
                errorImage.setFitWidth(50);
                errorImage.setFitHeight(50);
                errorImage.setCache(true);
                stage.setTitle("Error!");
                errorMessage.setMinWidth(25);
                errorMessage.setMinHeight(50);

                hbox.getChildren().add(errorImage);
                vbox.getChildren().add(new Label("Error was sent at runtime"));
                vbox.getChildren().add(errorMessage);
                hbox.getChildren().add(vbox);
                pane.getChildren().add(hbox);

                stage.setScene(new Scene(pane, 250, 100));
                stage.show();
            }
        });
    }

    protected void resetForReconnection() {
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
    protected void disconnectFromServer() {
        sendMessageToNetManager("usr/msg_quit;");
        resetForReconnection();
    }

}
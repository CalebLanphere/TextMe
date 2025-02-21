package com.creativitystudios.textmewebtranslator;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TextMeWebTranslatorController {
    @FXML Pane mainUI;
    @FXML Label portLabel;
    @FXML Label ipLabel;
    private Stage mainStage;
    protected TextMeWebTranslatorNetworkManager netManager = new TextMeWebTranslatorNetworkManager(mainUI, this);

    public TextMeWebTranslatorNetworkManager getNetworkManager() {
        return netManager;
    }

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
                    messageStage.setTitle("Error | TextMe Web Translator");
                    Image icon = new Image("file:src/main/java/com/creativitystudios/textmewebtranslator/AppIcons/TextMeAppError.png");
                    IconViewer.setImage(icon); // Sets the image viewer to show the image created above
                } else {
                    messageStage.setTitle("Message | TextMe Web Translator");
                    Image icon = new Image("file:src/main/java/com/creativitystudios/textmewebtranslator/AppIcons/TextMeAppMessage.png");
                    IconViewer.setImage(icon); // Sets the image viewer to show the image created above
                }
                messageStage.setResizable(false); // Makes window not resizable
                // Creates a popup with the associated size
                messageStage.setScene(new Scene(pane, 300, 115));
                // Sets the theme for the popup to follow
                //changeTheme(themeSelectorDropdown.getItems().get(selectedTheme).toString(), messageStage);
                messageStage.show(); // Shows popup
            }
        });
    }

    /**
     * Sets mainStage to the supplied stage
     * @param stage owning stage
     */
    public void setStageReference(Stage stage) {
        mainStage = stage;
    }

    @FXML public void onServerStart() {
        netManager.startServer(1200, "");

    }

    public void setConnectionInfo(String ip, int port) {
        Platform.runLater(new Runnable() {
            public void run() {
                ipLabel.setText("IP: " + ip); // Sets the server's IP
                portLabel.setText("Port: " + port); // Sets the server's Port
            }
        });
    }
}

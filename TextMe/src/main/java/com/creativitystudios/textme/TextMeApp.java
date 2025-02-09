/**
 * @author Caleb Lanphere
 *
 * TextMe Application Client
 *
 * Copyright 2025 | Caleb Lanphere | All Rights Reserved
 *
 */

package com.creativitystudios.textme;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;

public class TextMeApp extends Application {
    protected FXMLLoader fxmlLoader; // Loader used for loading TextMeAppLayout.fxml
    TextMeAppController appController; // Application controller reference
    // Creates the EventHandler that is used for closing the app
    EventHandler<WindowEvent> closeEvent = new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
            windowEvent.consume(); // Negate the input received
            closeApp();
        }
    };

    public static void main(String[] args) {
        launch(); // Start application
    }

    /**
     * Loads the AppLayout and creates the window
     * @param stage Stage window to create
     * @throws IOException IOException error to throw if function fails
     */
    @Override
    public void start(Stage stage) throws IOException {
        fxmlLoader = new FXMLLoader(TextMeApp.class.getResource("TextMeAppLayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("TextMe"); // Application Title
        stage.setScene(scene); // Sets the window to what the AppLayout.fxml dictates
        stage.setResizable(false); // Stops the window from being resized
        stage.getScene().getStylesheets().add(getClass().getResource("TextMeThemeLight.css").toExternalForm());
        stage.show(); // Shows the newly created window
        stage.setOnCloseRequest(closeEvent); // Initializes the close command to a function
        // Sends the reference of the controller to the controller
        appController = fxmlLoader.getController();
        // Disables the message box until the user is connected to a server
        appController.isConnectedToServer(false);
        // Initializes the Theme Selector
        appController.setupThemeSelectorDropdown();
        // Sends a reference of the stage that owns the controller to the controller
        appController.setStageReference(stage);
    }

    /**
     * Cloes the app and sends a message to the server if client is connected to one
     */
    public void closeApp() {
        if(appController.netC.isConnected()) { // If network manager is connected, send message to quit
            appController.sendMessageToNetManager("usr/msg_quit;"); // Sends a
        }
        System.exit(0); // Quits the application
    }

}

/**
 * @author Caleb Lanphere
 *
 * TextMe Application Server
 *
 * Copyright 2025 | Caleb Lanphere | All Rights Reserved
 *
 */

package com.creativitystudios.textmeserver;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class TextMeServerApplication extends Application {
    private TextMeServerController appController;
    EventHandler<WindowEvent> closeEvent = new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
            windowEvent.consume(); // Negates WindowEvent
            closeApp(); // Closes the app
        }
    };

    public static void main(String[] args) {
        launch();
    }

    /**
     * Creates the app and shows it to the user
     * @param stage stage to add AppLayout onto and show to the user
     * @throws IOException exception to throw if function fails
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TextMeServerApplication.class.getResource("TextMeServerLayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("TextMe Server"); // App title
        stage.setScene(scene);
        // Adds the stylesheet for the app to use
        stage.getScene().getStylesheets().add(getClass().getResource("TextMeServerThemeLight.css").toExternalForm());
        stage.setResizable(false); // Disables resizing of the window
        stage.show(); // Shows the app to the user
        stage.setOnCloseRequest(closeEvent); // Sets what to do upon closing
        appController = fxmlLoader.getController(); // Gives access to controller
        appController.setStageReference(stage); // Gives the controller the owning window
        appController.setKICK_EVENT_HANDLER(); // Sets up the kick event handler
        appController.setWARN_EVENT_HANDLER(); // Sets up the warn event handler
        appController.setupThemeSelectorDropdown(); // Sets up the theme dropdown

    }

    /**
     * Closes the app
     */
    public void closeApp() {
        appController.netS.closeServer(); // Tells network manager to close the server
        System.exit(0); // Closes the app
    }
}
package com.creativitystudios.textmewebtranslator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class TextMeWebTranslatorApplication extends Application {
    private TextMeWebTranslatorController appController;
    EventHandler<WindowEvent> closeEvent = new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
            windowEvent.consume(); // Negates WindowEvent
            closeApp(); // Closes the app
        }
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TextMeWebTranslatorApplication.class.getResource("TextMeWebTranslatorLayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 250);
        stage.setTitle("TextMe Server Web Translator"); // App title
        stage.setScene(scene);
        // Adds the stylesheet for the app to use
        //stage.getScene().getStylesheets().add(getClass().getResource("TextMeServerThemeLight.css").toExternalForm());
        stage.setResizable(false); // Disables resizing of the window
        stage.show(); // Shows the app to the user
        stage.setOnCloseRequest(closeEvent); // Sets what to do upon closing
        appController = fxmlLoader.getController(); // Gives access to controller
        appController.setStageReference(stage);
    }

    /**
     * Closes the app
     */
    public void closeApp() {
        System.exit(0); // Closes the app
    }

    public TextMeWebTranslatorController getWebTranslatorController() {
        return appController;
    }
}

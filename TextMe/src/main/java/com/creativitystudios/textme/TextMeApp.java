package com.creativitystudios.textme;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class TextMeApp extends Application {
    protected FXMLLoader fxmlLoader;
    TextMeAppController appController;

    @Override
    public void start(Stage stage) throws IOException {
        fxmlLoader = new FXMLLoader(TextMeApp.class.getResource("TextMeAppLayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("TextMe");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getScene().getStylesheets().add(getClass().getResource("TextMeThemeLight.css").toExternalForm());
        stage.show();
        stage.setOnCloseRequest(closeEvent);
        appController = fxmlLoader.getController();
        appController.isConnectedToServer(false);
        appController.setupThemeSelectorDropdown();
        appController.sendStageReference(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    public void closeApp() {
        appController.sendMessageToNetManager("usr/msg_quit;");
        System.exit(0);
    }

    EventHandler closeEvent = new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
            windowEvent.consume();
            closeApp();
        }
    };

}

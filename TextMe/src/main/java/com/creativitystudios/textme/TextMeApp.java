package com.creativitystudios.textme;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class TextMeApp extends Application {
    protected FXMLLoader fxmlLoader;
    TextMeAppController appController;

    @Override
    public void start(Stage stage) throws IOException {
        fxmlLoader = new FXMLLoader(TextMeApp.class.getResource("TextMeAppLayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 500);
        stage.setTitle("TextNow");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(closeEvent);
        appController = fxmlLoader.getController();
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

package com.creativitystudios.textmeserver;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class TextMeServerApplication extends Application {
    private TextMeServerController appController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TextMeServerApplication.class.getResource("TextMeServerLayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("TextMe Server");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(closeEvent);
        appController = fxmlLoader.getController();
    }

    public static void main(String[] args) {
        launch();
    }

    public void closeApp() {
        appController.netS.closeServer();
        System.exit(0);
    }

    EventHandler closeEvent = new EventHandler<WindowEvent>() {
        public void handle(WindowEvent windowEvent) {
            windowEvent.consume();
            closeApp();
        }
    };
}
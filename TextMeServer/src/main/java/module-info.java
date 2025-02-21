module com.creativitystudios {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.httpserver;
    requires javax.websocket.api;
    requires java.logging;
    requires tyrus.server;

    opens com.creativitystudios.textmeserver to javafx.fxml;
    exports com.creativitystudios.textmeserver;
}
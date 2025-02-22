module com.creativitystudios {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.httpserver;
    requires java.logging;
    requires org.java_websocket;

    opens com.creativitystudios.textmeserver to javafx.fxml;
    exports com.creativitystudios.textmeserver;
    exports com.creativitystudios.textmeserver.UserClasses;
    opens com.creativitystudios.textmeserver.UserClasses to javafx.fxml;
}
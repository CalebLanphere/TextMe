module com.creativitystudios.textmewebtranslator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires jdk.httpserver;
    requires java.naming;

    opens com.creativitystudios.textmewebtranslator to javafx.fxml;
    exports com.creativitystudios.textmewebtranslator;
}
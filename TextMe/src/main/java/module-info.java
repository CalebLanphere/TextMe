module com.creativitystudios.textme {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.creativitystudios.textme to javafx.fxml;
    exports com.creativitystudios.textme;
}
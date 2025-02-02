module com.creativitystudios.textmeserver {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.creativitystudios.textmeserver to javafx.fxml;
    exports com.creativitystudios.textmeserver;
}
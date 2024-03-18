module com.example.dictionaryapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.almasb.fxgl.all;
    requires javafx.web;
    requires voicerss.tts;
    requires javafx.media;

    opens com.example.dictionaryapp to javafx.fxml;
    exports com.example.dictionaryapp;
}
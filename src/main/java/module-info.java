module com.zenconf.zentecconfigurator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;

    opens com.zenconf.zentecconfigurator to javafx.fxml;
    opens com.zenconf.zentecconfigurator.models to com.fasterxml.jackson.databind;
    exports com.zenconf.zentecconfigurator;
    exports com.zenconf.zentecconfigurator.models;
}
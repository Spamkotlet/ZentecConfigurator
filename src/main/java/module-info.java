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
    requires json.simple;
    requires jlibmodbus;
    requires jssc;

    opens com.zenconf.zentecconfigurator to javafx.fxml;
    opens com.zenconf.zentecconfigurator.models to com.fasterxml.jackson.databind;
    exports com.zenconf.zentecconfigurator;
    exports com.zenconf.zentecconfigurator.models;
    exports com.zenconf.zentecconfigurator.controllers;
    opens com.zenconf.zentecconfigurator.controllers to javafx.fxml;
    exports com.zenconf.zentecconfigurator.controllers.testing;
    opens com.zenconf.zentecconfigurator.controllers.testing to javafx.fxml;
    exports com.zenconf.zentecconfigurator.models.nodes;
    opens com.zenconf.zentecconfigurator.models.nodes to com.fasterxml.jackson.databind;
    exports com.zenconf.zentecconfigurator.models.modbus;
}
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
    requires org.apache.logging.log4j;
    requires commons.net;

    opens com.zenconf.zentecconfigurator to javafx.fxml;
    opens com.zenconf.zentecconfigurator.models to com.fasterxml.jackson.databind;
    exports com.zenconf.zentecconfigurator;
    exports com.zenconf.zentecconfigurator.models;
    exports com.zenconf.zentecconfigurator.controllers;
    opens com.zenconf.zentecconfigurator.controllers to javafx.fxml;
    exports com.zenconf.zentecconfigurator.models.nodes;
    opens com.zenconf.zentecconfigurator.models.nodes to com.fasterxml.jackson.databind;
    exports com.zenconf.zentecconfigurator.models.modbus;
    exports com.zenconf.zentecconfigurator.utils.modbus;
    exports com.zenconf.zentecconfigurator.models.enums;
    exports com.zenconf.zentecconfigurator.models.z031;
    opens com.zenconf.zentecconfigurator.models.enums to com.fasterxml.jackson.databind;
    exports com.zenconf.zentecconfigurator.controllers.configurator;
    opens com.zenconf.zentecconfigurator.controllers.configurator to javafx.fxml;
    exports com.zenconf.zentecconfigurator.models.elements;
    opens com.zenconf.zentecconfigurator.models.elements to com.fasterxml.jackson.databind;
}
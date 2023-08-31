package com.zenconf.zentecconfigurator.models.nodes.mainview;

import com.zenconf.zentecconfigurator.controllers.HomePageController;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.HashMap;
import java.util.Map;

public class HeaderConfiguratorButton extends Button {

    private Map<String, Node> panels = new HashMap<>();

    public HeaderConfiguratorButton() {
        super("Конфигуратор");
        super.getStyleClass().add("button-main-header");
        super.setFocused(true);
        panels = HomePageController.panels;
        super.setOnAction(e -> {
            onClick("Конфигуратор");
        });
    }

    private void onClick(String panelName) {
        Node node = panels.get(panelName);
        if (node != null) {
            showNode(node);
        }
    }

    private void showNode(Node node) {
        HomePageController.mainViewVBox1.getChildren().clear();
        HomePageController.mainViewVBox1.getChildren().add(node);
    }
}

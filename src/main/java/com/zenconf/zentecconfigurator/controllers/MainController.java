package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.Application;
import com.zenconf.zentecconfigurator.models.MainParameters;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController extends CommonController implements Initializable {

    private final Map<Button, Node> panels = new HashMap<>();
    @FXML
    public Button goToHomeButton;
    @FXML
    public Button goToHelpButton;
    @FXML
    public Button goToSettingsButton;
    @FXML
    public Button goToJournalButton;
    @FXML
    public AnchorPane mainAnchorPane;
    public static MainParameters mainParameters;
    public static List<String> alarms0;
    public static List<String> alarms1;
    public static List<String> warnings;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (!goToHomeButton.getStyleClass().contains("button-main-header-active")) {
            goToHomeButton.getStyleClass().remove("button-main-header");
            goToHomeButton.getStyleClass().add("button-main-header-active");
        }

        Node node;
        try {
            node = createNewNode("home-page-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        showNode(node);

        goToHomeButton.setOnAction(this::onClickHomeButton);
        goToHelpButton.setOnAction(this::onClickHelpButton);
        goToSettingsButton.setOnAction(this::onClickSettingsButton);
        goToJournalButton.setOnAction(this::onClickJournalButton);

        mainParameters = getMainParametersFromJson();
        getAlarmsFromJson();
    }

    private void onClickHomeButton(ActionEvent actionEvent) {
        if (!goToHelpButton.getStyleClass().contains("button-main-header")) {
            goToHelpButton.getStyleClass().add("button-main-header");
            goToHelpButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToJournalButton.getStyleClass().contains("button-main-header")) {
            goToJournalButton.getStyleClass().add("button-main-header");
            goToJournalButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToSettingsButton.getStyleClass().contains("button-main-header")) {
            goToSettingsButton.getStyleClass().add("button-main-header");
            goToSettingsButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToHomeButton.getStyleClass().contains("button-main-header-active")) {
            goToHomeButton.getStyleClass().remove("button-main-header");
            goToHomeButton.getStyleClass().add("button-main-header-active");
        }

        Button button = (Button) actionEvent.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("home-page-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    private void onClickJournalButton(ActionEvent actionEvent) {
        if (!goToHomeButton.getStyleClass().contains("button-main-header")) {
            goToHomeButton.getStyleClass().add("button-main-header");
            goToHomeButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToHelpButton.getStyleClass().contains("button-main-header")) {
            goToHelpButton.getStyleClass().add("button-main-header");
            goToHelpButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToSettingsButton.getStyleClass().contains("button-main-header")) {
            goToSettingsButton.getStyleClass().add("button-main-header");
            goToSettingsButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToJournalButton.getStyleClass().contains("button-main-header-active")) {
            goToJournalButton.getStyleClass().remove("button-main-header");
            goToJournalButton.getStyleClass().add("button-main-header-active");
        }

        Button button = (Button) actionEvent.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("journal.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    private void onClickHelpButton(ActionEvent actionEvent) {
        if (!goToHomeButton.getStyleClass().contains("button-main-header")) {
            goToHomeButton.getStyleClass().add("button-main-header");
            goToHomeButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToJournalButton.getStyleClass().contains("button-main-header")) {
            goToJournalButton.getStyleClass().add("button-main-header");
            goToJournalButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToSettingsButton.getStyleClass().contains("button-main-header")) {
            goToSettingsButton.getStyleClass().add("button-main-header");
            goToSettingsButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToHelpButton.getStyleClass().contains("button-main-header-active")) {
            goToHelpButton.getStyleClass().remove("button-main-header");
            goToHelpButton.getStyleClass().add("button-main-header-active");
        }

        Button button = (Button) actionEvent.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("help-page-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    private void onClickSettingsButton(ActionEvent actionEvent) {
        if (!goToHomeButton.getStyleClass().contains("button-main-header")) {
            goToHomeButton.getStyleClass().add("button-main-header");
            goToHomeButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToJournalButton.getStyleClass().contains("button-main-header")) {
            goToJournalButton.getStyleClass().add("button-main-header");
            goToJournalButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToHelpButton.getStyleClass().contains("button-main-header")) {
            goToHelpButton.getStyleClass().add("button-main-header");
            goToHelpButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToSettingsButton.getStyleClass().contains("button-main-header-active")) {
            goToSettingsButton.getStyleClass().remove("button-main-header");
            goToSettingsButton.getStyleClass().add("button-main-header-active");
        }

        Button button = (Button) actionEvent.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("settings.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    private Node createNewNode(String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource(resourcePath));
        return loader.load();
    }

    private void showNode(Node node) {
        mainAnchorPane.getChildren().clear();
        mainAnchorPane.getChildren().add(node);
    }

    private MainParameters getMainParametersFromJson() {
        String file = "src/main_parameters.json";

        MainParameters mainParameters;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            mainParameters = mapper.readValue(jsonObject.get("mainParameters").toString(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mainParameters;
    }

    private void getAlarmsFromJson() {
        String file = "src/alarms_list.json";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            alarms0 = mapper.readValue(jsonObject.get("alarms0").toString(), new TypeReference<>() {
            });
            alarms1 = mapper.readValue(jsonObject.get("alarms1").toString(), new TypeReference<>() {
            });
            warnings = mapper.readValue(jsonObject.get("warnings").toString(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
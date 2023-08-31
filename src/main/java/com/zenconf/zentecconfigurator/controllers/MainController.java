package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.Application;
import com.zenconf.zentecconfigurator.models.MainParameters;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

    private final Map<String, Node> panels = new HashMap<>();
    @FXML
    public Button goToHomeButton;
    @FXML
    public Button goToHelpButton;
    @FXML
    public Button goToSettingsButton;
    @FXML
    public AnchorPane mainAnchorPane;
    @FXML
    public HBox leftHeaderButtonsHBox;
    public static HBox leftHeaderButtonsHBox1;
    public static MainParameters mainParameters;
    public static List<String> alarms0;
    public static List<String> alarms1;
    public static List<String> warnings;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        leftHeaderButtonsHBox1 = leftHeaderButtonsHBox;

        onClickButton("Домашняя", "home-page-view.fxml");

        goToHomeButton.setOnAction(e -> {
            onClickButton("Домашняя", "home-page-view.fxml");
        });
        goToHelpButton.setOnAction(e -> onClickButton("Справка", "help-page-view.fxml"));
        goToSettingsButton.setOnAction(e -> onClickButton("Настройки", "settings.fxml"));

        mainParameters = getMainParametersFromJson();
        getAlarmsFromJson();
    }

    private void onClickButton(String panelName, String resourcePath) {
        Node node = panels.get(panelName);
        if (node == null) {
            try {
                node = createNewNode(resourcePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(panelName, node);
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
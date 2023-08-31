package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.Application;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.MainParameters;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.Sensor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    public static List<Actuator> actuatorList;
    public static List<Sensor> sensorList;
    public static List<Scheme> schemes;
    private static final Logger logger = LogManager.getLogger(MainController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        leftHeaderButtonsHBox1 = leftHeaderButtonsHBox;

        onClickButton("Домашняя", "home-page-view.fxml");

        goToHomeButton.setOnAction(e -> {
            onClickButton("Домашняя", "home-page-view.fxml");
        });
        goToHelpButton.setOnAction(e -> onClickButton("Справка", "help-page-view.fxml"));
        goToSettingsButton.setOnAction(e -> onClickButton("Настройки", "settings.fxml"));

        getMainParametersFromJson();
        getAlarmsFromJson();
        getActuatorsFromJson();
        getSensorsFromJson();
        getSchemesFromJson();
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

    private void getMainParametersFromJson() {
        Thread thread;
        Runnable task = () -> {
            File dir1 = new File("");
            String file = null;
            try {
                file = dir1.getCanonicalPath() + "\\main_parameters.json";
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Файл отсутствует");
                    alert.setContentText(e.getMessage());
                    alert.show();
                });
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8))) {
                JSONParser parser = new JSONParser();
                ObjectMapper mapper = new ObjectMapper();
                Object obj = parser.parse(reader);
                JSONObject jsonObject = (JSONObject) obj;
                mainParameters = mapper.readValue(jsonObject.get("mainParameters").toString(), new TypeReference<MainParameters>() {
                });

            } catch (Exception e) {
                logger.error(e.getMessage());
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Файл отсутствует");
                    alert.setContentText(e.getMessage());
                    alert.show();
                });
                throw new RuntimeException(e);
            }
        };
        thread = new Thread(task);
        thread.start();
    }

    private void getAlarmsFromJson() {
        Thread thread;
        Runnable task = () -> {
            File dir1 = new File("");
            String file = null;
            try {
                file = dir1.getCanonicalPath() + "\\alarms_list.json";
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Файл отсутствует");
                    alert.setContentText(e.getMessage());
                    alert.show();
                });
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8))) {
                JSONParser parser = new JSONParser();
                ObjectMapper mapper = new ObjectMapper();
                Object obj = parser.parse(reader);
                JSONObject jsonObject = (JSONObject) obj;
                alarms0 = mapper.readValue(jsonObject.get("alarms0").toString(), new TypeReference<List<String>>() {
                });
                alarms1 = mapper.readValue(jsonObject.get("alarms1").toString(), new TypeReference<List<String>>() {
                });
                warnings = mapper.readValue(jsonObject.get("warnings").toString(), new TypeReference<List<String>>() {
                });

            } catch (Exception e) {
                logger.error(e.getMessage());
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Файл отсутствует");
                    alert.setContentText(e.getMessage());
                    alert.show();
                });
                throw new RuntimeException(e);
            }
        };
        thread = new Thread(task);
        thread.start();
    }

    private void getActuatorsFromJson() {
        Thread thread;
        Runnable task = () -> {
            File dir1 = new File("");
            String file = null;
            try {
                file = dir1.getCanonicalPath() + "\\actuators_attributes.json";
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Файл отсутствует");
                    alert.setContentText(e.getMessage());
                    alert.show();
                });
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8))) {
                JSONParser parser = new JSONParser();
                ObjectMapper mapper = new ObjectMapper();
                Object obj = parser.parse(reader);
                JSONObject jsonObject = (JSONObject) obj;
                actuatorList = mapper.readValue(jsonObject.get("actuators").toString(), new TypeReference<List<Actuator>>() {
                });

            } catch (Exception e) {
                logger.error(e.getMessage());
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Файл отсутствует");
                    alert.setContentText(e.getMessage());
                    alert.show();
                });
                throw new RuntimeException(e);
            }
        };
        thread = new Thread(task);
        thread.start();
    }

    private void getSensorsFromJson() {
        Thread thread;
        Runnable task = () -> {
            File dir1 = new File("");
            String file = null;
            try {
                file = dir1.getCanonicalPath() + "\\sensors_attributes.json";
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Файл отсутствует");
                    alert.setContentText(e.getMessage());
                    alert.show();
                });
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8))) {
                JSONParser parser = new JSONParser();
                ObjectMapper mapper = new ObjectMapper();
                Object obj = parser.parse(reader);
                JSONObject jsonObject = (JSONObject) obj;
                sensorList = mapper.readValue(jsonObject.get("sensors").toString(), new TypeReference<List<Sensor>>() {
                });

            } catch (Exception e) {
                logger.error(e.getMessage());
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Файл отсутствует");
                    alert.setContentText(e.getMessage());
                    alert.show();
                });
                throw new RuntimeException(e);
            }
        };
        thread = new Thread(task);
        thread.start();
    }

    // Чтение файла со схемами schemes.json и сохранение схем
    private void getSchemesFromJson() {
        Thread thread;
        Runnable task = () -> {
            File dir1 = new File("");
            String file = null;
            try {
                file = dir1.getCanonicalPath() + "\\schemes.json";
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Файл отсутствует");
                    alert.setContentText(e.getMessage());
                    alert.show();
                });
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            StandardCharsets.UTF_8))) {
                JSONParser parser = new JSONParser();
                ObjectMapper mapper = new ObjectMapper();
                Object obj = parser.parse(reader);
                JSONObject jsonObject = (JSONObject) obj;
                schemes = mapper.readValue(jsonObject.get("schemes").toString(), new TypeReference<List<Scheme>>() {
                });

            } catch (Exception e) {
                logger.error(e.getMessage());
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Файл отсутствует");
                    alert.setContentText(e.getMessage());
                    alert.show();
                });
                throw new RuntimeException(e);
            }
        };
        thread = new Thread(task);
        thread.start();
    }
}
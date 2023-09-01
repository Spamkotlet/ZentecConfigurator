package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.Application;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.MainParameters;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.z031.ElectricParameters;
import com.zenconf.zentecconfigurator.models.z031.WaterParameters;
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
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public static ElectricParameters electricParameters;
    public static WaterParameters waterParameters;
    private static final Logger logger = LogManager.getLogger(MainController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        leftHeaderButtonsHBox1 = leftHeaderButtonsHBox;

        onClickButton("Домашняя", "home-page-view.fxml");

        goToHomeButton.setOnAction(e -> onClickButton("Домашняя", "home-page-view.fxml"));
        goToHelpButton.setOnAction(e -> onClickButton("Справка", "help-page-view.fxml"));
        goToSettingsButton.setOnAction(e -> onClickButton("Настройки", "settings.fxml"));

        Thread thread;
        Runnable task = () -> {
            getMainParametersFromJson();
            getAlarmsFromJson();
            getActuatorsFromJson();
            getSensorsFromJson();
            getSchemesFromJson();
            getZ031ParametersFromJson();
        };
        thread = new Thread(task);
        thread.start();
    }

    private void onClickButton(String panelName, String resourcePath) {
        Node node = panels.get(panelName);
        if (node == null) {
            try {
                node = createNewNode(resourcePath);
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
            panels.put(panelName, node);
        }
        logger.info("Открыть окно <" + panelName + ">");
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
        File dir1 = new File("");
        String file;
        try {
            file = dir1.getCanonicalPath() + "\\settings\\main_parameters.json";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Files.newInputStream(Paths.get(file)),
                        StandardCharsets.UTF_8))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            mainParameters = mapper.readValue(jsonObject.get("mainParameters").toString(), new TypeReference<>() {
            });

            logger.info("Файл " + file + " (Найден и загружен)");
        } catch (Exception e) {
            logger.error(e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Файл отсутствует");
                alert.setContentText(e.getMessage());
                alert.setOnCloseRequest(ex -> Platform.exit());
                alert.show();
            });
            throw new RuntimeException(e);
        }
    }

    private void getAlarmsFromJson() {
        File dir1 = new File("");
        String file;
        try {
            file = dir1.getCanonicalPath() + "\\settings\\alarms_list.json";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Files.newInputStream(Paths.get(file)),
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

            logger.info("Файл " + file + " (Найден и загружен)");
        } catch (Exception e) {
            logger.error(e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Файл отсутствует");
                alert.setContentText(e.getMessage());
                alert.setOnCloseRequest(ex -> Platform.exit());
                alert.show();
            });
            throw new RuntimeException(e);
        }
    }

    private void getActuatorsFromJson() {
        File dir1 = new File("");
        String file;
        try {
            file = dir1.getCanonicalPath() + "\\settings\\actuators_attributes.json";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Files.newInputStream(Paths.get(file)),
                        StandardCharsets.UTF_8))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            actuatorList = mapper.readValue(jsonObject.get("actuators").toString(), new TypeReference<>() {
            });

            logger.info("Файл " + file + " (Найден и загружен)");
        } catch (Exception e) {
            logger.error(e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Файл отсутствует");
                alert.setContentText(e.getMessage());
                alert.setOnCloseRequest(ex -> Platform.exit());
                alert.show();
            });
            throw new RuntimeException(e);
        }
    }

    private void getSensorsFromJson() {
        File dir1 = new File("");
        String file;
        try {
            file = dir1.getCanonicalPath() + "\\settings\\sensors_attributes.json";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Files.newInputStream(Paths.get(file)),
                        StandardCharsets.UTF_8))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            sensorList = mapper.readValue(jsonObject.get("sensors").toString(), new TypeReference<>() {
            });

            logger.info("Файл " + file + " (Найден и загружен)");
        } catch (Exception e) {
            logger.error(e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Файл отсутствует");
                alert.setContentText(e.getMessage());
                alert.setOnCloseRequest(ex -> Platform.exit());
                alert.show();
            });
            throw new RuntimeException(e);
        }
    }

    // Чтение файла со схемами schemes.json и сохранение схем
    private void getSchemesFromJson() {
        File dir1 = new File("");
        String file;
        try {
            file = dir1.getCanonicalPath() + "\\settings\\schemes.json";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Files.newInputStream(Paths.get(file)),
                        StandardCharsets.UTF_8))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            schemes = mapper.readValue(jsonObject.get("schemes").toString(), new TypeReference<>() {
            });

            logger.info("Файл " + file + " (Найден и загружен)");
        } catch (Exception e) {
            logger.error(e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Файл отсутствует");
                alert.setContentText(e.getMessage());
                alert.setOnCloseRequest(ex -> Platform.exit());
                alert.show();
            });
            throw new RuntimeException(e);
        }
    }

    private void getZ031ParametersFromJson() {
        File dir1 = new File("");
        String file;
        try {
            file = dir1.getCanonicalPath() + "\\settings\\z031_parameters.json";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Files.newInputStream(Paths.get(file)),
                        StandardCharsets.UTF_8))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            electricParameters = mapper.readValue(jsonObject.get("electric").toString(), new TypeReference<>() {
            });
            waterParameters = mapper.readValue(jsonObject.get("water").toString(), new TypeReference<>() {
            });

            logger.info("Файл " + file + " (Найден и загружен)");
        } catch (Exception e) {
            logger.error(e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Файл отсутствует");
                alert.setContentText(e.getMessage());
                alert.setOnCloseRequest(ex -> Platform.exit());
                alert.show();
            });
            throw new RuntimeException(e);
        }
    }
}
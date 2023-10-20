package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.models.elements.Actuator;
import com.zenconf.zentecconfigurator.models.MainParameters;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.elements.Sensor;
import com.zenconf.zentecconfigurator.models.z031.ElectricParameters;
import com.zenconf.zentecconfigurator.models.z031.WaterParameters;
import com.zenconf.zentecconfigurator.updater.FtpClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainController extends CommonController implements Initializable {

    @FXML
    public Button goToHomeButton;
//    @FXML
//    public Button goToHelpButton;
    @FXML
    public Button goToSettingsButton;
    @FXML
    public AnchorPane mainControllerAnchorPane;
    @FXML
    public Label leftStatusLabel;
    public static AnchorPane mainAnchorPane1;
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

    public static final Map<String, Node> panels = new HashMap<>();
    public static Node activePanel = null;
    public static Stage primaryStage;
    public static HashMap<String, String> ftpServerProperties = new HashMap<>();
    public static HashMap<String, String> configuratorProperties = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        leftHeaderButtonsHBox1 = leftHeaderButtonsHBox;
        mainAnchorPane1 = mainControllerAnchorPane;
        initializeProperties();

        onCreateChildNode(mainControllerAnchorPane, "Домашняя", "home-page-view.fxml");

        goToHomeButton.setOnAction(e -> onCreateChildNode(mainControllerAnchorPane, "Домашняя", "home-page-view.fxml"));
//        goToHelpButton.setOnAction(e -> onCreateChildNode(mainControllerAnchorPane, "Справка", "help-page-view.fxml"));
        goToSettingsButton.setOnAction(e -> onCreateChildNode(mainControllerAnchorPane, "Настройки", "settings.fxml"));

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

        FtpClient ftpClient = new FtpClient();
        Task<Boolean> updateApplicationTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                boolean checkUpdates = ftpClient.checkUpdates();
                logger.info("UPDATE IS AVAILABLE: " + checkUpdates);
                return checkUpdates;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (super.getValue()) {
                    Dialog<ButtonType> dialog = new Dialog<>();
                    dialog.setTitle("Обновление");
                    dialog.setHeaderText("Найдена более новая версия приложения");
                    dialog.setContentText("Обновляем?");
                    dialog.getDialogPane().getButtonTypes().addAll(
                            new ButtonType("Да", ButtonBar.ButtonData.OK_DONE),
                            new ButtonType("Нет", ButtonBar.ButtonData.CANCEL_CLOSE)
                    );

                    Optional<ButtonType> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        if (result.orElseThrow().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                            try {
                                Runtime.getRuntime().exec("cmd /c start updater.jar");
                            } catch (IOException ex) {
                                logger.error(ex.getMessage(), ex);
                                ex.printStackTrace();
                                throw new RuntimeException(ex);
                            }
                            Platform.exit();
                        }
                    }
                }
            }
        };
        Thread updateApplicationThread = new Thread(updateApplicationTask);
        updateApplicationThread.start();
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
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
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

    private void initializeProperties() {
        Properties _FTPServerProperties = new Properties();
        try (FileInputStream fis = new FileInputStream("settings/properties/FTPServer.properties")) {
            logger.info("Инициализация FTPServer.properties");
            _FTPServerProperties.load(fis);
            Map map = _FTPServerProperties;
            Map<String, String> map1 = (Map<String, String>) map;
            ftpServerProperties = new HashMap<>(map1);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        Properties _ConfiguratorProperties = new Properties();
        try (FileInputStream fis = new FileInputStream("settings/properties/Configurator.properties")) {
            logger.info("Инициализация Configurator.properties");
            _ConfiguratorProperties.load(fis);
            Map map = _ConfiguratorProperties;
            Map<String, String> map1 = (Map<String, String>) map;
            configuratorProperties = new HashMap<>(map1);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
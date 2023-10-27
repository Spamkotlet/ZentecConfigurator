package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.models.elements.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.elements.Sensor;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class ConfiguratorController extends CommonController implements Initializable {

    @FXML
    public ImageView schemeImageView;
    @FXML
    public ImageView sensorsImageView;
    @FXML
    public ImageView actuatorsImageView;
    @FXML
    public ImageView peripheryImageView;
    @FXML
    public ImageView ioImageView;

    @FXML
    public BorderPane schemeBorderPane;
    @FXML
    public BorderPane sensorsBorderPane;
    @FXML
    public BorderPane actuatorsBorderPane;
    @FXML
    public BorderPane peripheryBorderPane;
    @FXML
    public BorderPane ioBorderPane;

    @FXML
    public AnchorPane splitPaneRight;
    @FXML
    public SplitPane mainSplitPane;
    @FXML
    public VBox disabledButtonsVBox;

    @FXML
    public Button resetParametersToDefaultButton;

    public static Sensor[] sensorsList;
    public static Actuator[] actuatorsList;
    public static List<Sensor> sensorsUsed = new ArrayList<>();
    public static List<Actuator> actuatorsUsed = new ArrayList<>();

    public static List<Attribute> attributesForResetToDefault = new ArrayList<>();

    public static Task<Void> resetAttributesToDefaultValuesTask = null;
    public static ResetAttributesService resetAttributesService = new ResetAttributesService();

//    static BooleanProperty isEnabled = new SimpleBooleanProperty();

    public static class ResetAttributesService extends Service<Boolean> {

        @Override
        protected Task<Boolean> createTask() {
            return new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    logger.info("Задача resetAttributesTask в ResetAtttributesSetvice начата");
                    Boolean result = Boolean.FALSE;
                    if (attributesForResetToDefault.isEmpty()) {
                        logger.info("ConfiguratorController.resetAttributesTask: восстановление параметров не требуется");
                        return Boolean.TRUE;
                    }

                    Platform.runLater(() -> showLoadWindow(this));

                    boolean isSuccessfulAction = true;
                    int successfulActionAttempt = 0;
                    Attribute attribute = null;
                    int attributesListSize = attributesForResetToDefault.size();
                    int attributesCounter = 1;
                    for (int i = attributesForResetToDefault.size() - 1; i >= 0; ) {
                        if (isSuccessfulAction) {
                            updateMessage("Загрузка...: " + attributesCounter + "/" + attributesListSize);
                            updateProgress(attributesCounter, attributesListSize);
                            attribute = attributesForResetToDefault.get(i);
                        }

                        try {
                            attribute.writeModbusDefaultValue(attribute.getDefaultValue());
                            isSuccessfulAction = true;
                            i--;
                            attributesCounter++;
                            attributesForResetToDefault.remove(attribute);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error(e.getMessage(), e);
                            isSuccessfulAction = false;
                            successfulActionAttempt++;
                            updateMessage("Попытка загрузки [" + successfulActionAttempt + "/3]\n" + attribute.getName());
                            if (successfulActionAttempt >= 3) {
                                isSuccessfulAction = true;
                                successfulActionAttempt = 0;
                                i--;
                                attributesCounter++;
                                updateMessage("Ошибка загрузки\n" + attribute.getName());
                                Thread.sleep(1000);
                            }
                            Thread.sleep(1000);
                        }
                        Thread.sleep(100);
                    }

                    if (!attributesForResetToDefault.isEmpty()) {
                        result = Boolean.FALSE;
                    } else {
                        result = Boolean.TRUE;
                    }

                    return result;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    logger.info("Задача resetAttributesToDefault выполнена успешно");
                    closeLoadWindow(this);
                }

                @Override
                protected void cancelled() {
                    super.cancelled();
                    logger.info("Задача resetAttributesToDefault прервана");
                    closeLoadWindow(this);
                }

                @Override
                protected void failed() {
                    super.failed();
                    logger.info("Задача resetAttributesToDefault завершилась ошибкой");
                    logger.error(this.getException().getMessage(), this.getException());
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Невозможно выполнить операцию");
                        alert.setContentText("- установите соединение с контроллером, или повторите ещё раз");
                        alert.show();
                    });
                    closeLoadWindow(this);
                }
            };
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            System.out.println("resetAttributeService.setOnSucceeded");
            if (resetAttributesService.getValue().equals(Boolean.FALSE)) {
                showAttributesWhichNotResetDialog();
            }
            resetAttributesService.reset();
        }

        @Override
        protected void cancelled() {
            super.cancelled();
            super.reset();
            System.out.println("resetAttributeService.setOnCancelled");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sensorsList = MainController.sensorList;
        actuatorsList = MainController.actuatorList;

        resetAttributesService = new ResetAttributesService();

        resetParametersToDefaultButton.setOnAction(e -> {
            System.out.println("Reset Button");
            checkingAttributesForResetToDefault();
        });

        openChangeSchemeView();
        schemeBorderPane.onMouseClickedProperty()
                .setValue(
                        e -> {
                            openChangeSchemeView();
                            if (!schemeBorderPane.getStyleClass().contains("button-configurator-active")) {
                                schemeBorderPane.getStyleClass().remove("button-configurator");
                                schemeBorderPane.getStyleClass().add("button-configurator-active");
                            }

                            if (!sensorsBorderPane.getStyleClass().contains("button-configurator")) {
                                sensorsBorderPane.getStyleClass().remove("button-configurator-active");
                                sensorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!actuatorsBorderPane.getStyleClass().contains("button-configurator")) {
                                actuatorsBorderPane.getStyleClass().remove("button-configurator-active");
                                actuatorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!ioBorderPane.getStyleClass().contains("button-configurator")) {
                                ioBorderPane.getStyleClass().remove("button-configurator-active");
                                ioBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!peripheryBorderPane.getStyleClass().contains("button-configurator")) {
                                peripheryBorderPane.getStyleClass().remove("button-configurator-active");
                                peripheryBorderPane.getStyleClass().add("button-configurator");
                            }
                        }
                );
        sensorsBorderPane.onMouseClickedProperty()
                .setValue(
                        e -> {
                            onCreateChildNode(splitPaneRight, "Датчики", "/com/zenconf/zentecconfigurator/fxml/configurator/sensors-settings.fxml");
                            if (!sensorsBorderPane.getStyleClass().contains("button-configurator-active")) {
                                sensorsBorderPane.getStyleClass().remove("button-configurator");
                                sensorsBorderPane.getStyleClass().add("button-configurator-active");
                            }

                            if (!schemeBorderPane.getStyleClass().contains("button-configurator")) {
                                schemeBorderPane.getStyleClass().remove("button-configurator-active");
                                schemeBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!actuatorsBorderPane.getStyleClass().contains("button-configurator")) {
                                actuatorsBorderPane.getStyleClass().remove("button-configurator-active");
                                actuatorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!ioBorderPane.getStyleClass().contains("button-configurator")) {
                                ioBorderPane.getStyleClass().remove("button-configurator-active");
                                ioBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!peripheryBorderPane.getStyleClass().contains("button-configurator")) {
                                peripheryBorderPane.getStyleClass().remove("button-configurator-active");
                                peripheryBorderPane.getStyleClass().add("button-configurator");
                            }
                        }
                );
        actuatorsBorderPane.onMouseClickedProperty()
                .setValue(
                        e -> {
                            onCreateChildNode(splitPaneRight, "Испонительные устройства", "/com/zenconf/zentecconfigurator/fxml/configurator/actuators-settings.fxml");
                            if (!actuatorsBorderPane.getStyleClass().contains("button-configurator-active")) {
                                actuatorsBorderPane.getStyleClass().remove("button-configurator");
                                actuatorsBorderPane.getStyleClass().add("button-configurator-active");
                            }

                            if (!sensorsBorderPane.getStyleClass().contains("button-configurator")) {
                                sensorsBorderPane.getStyleClass().remove("button-configurator-active");
                                sensorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!schemeBorderPane.getStyleClass().contains("button-configurator")) {
                                schemeBorderPane.getStyleClass().remove("button-configurator-active");
                                schemeBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!ioBorderPane.getStyleClass().contains("button-configurator")) {
                                ioBorderPane.getStyleClass().remove("button-configurator-active");
                                ioBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!peripheryBorderPane.getStyleClass().contains("button-configurator")) {
                                peripheryBorderPane.getStyleClass().remove("button-configurator-active");
                                peripheryBorderPane.getStyleClass().add("button-configurator");
                            }
                        }
                );
        ioBorderPane.onMouseClickedProperty()
                .setValue(
                        e -> {
                            onCreateChildNode(splitPaneRight, "Мониторинг", "/com/zenconf/zentecconfigurator/fxml/configurator/io-monitor.fxml");
                            if (!ioBorderPane.getStyleClass().contains("button-configurator-active")) {
                                ioBorderPane.getStyleClass().remove("button-configurator");
                                ioBorderPane.getStyleClass().add("button-configurator-active");
                            }

                            if (!sensorsBorderPane.getStyleClass().contains("button-configurator")) {
                                sensorsBorderPane.getStyleClass().remove("button-configurator-active");
                                sensorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!schemeBorderPane.getStyleClass().contains("button-configurator")) {
                                schemeBorderPane.getStyleClass().remove("button-configurator-active");
                                schemeBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!actuatorsBorderPane.getStyleClass().contains("button-configurator")) {
                                actuatorsBorderPane.getStyleClass().remove("button-configurator-active");
                                actuatorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!peripheryBorderPane.getStyleClass().contains("button-configurator")) {
                                peripheryBorderPane.getStyleClass().remove("button-configurator-active");
                                peripheryBorderPane.getStyleClass().add("button-configurator");
                            }
                        }
                );
        peripheryBorderPane.onMouseClickedProperty()
                .setValue(
                        e -> {
                            onCreateChildNode(splitPaneRight, "Периферия", "/com/zenconf/zentecconfigurator/fxml/configurator/periphery.fxml");
                            if (!peripheryBorderPane.getStyleClass().contains("button-configurator-active")) {
                                peripheryBorderPane.getStyleClass().remove("button-configurator");
                                peripheryBorderPane.getStyleClass().add("button-configurator-active");
                            }

                            if (!sensorsBorderPane.getStyleClass().contains("button-configurator")) {
                                sensorsBorderPane.getStyleClass().remove("button-configurator-active");
                                sensorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!schemeBorderPane.getStyleClass().contains("button-configurator")) {
                                schemeBorderPane.getStyleClass().remove("button-configurator-active");
                                schemeBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!actuatorsBorderPane.getStyleClass().contains("button-configurator")) {
                                actuatorsBorderPane.getStyleClass().remove("button-configurator-active");
                                actuatorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!ioBorderPane.getStyleClass().contains("button-configurator")) {
                                ioBorderPane.getStyleClass().remove("button-configurator-active");
                                ioBorderPane.getStyleClass().add("button-configurator");
                            }
                        }
                );

//        disabledButtonsVBox.visibleProperty().bind(isEnabled);

        Image schemeImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/scheme_button.png")));
        schemeImageView.setFitHeight(80);
        schemeImageView.setFitWidth(80);
        schemeImageView.setMouseTransparent(true);
        schemeImageView.setImage(schemeImage);

        Image sensorsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/sensors_button.png")));
        sensorsImageView.setFitHeight(80);
        sensorsImageView.setFitWidth(80);
        sensorsImageView.setMouseTransparent(true);
        sensorsImageView.setImage(sensorsImage);

        Image actuatorsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/actuators_button.png")));
        actuatorsImageView.setFitHeight(80);
        actuatorsImageView.setFitWidth(80);
        actuatorsImageView.setMouseTransparent(true);
        actuatorsImageView.setImage(actuatorsImage);

        Image peripheryImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/periphery.png")));
        peripheryImageView.setFitHeight(80);
        peripheryImageView.setFitWidth(80);
        peripheryImageView.setMouseTransparent(true);
        peripheryImageView.setImage(peripheryImage);

        Image ioImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/io_button.png")));
        ioImageView.setFitHeight(80);
        ioImageView.setFitWidth(80);
        ioImageView.setMouseTransparent(true);
        ioImageView.setImage(ioImage);
    }

    private void openChangeSchemeView() {
        onCreateChildNode(splitPaneRight, "Выбор схемы", "/com/zenconf/zentecconfigurator/fxml/configurator/change-scheme.fxml");

        if (!schemeBorderPane.getStyleClass().contains("button-configurator-active")) {
            schemeBorderPane.getStyleClass().remove("button-configurator");
            schemeBorderPane.getStyleClass().add("button-configurator-active");
        }

        if (!sensorsBorderPane.getStyleClass().contains("button-configurator")) {
            sensorsBorderPane.getStyleClass().remove("button-configurator-active");
            sensorsBorderPane.getStyleClass().add("button-configurator");
        }

        if (!actuatorsBorderPane.getStyleClass().contains("button-configurator")) {
            actuatorsBorderPane.getStyleClass().remove("button-configurator-active");
            actuatorsBorderPane.getStyleClass().add("button-configurator");
        }

        if (!ioBorderPane.getStyleClass().contains("button-configurator")) {
            ioBorderPane.getStyleClass().remove("button-configurator-active");
            ioBorderPane.getStyleClass().add("button-configurator");
        }

        if (!peripheryBorderPane.getStyleClass().contains("button-configurator")) {
            peripheryBorderPane.getStyleClass().remove("button-configurator-active");
            peripheryBorderPane.getStyleClass().add("button-configurator");
        }
    }

    public static void checkingAttributesForResetToDefault() {
        if (MainController.panels.get("Испонительные устройства") == null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Действие отклонено");
                alert.setHeaderText("Восстановление параметров");
                alert.setContentText("Некоторые параметры могут быть не восстановлены." +
                        "\nНеобходимо загрузить страницу «Испонительные устройства»");
                alert.show();
            });
            return;
        }
        if (MainController.panels.get("Датчики") == null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Действие отклонено");
                alert.setHeaderText("Восстановление параметров");
                alert.setContentText("Некоторые параметры могут быть не восстановлены." +
                        "\nНеобходимо загрузить страницу «Датчики»");
                alert.show();
            });
            return;
        }
        if (attributesForResetToDefault.isEmpty()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Действие отклонено");
                alert.setHeaderText("Восстановление параметров");
                alert.setContentText("Восстановление параметров по умолчанию не требуется");
                alert.show();
            });
            return;
        }
        showResetAttributesDialog();
    }

    public static void showResetAttributesDialog() {
        if (attributesForResetToDefault.isEmpty()) {
            resetAttributesService.restart();
            return;
        }
        if (resetAttributesService.getState() == Worker.State.READY) {
            Platform.runLater(() -> {
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.setTitle("Подтверждение");
                dialog.setHeaderText("Восстановление параметров по умолчанию");
                dialog.setContentText("Вы уверены что хотите восстановить параметры по умолчанию?");
                dialog.getDialogPane().getButtonTypes().addAll(
                        new ButtonType("Да", ButtonBar.ButtonData.OK_DONE),
                        new ButtonType("Нет", ButtonBar.ButtonData.CANCEL_CLOSE)
                );

                Optional<ButtonType> result = dialog.showAndWait();
                if (result.isPresent()) {
                    if (result.orElseThrow().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        resetAttributesService.restart();
                    } else {
                        resetAttributesService.cancel();
                    }
                }
            });
        }
    }

    private static void showAttributesWhichNotResetDialog() {
        if (attributesForResetToDefault.isEmpty()) {
            return;
        }
        StringBuilder contentText = new StringBuilder();
        for (Attribute notResetAttribute : attributesForResetToDefault) {
            contentText.append("- ").append(notResetAttribute.getName()).append("\n");
        }
        contentText.append("\nПопробовать снова?");

        Platform.runLater(() -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Предупреждение");
            dialog.setHeaderText("Некоторые параметры не были восстановлены:");
            dialog.setContentText(contentText.toString());
            dialog.getDialogPane().getButtonTypes().addAll(
                    new ButtonType("Повторить", ButtonBar.ButtonData.OK_DONE),
                    new ButtonType("Нет", ButtonBar.ButtonData.CANCEL_CLOSE)
            );

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent()) {
                if (result.orElseThrow().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    resetAttributesService.restart();
                } else {
                    resetAttributesService.cancel();
                }
            }
        });
    }
}

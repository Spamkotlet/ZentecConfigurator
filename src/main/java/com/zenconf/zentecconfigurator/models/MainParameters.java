package com.zenconf.zentecconfigurator.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MainParameters {
    private Attribute startStopAttribute;
    private Attribute resetAlarmsAttribute;
    private Attribute controlModeAttribute;
    private Attribute seasonAttribute;
    private Attribute statusAttribute;
    private List<Attribute> heatExchangerAttributes;
    private Attribute deviceAddressAttribute; // атрибут для проверки соединения

    public Attribute getStartStopAttribute() {
        return startStopAttribute;
    }

    public Attribute getResetAlarmsAttribute() {
        return resetAlarmsAttribute;
    }

    public Attribute getControlModeAttribute() {
        return controlModeAttribute;
    }

    public Attribute getSeasonAttribute() {
        return seasonAttribute;
    }

    public Attribute getStatusAttribute() {
        return statusAttribute;
    }

    public Attribute getDeviceAddressAttribute() {
        return deviceAddressAttribute;
    }

    public List<Attribute> getHeatExchangerAttributes() {
        return heatExchangerAttributes;
    }

    public static MainParameters getMainParametersFromJson() {
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
}

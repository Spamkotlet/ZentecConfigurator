package com.zenconf.zentecconfigurator.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class HelpPageController implements Initializable {

    @FXML
    public WebView helpPageWebView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        WebEngine webEngine = helpPageWebView.getEngine();
        webEngine.load("https://docs.oracle.com/javafx/2/webview/jfxpub-webview.htm");
    }
}

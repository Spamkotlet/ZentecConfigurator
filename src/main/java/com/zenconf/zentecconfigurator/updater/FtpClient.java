package com.zenconf.zentecconfigurator.updater;

import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class FtpClient {
    public static final Logger logger = LogManager.getLogger();
    private String server = "31.31.198.105";
    private int port = 21;
    private String user = "u2282917";
    private String password = "4mS5k3QUKnink1Ga";
    private FTPClient ftp;

    public void checkUpdates() throws IOException {
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(server, port);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftp.login(user, password);
        ftp.enterLocalPassiveMode();

        try {
            String remoteVersion = new String(downloadFile("/zconf/lastVersion.txt", ftp), StandardCharsets.UTF_8);
            System.out.println("Remote version " + remoteVersion);
            File file = new File("lastVersion.txt");
            if (!file.exists()) {
                return;
            }
            String currentVersion = Files.readString(file.toPath());
            System.out.println("Обновление требуется:" + !remoteVersion.equals(currentVersion));
            if (!remoteVersion.equals(currentVersion)) {
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
                        Runtime.getRuntime().exec("cmd /c start updater.jar");
                        Platform.exit();
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        ftp.logout();
        ftp.disconnect();
    }

    public byte[] downloadFile(String path, FTPClient ftpClient) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.out.println();
        System.out.printf("[downloadFile][%d] Is success to download file : %s -> %b",
                System.currentTimeMillis(), path, ftpClient.retrieveFile(path, byteArrayOutputStream));
        System.out.println();
        return byteArrayOutputStream.toByteArray();
    }
}

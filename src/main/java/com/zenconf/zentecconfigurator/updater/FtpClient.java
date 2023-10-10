package com.zenconf.zentecconfigurator.updater;

import com.zenconf.zentecconfigurator.Application;
import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class FtpClient {

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

//        FileOutputStream out = new FileOutputStream("test.txt");
//        ftp.retrieveFile("/zconf/version.txt", out);
        try {
            String remoteVersion = new String(downloadFile("/zconf/version.txt", ftp), StandardCharsets.UTF_8);
            String currentVersion = Files.readString(Paths.get("test.txt"));
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
                        Runtime.getRuntime().exec("cmd /c start cmd.exe");
                        Platform.exit();
                    }
                }
//                String[] remoteVersionArray = remoteVersion.split("\\.");
//                String[] currentVersionArray = currentVersion.split("\\.");
//                String remoteA = remoteVersionArray[0];
//                String remoteB = remoteVersionArray[1];
//                String remoteC = remoteVersionArray[2];
//                String currentA = currentVersionArray[0];
//                String currentB = currentVersionArray[1];
//                String currentC = currentVersionArray[2];
//                if (!remoteA.equals(currentA)) {
//                    System.out.println("Требуется глобальное обновление");
//                }
//                if (!remoteB.equals(currentB)) {
//                    System.out.println("Есть обновление, которое добавляет новый функционал");
//                }
//                if (!remoteC.equals(currentC)) {
//                    System.out.println("Найдено мелкое обновление");
//                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ftp.logout();
        ftp.disconnect();
    }

    public void close() throws IOException {
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

    public void printTree(String path, FTPClient ftpClient) throws Exception {
        for (FTPFile ftpFile : ftpClient.listFiles(path)) {
            System.out.println();
            System.out.printf("[printTree][%d]\n", System.currentTimeMillis());
            System.out.printf("[printTree][%d] Get name : %s \n", System.currentTimeMillis(), ftpFile.getName());
            System.out.printf("[printTree][%d] Get timestamp : %s \n", System.currentTimeMillis(), ftpFile.getTimestamp().getTimeInMillis());
            System.out.printf("[printTree][%d] Get group : %s \n", System.currentTimeMillis(), ftpFile.getGroup());
            System.out.printf("[printTree][%d] Get link : %s \n", System.currentTimeMillis(), ftpFile.getLink());
            System.out.printf("[printTree][%d] Get user : %s \n", System.currentTimeMillis(), ftpFile.getUser());
            System.out.printf("[printTree][%d] Get type : %s \n", System.currentTimeMillis(), ftpFile.getType());
            System.out.printf("[printTree][%d] Is file : %s \n", System.currentTimeMillis(), ftpFile.isFile());
            System.out.printf("[printTree][%d] Is directory : %s \n", System.currentTimeMillis(), ftpFile.isDirectory());
            System.out.printf("[printTree][%d] Formatted string : %s \n", System.currentTimeMillis(), ftpFile.toFormattedString());
            System.out.println();

            if (ftpFile.isDirectory()) {
                printTree(path + File.separator + ftpFile.getName(), ftpClient);
            }
        }
    }
}

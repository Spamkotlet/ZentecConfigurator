package com.zenconf.zentecconfigurator.updater;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.controllers.MainController;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class FtpClient {

    public static final Logger logger = LogManager.getLogger();
    private FTPClient ftp;
    private final String server = MainController.ftpServerProperties.get("FTPServer.server");
    private final int port = Integer.parseInt(MainController.ftpServerProperties.get("FTPServer.port"));
    private final String user = MainController.ftpServerProperties.get("FTPServer.user");
    private final String password = MainController.ftpServerProperties.get("FTPServer.password");
    private final String lastVersionPath = MainController.configuratorProperties.get("Configurator.lastVersionPath");
    private final String currentVersion = MainController.configuratorProperties.get("Configurator.currentVersion");
    private String remoteVersion = "";
    private final String changeLogPath = MainController.configuratorProperties.get("Configurator.changeLogPath");

    public boolean checkUpdates() throws Exception {
        logger.info("CHECK UPDATES");
        connectToFTPServer();
        boolean updateRequired;
        try {
            Properties _remoteConfiguratorProperties = new Properties();
            HashMap<String, String> remoteConfiguratorProperties;
            try (InputStream fis = new ByteArrayInputStream(downloadFile(lastVersionPath, ftp))) {
                logger.info("Инициализация remote Configurator.properties");
                _remoteConfiguratorProperties.load(fis);
                Map map = _remoteConfiguratorProperties;
                Map<String, String> map1 = (Map<String, String>) map;
                remoteConfiguratorProperties = new HashMap<>(map1);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            remoteVersion = remoteConfiguratorProperties.get("Configurator.currentVersion");
            logger.info("Remote version " + remoteVersion);
            logger.info("Current version " + currentVersion);
            updateRequired = !remoteVersion.equals(currentVersion);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            throw e;
        }
        ftp.logout();
        ftp.disconnect();

        return updateRequired;
    }

    private void connectToFTPServer() throws Exception {
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
    }

    public String getChangeLog() throws Exception {
        connectToFTPServer();

        Map<String, String> versions;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(downloadFile(changeLogPath, ftp))))) {
            ObjectMapper mapper = new ObjectMapper();
            versions = mapper.readValue(reader, new TypeReference<>(){});
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        StringBuilder lastVersionChanges = new StringBuilder();
        lastVersionChanges.append("Что нового:\n");
        lastVersionChanges.append(versions.get(remoteVersion));

        StringBuilder previousVersionsSB = new StringBuilder();
        previousVersionsSB.append("В прошлых версиях, которые были пропущены:\n");
        List<String> keys = new ArrayList<>(versions.keySet());
        boolean isSkippedPreviousVersions = false;
        for (int i = keys.indexOf(currentVersion) + 1; i < keys.indexOf(remoteVersion); i++) {
            isSkippedPreviousVersions = true;
            previousVersionsSB.append("- ").append(versions.get(keys.get(i))).append("\n");
        }

        StringBuilder changeLog = new StringBuilder();
        if (isSkippedPreviousVersions) {
            changeLog.append(previousVersionsSB).append("\n");
        }
        changeLog.append(lastVersionChanges);

        return changeLog.toString();
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

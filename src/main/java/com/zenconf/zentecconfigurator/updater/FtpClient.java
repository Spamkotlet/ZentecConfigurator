package com.zenconf.zentecconfigurator.updater;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FtpClient {
    public static final Logger logger = LogManager.getLogger();
    private String server = "31.31.198.105";
    private int port = 21;
    private String user = "u2282917";
    private String password = "y690wNLM3nNrnkYx";
    private FTPClient ftp;

    public boolean checkUpdates() throws Exception {
        logger.info("checkUpdates()");
        connectToFTPServer();
        boolean updateRequired = false;
        try {
            String remoteVersion = new String(downloadFile("/zconf/lastVersion.txt", ftp), StandardCharsets.UTF_8);
            logger.info("Remote version " + remoteVersion);
            File file = new File("lastVersion.txt");
            logger.info(file.getCanonicalPath());
            if (!file.exists()) {
                return updateRequired;
            }
            String currentVersion = Files.readString(file.toPath());
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

    public byte[] downloadFile(String path, FTPClient ftpClient) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.out.println();
        System.out.printf("[downloadFile][%d] Is success to download file : %s -> %b",
                System.currentTimeMillis(), path, ftpClient.retrieveFile(path, byteArrayOutputStream));
        System.out.println();
        return byteArrayOutputStream.toByteArray();
    }
}

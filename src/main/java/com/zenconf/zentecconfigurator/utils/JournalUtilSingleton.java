package com.zenconf.zentecconfigurator.utils;

public class JournalUtilSingleton {
    private static JournalUtilSingleton instance;

    public static JournalUtilSingleton getInstance() {
        if (instance == null) {
            instance = new JournalUtilSingleton();
        }
        return instance;
    }

    public JournalUtilSingleton() {

    }
}

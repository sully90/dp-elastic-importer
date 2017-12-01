package com.github.onsdigitial.elastic.importer.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author sullid (David Sullivan) on 30/11/2017
 * @project dp-elastic-importer
 */
public class Configuration {
    private static Properties properties;

    static {
        properties = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("conf/application.conf");

            // load a properties file
            properties.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String getGroupProperty(String group, String key) {
        return getProperty(group + '.' + key);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String[] getPropertyArray(String key) {
        return getPropertyArray(key, ",");
    }

    public static String[] getPropertyArray(String key, String delimiter) {
        return properties.getProperty(key).split(delimiter);
    }

    public static String filenameInClasspath(String filename) {
        return Thread.currentThread().getContextClassLoader().getResource(filename).getFile();
    }
}

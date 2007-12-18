package com.enjoyxstudy.selenium.autoexec;

import java.io.File;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.SeleniumServer;

/**
 * @author onozaty
 */
public class AutoExecServer {

    /** logger */
    private static Log log = LogFactory.getLog(AutoExecServer.class);

    /** config */
    private Config config;

    /** seleniumServer */
    private SeleniumServer seleniumServer;

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO
    }

    /**
     * server startup.
     * 
     * @param properties 
     * @throws Exception 
     */
    public void startup(Properties properties) throws Exception {

        String temp;

        config = new Config();
        config.setBrowsers(properties.getProperty("browser").split(","));
        config.setStartURL(properties.getProperty("startURL"));
        config.setSuite(properties.getProperty("suite"));
        config.setGenerateSuite(Boolean.parseBoolean(properties
                .getProperty("generateSuite")));
        config.setResult(properties.getProperty("result"));

        if ((temp = properties.getProperty("timeout")) != null) {
            config.setTimeoutInSeconds(Integer.parseInt(temp));
        }

        // create server
        int port = SeleniumServer.getDefaultPort();
        if ((temp = properties.getProperty("port")) != null) {
            port = Integer.parseInt(temp);
        }

        boolean multiWindow = false;
        if ((temp = properties.getProperty("multiWindow")) != null) {
            multiWindow = Boolean.parseBoolean(temp);
        }

        if ((temp = properties.getProperty("avoidProxy")) != null) {
            SeleniumServer.setAvoidProxy(Boolean.parseBoolean(temp));
        }

        if ((temp = properties.getProperty("debug")) != null) {
            SeleniumServer.setDebugMode(Boolean.parseBoolean(temp));
        }

        if ((temp = properties.getProperty("log")) != null) {
            System.setProperty("selenium.log", temp);
        }

        File userExtensions = null;
        if ((temp = properties.getProperty("userExtensions")) != null) {
            userExtensions = new File(temp);
            if (!userExtensions.exists()) {
                throw new RuntimeException(
                        "User Extensions file doesn't exist: "
                                + userExtensions.getAbsolutePath());
            }
            if (!"user-extensions.js"
                    .equalsIgnoreCase(userExtensions.getName())) {
                throw new RuntimeException(
                        "User extensions file MUST be called \"user-extensions.js\": "
                                + userExtensions.getAbsolutePath());
            }
        }

        if ((temp = properties.getProperty("proxyHost")) != null) {
            System.setProperty("http.proxyHost", temp);
        }
        if ((temp = properties.getProperty("proxyPort")) != null) {
            System.setProperty("http.proxyPort", temp);
        }

        seleniumServer = new SeleniumServer(port, false, multiWindow);

        if (userExtensions != null) {
            seleniumServer.addNewStaticContent(userExtensions.getParentFile());
        }

        seleniumServer.start();

        log.info("selenium server start.");
    }

    /**
     * destroy.
     */
    public void destroy() {
        seleniumServer.stop();
        log.info("selenium server stop.");
    }

    /**
     * 
     */
    public void process() {

        log.info("process start.");
        // TODO
    }
}

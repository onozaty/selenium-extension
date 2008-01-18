package com.enjoyxstudy.selenium.autoexec;

import java.io.File;
import java.util.Properties;

import org.openqa.selenium.server.SeleniumServer;

/**
 * @author onozaty
 */
public class Config {

    /** port */
    private int port = SeleniumServer.getDefaultPort();

    /** multiWindow */
    private boolean multiWindow;

    /** avoidProxy */
    private boolean avoidProxy;

    /** debug */
    private boolean debug;

    /** log */
    private String log;

    /** userExtensions */
    private File userExtensions;

    /** proxyHost */
    private String proxyHost;

    /** proxyPort */
    private String proxyPort;

    /** browsers */
    private String[] browsers;

    /** startURL */
    private String startURL;

    /** suiteDir */
    private String suiteDir;

    /** suiteRepo */
    private String suiteRepo;

    /** suiteRepoUsername */
    private String suiteRepoUsername;

    /** suiteRepoPassword */
    private String suiteRepoPassword;

    /** generateSuite */
    private boolean generateSuite;

    /** resultDir */
    private String resultDir;

    /** timeoutInSeconds */
    private int timeoutInSeconds = 60 * 60;

    /**
     * 
     */
    public Config() {
        //
    }

    /**
     * @param properties
     */
    public Config(Properties properties) {

        String temp;
        if ((temp = properties.getProperty("port")) != null) {
            port = Integer.parseInt(temp);
        }

        multiWindow = Boolean.parseBoolean(properties
                .getProperty("multiWindow"));

        avoidProxy = Boolean.parseBoolean(properties.getProperty("avoidProxy"));

        debug = Boolean.parseBoolean(properties.getProperty("debug"));

        log = properties.getProperty("log");

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

        proxyHost = properties.getProperty("proxyHost");
        proxyPort = properties.getProperty("proxyPort");

        browsers = properties.getProperty("browser").split(",");
        startURL = properties.getProperty("startURL");
        suiteDir = properties.getProperty("suiteDir");
        suiteRepo = properties.getProperty("suiteRepo");
        suiteRepoUsername = properties.getProperty("suiteRepoUsername");
        suiteRepoPassword = properties.getProperty("suiteRepoPassword");
        generateSuite = Boolean.parseBoolean(properties
                .getProperty("generateSuite"));
        resultDir = properties.getProperty("resultDir");

        if ((temp = properties.getProperty("timeout")) != null) {
            timeoutInSeconds = Integer.parseInt(temp);
        }
    }

    /**
     * @return browsers
     */
    public String[] getBrowsers() {
        return browsers;
    }

    /**
     * @param browsers browsers
     */
    public void setBrowsers(String[] browsers) {
        this.browsers = browsers;
    }

    /**
     * @return generateSuite
     */
    public boolean isGenerateSuite() {
        return generateSuite;
    }

    /**
     * @param generateSuite generateSuite
     */
    public void setGenerateSuite(boolean generateSuite) {
        this.generateSuite = generateSuite;
    }

    /**
     * @return resultDir
     */
    public String getResultDir() {
        return resultDir;
    }

    /**
     * @param resultDir resultDir
     */
    public void setResultDir(String resultDir) {
        this.resultDir = resultDir;
    }

    /**
     * @return startURL
     */
    public String getStartURL() {
        return startURL;
    }

    /**
     * @param startURL startURL
     */
    public void setStartURL(String startURL) {
        this.startURL = startURL;
    }

    /**
     * @return suiteDir
     */
    public String getSuiteDir() {
        return suiteDir;
    }

    /**
     * @param suiteDir suiteDir
     */
    public void setSuiteDir(String suiteDir) {
        this.suiteDir = suiteDir;
    }

    /**
     * @return suiteRepo
     */
    public String getSuiteRepo() {
        return suiteRepo;
    }

    /**
     * @param suiteRepo suiteRepo
     */
    public void setSuiteRepo(String suiteRepo) {
        this.suiteRepo = suiteRepo;
    }

    /**
     * @return suiteRepoPassword
     */
    public String getSuiteRepoPassword() {
        return suiteRepoPassword;
    }

    /**
     * @param suiteRepoPassword suiteRepoPassword
     */
    public void setSuiteRepoPassword(String suiteRepoPassword) {
        this.suiteRepoPassword = suiteRepoPassword;
    }

    /**
     * @return suiteRepoUsername
     */
    public String getSuiteRepoUsername() {
        return suiteRepoUsername;
    }

    /**
     * @param suiteRepoUsername suiteRepoUsername
     */
    public void setSuiteRepoUsername(String suiteRepoUsername) {
        this.suiteRepoUsername = suiteRepoUsername;
    }

    /**
     * @return timeoutInSeconds
     */
    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    /**
     * @param timeoutInSeconds timeoutInSeconds
     */
    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    /**
     * @return avoidProxy
     */
    public boolean isAvoidProxy() {
        return avoidProxy;
    }

    /**
     * @param avoidProxy avoidProxy
     */
    public void setAvoidProxy(boolean avoidProxy) {
        this.avoidProxy = avoidProxy;
    }

    /**
     * @return debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return log
     */
    public String getLog() {
        return log;
    }

    /**
     * @param log log
     */
    public void setLog(String log) {
        this.log = log;
    }

    /**
     * @return multiWindow
     */
    public boolean isMultiWindow() {
        return multiWindow;
    }

    /**
     * @param multiWindow multiWindow
     */
    public void setMultiWindow(boolean multiWindow) {
        this.multiWindow = multiWindow;
    }

    /**
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return userExtensions
     */
    public File getUserExtensions() {
        return userExtensions;
    }

    /**
     * @param userExtensions userExtensions
     */
    public void setUserExtensions(File userExtensions) {
        this.userExtensions = userExtensions;
    }

    /**
     * @return proxyHost
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * @param proxyHost proxyHost
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * @return proxyPort
     */
    public String getProxyPort() {
        return proxyPort;
    }

    /**
     * @param proxyPort proxyPort
     */
    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

}

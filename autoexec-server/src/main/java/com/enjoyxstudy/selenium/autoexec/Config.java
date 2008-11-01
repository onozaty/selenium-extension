/*
 * Copyright (c) 2007, 2008 onozaty (http://www.enjoyxstudy.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.enjoyxstudy.selenium.autoexec;

import java.io.File;
import java.util.Properties;

import org.openqa.selenium.server.SeleniumServer;

import com.enjoyxstudy.selenium.htmlsuite.util.PropertiesUtils;

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

    /** firefox profile */
    private File firefoxProfileTemplate;

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
    private File suiteDir;

    /** default suiteDir name */
    private static final String DEFAULT_SUITE_DIR_NAME = "suite";

    /** suiteRepo */
    private String suiteRepo;

    /** suiteRepoUsername */
    private String suiteRepoUsername;

    /** suiteRepoPassword */
    private String suiteRepoPassword;

    /** generateSuite */
    private boolean generateSuite = true;

    /** resultDir */
    private File resultDir;

    /** default resultDir name */
    private static final String DEFAULT_RESULT_DIR_NAME = "result";

    /** permanentResult */
    private boolean permanentResult = true;

    /** timeoutInSeconds */
    private int timeoutInSeconds = 60 * 60;

    /** autoExecTime */
    private String autoExecTime;

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

        port = PropertiesUtils.getInt(properties, "port", port);
        multiWindow = PropertiesUtils.getBoolean(properties, "multiWindow",
                multiWindow);

        avoidProxy = PropertiesUtils.getBoolean(properties, "avoidProxy",
                avoidProxy);

        debug = PropertiesUtils.getBoolean(properties, "debug", debug);

        log = PropertiesUtils.getString(properties, "log", log);

        String userExtensionsName = PropertiesUtils.getString(properties,
                "userExtensions");
        if (userExtensionsName != null) {
            userExtensions = new File(userExtensionsName);
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

        proxyHost = PropertiesUtils.getString(properties, "proxyHost",
                proxyHost);
        proxyPort = PropertiesUtils.getString(properties, "proxyPort",
                proxyPort);

        String firefoxProfileTemplateName = PropertiesUtils.getString(
                properties, "firefoxProfileTemplate");
        if (firefoxProfileTemplateName != null) {
            firefoxProfileTemplate = new File(firefoxProfileTemplateName);
            if (!firefoxProfileTemplate.exists()) {
                throw new RuntimeException(
                        "Firefox profile template doesn't exist: "
                                + firefoxProfileTemplate.getAbsolutePath());
            }
        }

        browsers = PropertiesUtils.getString(properties, "browser").split(",");
        startURL = PropertiesUtils.getString(properties, "startURL");

        suiteDir = new File(PropertiesUtils.getString(properties, "suiteDir",
                DEFAULT_SUITE_DIR_NAME));

        suiteRepo = PropertiesUtils.getString(properties, "suiteRepo");
        suiteRepoUsername = PropertiesUtils.getString(properties,
                "suiteRepoUsername");
        suiteRepoPassword = PropertiesUtils.getString(properties,
                "suiteRepoPassword");

        generateSuite = PropertiesUtils.getBoolean(properties, "generateSuite",
                generateSuite);

        resultDir = new File(PropertiesUtils.getString(properties, "resultDir",
                DEFAULT_RESULT_DIR_NAME));

        permanentResult = PropertiesUtils.getBoolean(properties,
                "permanentResult", permanentResult);

        timeoutInSeconds = PropertiesUtils.getInt(properties, "timeout",
                timeoutInSeconds);

        autoExecTime = PropertiesUtils.getString(properties, "autoExecTime",
                autoExecTime);
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
    public File getResultDir() {
        return resultDir;
    }

    /**
     * @param resultDir resultDir
     */
    public void setResultDir(File resultDir) {
        this.resultDir = resultDir;
    }

    /**
     * @return permanentResult
     */
    public boolean isPermanentResult() {
        return permanentResult;
    }

    /**
     * @param permanentResult permanentResult
     */
    public void setPermanentResult(boolean permanentResult) {
        this.permanentResult = permanentResult;
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
    public File getSuiteDir() {
        return suiteDir;
    }

    /**
     * @param suiteDir suiteDir
     */
    public void setSuiteDir(File suiteDir) {
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

    /**
     * @return autoExecTime
     */
    public String getAutoExecTime() {
        return autoExecTime;
    }

    /**
     * @param autoExecTime autoExecTime
     */
    public void setAutoExecTime(String autoExecTime) {
        this.autoExecTime = autoExecTime;
    }

    /**
     * @return firefoxProfileTemplate
     */
    public File getFirefoxProfileTemplate() {
        return firefoxProfileTemplate;
    }

    /**
     * @param firefoxProfileTemplate firefoxProfileTemplate
     */
    public void setFirefoxProfileTemplate(File firefoxProfileTemplate) {
        this.firefoxProfileTemplate = firefoxProfileTemplate;
    }

}

/*
 * Copyright (c) 2007 - 2009 onozaty (http://www.enjoyxstudy.com)
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
package com.enjoyxstudy.selenium.htmlsuite;

import java.io.File;
import java.util.Properties;

import org.openqa.selenium.server.RemoteControlConfiguration;

import com.enjoyxstudy.selenium.htmlsuite.util.PropertiesUtils;

/**
 * @author onozaty
 */
public class HTMLSuiteConfiguration extends RemoteControlConfiguration {

    /** browsers */
    private String[] browsers;

    /** startURL */
    private String startURL;

    /** suite */
    private String suite;

    /** generateSuite */
    private boolean generateSuite;

    /** result */
    private String result;

    /** proxyHost */
    private String proxyHost;

    /** proxyPort */
    private String proxyPort;

    /**
     *
     */
    public HTMLSuiteConfiguration() {
        super();
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
     * @return suite
     */
    public String getSuite() {
        return suite;
    }

    /**
     * @param suite suite
     */
    public void setSuite(String suite) {
        this.suite = suite;
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
     * @return result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result result
     */
    public void setResult(String result) {
        this.result = result;
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
     * @param properties
     * @return configuration
     */
    public static HTMLSuiteConfiguration Load(Properties properties) {

        HTMLSuiteConfiguration configuration = new HTMLSuiteConfiguration();

        return Load(configuration, properties);
    }

    /**
     * @param configuration
     * @param properties
     * @return configuration
     */
    public static HTMLSuiteConfiguration Load(
            HTMLSuiteConfiguration configuration, Properties properties) {

        configuration.setBrowsers(PropertiesUtils.getString(properties,
                "browser").split(","));
        configuration.setStartURL(PropertiesUtils.getString(properties,
                "startURL"));

        configuration.setSuite(PropertiesUtils.getString(properties, "suite",
                configuration.getSuite()));
        configuration.setGenerateSuite(PropertiesUtils.getBoolean(properties,
                "generateSuite", configuration.isGenerateSuite()));

        configuration.setResult(PropertiesUtils.getString(properties, "result",
                configuration.getResult()));

        configuration.setPort(PropertiesUtils.getInt(properties, "port",
                getDefaultPort()));
        configuration.setSingleWindow(PropertiesUtils.getBoolean(properties,
                "singleWindow", configuration.isSingleWindow()));
        configuration.setAvoidProxy(PropertiesUtils.getBoolean(properties,
                "avoidProxy", configuration.isAvoidProxy()));
        configuration.setDebugMode((PropertiesUtils.getBoolean(properties, "debug",
                configuration.isDebugMode())));

        configuration.setLogOutFileName(PropertiesUtils.getString(properties, "log",
                configuration.getLogOutFileName()));

        File userExtensions = null;
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
            configuration.setUserExtensions(userExtensions);
        }

        configuration.setTimeoutInSeconds(PropertiesUtils.getInt(properties,
                "timeout", configuration.getTimeoutInSeconds()));

        configuration.setProxyHost(PropertiesUtils.getString(properties,
                "proxyHost", configuration.getProxyHost()));
        configuration.setProxyPort(PropertiesUtils.getString(properties,
                "proxyPort", configuration.getProxyPort()));

        String firefoxProfileTemplateName = PropertiesUtils.getString(
                properties, "firefoxProfileTemplate");
        if (firefoxProfileTemplateName != null) {
            File firefoxProfileTemplate = new File(firefoxProfileTemplateName);
            if (!firefoxProfileTemplate.exists()) {
                throw new RuntimeException(
                        "Firefox profile template doesn't exist: "
                                + firefoxProfileTemplate.getAbsolutePath());
            }
            configuration.setFirefoxProfileTemplate(firefoxProfileTemplate);
        }

        configuration.setTrustAllSSLCertificates(PropertiesUtils.getBoolean(
                properties, "trustAllSSLCertificates", configuration
                        .trustAllSSLCertificates()));

        return configuration;
    }
}

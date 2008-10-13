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
package com.enjoyxstudy.selenium.htmlsuite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.SeleniumServer;

import com.enjoyxstudy.selenium.htmlsuite.util.PropertiesUtils;

/**
 * @author onozaty
 */
public class MultiHTMLSuiteRunner {

    /** logger */
    private static Log log = LogFactory.getLog(HTMLSuiteLauncher.class);

    /** SeleniumServer */
    private SeleniumServer server;

    /** htmlSuites */
    private ArrayList<HTMLSuite> htmlSuiteList = new ArrayList<HTMLSuite>();

    /** result */
    private boolean result;

    /** passedCount */
    private int passedCount;

    /** failedCount */
    private int failedCount;

    /** start time */
    private long startTime;

    /** end time */
    private long endTime;

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {

        String propertyFile = "htmlSuite.properties"; // default
        if (args.length != 0) {
            propertyFile = args[0];
        }
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(propertyFile);
        try {
            properties.load(inputStream);
        } finally {
            inputStream.close();
        }

        MultiHTMLSuiteRunner runner = execute(properties);

        System.exit(runner.getResult() ? 0 : 1);
    }

    /**
     * @param properties 
     * @return MultiHTMLSuiteRunner
     * @throws Exception 
     */
    public static MultiHTMLSuiteRunner execute(Properties properties)
            throws Exception {

        String[] browsers = PropertiesUtils.getString(properties, "browser")
                .split(",");
        String startURL = PropertiesUtils.getString(properties, "startURL");
        String suite = PropertiesUtils.getString(properties, "suite");
        boolean generateSuite = PropertiesUtils.getBoolean(properties,
                "generateSuite");
        String result = PropertiesUtils.getString(properties, "result");

        int port = PropertiesUtils.getInt(properties, "port", SeleniumServer
                .getDefaultPort());

        boolean multiWindow = PropertiesUtils.getBoolean(properties,
                "multiWindow");

        if (PropertiesUtils.getBoolean(properties, "avoidProxy")) {
            SeleniumServer.setAvoidProxy(true);
        }

        if (PropertiesUtils.getBoolean(properties, "debug")) {
            SeleniumServer.setDebugMode(true);
        }

        String logFileName = PropertiesUtils.getString(properties, "log");
        if (logFileName != null) {
            System.setProperty("selenium.log", logFileName);
        }

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
        }

        int timeoutInSeconds = PropertiesUtils.getInt(properties, "timeout",
                60 * 60);

        String proxyHost = PropertiesUtils.getString(properties, "proxyHost");
        if (proxyHost != null) {
            System.setProperty("http.proxyHost", proxyHost);
        }
        String proxyPort = PropertiesUtils.getString(properties, "proxyPort");
        if (proxyPort != null) {
            System.setProperty("http.proxyPort", proxyPort);
        }

        String firefoxProfileTemplateName = PropertiesUtils.getString(
                properties, "firefoxProfileTemplate");
        if (firefoxProfileTemplateName != null) {
            File firefoxProfileTemplate = new File(firefoxProfileTemplateName);
            if (!firefoxProfileTemplate.exists()) {
                throw new RuntimeException(
                        "Firefox profile template doesn't exist: "
                                + firefoxProfileTemplate.getAbsolutePath());
            }
            SeleniumServer.setFirefoxProfileTemplate(firefoxProfileTemplate);
        }

        SeleniumServer seleniumServer = new SeleniumServer(port, false,
                multiWindow);

        try {
            if (userExtensions != null) {
                seleniumServer.addNewStaticContent(userExtensions
                        .getParentFile());
            }

            seleniumServer.start();

            MultiHTMLSuiteRunner htmlSuiteRunner = new MultiHTMLSuiteRunner(
                    seleniumServer);
            if (generateSuite) {
                if (result != null) {
                    htmlSuiteRunner.addHTMLSuiteGenerate(browsers, startURL,
                            suite, result, timeoutInSeconds);
                } else {
                    htmlSuiteRunner.addHTMLSuiteGenerate(browsers, startURL,
                            suite, timeoutInSeconds);
                }
            } else {
                if (result != null) {
                    htmlSuiteRunner.addHTMLSuites(browsers, startURL, suite,
                            result, timeoutInSeconds);
                } else {
                    htmlSuiteRunner.addHTMLSuites(browsers, startURL, suite,
                            timeoutInSeconds);
                }
            }

            htmlSuiteRunner.runHTMLSuites();

            if (htmlSuiteRunner.getResult()) {
                log.info("HTML Suites passed.");
            } else {
                log.info("HTML Suites failed.");
            }

            log.info("total: " + htmlSuiteRunner.getHtmlSuiteList().size()
                    + ", passed: " + htmlSuiteRunner.getPassedCount()
                    + ", failed: " + htmlSuiteRunner.getFailedCount());

            int count = 0;
            for (HTMLSuite htmlSuite : htmlSuiteRunner.getHtmlSuiteList()) {

                StringBuilder builder = new StringBuilder();
                builder.append(++count);

                if (htmlSuite.isPassed()) {
                    builder.append(": [passed] ");
                } else {
                    builder.append(": [failed] ");
                }
                builder.append(htmlSuite.getSuiteFile().getName()).append(" ")
                        .append(htmlSuite.getBrowser());
                log.info(builder.toString());
            }

            return htmlSuiteRunner;

        } finally {
            seleniumServer.stop();
        }
    }

    /**
     * @param server
     */
    public MultiHTMLSuiteRunner(SeleniumServer server) {
        this.server = server;
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * @param browsers 
     * @param browserURL 
     * @param testCaseDirPath 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuiteGenerate(String[] browsers, String browserURL,
            String testCaseDirPath, int timeoutInSeconds) throws IOException {

        File suiteFile = generateHTMLSute(new File(testCaseDirPath));

        for (int i = 0; i < browsers.length; i++) {
            _addHTMLSuite(browsers[i], browserURL, suiteFile, null,
                    timeoutInSeconds);
        }
    }

    /**
     * @param browsers 
     * @param browserURL 
     * @param testCaseDirPath 
     * @param resultDirPath 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuiteGenerate(String[] browsers, String browserURL,
            String testCaseDirPath, String resultDirPath, int timeoutInSeconds)
            throws IOException {

        File suiteFile = generateHTMLSute(new File(testCaseDirPath));

        for (int i = 0; i < browsers.length; i++) {
            File resultDir = createResultDir(browsers[i], resultDirPath);
            _addHTMLSuite(browsers[i], browserURL, suiteFile, createResultFile(
                    resultDir, suiteFile), timeoutInSeconds);
        }
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * @param browsers 
     * @param browserURL 
     * @param suitePath 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuites(String[] browsers, String browserURL,
            String suitePath, int timeoutInSeconds) throws IOException {

        File suiteFile = new File(suitePath);
        for (int i = 0; i < browsers.length; i++) {
            if (suiteFile.isDirectory()) {
                addHTMLSuiteDir(browsers[i], browserURL, suiteFile,
                        timeoutInSeconds);
            } else {
                _addHTMLSuite(browsers[i], browserURL, suiteFile, null,
                        timeoutInSeconds);
            }
        }
    }

    /**
     * @param browsers 
     * @param browserURL 
     * @param suitePath 
     * @param resultDirPath 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuites(String[] browsers, String browserURL,
            String suitePath, String resultDirPath, int timeoutInSeconds)
            throws IOException {

        File suiteFile = new File(suitePath);

        for (int i = 0; i < browsers.length; i++) {
            File resultDir = createResultDir(browsers[i], resultDirPath);
            if (suiteFile.isDirectory()) {
                addHTMLSuiteDir(browsers[i], browserURL, suiteFile, resultDir,
                        timeoutInSeconds);
            } else {
                addHTMLSuites(browsers[i], browserURL,
                        new File[] { suiteFile }, resultDir, timeoutInSeconds);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * @param browser 
     * @param browserURL 
     * @param suiteDirPath 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuiteDir(String browser, String browserURL,
            String suiteDirPath, int timeoutInSeconds) throws IOException {

        addHTMLSuiteDir(browser, browserURL, new File(suiteDirPath),
                timeoutInSeconds);
    }

    /**
     * @param browser 
     * @param browserURL 
     * @param suiteDirPath 
     * @param resultDirPath 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuiteDir(String browser, String browserURL,
            String suiteDirPath, String resultDirPath, int timeoutInSeconds)
            throws IOException {

        addHTMLSuiteDir(browser, browserURL, new File(suiteDirPath), new File(
                resultDirPath), timeoutInSeconds);
    }

    /**
     * @param browser 
     * @param browserURL 
     * @param suiteDir 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuiteDir(String browser, String browserURL,
            File suiteDir, int timeoutInSeconds) throws IOException {

        File[] suiteFiles = collectSuiteFiles(suiteDir);
        if (suiteFiles.length == 0) {
            throw new IOException("Can't find HTML Suite dir:" + suiteDir);
        }
        addHTMLSuites(browser, browserURL, suiteFiles, timeoutInSeconds);
    }

    /**
     * @param browser 
     * @param browserURL 
     * @param suiteDir 
     * @param resultDir 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuiteDir(String browser, String browserURL,
            File suiteDir, File resultDir, int timeoutInSeconds)
            throws IOException {

        File[] suiteFiles = collectSuiteFiles(suiteDir);
        if (suiteFiles.length == 0) {
            throw new IOException("Can't find HTML Suite dir:" + suiteDir);
        }
        addHTMLSuites(browser, browserURL, suiteFiles, resultDir,
                timeoutInSeconds);
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * @param browser 
     * @param browserURL 
     * @param suiteFilePaths 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuites(String browser, String browserURL,
            String[] suiteFilePaths, int timeoutInSeconds) throws IOException {

        addHTMLSuites(browser, browserURL, toFiles(suiteFilePaths),
                timeoutInSeconds);
    }

    /**
     * @param browser 
     * @param browserURL 
     * @param suiteFiles 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuites(String browser, String browserURL,
            File[] suiteFiles, int timeoutInSeconds) throws IOException {

        addHTMLSuites(browser, browserURL, suiteFiles, (File[]) null,
                timeoutInSeconds);
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * @param browser 
     * @param browserURL 
     * @param suiteFilePaths 
     * @param resultDirPath 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuites(String browser, String browserURL,
            String[] suiteFilePaths, String resultDirPath, int timeoutInSeconds)
            throws IOException {

        addHTMLSuites(browser, browserURL, toFiles(suiteFilePaths), new File(
                resultDirPath), timeoutInSeconds);
    }

    /**
     * @param browser 
     * @param browserURL 
     * @param suiteFiles 
     * @param resultDir 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuites(String browser, String browserURL,
            File[] suiteFiles, File resultDir, int timeoutInSeconds)
            throws IOException {

        addHTMLSuites(browser, browserURL, suiteFiles, createResultFiles(
                resultDir, suiteFiles), timeoutInSeconds);
    }

    /**
     * @param browser 
     * @param browserURL 
     * @param suiteFiles 
     * @param resultFiles 
     * @param timeoutInSeconds 
     * @throws IOException 
     */
    public void addHTMLSuites(String browser, String browserURL,
            File[] suiteFiles, File[] resultFiles, int timeoutInSeconds)
            throws IOException {

        for (int i = 0; i < suiteFiles.length; i++) {
            if (!suiteFiles[i].exists() || suiteFiles[i].isDirectory()) {
                throw new IOException("Can't find Result file:"
                        + suiteFiles[i].getAbsolutePath());
            }
            _addHTMLSuite(browser, browserURL, suiteFiles[i],
                    (resultFiles != null) ? resultFiles[i] : null,
                    timeoutInSeconds);
        }
    }

    /**
     * @param browser 
     * @param browserURL 
     * @param suiteFile 
     * @param resultFile 
     * @param timeoutInSeconds 
     */
    private void _addHTMLSuite(String browser, String browserURL,
            File suiteFile, File resultFile, int timeoutInSeconds) {

        HTMLSuite htmlSuite = new HTMLSuite();
        htmlSuite.setBrowser(browser);
        htmlSuite.setBrowserURL(browserURL);
        htmlSuite.setSuiteFile(suiteFile);
        if (resultFile != null) {
            htmlSuite.setResultFile(resultFile);
        }
        htmlSuite.setTimeoutInSeconds(timeoutInSeconds);
        htmlSuiteList.add(htmlSuite);
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * @return result
     * @throws IOException
     */
    public boolean runHTMLSuites() throws IOException {

        startTime = System.currentTimeMillis();

        HTMLSuiteLauncher launcher = new HTMLSuiteLauncher(server);

        boolean tempResult = true;
        for (HTMLSuite htmlSuite : htmlSuiteList) {
            boolean passed = false;

            htmlSuite.setStatus(HTMLSuite.STATUS_RUN);
            if (htmlSuite.getResultFile() != null) {
                passed = launcher.runHTMLSuite(htmlSuite.getBrowser(),
                        htmlSuite.getBrowserURL(), htmlSuite.getSuiteFile(),
                        htmlSuite.getResultFile(), htmlSuite
                                .getTimeoutInSeconds());
            } else {
                passed = launcher.runHTMLSuite(htmlSuite.getBrowser(),
                        htmlSuite.getBrowserURL(), htmlSuite.getSuiteFile(),
                        htmlSuite.getTimeoutInSeconds());
            }
            htmlSuite.setStatus(HTMLSuite.STATUS_FINISH);

            htmlSuite.setTestResults(launcher.getResults());
            htmlSuite.setPassed(passed);

            if (passed) {
                passedCount++;
            } else {
                failedCount++;
                tempResult = false;
            }
        }
        endTime = System.currentTimeMillis();

        result = tempResult;
        return result;
    }

    ///////////////////////////////////////////////////////////////////////

    /**
     * @param suiteDir
     * @return suiteFiles
     * @throws IOException 
     */
    private File[] collectSuiteFiles(File suiteDir) throws IOException {

        if (!suiteDir.exists() || !suiteDir.isDirectory()) {
            throw new IOException("Can't find HTML Suite dir:"
                    + suiteDir.getAbsolutePath());
        }

        return suiteDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile()
                        && pathname.getName().matches(".*[Ss]uite.*\\.html");
            }
        });
    }

    /**
     * @param testCaseDir
     * @return suiteFile
     * @throws IOException
     */
    private File generateHTMLSute(File testCaseDir) throws IOException {

        File suiteFile = null;
        if (!testCaseDir.isDirectory()) {
            suiteFile = testCaseDir;
            testCaseDir = testCaseDir.getParentFile();
        }

        if (!testCaseDir.exists()) {
            throw new IOException("Can't find Test Case dir:"
                    + testCaseDir.getAbsolutePath());
        }

        File[] testCaseFiles = testCaseDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile()
                        && pathname.getName().matches(".*[Cc]ase.*\\.html");
            }
        });
        Arrays.sort(testCaseFiles);

        if (suiteFile == null) {
            suiteFile = new File(testCaseDir, "generatedTestSuite.html");
        }
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(suiteFile), "UTF-8"));
        try {
            writer
                    .write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            writer.write("<html><head>");
            writer
                    .write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
            writer.write("<title>Generated Test Suite</title>");
            writer.write("</head>");
            writer.write("<body>");
            writer.write("<table border=\"1\">");
            writer.write("<tr><td>Generated Test Suite</td></tr>");
            for (File testCase : testCaseFiles) {
                writer.write("<tr><td><a href=\"" + testCase.getName() + "\">"
                        + testCase.getName() + "</a></td></tr>");
            }
            writer.write("</table>");
            writer.write("</body></html>");

        } finally {
            writer.close();
        }
        log.info("Generate HTMLSuite[" + suiteFile.getName()
                + "] testCase count[" + testCaseFiles.length + "]");
        return suiteFile;
    }

    /**
     * @param resultDir
     * @param suiteFiles
     * @return resultFiles
     * @throws IOException 
     */
    private File[] createResultFiles(File resultDir, File[] suiteFiles)
            throws IOException {

        if (!resultDir.exists() || !resultDir.isDirectory()) {
            throw new IOException("Can't find Result dir:"
                    + resultDir.getAbsolutePath());
        }

        if (!resultDir.canWrite()) {
            throw new IOException("Can't write to Result dir: "
                    + resultDir.getAbsolutePath());
        }

        File[] resultFiles = new File[suiteFiles.length];
        for (int i = 0; i < suiteFiles.length; i++) {
            resultFiles[i] = createResultFile(resultDir, suiteFiles[i]);
        }
        return resultFiles;
    }

    /**
     * @param resultDir
     * @param suiteFile
     * @return resultFile
     */
    private File createResultFile(File resultDir, File suiteFile) {

        String suiteFileName = suiteFile.getName();
        int index = suiteFileName.lastIndexOf('.');

        return new File(resultDir, suiteFileName.substring(0, index)
                + "_result" + suiteFileName.substring(index));
    }

    /**
     * @param filePaths
     * @return files
     */
    private File[] toFiles(String[] filePaths) {

        File[] files = new File[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            files[i] = new File(filePaths[i]);
        }
        return files;
    }

    /**
     * @param browser
     * @param resultDirPath
     * @return resultDir
     * @throws IOException 
     */
    private File createResultDir(String browser, String resultDirPath)
            throws IOException {

        File resultDir = new File(resultDirPath, browser.substring(1)
                .replaceAll(" .*", ""));
        if (!resultDir.exists() || !resultDir.isDirectory()) {
            if (!resultDir.mkdir()) {
                throw new IOException("Can't make Result dir:"
                        + resultDir.getAbsolutePath());
            }
        }
        return resultDir;
    }

    /**
     * @return htmlSuiteList
     */
    public ArrayList<HTMLSuite> getHtmlSuiteList() {
        return htmlSuiteList;
    }

    /**
     * @return result
     */
    public boolean getResult() {
        return result;
    }

    /**
     * @return failedCount
     */
    public int getFailedCount() {
        return failedCount;
    }

    /**
     * @return passedCount
     */
    public int getPassedCount() {
        return passedCount;
    }

    /**
     * @return endTime
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * @return startTime
     */
    public long getStartTime() {
        return startTime;
    }
}

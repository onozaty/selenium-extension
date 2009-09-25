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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.SeleniumServer;

/**
 * @author onozaty
 */
public class MultiHTMLSuiteRunner {

    /** logger */
    private static Log log = LogFactory.getLog(HTMLSuiteLauncher.class);

    /** path separator */
    private static final String PATH_SEPARATOR = "/";

    /** test suite regexp */
    static final Pattern TEST_SUITE_REGEXP = Pattern
            .compile(".*[Ss]uite.*\\.html");

    /** test case regexp */
    static final Pattern TEST_CASE_REGEXP = Pattern
            .compile(".*[Cc]ase.*\\.html");

    /** SeleniumServer */
    private SeleniumServer server;

    /** htmlSuites */
    private CopyOnWriteArrayList<HTMLSuite> htmlSuiteList = new CopyOnWriteArrayList<HTMLSuite>();

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

        HTMLSuiteConfiguration configuration = HTMLSuiteConfiguration
                .Load(properties);

        if (configuration.getProxyHost() != null) {
            System.setProperty("http.proxyHost", configuration.getProxyHost());
        }
        if (configuration.getProxyPort() != null) {
            System.setProperty("http.proxyPort", configuration.getProxyPort());
        }

        SeleniumServer seleniumServer = new SeleniumServer(configuration);

        if (configuration.getUserExtensions() != null) {
            seleniumServer.addNewStaticContent(configuration
                    .getUserExtensions().getParentFile());
        }

        try {
            seleniumServer.start();

            MultiHTMLSuiteRunner htmlSuiteRunner = new MultiHTMLSuiteRunner(
                    seleniumServer);
            if (configuration.isGenerateSuite()) {
                htmlSuiteRunner.addHTMLSuiteGenerate(configuration
                        .getBrowsers(), configuration.getStartURL(),
                        configuration.getSuite(), configuration.getResult(),
                        configuration.getTimeoutInSeconds());
            } else {
                htmlSuiteRunner.addHTMLSuites(configuration.getBrowsers(),
                        configuration.getStartURL(), configuration.getSuite(),
                        configuration.getResult(), configuration
                                .getTimeoutInSeconds());
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

        addHTMLSuiteGenerate(browsers, browserURL, testCaseDirPath, null,
                timeoutInSeconds);
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

        addHTMLSuiteGenerate(browsers, browserURL, new File(testCaseDirPath),
                toFile(resultDirPath), timeoutInSeconds);
    }

    /**
     * @param browsers
     * @param browserURL
     * @param testCaseDir
     * @param resultDir
     * @param timeoutInSeconds
     * @throws IOException
     */
    public void addHTMLSuiteGenerate(String[] browsers, String browserURL,
            File testCaseDir, File resultDir, int timeoutInSeconds)
            throws IOException {

        File[] suiteFiles = generateHTMLSutes(testCaseDir);

        for (int i = 0; i < browsers.length; i++) {
            File resultDirByBrowser = createResultDirByBrowser(browsers[i],
                    resultDir);
            addHTMLSuites(browsers[i], browserURL, suiteFiles,
                    createResultFiles(resultDirByBrowser, suiteFiles),
                    timeoutInSeconds);
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

        addHTMLSuites(browsers, browserURL, suitePath, null, timeoutInSeconds);
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

        addHTMLSuites(browsers, browserURL, new File(suitePath),
                toFile(resultDirPath), timeoutInSeconds);
    }

    /**
     * @param browsers
     * @param browserURL
     * @param suiteFile
     * @param resultDir
     * @param timeoutInSeconds
     * @throws IOException
     */
    public void addHTMLSuites(String[] browsers, String browserURL,
            File suiteFile, File resultDir, int timeoutInSeconds)
            throws IOException {

        for (int i = 0; i < browsers.length; i++) {
            File resultDirByBrowser = createResultDirByBrowser(browsers[i],
                    resultDir);
            if (suiteFile.isDirectory()) {
                addHTMLSuiteDir(browsers[i], browserURL, suiteFile,
                        resultDirByBrowser, timeoutInSeconds);
            } else {
                addHTMLSuites(browsers[i], browserURL,
                        new File[] { suiteFile }, resultDirByBrowser,
                        timeoutInSeconds);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////
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

        addHTMLSuiteDir(browser, browserURL, new File(suiteDirPath), null,
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

        addHTMLSuiteDir(browser, browserURL, new File(suiteDirPath),
                toFile(resultDirPath), timeoutInSeconds);
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

        addHTMLSuiteDir(browser, browserURL, suiteDir, null, timeoutInSeconds);
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

        addHTMLSuites(browser, browserURL, suiteFilePaths, null,
                timeoutInSeconds);
    }

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

        addHTMLSuites(browser, browserURL, toFiles(suiteFilePaths),
                toFile(resultDirPath), timeoutInSeconds);
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

        addHTMLSuites(browser, browserURL, suiteFiles, (File) null,
                timeoutInSeconds);
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
                throw new IOException("Can't find Suite file:"
                        + suiteFiles[i].getAbsolutePath());
            }
            _addHTMLSuite(browser, browserURL, suiteFiles[i],
                    (resultFiles != null) ? resultFiles[i] : null,
                    timeoutInSeconds);
        }
    }

    ///////////////////////////////////////////////////////////////////////

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

            passed = launcher.runHTMLSuiteResult(htmlSuite.getBrowser(),
                    htmlSuite.getBrowserURL(), htmlSuite.getSuiteFile(),
                    htmlSuite.getResultFile(), htmlSuite.getTimeoutInSeconds(),
                    !server.getConfiguration().isSingleWindow());

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
                        && TEST_SUITE_REGEXP.matcher(pathname.getName())
                                .matches();
            }
        });
    }

    /**
     * @param testCaseDir
     * @return suiteFile
     * @throws IOException
     */
    private File[] generateHTMLSutes(File testCaseDir) throws IOException {

        File suiteFile = null;
        if (!testCaseDir.isDirectory()) {
            suiteFile = testCaseDir;
            testCaseDir = testCaseDir.getParentFile();
        }

        if (!testCaseDir.exists()) {
            throw new IOException("Can't find Test Case dir:"
                    + testCaseDir.getAbsolutePath());
        }

        ArrayList<File> testSuiteList = new ArrayList<File>();

        File[] testCaseFiles = collectTestCaseFiles(testCaseDir);
        if (testCaseFiles.length != 0) {

            if (suiteFile == null) {
                suiteFile = new File(testCaseDir, "generatedTestSuite.html");
            }
            generateHTMLSute(suiteFile, testCaseFiles, null);
            testSuiteList.add(suiteFile);

        } else {

            File[] childDirs = testCaseDir.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.isHidden();
                }
            });
            Arrays.sort(childDirs);

            for (File childDir : childDirs) {
                File childSuiteFile = new File(testCaseDir, childDir.getName()
                        + ".html");
                generateHTMLSute(childSuiteFile,
                        collectTestCaseFiles(childDir), childDir.getName()
                                + PATH_SEPARATOR);
                testSuiteList.add(childSuiteFile);
            }
        }

        log.info("Generate HTMLSuites total count[" + testSuiteList.size()
                + "]");

        return testSuiteList.toArray(new File[testSuiteList.size()]);
    }

    /**
     * @param suiteFile
     * @param testCaseFiles
     * @param pathDiff
     * @throws IOException
     */
    private void generateHTMLSute(File suiteFile, File[] testCaseFiles,
            String pathDiff) throws IOException {

        if (pathDiff == null) {
            pathDiff = "";
        }

        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(suiteFile), "UTF-8"));
        try {
            writer
                    .write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            writer.write("<html><head>");
            writer
                    .write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
            writer.write("<title>" + suiteFile.getName() + "</title>");
            writer.write("</head>");
            writer.write("<body>");
            writer.write("<table border=\"1\">");
            writer.write("<tr><td>" + suiteFile.getName() + "</td></tr>");
            for (File testCase : testCaseFiles) {
                writer.write("<tr><td><a href=\"./" + pathDiff
                        + testCase.getName() + "\">" + pathDiff
                        + testCase.getName() + "</a></td></tr>");
            }
            writer.write("</table>");
            writer.write("</body></html>");

        } finally {
            writer.close();
        }
        log.info("Generate HTMLSuite[" + suiteFile.getName()
                + "] testCase count[" + testCaseFiles.length + "]");
    }

    /**
     * @param testCaseDir
     * @return testCase files
     */
    private File[] collectTestCaseFiles(File testCaseDir) {

        File[] testCaseFiles = testCaseDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile()
                        && TEST_CASE_REGEXP.matcher(pathname.getName())
                                .matches();
            }
        });
        Arrays.sort(testCaseFiles);
        return testCaseFiles;
    }

    /**
     * @param resultDir
     * @param suiteFiles
     * @return resultFiles
     * @throws IOException
     */
    private File[] createResultFiles(File resultDir, File[] suiteFiles)
            throws IOException {

        if (resultDir == null) {
            return null;
        }

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

        if (resultDir == null) {
            return null;
        }

        String suiteFileName = suiteFile.getName();
        int index = suiteFileName.lastIndexOf('.');

        return new File(resultDir, suiteFileName.substring(0, index)
                + "_result" + suiteFileName.substring(index));
    }

    /**
     * @param filePath
     * @return file
     */
    private File toFile(String filePath) {

        if (filePath == null) {
            return null;
        }
        return new File(filePath);
    }

    /**
     * @param filePaths
     * @return files
     */
    private File[] toFiles(String[] filePaths) {

        File[] files = new File[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            files[i] = toFile(filePaths[i]);
        }
        return files;
    }

    /**
     * @param browser
     * @param resultDir
     * @return resultDir
     * @throws IOException
     */
    private File createResultDirByBrowser(String browser, File resultDir)
            throws IOException {

        if (resultDir == null) {
            return null;
        }

        File resultDirByBrowser = new File(resultDir, browser.substring(1)
                .replaceAll(" .*", ""));
        if (!resultDirByBrowser.exists() || !resultDirByBrowser.isDirectory()) {
            if (!resultDirByBrowser.mkdir()) {
                throw new IOException("Can't make Result dir:"
                        + resultDirByBrowser.getAbsolutePath());
            }
        }
        return resultDirByBrowser;
    }

    /**
     * @return htmlSuiteList
     */
    public CopyOnWriteArrayList<HTMLSuite> getHtmlSuiteList() {
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

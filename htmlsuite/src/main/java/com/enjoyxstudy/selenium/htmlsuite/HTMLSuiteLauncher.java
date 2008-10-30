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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.SeleniumCommandTimedOutException;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.BrowserSessionFactory.BrowserSessionInfo;
import org.openqa.selenium.server.browserlaunchers.AsyncExecute;
import org.openqa.selenium.server.browserlaunchers.BrowserLauncher;
import org.openqa.selenium.server.browserlaunchers.BrowserLauncherFactory;
import org.openqa.selenium.server.browserlaunchers.LauncherUtils;
import org.openqa.selenium.server.htmlrunner.HTMLResultsListener;
import org.openqa.selenium.server.htmlrunner.HTMLTestResults;

/**
 * base org.openqa.selenium.server.htmlrunner.HTMLLauncher
 * 
 * @author onozaty
 */
public class HTMLSuiteLauncher implements HTMLResultsListener {

    /** logger */
    private static Log log = LogFactory.getLog(HTMLSuiteLauncher.class);

    /** SeleniumServer */
    private SeleniumServer server;

    /** results */
    private HTMLTestResults results;

    /**
     * @param server
     */
    public HTMLSuiteLauncher(SeleniumServer server) {
        this.server = server;
    }

    /**
     * Launches a single HTML Selenium test suite.
     * 
     * @param browser - the browserString ("*firefox", "*iexplore" or an executable path)
     * @param browserURL - the start URL for the browser
     * @param suiteFilePath - a file containing the HTML suite to run
     * @param resultFilePath - The file to which we'll output the HTML results
     * @param timeoutInSeconds - the amount of time (in seconds) to wait for the browser to finish
     * @return result
     * @throws IOException if we can't read the write file
     */
    public boolean runHTMLSuite(String browser, String browserURL,
            String suiteFilePath, String resultFilePath, int timeoutInSeconds)
            throws IOException {

        return runHTMLSuite(browser, browserURL, new File(suiteFilePath),
                new File(resultFilePath), timeoutInSeconds);
    }

    /**
     * Launches a single HTML Selenium test suite.
     * 
     * @param browser - the browserString ("*firefox", "*iexplore" or an executable path)
     * @param browserURL - the start URL for the browser
     * @param suiteFile - a file containing the HTML suite to run
     * @param resultFile - The file to which we'll output the HTML results
     * @param timeoutInSeconds - the amount of time (in seconds) to wait for the browser to finish
     * @return result
     * @throws IOException if we can't read the write file
     */
    public boolean runHTMLSuite(String browser, String browserURL,
            File suiteFile, File resultFile, int timeoutInSeconds)
            throws IOException {

        resultFile.createNewFile();
        if (!resultFile.canWrite()) {
            throw new IOException("Can't write to outputFile: "
                    + resultFile.getAbsolutePath());
        }

        boolean result = runHTMLSuite(browser, browserURL, suiteFile,
                timeoutInSeconds);

        FileWriter fileWriter = new FileWriter(resultFile);
        try {
            results.write(fileWriter);
        } finally {
            fileWriter.close();
        }

        return result;
    }

    /**
     * Launches a single HTML Selenium test suite.
     * 
     * @param browser - the browserString ("*firefox", "*iexplore" or an executable path)
     * @param browserURL - the start URL for the browser
     * @param suiteFilePath - a file containing the HTML suite to run
     * @param timeoutInSeconds - the amount of time (in seconds) to wait for the browser to finish
     * @return result
     * @throws IOException if we can't read the write file
     */
    public boolean runHTMLSuite(String browser, String browserURL,
            String suiteFilePath, int timeoutInSeconds) throws IOException {

        return runHTMLSuite(browser, browserURL, new File(suiteFilePath),
                timeoutInSeconds);
    }

    /**
     * Launches a single HTML Selenium test suite.
     * 
     * @param browser - the browserString ("*firefox", "*iexplore" or an executable path)
     * @param browserURL - the start URL for the browser
     * @param suiteFile - a file containing the HTML suite to run
     * @param timeoutInSeconds - the amount of time (in seconds) to wait for the browser to finish
     * @return result
     * @throws IOException if we can't read the write file
     */
    public boolean runHTMLSuite(String browser, String browserURL,
            File suiteFile, int timeoutInSeconds) throws IOException {

        if (browser == null)
            throw new IllegalArgumentException("browser may not be null");
        if (!suiteFile.exists()) {
            throw new IOException("Can't find HTML Suite file:"
                    + suiteFile.getAbsolutePath());
        }
        if (!suiteFile.canRead()) {
            throw new IOException("Can't read HTML Suite file: "
                    + suiteFile.getAbsolutePath());
        }
        if (!suiteFile.isAbsolute()) {
            suiteFile = suiteFile.getAbsoluteFile();
        }

        server.addNewStaticContent(suiteFile.getParentFile());

        // DGF this is a hack, but I can't find a better place to put it
        String suiteURL;
        if (browser.startsWith("*chrome") || browser.startsWith("*iehta")) {
            suiteURL = "http://localhost:"
                    + SeleniumServer.getPortDriversShouldContact()
                    + "/selenium-server/tests/" + suiteFile.getName();
        } else {
            suiteURL = LauncherUtils.stripStartURL(browserURL)
                    + "/selenium-server/tests/" + suiteFile.getName();
        }
        return run(browser, browserURL, suiteURL, timeoutInSeconds);
    }

    /**
     * Launches a single HTML Selenium test suite.
     * 
     * @param browser - the browserString ("*firefox", "*iexplore" or an executable path)
     * @param browserURL - the start URL for the browser
     * @param suiteURL - the relative URL to the HTML suite
     * @param timeoutInSeconds - the amount of time (in seconds) to wait for the browser to finish
     * @return result
     */
    private boolean run(String browser, String browserURL, String suiteURL,
            int timeoutInSeconds) {

        String suiteName = suiteURL.substring(suiteURL.lastIndexOf('/') + 1);
        log.info("HTML Suite Start. suite[" + suiteName + "] browser["
                + browser + "]");

        results = null;

        long timeoutInMs = 1000l * timeoutInSeconds;
        if (timeoutInMs < 0) {
            log
                    .warn("Looks like the timeout overflowed, so resetting it to the maximum.");
            timeoutInMs = Long.MAX_VALUE;
        }
        server.handleHTMLRunnerResults(this);
        BrowserLauncherFactory blf = new BrowserLauncherFactory();
        String sessionId = Long.toString(System.currentTimeMillis() % 1000000);
        BrowserLauncher launcher = blf.getBrowserLauncher(browser, sessionId);

        BrowserSessionInfo sessionInfo = new BrowserSessionInfo(sessionId,
                browser, browserURL, launcher, null);
        server.registerBrowserSession(sessionInfo);

        // JB: -- aren't these URLs in the wrong order according to declaration?
        launcher.launchHTMLSuite(suiteURL, browserURL, server.isMultiWindow(),
                "info");
        long now = System.currentTimeMillis();
        long end = now + timeoutInMs;
        while (results == null && System.currentTimeMillis() < end) {
            AsyncExecute.sleepTight(500);
        }
        launcher.close();
        server.deregisterBrowserSession(sessionInfo);
        if (results == null) {
            throw new SeleniumCommandTimedOutException();
        }

        boolean result = results.getResult().toUpperCase().equals("PASSED");

        String endMessage = "HTML Suite End. result[" + results.getResult()
                + "] suite[" + suiteName + "] browser[" + browser + "]";

        if (result) {
            log.info(endMessage);
        } else {
            log.warn(endMessage);
        }
        return result;
    }

    /**
     * Accepts HTMLTestResults for later asynchronous handling 
     * @param resultsParm 
     */
    public void processResults(HTMLTestResults resultsParm) {
        this.results = resultsParm;
    }

    /**
     * @return results
     */
    public HTMLTestResults getResults() {
        return results;
    }

}

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
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.htmlrunner.HTMLLauncher;
import org.openqa.selenium.server.htmlrunner.HTMLTestResults;

/**
 * @author onozaty
 */
public class HTMLSuiteLauncher extends HTMLLauncher {

    /** result passed string */
    private static final String RESULT_PASSED = "PASSED";

    /** create temp result file use prefix */
    private static final String TEMP_RESULT_FILE_PREFIX = "selenium";

    /** logger */
    private static Log log = LogFactory.getLog(HTMLSuiteLauncher.class);

    /** results */
    private HTMLTestResults results;

    /**
     * @param remoteControl
     */
    public HTMLSuiteLauncher(SeleniumServer remoteControl) {
        super(remoteControl);
    }

    /**
     * Launches a single HTML Selenium test suite.
     * 
     * @param browser - the browserString ("*firefox", "*iexplore" or an executable path)
     * @param browserURL - the start URL for the browser
     * @param suiteFile - a file containing the HTML suite to run
     * @param timeoutInSeconds - the amount of time (in seconds) to wait for the browser to finish
     * @param multiWindow 
     * @return result
     * @throws IOException if we can't read the write file
     */
    public boolean runHTMLSuiteResult(String browser, String browserURL,
            File suiteFile, int timeoutInSeconds, boolean multiWindow)
            throws IOException {

        return runHTMLSuiteResult(browser, browserURL, suiteFile, null,
                timeoutInSeconds, multiWindow);
    }

    /**
     * Launches a single HTML Selenium test suite.
     * 
     * @param browser - the browserString ("*firefox", "*iexplore" or an executable path)
     * @param browserURL - the start URL for the browser
     * @param suiteFile - a file containing the HTML suite to run
     * @param resultFile - The file to which we'll output the HTML results
     * @param timeoutInSeconds - the amount of time (in seconds) to wait for the browser to finish
     * @param multiWindow 
     * @return result
     * @throws IOException if we can't read the write file
     */
    public boolean runHTMLSuiteResult(String browser, String browserURL,
            File suiteFile, File resultFile, int timeoutInSeconds,
            boolean multiWindow) throws IOException {

        log.info("HTML Suite Start. suite[" + suiteFile + "] browser["
                + browser + "]");

        processResults(null); // init result

        if (!suiteFile.isAbsolute()) {
            suiteFile = suiteFile.getAbsoluteFile();
        }

        boolean isResultTemprary = false;
        if (resultFile == null) {
            resultFile = File.createTempFile(TEMP_RESULT_FILE_PREFIX, null);
            isResultTemprary = true;
        }

        String resultString;
        try {
            resultString = runHTMLSuite(browser, browserURL, suiteFile,
                    resultFile, timeoutInSeconds, multiWindow);
        } finally {
            if (isResultTemprary) {
                resultFile.delete();
            }
        }

        boolean result = resultString.equals(RESULT_PASSED);

        String endMessage = "HTML Suite End. result[" + resultString
                + "] suite[" + suiteFile + "] browser[" + browser + "]";

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
    @Override
    public void processResults(HTMLTestResults resultsParm) {
        super.processResults(resultsParm);
        this.results = resultsParm;
    }

    /**
     * @return results
     */
    public HTMLTestResults getResults() {
        return results;
    }

}

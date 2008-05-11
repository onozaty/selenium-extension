/*
 * Copyright 2007 onozaty (http://www.enjoyxstudy.com)
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
import java.io.Serializable;

import org.openqa.selenium.server.htmlrunner.HTMLTestResults;

/**
 * @author onozaty
 */
public class HTMLSuite implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -2119102756934000899L;

    /** status wait */
    public static final int STATUS_WAIT = 0;

    /** status run */
    public static final int STATUS_RUN = 1;

    /** status finish */
    public static final int STATUS_FINISH = 2;

    /** suiteFile */
    private File suiteFile;

    /** browser */
    private String browser;

    /** browserURL */
    private String browserURL;

    /** timeoutInSeconds */
    private int timeoutInSeconds;

    /** testResults */
    private HTMLTestResults testResults;

    /** resultFile */
    private File resultFile;

    /** passed */
    private boolean passed;

    /** status */
    private int status;

    /**
     * @return browser
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * @param browser
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * @return browserURL
     */
    public String getBrowserURL() {
        return browserURL;
    }

    /**
     * @param browserURL
     */
    public void setBrowserURL(String browserURL) {
        this.browserURL = browserURL;
    }

    /**
     * @return resultFile
     */
    public File getResultFile() {
        return resultFile;
    }

    /**
     * @param resultFile
     */
    public void setResultFile(File resultFile) {
        this.resultFile = resultFile;
    }

    /**
     * @return suiteFile
     */
    public File getSuiteFile() {
        return suiteFile;
    }

    /**
     * @param suiteFile
     */
    public void setSuiteFile(File suiteFile) {
        this.suiteFile = suiteFile;
    }

    /**
     * @return testResults
     */
    public HTMLTestResults getTestResults() {
        return testResults;
    }

    /**
     * @param testResults
     */
    public void setTestResults(HTMLTestResults testResults) {
        this.testResults = testResults;
    }

    /**
     * @return timeoutInSeconds
     */
    public int getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    /**
     * @param timeoutInSeconds
     */
    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }

    /**
     * @return passed
     */
    public boolean isPassed() {
        return passed;
    }

    /**
     * @param passed
     */
    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    /**
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status status
     */
    public void setStatus(int status) {
        this.status = status;
    }

}

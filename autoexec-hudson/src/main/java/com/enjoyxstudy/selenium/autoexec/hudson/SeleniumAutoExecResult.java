/*
 * Copyright (c) 2008 onozaty (http://www.enjoyxstudy.com)
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
package com.enjoyxstudy.selenium.autoexec.hudson;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author onozaty
 */
public class SeleniumAutoExecResult implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 6857331953880672718L;

    /** serverUrl */
    private String serverUrl;

    /** passed */
    private boolean isPassed;

    /** passedCount */
    private int passedCount;

    /** failedCount */
    private int failedCount;

    /** total count */
    private int totalCount;

    /** startTime */
    private String startTime;

    /** endTime */
    private String endTime;

    /** suites */
    private ArrayList<Suite> suites;

    /**
     * @return serverUrl
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * @param serverUrl serverUrl
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * @return isPassed
     */
    public boolean isPassed() {
        return isPassed;
    }

    /**
     * @param isPassed isPassed
     */
    public void setPassed(boolean isPassed) {
        this.isPassed = isPassed;
    }

    /**
     * @return passedCount
     */
    public int getPassedCount() {
        return passedCount;
    }

    /**
     * @param passedCount passedCount
     */
    public void setPassedCount(int passedCount) {
        this.passedCount = passedCount;
    }

    /**
     * @return failedCount
     */
    public int getFailedCount() {
        return failedCount;
    }

    /**
     * @param failedCount failedCount
     */
    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    /**
     * @return totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return suites
     */
    public ArrayList<Suite> getSuites() {
        return suites;
    }

    /**
     * @param suites suites
     */
    public void setSuites(ArrayList<Suite> suites) {
        this.suites = suites;
    }

    /**
     * @author onozaty
     */
    public class Suite implements Serializable {

        /** serialVersionUID */
        private static final long serialVersionUID = -301770846827562197L;

        /** suiteName */
        private String suiteName;

        /** resultPath */
        private String resultPath;

        /** browser */
        private String browser;

        /** status */
        private String status;

        /**
         * @return suiteName
         */
        public String getSuiteName() {
            return suiteName;
        }

        /**
         * @param suiteName suiteName
         */
        public void setSuiteName(String suiteName) {
            this.suiteName = suiteName;
        }

        /**
         * @return resultPath
         */
        public String getResultPath() {
            return resultPath;
        }

        /**
         * @param resultPath resultPath
         */
        public void setResultPath(String resultPath) {
            this.resultPath = resultPath;
        }

        /**
         * @return browser
         */
        public String getBrowser() {
            return browser;
        }

        /**
         * @param browser browser
         */
        public void setBrowser(String browser) {
            this.browser = browser;
        }

        /**
         * @return status
         */
        public String getStatus() {
            return status;
        }

        /**
         * @param status status
         */
        public void setStatus(String status) {
            this.status = status;
        }

    }
}

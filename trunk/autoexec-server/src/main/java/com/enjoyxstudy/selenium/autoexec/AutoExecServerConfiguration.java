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
package com.enjoyxstudy.selenium.autoexec;

import java.io.File;
import java.util.Properties;

import com.enjoyxstudy.selenium.htmlsuite.HTMLSuiteConfiguration;
import com.enjoyxstudy.selenium.htmlsuite.util.PropertiesUtils;

/**
 * @author onozaty
 */
public class AutoExecServerConfiguration extends HTMLSuiteConfiguration {

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

    /** resultDir */
    private File resultDir;

    /** default resultDir name */
    private static final String DEFAULT_RESULT_DIR_NAME = "result";

    /** permanentResult */
    private boolean permanentResult = true;

    /** autoExecTime */
    private String autoExecTime;

    /** beforeCommand */
    private String beforeCommand;

    /** afterCommand */
    private String afterCommand;

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
     * @return beforeCommand
     */
    public String getBeforeCommand() {
        return beforeCommand;
    }

    /**
     * @param beforeCommand beforeCommand
     */
    public void setBeforeCommand(String beforeCommand) {
        this.beforeCommand = beforeCommand;
    }

    /**
     * @return afterCommand
     */
    public String getAfterCommand() {
        return afterCommand;
    }

    /**
     * @param afterCommand afterCommand
     */
    public void setAfterCommand(String afterCommand) {
        this.afterCommand = afterCommand;
    }

    /**
     * @param properties
     */
    public AutoExecServerConfiguration(Properties properties) {

        // generateSuite default true
        setGenerateSuite(true);

        HTMLSuiteConfiguration.Load(this, properties);

        suiteDir = new File(PropertiesUtils.getString(properties, "suiteDir",
                DEFAULT_SUITE_DIR_NAME));

        suiteRepo = PropertiesUtils.getString(properties, "suiteRepo",
                suiteRepo);
        suiteRepoUsername = PropertiesUtils.getString(properties,
                "suiteRepoUsername", suiteRepoUsername);
        suiteRepoPassword = PropertiesUtils.getString(properties,
                "suiteRepoPassword", suiteRepoPassword);

        resultDir = new File(PropertiesUtils.getString(properties, "resultDir",
                DEFAULT_RESULT_DIR_NAME));

        permanentResult = PropertiesUtils.getBoolean(properties,
                "permanentResult", permanentResult);

        autoExecTime = PropertiesUtils.getString(properties, "autoExecTime",
                autoExecTime);

        beforeCommand = PropertiesUtils.getString(properties, "beforeCommand",
                beforeCommand);
        afterCommand = PropertiesUtils.getString(properties, "afterCommand",
                afterCommand);

    }
}

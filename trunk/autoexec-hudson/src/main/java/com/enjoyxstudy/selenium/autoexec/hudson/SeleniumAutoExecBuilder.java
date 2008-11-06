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

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Result;
import hudson.tasks.Builder;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import com.enjoyxstudy.selenium.autoexec.client.RemoteControlClient;
import com.enjoyxstudy.selenium.autoexec.hudson.SeleniumAutoExecResult.Suite;

/**
 * @author onozaty
 */
public class SeleniumAutoExecBuilder extends Builder {

    /** server url */
    private final String serverUrl;

    /** base url */
    private final String baseUrl;

    /**
     * @param serverUrl
     * @throws MalformedURLException 
     */
    SeleniumAutoExecBuilder(String serverUrl) throws MalformedURLException {
        this.serverUrl = serverUrl;

        URL url = new URL(serverUrl);
        baseUrl = url.getProtocol()
                + "://"
                + url.getHost()
                + ((url.getPort() != -1) ? ":" + String.valueOf(url.getPort())
                        : "");
    }

    /**
     * @return serverUrl
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * @see hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build,
            @SuppressWarnings("unused")
            Launcher launcher, BuildListener listener) throws IOException {

        PrintStream logger = listener.getLogger();

        logger.println("Test Selenium Auto Exec Server(" + serverUrl + ").");

        String resultString;
        try {
            resultString = new RemoteControlClient(serverUrl.replaceAll("/$",
                    "")
                    + "/command/").runString(RemoteControlClient.TYPE_JSON);
        } catch (IOException e) {
            logger.println("Error return from Selenium Auto Exec Server("
                    + serverUrl + ").");
            throw e;
        }

        SeleniumAutoExecResult result = parseResult(resultString);

        logger.println("result: " + (result.isPassed() ? "passed" : "failed"));
        logger.println("number of cases: passed: " + result.getPassedCount()
                + " / failed: " + result.getFailedCount() + " / total: "
                + result.getTotalCount());

        if (!result.isPassed()) {
            build.setResult(Result.UNSTABLE);
        }

        SeleniumAutoExecAction action = build
                .getAction(SeleniumAutoExecAction.class);

        if (action == null) {
            action = new SeleniumAutoExecAction(build);
            build.addAction(action);
        }
        action.getResultList().add(result);

        return true;
    }

    /**
     * @see hudson.model.Describable#getDescriptor()
     */
    public Descriptor<Builder> getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * Descriptor should be singleton.
     */
    public static DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    /**
     * @param resultString
     * @return result
     */
    private SeleniumAutoExecResult parseResult(String resultString) {

        JSONObject resultJson = JSONObject.fromObject(resultString);

        SeleniumAutoExecResult result = new SeleniumAutoExecResult();

        result.setServerUrl(serverUrl);
        result.setPassed(resultJson.get("result").equals(
                RemoteControlClient.PASSED));
        result.setTotalCount(resultJson.getInt("totalCount"));
        result.setPassedCount(resultJson.getInt("passedCount"));
        result.setFailedCount(resultJson.getInt("failedCount"));
        result.setStartTime(resultJson.getString("startTime"));
        result.setEndTime(resultJson.getString("endTime"));

        JSONArray suitesJson = resultJson.getJSONArray("suites");

        ArrayList<Suite> suites = new ArrayList<Suite>();

        for (Iterator<?> iterator = suitesJson.iterator(); iterator.hasNext();) {
            JSONObject suiteJson = (JSONObject) iterator.next();

            Suite suite = result.new Suite();

            suite.setSuiteName(suiteJson.getString("suiteName"));
            suite.setBrowser(suiteJson.getString("browser"));
            suite.setStatus(suiteJson.getString("status"));
            suite.setResultPath(baseUrl + suiteJson.getString("resultPath"));

            suites.add(suite);
        }

        result.setSuites(suites);

        return result;
    }

    /**
     * @author onozaty
     */
    public static final class DescriptorImpl extends Descriptor<Builder> {

        /**
         * constructor.
         */
        DescriptorImpl() {
            super(SeleniumAutoExecBuilder.class);
        }

        /**
         * This human readable name is used in the configuration screen.
         * @return display name
         */
        @Override
        public String getDisplayName() {
            return "Selenium Auto Exec Server";
        }

        /**
         * @throws FormException 
         * @see hudson.model.Descriptor#newInstance(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
         */
        @Override
        public Builder newInstance(StaplerRequest req,
                @SuppressWarnings("unused")
                JSONObject formData) throws FormException {
            try {
                return new SeleniumAutoExecBuilder(req
                        .getParameter("selenium_autoexec.serverUrl"));
            } catch (MalformedURLException e) {
                throw new FormException(e, "illegal url");
            }
        }
    }
}

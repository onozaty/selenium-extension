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

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import com.enjoyxstudy.selenium.autoexec.client.RemoteControlClient;

/**
 * @author onozaty
 */
public class SeleniumAutoExecBuilder extends Builder {

    /** url */
    private final String url;

    /**
     * @param url
     */
    SeleniumAutoExecBuilder(String url) {
        this.url = url;
    }

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @see hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build,
            @SuppressWarnings("unused")
            Launcher launcher, BuildListener listener) throws IOException {

        PrintStream logger = listener.getLogger();

        logger.println("Test Selenium Auto Exec Server(" + url + ").");

        String result;
        try {
            result = new RemoteControlClient(url.replaceAll("/$", "")
                    + "/command/").runString();
        } catch (IOException e) {
            logger.println("Error return from Selenium Auto Exec Server(" + url
                    + ").");
            throw e;
        }
        logger.println(result);

        if (!RemoteControlClient.isPassedResult(result)) {
            build.setResult(Result.UNSTABLE);
        }
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
         * @see hudson.model.Descriptor#newInstance(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
         */
        @Override
        public Builder newInstance(StaplerRequest req,
                @SuppressWarnings("unused")
                JSONObject formData) {
            return new SeleniumAutoExecBuilder(req
                    .getParameter("selenium_autoexec.url"));
        }
    }
}

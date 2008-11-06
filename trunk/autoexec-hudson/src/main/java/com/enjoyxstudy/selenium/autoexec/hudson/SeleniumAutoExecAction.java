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

import java.util.ArrayList;

import hudson.model.AbstractBuild;
import hudson.model.Action;

/**
 * @author onozaty
 */
public class SeleniumAutoExecAction implements Action {

    /** build */
    private AbstractBuild<?, ?> build;

    /** result */
    private ArrayList<SeleniumAutoExecResult> resultList = new ArrayList<SeleniumAutoExecResult>();

    /**
     * @param build 
     */
    public SeleniumAutoExecAction(AbstractBuild<?, ?> build) {
        super();
        this.build = build;
    }

    /**
     * @see hudson.model.Action#getDisplayName()
     */
    public String getDisplayName() {
        return "Selenium AES Results";
    }

    /**
     * @see hudson.model.Action#getIconFileName()
     */
    public String getIconFileName() {
        return "clipboard.gif";
    }

    /**
     * @see hudson.model.Action#getUrlName()
     */
    public String getUrlName() {
        return "seleniumaes";
    }

    /**
     * @return build
     */
    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    /**
     * @return resultList
     */
    public ArrayList<SeleniumAutoExecResult> getResultList() {
        return resultList;
    }

}

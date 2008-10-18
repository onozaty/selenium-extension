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

import hudson.Plugin;
import hudson.tasks.BuildStep;

/**
 * @author onozaty
 */
public class PluginImpl extends Plugin {

    /**
     * @see hudson.Plugin#start()
     */
    @Override
    public void start() throws Exception {
        BuildStep.BUILDERS.add(SeleniumAutoExecBuilder.DESCRIPTOR);
    }
}

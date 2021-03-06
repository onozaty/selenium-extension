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
package com.enjoyxstudy.selenium.autoexec.client;

import junit.framework.TestCase;

/**
 * @author onozaty
 */
public class SeleniumAutoExecRemoteTest extends TestCase {

    /**
     * @param name
     */
    public SeleniumAutoExecRemoteTest(String name) {
        super(name);
    }

    /**
     * @param args
     */
    public static void main(String args[]) {
        junit.textui.TestRunner.run(SeleniumAutoExecRemoteTest.class);
    }

    /**
     * @throws Exception
     */
    public void test() throws Exception {

        RemoteControlClient client = new RemoteControlClient(System
                .getProperty("seleniumaes.url"));

        if ("true".equals(System.getProperty("seleniumaes.async"))) {

            assertTrue("run async failed.", client.runAsync());
        } else {

            String result = client.runString();

            assertTrue("run failed.", RemoteControlClient.isPassedResult(result));
            System.out.print(result);
        }

    }
}

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

import junit.framework.TestCase;

import org.openqa.selenium.server.SeleniumServer;

/**
 * @author onozaty
 */
public class HTMLSuiteLauncherTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testRunHTMLSuiteResult1() throws Exception {

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            HTMLSuiteLauncher launcher = new HTMLSuiteLauncher(server);
            boolean result = launcher.runHTMLSuiteResult("*firefox",
                    "http://www.google.com/", new File(
                            "suite/pattern1/suite1.html"), 30, !server
                            .getConfiguration().isSingleWindow());

            assertTrue(result);
            assertEquals("passed", launcher.getResults().getResult());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testRunHTMLSuiteResult2() throws Exception {

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            HTMLSuiteLauncher launcher = new HTMLSuiteLauncher(server);
            boolean result = launcher.runHTMLSuiteResult("*firefox",
                    "http://www.google.com/", new File(
                            "suite/pattern1/failed2.html"), 30, !server
                            .getConfiguration().isSingleWindow());

            assertFalse(result);
            assertEquals("failed", launcher.getResults().getResult());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testRunHTMLSuiteResult3() throws Exception {

        String resultFile = "result/failed1_result.html";
        new File(resultFile).delete();

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            HTMLSuiteLauncher launcher = new HTMLSuiteLauncher(server);
            boolean result = launcher.runHTMLSuiteResult("*firefox",
                    "http://www.google.com/", new File(
                            "suite/pattern1/failed1.html"),
                    new File(resultFile), 30, !server.getConfiguration()
                            .isSingleWindow());

            assertFalse(result);
            assertEquals("failed", launcher.getResults().getResult());
            assertTrue(new File(resultFile).exists());

        } finally {
            server.stop();
        }
    }

}

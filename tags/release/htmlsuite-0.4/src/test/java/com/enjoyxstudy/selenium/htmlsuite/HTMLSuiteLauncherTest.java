/*
 * Copyright (c) 2007, 2008 onozaty (http://www.enjoyxstudy.com)
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
    public void testRunHTMLSuite1() throws Exception {

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            HTMLSuiteLauncher launcher = new HTMLSuiteLauncher(server);
            boolean result = launcher.runHTMLSuite("*firefox",
                    "http://www.google.com/", "suite/suite1.html", 30);

            assertTrue(result);
            assertEquals("passed", launcher.getResults().getResult());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testRunHTMLSuite2() throws Exception {

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            HTMLSuiteLauncher launcher = new HTMLSuiteLauncher(server);
            boolean result = launcher.runHTMLSuite("*firefox",
                    "http://www.google.com/", "suite/failed2.html", 30);

            assertFalse(result);
            assertEquals("failed", launcher.getResults().getResult());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testRunHTMLSuite3() throws Exception {

        String resultFile = "result/failed1_result.html";
        new File(resultFile).delete();

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            HTMLSuiteLauncher launcher = new HTMLSuiteLauncher(server);
            boolean result = launcher.runHTMLSuite("*firefox",
                    "http://www.google.com/", "suite/failed1.html", resultFile,
                    30);

            assertFalse(result);
            assertEquals("failed", launcher.getResults().getResult());
            assertTrue(new File(resultFile).exists());

        } finally {
            server.stop();
        }
    }

}

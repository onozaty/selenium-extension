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
public class MultiHTMLSuiteRunnerTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testAddHTMLSuites1() throws Exception {

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuites("*firefox", "http://www.google.com/",
                    new String[] { "suite/pattern1/suite1.html" }, 30);

            assertTrue(runner.runHTMLSuites());
            assertEquals(1, runner.getHtmlSuiteList().size());
            assertEquals("suite1.html", runner.getHtmlSuiteList().get(0)
                    .getSuiteFile().getName());
            assertEquals("passed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testAddHTMLSuites2() throws Exception {

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuites("*firefox", "http://www.google.com/",
                    new String[] { "suite/pattern1/failed1.html",
                            "suite/pattern1/suite3.html" }, 30);

            assertFalse(runner.runHTMLSuites());
            assertEquals(2, runner.getHtmlSuiteList().size());
            assertEquals("failed1.html", runner.getHtmlSuiteList().get(0)
                    .getSuiteFile().getName());
            assertEquals("failed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());
            assertEquals("suite3.html", runner.getHtmlSuiteList().get(1)
                    .getSuiteFile().getName());
            assertEquals("passed", runner.getHtmlSuiteList().get(1)
                    .getTestResults().getResult());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testAddHTMLSuites3() throws Exception {

        String resultFile1 = "result/suite2_result.html";
        new File(resultFile1).delete();
        String resultFile2 = "result/suite1_result.html";
        new File(resultFile2).delete();

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuites("*firefox", "http://www.google.com/",
                    new String[] { "suite/pattern1/suite2.html",
                            "suite/pattern1/suite1.html" }, "result", 30);

            assertTrue(runner.runHTMLSuites());
            assertEquals(2, runner.getHtmlSuiteList().size());
            assertEquals("suite2.html", runner.getHtmlSuiteList().get(0)
                    .getSuiteFile().getName());
            assertEquals("passed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());
            assertTrue(new File(resultFile1).exists());

            assertEquals("suite1.html", runner.getHtmlSuiteList().get(1)
                    .getSuiteFile().getName());
            assertEquals("passed", runner.getHtmlSuiteList().get(1)
                    .getTestResults().getResult());
            assertTrue(new File(resultFile2).exists());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testAddHTMLSuites4() throws Exception {

        String suiteFile = "suite/pattern1/generatedTestSuite.html";
        new File(suiteFile).delete();

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuites(new String[] { "*firefox", "*iexplore" },
                    "http://www.google.com/", "suite/pattern1", 30);

            assertTrue(runner.runHTMLSuites());
            assertEquals(6, runner.getHtmlSuiteList().size());

            assertEquals("suite1.html", runner.getHtmlSuiteList().get(0)
                    .getSuiteFile().getName());
            assertEquals("*firefox", runner.getHtmlSuiteList().get(0)
                    .getBrowser());
            assertEquals("passed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());

            assertEquals("suite2.html", runner.getHtmlSuiteList().get(1)
                    .getSuiteFile().getName());
            assertEquals("*firefox", runner.getHtmlSuiteList().get(1)
                    .getBrowser());
            assertEquals("passed", runner.getHtmlSuiteList().get(1)
                    .getTestResults().getResult());

            assertEquals("suite3.html", runner.getHtmlSuiteList().get(2)
                    .getSuiteFile().getName());
            assertEquals("*firefox", runner.getHtmlSuiteList().get(2)
                    .getBrowser());
            assertEquals("passed", runner.getHtmlSuiteList().get(2)
                    .getTestResults().getResult());
            assertNull(runner.getHtmlSuiteList().get(2).getResultFile());

            assertEquals("suite1.html", runner.getHtmlSuiteList().get(3)
                    .getSuiteFile().getName());
            assertEquals("*iexplore", runner.getHtmlSuiteList().get(3)
                    .getBrowser());
            assertEquals("passed", runner.getHtmlSuiteList().get(3)
                    .getTestResults().getResult());

            assertEquals("suite2.html", runner.getHtmlSuiteList().get(4)
                    .getSuiteFile().getName());
            assertEquals("*iexplore", runner.getHtmlSuiteList().get(4)
                    .getBrowser());
            assertEquals("passed", runner.getHtmlSuiteList().get(4)
                    .getTestResults().getResult());

            assertEquals("suite3.html", runner.getHtmlSuiteList().get(5)
                    .getSuiteFile().getName());
            assertEquals("*iexplore", runner.getHtmlSuiteList().get(5)
                    .getBrowser());
            assertEquals("passed", runner.getHtmlSuiteList().get(5)
                    .getTestResults().getResult());
            assertNull(runner.getHtmlSuiteList().get(5).getResultFile());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testAddHTMLSuites5() throws Exception {

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuites(new String[] { "*iexplore", },
                    "http://www.google.com/", "suite/pattern1/suite1.html", 30);

            assertTrue(runner.runHTMLSuites());
            assertEquals(1, runner.getHtmlSuiteList().size());

            assertEquals("suite1.html", runner.getHtmlSuiteList().get(0)
                    .getSuiteFile().getName());
            assertEquals("*iexplore", runner.getHtmlSuiteList().get(0)
                    .getBrowser());
            assertEquals("passed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testAddHTMLSuites6() throws Exception {

        String resultFile1 = "result/iexplore/suite1_result.html";
        new File(resultFile1).delete();
        String resultFile2 = "result/firefox/suite1_result.html";
        new File(resultFile2).delete();

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuites(new String[] { "*iexplore", "*firefox" },
                    "http://www.google.com/", "suite/pattern1/suite1.html",
                    "result", 30);

            assertTrue(runner.runHTMLSuites());
            assertEquals(2, runner.getHtmlSuiteList().size());

            assertEquals("suite1.html", runner.getHtmlSuiteList().get(0)
                    .getSuiteFile().getName());
            assertEquals("*iexplore", runner.getHtmlSuiteList().get(0)
                    .getBrowser());
            assertEquals("passed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());
            assertTrue(new File(resultFile1).exists());

            assertEquals("suite1.html", runner.getHtmlSuiteList().get(1)
                    .getSuiteFile().getName());
            assertEquals("*firefox", runner.getHtmlSuiteList().get(1)
                    .getBrowser());
            assertEquals("passed", runner.getHtmlSuiteList().get(1)
                    .getTestResults().getResult());
            assertTrue(new File(resultFile2).exists());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testAddHTMLSuitesDir1() throws Exception {

        String suiteFile = "suite/generatedTestSuite.html";
        new File(suiteFile).delete();

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuiteDir("*firefox", "http://www.google.com/",
                    "suite/pattern1", 30);

            assertTrue(runner.runHTMLSuites());
            assertEquals(3, runner.getHtmlSuiteList().size());
            assertEquals("suite1.html", runner.getHtmlSuiteList().get(0)
                    .getSuiteFile().getName());
            assertEquals("passed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());
            assertEquals("suite2.html", runner.getHtmlSuiteList().get(1)
                    .getSuiteFile().getName());
            assertEquals("passed", runner.getHtmlSuiteList().get(1)
                    .getTestResults().getResult());
            assertEquals("suite3.html", runner.getHtmlSuiteList().get(2)
                    .getSuiteFile().getName());
            assertEquals("passed", runner.getHtmlSuiteList().get(2)
                    .getTestResults().getResult());
            assertNull(runner.getHtmlSuiteList().get(2).getResultFile());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testAddHTMLSuitesDir2() throws Exception {

        String suiteFile = "suite/pattern1/generatedTestSuite.html";
        new File(suiteFile).delete();

        String resultFile1 = "result/suite1_result.html";
        new File(resultFile1).delete();
        String resultFile2 = "result/suite2_result.html";
        new File(resultFile2).delete();
        String resultFile3 = "result/suite3_result.html";
        new File(resultFile3).delete();

        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuiteDir("*firefox", "http://www.google.com/",
                    "suite/pattern1", "result", 30);

            assertTrue(runner.runHTMLSuites());
            assertEquals(3, runner.getHtmlSuiteList().size());

            assertTrue(runner.runHTMLSuites());
            assertEquals(3, runner.getHtmlSuiteList().size());
            assertEquals("suite1.html", runner.getHtmlSuiteList().get(0)
                    .getSuiteFile().getName());
            assertEquals("passed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());
            assertTrue(new File(resultFile1).exists());

            assertEquals("suite2.html", runner.getHtmlSuiteList().get(1)
                    .getSuiteFile().getName());
            assertEquals("passed", runner.getHtmlSuiteList().get(1)
                    .getTestResults().getResult());
            assertTrue(new File(resultFile2).exists());

            assertEquals("suite3.html", runner.getHtmlSuiteList().get(2)
                    .getSuiteFile().getName());
            assertEquals("passed", runner.getHtmlSuiteList().get(2)
                    .getTestResults().getResult());
            assertEquals("suite3_result.html", runner.getHtmlSuiteList().get(2)
                    .getResultFile().getName());
            assertTrue(new File(resultFile3).exists());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testAddHTMLSuiteGenerate1() throws Exception {

        SeleniumServer server = new SeleniumServer();

        String suiteFile = "suite/pattern1/generatedTestSuite.html";
        new File(suiteFile).delete();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuiteGenerate(
                    new String[] { "*firefox", "*iexplore" },
                    "http://www.google.com/", "suite/pattern1", 60);

            assertFalse(runner.runHTMLSuites());
            assertEquals(2, runner.getHtmlSuiteList().size());

            assertEquals("generatedTestSuite.html", runner.getHtmlSuiteList()
                    .get(0).getSuiteFile().getName());
            assertEquals("*firefox", runner.getHtmlSuiteList().get(0)
                    .getBrowser());
            assertEquals("failed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());

            assertEquals("generatedTestSuite.html", runner.getHtmlSuiteList()
                    .get(1).getSuiteFile().getName());
            assertEquals("*iexplore", runner.getHtmlSuiteList().get(1)
                    .getBrowser());
            assertEquals("failed", runner.getHtmlSuiteList().get(1)
                    .getTestResults().getResult());

            assertTrue(new File(suiteFile).exists());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testAddHTMLSuiteGenerate2() throws Exception {

        String resultFile1 = "result/iexplore/generatedTestSuite_result.html";
        new File(resultFile1).delete();
        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuiteGenerate(new String[] { "*iexplore" },
                    "http://www.google.com/", "suite/pattern1", "result", 60);

            assertFalse(runner.runHTMLSuites());
            assertEquals(1, runner.getHtmlSuiteList().size());

            assertEquals("generatedTestSuite.html", runner.getHtmlSuiteList()
                    .get(0).getSuiteFile().getName());
            assertEquals("*iexplore", runner.getHtmlSuiteList().get(0)
                    .getBrowser());
            assertEquals("failed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());
            assertTrue(new File(resultFile1).exists());

        } finally {
            server.stop();
        }
    }

    /**
     * @throws Exception
     */
    public void testAddHTMLSuiteGenerate3() throws Exception {

        String resultFile1 = "result/iexplore/pattern1_result.html";
        new File(resultFile1).delete();
        SeleniumServer server = new SeleniumServer();

        try {
            server.start();
            MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(server);
            runner.addHTMLSuiteGenerate(new String[] { "*iexplore", "*firefox" },
                    "http://www.google.com/", "suite", "result", 60);

            assertFalse(runner.runHTMLSuites());
            assertEquals(4, runner.getHtmlSuiteList().size());

            assertEquals("pattern1.html", runner.getHtmlSuiteList()
                    .get(0).getSuiteFile().getName());
            assertEquals("*iexplore", runner.getHtmlSuiteList().get(0)
                    .getBrowser());
            assertEquals("failed", runner.getHtmlSuiteList().get(0)
                    .getTestResults().getResult());
            assertTrue(new File(resultFile1).exists());

        } finally {
            server.stop();
        }
    }

}

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author onozaty
 */
public class HTMLSuiteTest extends TestCase {

    /** DEFAULT_PROPERTIES */
    private static final String DEFAULT_PROPERTIES = "/htmlSuite.properties";

    /**
     * @throws Exception 
     */
    public void test() throws Exception {

        Properties properties = loadProperties();

        MultiHTMLSuiteRunner multiHTMLSuiteRunner = MultiHTMLSuiteRunner
                .execute(properties);
        assertTrue("HTML Suites failed.", multiHTMLSuiteRunner.getResult());
    }

    /**
     * @return properties
     * @throws IOException
     */
    private Properties loadProperties() throws IOException {

        InputStream stream = this.getClass().getResourceAsStream(
                this.getClass().getSimpleName() + ".properties");

        if (stream == null) {
            stream = this.getClass().getResourceAsStream(DEFAULT_PROPERTIES);
        }

        if (stream == null) {
            throw new IOException("properties Not Found.");
        }

        Properties properties = new Properties();
        properties.load(stream);

        return properties;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(HTMLSuiteTest.class);
    }
}

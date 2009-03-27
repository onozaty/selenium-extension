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
package com.enjoyxstudy.selenium.autoexec.mail;

import java.io.File;

import junit.framework.TestCase;

import com.enjoyxstudy.selenium.htmlsuite.MultiHTMLSuiteRunner;

/**
 * @author onozaty
 */
public class MailSenderTest extends TestCase {

    /**
     * test
     * 
     * @throws Exception
     */
    public void testSend() throws Exception {

        MailConfiguration configuration = new MailConfiguration();
        configuration.setHost("smtp.gmail.com");
        configuration.setPort(465);
        configuration.setSsl(true);
        configuration.setDebug(true);
        configuration.setTo("selenium.extension@gmail.com");
        configuration.setFrom("selenium.extension@gmail.com");
        configuration.setUsername("selenium.extension@gmail.com");
        configuration.setPassword("");
        configuration.setSubject("subject");
        configuration.setBody("body");

        MailSender sender = new MailSender(configuration);

        MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(null);
        sender.send(runner, new File("work/result"));
    }
}

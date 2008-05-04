package com.enjoyxstudy.selenium.autoexec.mail;

import java.io.File;

import com.enjoyxstudy.selenium.htmlsuite.MultiHTMLSuiteRunner;

import junit.framework.TestCase;

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

        MailConfig config = new MailConfig();
        config.setHost("smtp.gmail.com");
        config.setPort(465);
        config.setSsl(true);
        config.setDebug(true);
        config.setTo("selenium.extension@gmail.com");
        config.setFrom("selenium.extension@gmail.com");
        config.setUsername("selenium.extension@gmail.com");
        config.setPassword("");
        config.setSubject("subject");
        config.setBody("body");

        MailSender sender = new MailSender(config);

        MultiHTMLSuiteRunner runner = new MultiHTMLSuiteRunner(null);
        sender.send(runner, new File("work/result"));
    }
}

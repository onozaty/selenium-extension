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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;

import com.enjoyxstudy.selenium.autoexec.util.TemplateUtils;
import com.enjoyxstudy.selenium.autoexec.util.ZipUtils;
import com.enjoyxstudy.selenium.htmlsuite.MultiHTMLSuiteRunner;

/**
 * @author onozaty
 */
public class MailSender {

    /** logger */
    private static Log log = LogFactory.getLog(MailSender.class);

    /** config */
    private MailConfiguration config;

    /** session */
    private Session session;

    /** RESULT_ARCHIVE_FILE */
    private static final String RESULT_ARCHIVE_FILE = "result.zip";

    /**
     * @param config
     * @throws Exception
     */
    public MailSender(final MailConfiguration config) throws Exception {

        this.config = config;

        Properties prop = new Properties();

        if (config.isSsl()) {
            prop.setProperty("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            prop.setProperty("mail.smtp.socketFactory.fallback", "false");
            prop.setProperty("mail.smtp.socketFactory.port", String
                    .valueOf(config.getPort()));
        }

        prop.setProperty("mail.transport.protocol", config.getProtocol());
        prop.setProperty("mail.smtp.host", config.getHost());
        prop.setProperty("mail.smtp.port", String.valueOf(config.getPort()));
        log.info("smtp host:" + config.getHost() + " port:" + config.getPort());
        prop.setProperty("mail.smtp.connectiontimeout", String.valueOf(config
                .getConnectionTimeout()));
        prop.setProperty("mail.smtp.timeout", String.valueOf(config
                .getReadTimeout()));

        prop.setProperty("mail.debug", String.valueOf(config.isDebug()));

        if (config.getUsername() != null && config.getUsername().length() != 0) {
            prop.setProperty("mail.smtp.auth", "true");

            session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getUsername(),
                            config.getPassword());
                }
            });
        } else {
            session = Session.getInstance(prop);
        }
        log.info("smtp host:" + config.getHost());
    }

    /**
     * @param runner 
     * @param resultDir
     * @throws MessagingException
     * @throws IOException 
     */
    public void send(MultiHTMLSuiteRunner runner, File resultDir)
            throws MessagingException, IOException {

        MimeMessage mimeMessage = new MimeMessage(session);

        mimeMessage.setHeader("Content-Transfer-Encoding", "7bit");

        // To
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress
                .parse(config.getTo()));

        // From
        mimeMessage.setFrom(new InternetAddress(config.getFrom()));

        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("result", runner.getResult() ? "passed" : "failed");
        context.put("passedCount", new Integer(runner.getPassedCount()));
        context.put("failedCount", new Integer(runner.getFailedCount()));
        context
                .put("totalCount",
                        new Integer(runner.getHtmlSuiteList().size()));
        context.put("startTime", new Date(runner.getStartTime()));
        context.put("endTime", new Date(runner.getEndTime()));
        context.put("htmlSuites", runner.getHtmlSuiteList());

        // subject
        mimeMessage.setSubject(TemplateUtils
                .merge(config.getSubject(), context), config.getCharset());

        // multipart message
        MimeMultipart content = new MimeMultipart();

        MimeBodyPart body = new MimeBodyPart();
        body.setText(TemplateUtils.merge(config.getBody(), context), config
                .getCharset());
        content.addBodyPart(body);

        File resultArchive = createResultArchive(resultDir);

        MimeBodyPart attachmentFile = new MimeBodyPart();
        attachmentFile.setDataHandler(new DataHandler(new FileDataSource(
                resultArchive)));
        attachmentFile.setFileName(RESULT_ARCHIVE_FILE);
        content.addBodyPart(attachmentFile);

        mimeMessage.setContent(content);

        // send mail
        _send(mimeMessage);
    }

    /**
     * @param resultDir
     * @return archive file
     * @throws IOException 
     */
    private File createResultArchive(File resultDir) throws IOException {

        File resultArchiveFile = File.createTempFile("result"
                + System.currentTimeMillis(), ".zip");

        FileOutputStream outputStream = new FileOutputStream(resultArchiveFile);
        try {
            ZipUtils.zip(outputStream, resultDir);
        } finally {
            outputStream.close();
        }

        return resultArchiveFile;
    }

    /**
     * @param mimeMessage
     * @throws MessagingException
     */
    private void _send(MimeMessage mimeMessage) throws MessagingException {

        Transport transport = null;

        try {

            transport = session.getTransport();
            transport.connect();

            mimeMessage.setSentDate(new Date());
            mimeMessage.saveChanges();

            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        } finally {
            if (transport != null && transport.isConnected()) {
                transport.close();
            }
        }
    }

}

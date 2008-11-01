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
package com.enjoyxstudy.selenium.autoexec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.mortbay.http.HttpContext;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.Server;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.SeleniumServer;
import org.tmatesoft.svn.core.SVNException;

import com.enjoyxstudy.selenium.autoexec.client.RemoteControlClient;
import com.enjoyxstudy.selenium.autoexec.mail.MailConfig;
import com.enjoyxstudy.selenium.autoexec.mail.MailSender;
import com.enjoyxstudy.selenium.autoexec.util.FileUtils;
import com.enjoyxstudy.selenium.autoexec.util.SVNUtils;
import com.enjoyxstudy.selenium.htmlsuite.HTMLSuite;
import com.enjoyxstudy.selenium.htmlsuite.MultiHTMLSuiteRunner;
import com.enjoyxstudy.selenium.htmlsuite.util.PropertiesUtils;

/**
 * @author onozaty
 */
public class AutoExecServer {

    /** logger */
    static Log log = LogFactory.getLog(AutoExecServer.class);

    /** startup command */
    private static final String COMMAND_STARTUP = "startup";

    /** shutdown command */
    private static final String COMMAND_SHUTDOWN = "shutdown";

    /** property file default name */
    private static final String DEFAULT_PROPERTY_FILE_NAME = "setting.properties";

    /** runningLoop wait time(ms) */
    private static final int RUNNING_LOOP_WAIT = 1000;

    /** status idle */
    public static final int STATUS_IDLE = 0;

    /** status running */
    public static final int STATUS_RUNNING = 1;

    /** root context path */
    public static final String CONTEXT_PATH_ROOT = "/selenium-server/autoexec/";

    /** command context path */
    public static final String CONTEXT_PATH_COMMAND = CONTEXT_PATH_ROOT
            + "command/";

    /** result context path */
    public static final String CONTEXT_PATH_RESULT = CONTEXT_PATH_ROOT
            + "result/";

    /** contents directory */
    private static final String CONTENTS_DIR = "./contents/";

    /** date format for result directory */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyyMMddHHmmss");

    /** config */
    private Config config;

    /** mailConfig */
    private MailConfig mailConfig;

    /** seleniumServer */
    private SeleniumServer seleniumServer;

    /** resultIndexHtmlWriter */
    private static final ResultIndexHtmlWriter resultIndexHtmlWriter = new ResultIndexHtmlWriter();

    /** status */
    private int status = STATUS_IDLE;

    /** stop flag */
    private boolean isStop;

    /** HTMLSuite runner */
    private MultiHTMLSuiteRunner runner;

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {

        String command = null;
        if (args.length == 0) {
            command = COMMAND_STARTUP;
        } else if (args[0].equals(COMMAND_STARTUP)) {
            command = COMMAND_STARTUP;
        } else if (args[0].equals(COMMAND_SHUTDOWN)) {
            command = COMMAND_SHUTDOWN;
        }

        if (command == null) {
            throw new IllegalArgumentException();
        }

        String propertyFile = DEFAULT_PROPERTY_FILE_NAME; // default
        if (args.length == 2) {
            propertyFile = args[0];
        }
        final Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(propertyFile);
        try {
            properties.load(inputStream);
        } finally {
            inputStream.close();
        }

        if (command.equals(COMMAND_STARTUP)) {

            new Thread(new Runnable() {
                public void run() {
                    try {

                        AutoExecServer autoExecServer = new AutoExecServer();

                        autoExecServer.startup(properties);
                        autoExecServer.runningLoop();
                        autoExecServer.destroy();

                    } catch (Exception e) {
                        log.error("Error", e);
                        e.printStackTrace();
                    } finally {
                        System.exit(0);
                    }
                }
            }).start();

        } else {

            StringBuilder commandURL = new StringBuilder("http://localhost:");
            commandURL.append(PropertiesUtils.getInt(properties, "port",
                    SeleniumServer.DEFAULT_PORT));
            commandURL.append(CONTEXT_PATH_COMMAND);

            RemoteControlClient client = new RemoteControlClient(commandURL
                    .toString());

            client.stopServer();
        }
    }

    /**
     * server startup.
     * 
     * @param properties 
     * @throws Exception 
     */
    public void startup(Properties properties) throws Exception {

        config = new Config(properties);

        SeleniumServer.setAvoidProxy(config.isAvoidProxy());
        SeleniumServer.setDebugMode(config.isDebug());

        if (config.getLog() != null) {
            System.setProperty("selenium.log", config.getLog());
        }

        if (config.getProxyHost() != null) {
            System.setProperty("http.proxyHost", config.getProxyHost());
        }

        if (config.getProxyPort() != null) {
            System.setProperty("http.proxyPort", config.getProxyPort());
        }

        if (config.getFirefoxProfileTemplate() != null) {
            SeleniumServer.setFirefoxProfileTemplate(config
                    .getFirefoxProfileTemplate());
        }

        seleniumServer = new SeleniumServer(config.getPort(), false, config
                .isMultiWindow());

        if (config.getUserExtensions() != null) {
            seleniumServer.addNewStaticContent(config.getUserExtensions()
                    .getParentFile());
        }

        mailConfig = new MailConfig(properties);

        Server server = seleniumServer.getServer();

        // add context
        HttpContext rootContext = new HttpContext();
        rootContext.setContextPath(CONTEXT_PATH_ROOT);
        rootContext.setResourceBase(CONTENTS_DIR);
        rootContext.addHandler(new ResourceHandler());
        server.addContext(rootContext);

        HttpContext commandContext = new HttpContext();
        commandContext.setContextPath(CONTEXT_PATH_COMMAND);
        commandContext.addHandler(new CommandHandler(this));
        server.addContext(commandContext);

        HttpContext resultContext = new HttpContext();
        resultContext.setContextPath(CONTEXT_PATH_RESULT);
        resultContext.setResourceBase(config.getResultDir().getAbsolutePath());
        resultContext.addHandler(new ResourceHandler());
        server.addContext(resultContext);

        seleniumServer.start();

        if (config.getAutoExecTime() != null) {
            // set auto exec timer

            String[] time = config.getAutoExecTime().split(":");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            log.info("Auto exec first time: " + calendar.getTime());

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    try {
                        log.info("Start auto exec.");
                        process();
                        log.info("End auto exec.");
                    } catch (Exception e) {
                        log.error("Error auto exec.");
                    }
                }
            }, calendar.getTime(), 1000 * 60 * 60 * 24);
        }

        log.info("Start Selenium Auto Exec Server.");
    }

    /**
     * destroy.
     */
    public void destroy() {
        seleniumServer.stop();
        log.info("Stop Selenium Auto Exec Server.");
    }

    /**
     * process.
     * 
     * @return HTMLSuite runner
     * @throws Exception 
     */
    public synchronized MultiHTMLSuiteRunner process() throws Exception {

        log.info("Start test process.");

        status = STATUS_RUNNING;
        runner = null; // reset
        try {

            File resultDir = getResultDir();

            // svn export
            if (config.getSuiteRepo() != null) {
                exportSuiteRepository();
            }

            // exec test suite
            runTestSuite(resultDir);

            // write result index.html
            writeResultIndexHtml(resultDir);

            // send result mail
            if (mailConfig.getHost() != null
                    && !mailConfig.getHost().equals("")) {
                MailSender mailSender = new MailSender(mailConfig);
                mailSender.send(runner, resultDir);
            }

        } finally {
            status = STATUS_IDLE;
        }

        log.info("End test process.");

        return runner;
    }

    /**
     * 
     * @throws InterruptedException 
     */
    public void runningLoop() throws InterruptedException {

        while (!isStop) {
            Thread.sleep(RUNNING_LOOP_WAIT);
        }
    }

    /**
     * @param resultDir 
     * @throws IOException
     */
    private void runTestSuite(File resultDir) throws IOException {

        runner = new MultiHTMLSuiteRunner(seleniumServer);
        if (config.isGenerateSuite()) {
            runner.addHTMLSuiteGenerate(config.getBrowsers(), config
                    .getStartURL(), config.getSuiteDir(), resultDir, config
                    .getTimeoutInSeconds());
        } else {
            runner.addHTMLSuites(config.getBrowsers(), config.getStartURL(),
                    config.getSuiteDir(), resultDir, config
                            .getTimeoutInSeconds());
        }

        runner.runHTMLSuites();

        if (runner.getResult()) {
            log.info("HTML Suites passed.");
        } else {
            log.info("HTML Suites failed.");
        }

        log.info("total: " + runner.getHtmlSuiteList().size() + ", passed: "
                + runner.getPassedCount() + ", failed: "
                + runner.getFailedCount());

        int count = 0;
        for (HTMLSuite htmlSuite : runner.getHtmlSuiteList()) {

            StringBuilder builder = new StringBuilder();
            builder.append(++count);

            if (htmlSuite.isPassed()) {
                builder.append(": [passed] ");
            } else {
                builder.append(": [failed] ");
            }
            builder.append(htmlSuite.getSuiteFile().getName()).append(" ")
                    .append(htmlSuite.getBrowser());
            log.info(builder.toString());
        }

    }

    /**
     * @throws IOException
     * @throws SVNException
     */
    private void exportSuiteRepository() throws IOException, SVNException {

        File suiteDir = config.getSuiteDir();
        if (!suiteDir.isAbsolute()) {
            suiteDir = suiteDir.getAbsoluteFile();
        }

        if (suiteDir.exists()) {
            log.info("Delete suite directory.");
            FileUtils.deleteDirectory(suiteDir);
        }

        SVNUtils.export(config.getSuiteRepo(), suiteDir, config
                .getSuiteRepoUsername(), config.getSuiteRepoPassword());
        log.info("Suite Export repository[" + config.getSuiteRepo()
                + "] dist=[" + suiteDir.getPath() + "]");
    }

    /**
     * @param resultDir 
     * @throws IOException 
     */
    private void writeResultIndexHtml(File resultDir) throws IOException {

        resultIndexHtmlWriter.write(resultDir, runner);
    }

    /**
     * @return result directory
     */
    private File getResultDir() {

        File resultDir = config.getResultDir();

        if (config.isResultPermanent()) {
            resultDir = new File(resultDir, DATE_FORMAT.format(new Date()));
            resultDir.mkdir();
        }

        return resultDir;
    }

    /**
     * stop running.
     */
    public void runningStop() {
        isStop = true;
    }

    /**
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return runner
     */
    public MultiHTMLSuiteRunner getRunner() {
        return runner;
    }

    /**
     * @return config
     */
    public Config getConfig() {
        return config;
    }

}

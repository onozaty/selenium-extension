package com.enjoyxstudy.selenium.autoexec;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.SeleniumServer;
import org.tmatesoft.svn.core.SVNException;

import com.enjoyxstudy.selenium.autoexec.util.FileUtils;
import com.enjoyxstudy.selenium.autoexec.util.SVNUtils;
import com.enjoyxstudy.selenium.htmlsuite.HTMLSuite;
import com.enjoyxstudy.selenium.htmlsuite.MultiHTMLSuiteRunner;

/**
 * @author onozaty
 */
public class AutoExecServer {

    /** logger */
    private static Log log = LogFactory.getLog(AutoExecServer.class);

    /** config */
    private Config config;

    /** seleniumServer */
    private SeleniumServer seleniumServer;

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO
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

        seleniumServer = new SeleniumServer(config.getPort(), false, config
                .isMultiWindow());

        if (config.getUserExtensions() != null) {
            seleniumServer.addNewStaticContent(config.getUserExtensions()
                    .getParentFile());
        }

        seleniumServer.start();

        log.info("Start Selenium Server.");
    }

    /**
     * destroy.
     */
    public void destroy() {
        seleniumServer.stop();
        log.info("Stop Selenium Server.");
    }

    /**
     * 
     * @throws SVNException 
     * @throws IOException 
     */
    public void process() throws IOException, SVNException {

        log.info("Start test process.");

        // svn export
        if (config.getSuiteRepo() != null) {
            exportSuiteRepository();
        }

        // exec test suite
        MultiHTMLSuiteRunner runner = runTestSuite();

        // TODO

        log.info("End test process.");
    }

    /**
     * @return MultiHTMLSuiteRunner
     * @throws IOException
     */
    private MultiHTMLSuiteRunner runTestSuite() throws IOException {

        MultiHTMLSuiteRunner htmlSuiteRunner = new MultiHTMLSuiteRunner(
                seleniumServer);
        if (config.isGenerateSuite()) {
            htmlSuiteRunner.addHTMLSuiteGenerate(config.getBrowsers(), config
                    .getStartURL(), config.getSuiteDir(),
                    config.getResultDir(), config.getTimeoutInSeconds());
        } else {
            htmlSuiteRunner.addHTMLSuites(config.getBrowsers(), config
                    .getStartURL(), config.getSuiteDir(),
                    config.getResultDir(), config.getTimeoutInSeconds());
        }

        htmlSuiteRunner.runHTMLSuites();

        if (htmlSuiteRunner.getResult()) {
            log.info("HTML Suites passed.");
        } else {
            log.info("HTML Suites failed.");
        }

        int total = 0;
        int passed = 0;
        int failed = 0;
        for (HTMLSuite htmlSuite : htmlSuiteRunner.getHtmlSuiteList()) {
            total++;
            if (htmlSuite.isPassed()) {
                passed++;
            } else {
                failed++;
            }
        }

        log.info("total: " + total + ", passed: " + passed + ", failed: "
                + failed);
        int count = 0;
        for (HTMLSuite htmlSuite : htmlSuiteRunner.getHtmlSuiteList()) {

            StringBuilder builder = new StringBuilder();
            builder.append(++count);

            if (htmlSuite.isPassed()) {
                builder.append(": [passed] ");
            } else {
                failed++;
                builder.append(": [failed] ");
            }
            builder.append(htmlSuite.getSuiteFile().getName()).append(" ")
                    .append(htmlSuite.getBrowser());
            log.info(builder.toString());
        }

        return htmlSuiteRunner;
    }

    /**
     * @throws IOException
     * @throws SVNException
     */
    private void exportSuiteRepository() throws IOException, SVNException {

        File suiteDir = new File(config.getSuiteDir());
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

}

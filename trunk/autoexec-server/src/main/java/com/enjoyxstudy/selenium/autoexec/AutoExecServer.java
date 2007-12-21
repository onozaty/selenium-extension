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

        String temp;

        config = new Config();
        config.setBrowsers(properties.getProperty("browser").split(","));
        config.setStartURL(properties.getProperty("startURL"));
        config.setSuiteDir(properties.getProperty("suiteDir"));
        config.setSuiteRepo(properties.getProperty("suiteRepo"));
        config
                .setSuiteRepoUsername(properties
                        .getProperty("suiteRepoUsername"));
        config
                .setSuiteRepoPassword(properties
                        .getProperty("suiteRepoPassword"));
        config.setGenerateSuite(Boolean.parseBoolean(properties
                .getProperty("generateSuite")));
        config.setResultDir(properties.getProperty("resultDir"));

        if ((temp = properties.getProperty("timeout")) != null) {
            config.setTimeoutInSeconds(Integer.parseInt(temp));
        }

        // create server
        int port = SeleniumServer.getDefaultPort();
        if ((temp = properties.getProperty("port")) != null) {
            port = Integer.parseInt(temp);
        }

        boolean multiWindow = false;
        if ((temp = properties.getProperty("multiWindow")) != null) {
            multiWindow = Boolean.parseBoolean(temp);
        }

        if ((temp = properties.getProperty("avoidProxy")) != null) {
            SeleniumServer.setAvoidProxy(Boolean.parseBoolean(temp));
        }

        if ((temp = properties.getProperty("debug")) != null) {
            SeleniumServer.setDebugMode(Boolean.parseBoolean(temp));
        }

        if ((temp = properties.getProperty("log")) != null) {
            System.setProperty("selenium.log", temp);
        }

        File userExtensions = null;
        if ((temp = properties.getProperty("userExtensions")) != null) {
            userExtensions = new File(temp);
            if (!userExtensions.exists()) {
                throw new RuntimeException(
                        "User Extensions file doesn't exist: "
                                + userExtensions.getAbsolutePath());
            }
            if (!"user-extensions.js"
                    .equalsIgnoreCase(userExtensions.getName())) {
                throw new RuntimeException(
                        "User extensions file MUST be called \"user-extensions.js\": "
                                + userExtensions.getAbsolutePath());
            }
        }

        if ((temp = properties.getProperty("proxyHost")) != null) {
            System.setProperty("http.proxyHost", temp);
        }
        if ((temp = properties.getProperty("proxyPort")) != null) {
            System.setProperty("http.proxyPort", temp);
        }

        seleniumServer = new SeleniumServer(port, false, multiWindow);

        if (userExtensions != null) {
            seleniumServer.addNewStaticContent(userExtensions.getParentFile());
        }

        seleniumServer.start();

        log.info("selenium server start.");
    }

    /**
     * destroy.
     */
    public void destroy() {
        seleniumServer.stop();
        log.info("selenium server stop.");
    }

    /**
     * 
     * @throws SVNException 
     * @throws IOException 
     */
    public void process() throws IOException, SVNException {

        log.info("process start.");

        // svn export
        if (config.getSuiteRepo() != null) {
            exportSuiteRepository();
        }

        // TODO
    }

    /**
     * @throws IOException
     * @throws SVNException
     */
    private void exportSuiteRepository() throws IOException, SVNException {

        log.info("suite export start. repository="
                + config.getSuiteRepo());
        cleanSuiteDir();

        SVNUtils.export(config.getSuiteRepo(), config.getSuiteDir(), config
                .getSuiteRepoUsername(), config.getSuiteRepoPassword());
        log.info("suite export end.");
    }

    /**
     * @throws IOException
     */
    private void cleanSuiteDir() throws IOException {
        File suiteDir = new File(config.getSuiteDir());
        if (suiteDir.exists()) {
            log.info("delete suite directory.");
            FileUtils.deleteDirectory(suiteDir);
        }
    }
}

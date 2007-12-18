package com.enjoyxstudy.selenium.autoexec;

import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author onozaty
 */
public class AutoExecServerTest extends TestCase {

    /**
     * @throws Exception
     */
    public void testStartup() throws Exception {

        InputStream stream = this.getClass().getResourceAsStream(
                "./AutoExecServerTest.properties");

        Properties properties = new Properties();
        properties.load(stream);

        AutoExecServer autoExecServer = new AutoExecServer();
        autoExecServer.startup(properties);
        Thread.sleep(5000);
        autoExecServer.destroy();

    }
}

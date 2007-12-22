package com.enjoyxstudy.selenium.autoexec;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import com.enjoyxstudy.selenium.autoexec.util.FileUtils;

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

        File resultDir = new File((String) properties.get("resultDir"));
        if (resultDir.exists()) {
            FileUtils.deleteDirectory(resultDir);
        }
        resultDir.mkdir();

        AutoExecServer autoExecServer = new AutoExecServer();
        autoExecServer.startup(properties);
        autoExecServer.process();
        autoExecServer.destroy();

    }
}

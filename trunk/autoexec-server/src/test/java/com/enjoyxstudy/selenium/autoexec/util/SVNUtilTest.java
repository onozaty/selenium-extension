package com.enjoyxstudy.selenium.autoexec.util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.tmatesoft.svn.core.SVNException;

/**
 * @author onozaty
 */
public class SVNUtilTest extends TestCase {

    /**
     * @throws SVNException 
     * @throws IOException 
     */
    public void testExport() throws SVNException, IOException {

        File dir = new File("work/user-extension");
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }

        SVNUtils
                .export(
                        "http://selenium-extension.googlecode.com/svn/trunk/user-extension",
                        "work/user-extension", null, null);
    }
}

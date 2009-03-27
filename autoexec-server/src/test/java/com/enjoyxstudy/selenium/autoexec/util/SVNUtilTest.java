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
                        new File("work/user-extension"), null, null);
    }
}

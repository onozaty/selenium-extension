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
package com.enjoyxstudy.selenium.autoexec.util;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * @author onozaty
 */
public final class SVNUtils {

    /**
     * svn export
     * 
     * @param svnUrl
     * @param dist
     * @param userName
     * @param password
     * @throws SVNException
     */
    public static void export(String svnUrl, File dist, String userName,
            String password) throws SVNException {

        setupLibrary();

        SVNUpdateClient client = createSVNClientManager(userName, password)
                .getUpdateClient();

        client.doExport(SVNURL.parseURIEncoded(svnUrl), dist, SVNRevision.HEAD,
                SVNRevision.HEAD, null, false, true);
    }

    /**
     * create SVNClientManager
     * 
     * @param userName
     * @param password
     * @return SVNClientManager
     */
    private static SVNClientManager createSVNClientManager(String userName,
            String password) {
        return SVNClientManager.newInstance(SVNWCUtil
                .createDefaultOptions(true), userName, password);
    }

    /**
     * setup
     */
    private static void setupLibrary() {
        // For using over http:// and https://
        DAVRepositoryFactory.setup();

        // For using over svn:// and svn+xxx://
        SVNRepositoryFactoryImpl.setup();

        // For using over file:///
        FSRepositoryFactory.setup();
    }
}

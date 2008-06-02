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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author onozaty
 */
public final class ZipUtils {

    /** BUF_SIZE */
    private static final int BUF_SIZE = 1024;

    /** PATH_SEPARATOR */
    private static final String PATH_SEPARATOR = "/";

    /**
     * @param outputStream
     * @param targetFile
     * @throws IOException
     */
    public static void zip(OutputStream outputStream, File targetFile)
            throws IOException {

        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        try {
            addZipEntry(zipOutputStream, targetFile, "");
        } finally {
            zipOutputStream.close();
        }
    }

    /**
     * @param zipOutputStream
     * @param entryFile
     * @param parentPath
     * @throws IOException
     */
    private static void addZipEntry(ZipOutputStream zipOutputStream,
            File entryFile, String parentPath) throws IOException {

        if (entryFile.isDirectory()) {
            String dirPath = parentPath + entryFile.getName() + PATH_SEPARATOR;

            File[] files = entryFile.listFiles();
            if (files.length == 0) {
                zipOutputStream.putNextEntry(new ZipEntry(dirPath));
            } else {
                for (int i = 0; i < files.length; i++) {
                    addZipEntry(zipOutputStream, files[i], dirPath);
                }
            }
        } else {
            InputStream entryFileStream = new FileInputStream(entryFile);

            try {
                ZipEntry entry = new ZipEntry(parentPath + entryFile.getName());

                zipOutputStream.putNextEntry(entry);

                byte[] buf = new byte[BUF_SIZE];
                int bufSize = 0;

                while ((bufSize = entryFileStream.read(buf, 0, BUF_SIZE)) != -1) {
                    zipOutputStream.write(buf, 0, bufSize);
                }

                zipOutputStream.closeEntry();
            } finally {
                entryFileStream.close();
            }
        }
    }
}

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;

import com.enjoyxstudy.selenium.autoexec.util.TemplateUtils;
import com.enjoyxstudy.selenium.htmlsuite.MultiHTMLSuiteRunner;

/**
 * @author onozaty
 */
public class ResultIndexHtmlWriter {

    /** logger */
    private static Log log = LogFactory.getLog(ResultIndexHtmlWriter.class);

    /** template file name */
    private static final String TEMPLATE_FILE_NAME = "resultIndexTemplate.html";

    /** BUF_SIZE */
    private static final int BUF_SIZE = 1024;

    /** template text */
    private String templateText;

    /**
     * 
     */
    public ResultIndexHtmlWriter() {

        try {
            // read template
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    this.getClass().getResourceAsStream(TEMPLATE_FILE_NAME),
                    "UTF-8"));
            try {
                StringBuilder builder = new StringBuilder();
                char[] buf = new char[BUF_SIZE];
                int bufSize = 0;

                while ((bufSize = reader.read(buf, 0, BUF_SIZE)) != -1) {
                    builder.append(buf, 0, bufSize);
                }

                templateText = builder.toString();

            } finally {
                reader.close();
            }
        } catch (IOException e) {
            log.error("Error Can't write result index.html", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param resultDir
     * @param runner
     * @throws IOException
     */
    public void write(File resultDir, MultiHTMLSuiteRunner runner)
            throws IOException {

        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(new File(resultDir, "index.html")),
                "UTF-8"));
        try {

            HashMap<String, Object> context = new HashMap<String, Object>();
            context.put("result", Boolean.valueOf(runner.getResult()));
            context.put("passedCount", new Integer(runner.getPassedCount()));
            context.put("failedCount", new Integer(runner.getFailedCount()));
            context.put("totalCount", new Integer(runner.getHtmlSuiteList()
                    .size()));
            context.put("startTime", new Date(runner.getStartTime()));
            context.put("endTime", new Date(runner.getEndTime()));
            context.put("htmlSuites", runner.getHtmlSuiteList());

            TemplateUtils.merge(templateText, context, writer);
        } finally {
            writer.close();
        }
    }
}

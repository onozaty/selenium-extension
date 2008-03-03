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

import com.enjoyxstudy.selenium.autoexec.util.TemplateUtils;
import com.enjoyxstudy.selenium.htmlsuite.MultiHTMLSuiteRunner;

/**
 * @author onozaty
 */
public class ResultIndexHtmlWriter {

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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @param resultDir
     * @param runner
     * @throws IOException
     */
    public void write(String resultDir, MultiHTMLSuiteRunner runner)
            throws IOException {

        Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(resultDir
                        + File.separator + "index.html"), "UTF-8"));
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

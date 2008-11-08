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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.log.LogFactory;

import com.enjoyxstudy.selenium.htmlsuite.HTMLSuite;
import com.enjoyxstudy.selenium.htmlsuite.MultiHTMLSuiteRunner;

/**
 * @author onozaty
 */
public class CommandHandler implements HttpHandler {

    /** serialVersionUID */
    private static final long serialVersionUID = -8717254990316146272L;

    /** logger */
    static Log log = LogFactory.getLog(CommandHandler.class);

    /** SUCCESS */
    private static final String SUCCESS = "success";

    /** FAILED */
    private static final String FAILED = "failed";

    /** PASSED */
    private static final String PASSED = "passed";

    /** DUPLICATE */
    private static final String DUPLICATE = "duplicate";

    /** RUN */
    private static final String RUN = "run";

    /** WAIT */
    private static final String WAIT = "wait";

    /** IDLE */
    private static final String IDLE = "idle";

    /** RUNNING */
    private static final String RUNNING = "running";

    /** LF */
    private static final String LF = "\n";

    /** type text */
    private static final String TYPE_TEXT = "text";

    /** type json */
    private static final String TYPE_JSON = "json";

    /** content type text */
    private static final String CONTENT_TYPE_TEXT = "text/plain; charset=utf-8";

    /** content type json */
    private static final String CONTENT_TYPE_JSON = "application/x-javascript; charset=utf-8";

    /** url separator */
    private static final String URL_SEPARATOR = "/";

    /** date format */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    /** context */
    private HttpContext context;

    /** is started */
    private boolean isStarted;

    /** autoExecServer */
    AutoExecServer autoExecServer;

    /** base result directory path */
    private final String baseResultDirPath;

    /**
     * @param _autoExecServer
     */
    public CommandHandler(AutoExecServer _autoExecServer) {
        super();
        autoExecServer = _autoExecServer;
        baseResultDirPath = autoExecServer.getConfig().getResultDir()
                .getAbsolutePath();
    }

    /**
     * @see org.mortbay.http.HttpHandler#handle(java.lang.String, java.lang.String, org.mortbay.http.HttpRequest, org.mortbay.http.HttpResponse)
     */
    public void handle(String pathInContext, @SuppressWarnings("unused")
    String pathParams, HttpRequest request, HttpResponse response)
            throws HttpException, IOException {

        request.setHandled(true);

        String commandName = pathInContext.replaceAll("^\\/|\\/$", "");

        if (log.isDebugEnabled()) {
            log.debug("Command[" + commandName + "] Exec.");
        }

        command(commandName, request, response);
    }

    /**
     * handle command.
     * 
     * @param commandName
     * @param request
     * @param response
     * @throws HttpException
     * @throws IOException 
     */
    private void command(String commandName, HttpRequest request,
            HttpResponse response) throws HttpException, IOException {

        // type
        String type = request.getParameter("type");
        if (type == null || type.equals("")) {
            type = TYPE_TEXT;
        }

        if (!type.equals(TYPE_TEXT) && !type.equals(TYPE_JSON)) {
            throw new HttpException(HttpServletResponse.SC_BAD_REQUEST);
        }

        try {
            if (commandName.equals("server/stop")) {

                log.info("Receive command(Stop server).");

                commandServerStop(type, response);

            } else if (commandName.equals("run")) {

                log.info("Receive command(Run test).");

                commandRun(type, response);
            } else if (commandName.equals("run/async")) {

                log.info("Receive command(Run test async).");

                commandRunAsync(type, response);
            } else if (commandName.equals("status")) {

                log.info("Receive command(Get status).");

                commandStatus(type, response);

            } else {
                throw new HttpException(HttpServletResponse.SC_BAD_REQUEST);
            }

        } catch (Exception e) {
            log.error("Command Error", e);
            throw new HttpException(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * stop server command.
     * 
     * @param type
     * @param response
     * @throws IOException
     */
    private void commandServerStop(String type, HttpResponse response)
            throws IOException {

        autoExecServer.runningStop();

        resultToResponse(response, SUCCESS, type);
    }

    /**
     * run command.
     * 
     * @param type
     * @param response
     * @throws Exception 
     */
    private void commandRun(String type, HttpResponse response)
            throws Exception {

        if (autoExecServer.getStatus() == AutoExecServer.STATUS_IDLE) {
            // idle
            MultiHTMLSuiteRunner runner = autoExecServer.process();

            String responseText;
            if (type.equals(TYPE_TEXT)) {
                // text
                StringBuilder stringBuilder = new StringBuilder();

                addResultString(stringBuilder, runner, false);
                responseText = stringBuilder.toString();
            } else {
                // json
                JSONObject json = new JSONObject();

                addResultJSON(json, runner, false);
                responseText = json.toString();
            }
            toResponse(response, responseText, type);
        } else {
            // running
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            resultToResponse(response, DUPLICATE, type);
        }

    }

    /**
     * run async command.
     * 
     * @param type
     * @param response
     * @throws IOException
     */
    private void commandRunAsync(String type, HttpResponse response)
            throws IOException {

        if (autoExecServer.getStatus() == AutoExecServer.STATUS_IDLE) {
            // idle
            new Thread(new Runnable() {
                public void run() {
                    try {
                        autoExecServer.process();
                    } catch (Exception e) {
                        log.error("Error exec process.", e);
                    }
                }
            }).start();

            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            resultToResponse(response, SUCCESS, type);

        } else {
            // running
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            resultToResponse(response, DUPLICATE, type);
        }
    }

    /**
     * status command.
     * 
     * @param type
     * @param response
     * @throws IOException 
     */
    private void commandStatus(String type, HttpResponse response)
            throws IOException {

        boolean isRunning = (autoExecServer.getStatus() == AutoExecServer.STATUS_RUNNING);

        String status = isRunning ? RUNNING : IDLE;

        String responseText;
        if (type.equals(TYPE_TEXT)) {
            // text
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("status: ").append(status).append(LF);
            stringBuilder.append("now time: ").append(
                    DATE_FORMAT.format(new Date())).append(LF);
            addResultString(stringBuilder, autoExecServer.getRunner(),
                    isRunning);

            responseText = stringBuilder.toString();
        } else {
            // json
            JSONObject json = new JSONObject();

            json.put("status", status);
            json.put("nowTime", DATE_FORMAT.format(new Date()));
            addResultJSON(json, autoExecServer.getRunner(), isRunning);

            responseText = json.toString();
        }
        toResponse(response, responseText, type);
    }

    /**
     * result to json.
     * 
     * @param json
     * @param runner
     * @param isRunning
     */
    private void addResultJSON(JSONObject json, MultiHTMLSuiteRunner runner,
            boolean isRunning) {

        if (runner != null) {
            json.put("result", isRunning ? null : runner.getResult() ? PASSED
                    : FAILED);
            json.put("totalCount",
                    new Integer(runner.getHtmlSuiteList().size()));
            json.put("passedCount", new Integer(runner.getPassedCount()));
            json.put("failedCount", new Integer(runner.getFailedCount()));

            json.put("startTime", DATE_FORMAT.format(new Date(runner
                    .getStartTime())));
            json.put("endTime", isRunning ? null : DATE_FORMAT.format(new Date(
                    runner.getEndTime())));

            JSONArray suiteArray = new JSONArray();
            for (HTMLSuite htmlSuite : runner.getHtmlSuiteList()) {

                JSONObject suite = new JSONObject();

                suite.put("suiteName", htmlSuite.getSuiteFile().getName());
                suite.put("resultPath", createResultFileURL(htmlSuite
                        .getResultFile()));
                suite.put("browser", htmlSuite.getBrowser());
                suite.put("status", selectSuiteStatus(htmlSuite, null));
                suiteArray.add(suite);
            }
            json.put("suites", suiteArray);
        }
    }

    /**
     * result to String.
     * 
     * @param stringBuilder
     * @param runner
     * @param isRunning
     */
    private void addResultString(StringBuilder stringBuilder,
            MultiHTMLSuiteRunner runner, boolean isRunning) {

        if (runner != null) {
            stringBuilder.append("result: ").append(
                    isRunning ? "-" : runner.getResult() ? PASSED : FAILED)
                    .append(LF);
            stringBuilder.append("number of cases: ");
            stringBuilder.append("passed: ").append(runner.getPassedCount());
            stringBuilder.append(" / failed: ").append(runner.getFailedCount());
            stringBuilder.append(" / total: ").append(
                    runner.getHtmlSuiteList().size()).append(LF);

            stringBuilder.append("start time: ").append(
                    DATE_FORMAT.format(new Date(runner.getStartTime())))
                    .append(LF);
            stringBuilder.append("end time  : ").append(
                    isRunning ? "-" : DATE_FORMAT.format(new Date(runner
                            .getEndTime()))).append(LF);

            stringBuilder.append(
                    "----------------------------------------------")
                    .append(LF);

            for (HTMLSuite htmlSuite : runner.getHtmlSuiteList()) {

                stringBuilder.append(htmlSuite.getSuiteFile().getName())
                        .append(": ");
                stringBuilder.append(htmlSuite.getBrowser()).append(": ");

                stringBuilder.append(selectSuiteStatus(htmlSuite, "-")).append(
                        LF);
            }
            stringBuilder.append(
                    "----------------------------------------------")
                    .append(LF);
        }
    }

    /**
     * select suite status.
     * 
     * @param htmlSuite
     * @param defaultStatus 
     * @return suite status
     */
    private String selectSuiteStatus(HTMLSuite htmlSuite, String defaultStatus) {

        String suiteStatus = defaultStatus;

        switch (htmlSuite.getStatus()) {
        case HTMLSuite.STATUS_WAIT:
            suiteStatus = WAIT;
            break;
        case HTMLSuite.STATUS_RUN:
            suiteStatus = RUN;
            break;
        case HTMLSuite.STATUS_FINISH:
            suiteStatus = htmlSuite.isPassed() ? PASSED : FAILED;
            break;
        default:
            break;
        }
        return suiteStatus;
    }

    /**
     * result to response.
     * 
     * @param response
     * @param text
     * @param type
     * @throws IOException
     */
    private void resultToResponse(HttpResponse response, String text,
            String type) throws IOException {

        if (type.equals(TYPE_TEXT)) {
            // text
            text = "result: " + text;
        } else {
            // json
            JSONObject json = new JSONObject();
            json.put("result", text);
            text = json.toString();
        }
        toResponse(response, text, type);
    }

    /**
     * text to response.
     * 
     * @param response
     * @param text
     * @param type
     * @throws IOException
     */
    private void toResponse(HttpResponse response, String text, String type)
            throws IOException {

        if (type.equals(TYPE_TEXT)) {
            // text
            response.setContentType(CONTENT_TYPE_TEXT);
        } else {
            // json
            response.setContentType(CONTENT_TYPE_JSON);
        }

        Writer writer = getResponceWriter(response);

        try {
            writer.write(text);
        } finally {
            writer.close();
        }
    }

    /**
     * return response writer.
     * 
     * @param response
     * @return response writer
     * @throws UnsupportedEncodingException
     */
    private Writer getResponceWriter(HttpResponse response)
            throws UnsupportedEncodingException {

        OutputStream outputStream = response.getOutputStream();
        Writer writer = new OutputStreamWriter(outputStream, response
                .getCharacterEncoding());
        return writer;
    }

    /**
     * @param resultFile
     * @return result file URL
     */
    private String createResultFileURL(File resultFile) {

        StringBuilder resultFileURL = new StringBuilder(
                AutoExecServer.CONTEXT_PATH_RESULT);
        resultFileURL.append(resultFile.getAbsolutePath().substring(
                baseResultDirPath.length() + URL_SEPARATOR.length()).replace(
                File.separator, URL_SEPARATOR));

        return resultFileURL.toString();
    }

    /**
     * @see org.mortbay.http.HttpHandler#initialize(org.mortbay.http.HttpContext)
     */
    public void initialize(HttpContext _context) {
        context = _context;
    }

    /**
     * @see org.mortbay.http.HttpHandler#getHttpContext()
     */
    public HttpContext getHttpContext() {
        return context;
    }

    /**
     * @see org.mortbay.http.HttpHandler#getName()
     */
    public String getName() {
        return CommandHandler.class.getName();
    }

    /**
     * @see org.mortbay.util.LifeCycle#isStarted()
     */
    public boolean isStarted() {
        return isStarted;
    }

    /**
     * @see org.mortbay.util.LifeCycle#start()
     */
    public void start() {
        isStarted = true;
    }

    /**
     * @see org.mortbay.util.LifeCycle#stop()
     */
    public void stop() {
        isStarted = false;
    }

}

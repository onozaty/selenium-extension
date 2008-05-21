package com.enjoyxstudy.selenium.autoexec;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

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

    /** type text */
    private static String TYPE_TEXT = "text";

    /** type json */
    private static String TYPE_JSON = "json";

    /** content type text */
    private static final String CONTENT_TYPE_TEXT = "text/plain; charset=utf-8";

    /** content type json */
    private static final String CONTENT_TYPE_JSON = "application/x-javascript; charset=utf-8";

    /** context */
    private HttpContext context;

    /** is started */
    private boolean isStarted;

    /** autoExecServer */
    AutoExecServer autoExecServer;

    /**
     * @param _autoExecServer
     */
    public CommandHandler(AutoExecServer _autoExecServer) {
        super();
        autoExecServer = _autoExecServer;
    }

    /**
     * @see org.mortbay.http.HttpHandler#handle(java.lang.String, java.lang.String, org.mortbay.http.HttpRequest, org.mortbay.http.HttpResponse)
     */
    public void handle(String pathInContext, @SuppressWarnings("unused")
    String pathParams, HttpRequest request, HttpResponse response)
            throws HttpException, IOException {

        request.setHandled(true);

        String commandName = pathInContext.replaceAll("^\\/|\\/$", "");

        log.info("Command[" + commandName + "] Exec.");

        command(commandName, request, response);
    }

    /**
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

        if (commandName.equals("server/stop")) {
            autoExecServer.runningStop();
            resultToResponse(response, "success", type);
        } else if (commandName.equals("run")) {
            try {
                if (autoExecServer.getStatus() == AutoExecServer.STATUS_IDLE) {
                    // idle
                    MultiHTMLSuiteRunner runner = autoExecServer.process();
                    resultToResponse(response, (runner.getResult() ? "passed"
                            : "failed"), type);
                } else {
                    // running
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    resultToResponse(response, "duplicate", type);
                }
            } catch (Exception e) {
                throw new HttpException(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if (commandName.equals("run/async")) {
            try {
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
                    resultToResponse(response, "success", type);

                } else {
                    // running
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    resultToResponse(response, "duplicate", type);
                }

            } catch (Exception e) {
                throw new HttpException(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if (commandName.equals("status")) {
            try {
                commandStatus(type, response);
            } catch (Exception e) {
                throw new HttpException(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new HttpException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * @param type
     * @param response
     * @throws IOException 
     */
    private void commandStatus(String type, HttpResponse response)
            throws IOException {

        boolean isRunning = (autoExecServer.getStatus() == AutoExecServer.STATUS_RUNNING);

        String status = isRunning ? "running" : "idle";

        if (type.equals(TYPE_TEXT)) {
            // text
            resultToResponse(response, status, type);
        } else {
            // json
            StringBuilder jsonString = new StringBuilder("{");
            jsonString.append("\"status\": ").append(toJSON(status));

            MultiHTMLSuiteRunner runner = autoExecServer.getRunner();

            if (runner != null) {
                jsonString.append(", \"result\": ").append(
                        toJSON(isRunning ? null : runner.getResult() ? "passed"
                                : "failed"));
                jsonString.append(", \"totalCount\": ").append(
                        runner.getHtmlSuiteList().size());
                jsonString.append(", \"passedCount\": ").append(
                        runner.getPassedCount());
                jsonString.append(", \"failedCount\": ").append(
                        runner.getFailedCount());

                jsonString.append(", \"startTime\": ").append(
                        toJSON(new Date(runner.getStartTime()).toString()));
                jsonString.append(", \"endTime\": ").append(
                        isRunning ? null : toJSON(new Date(runner.getEndTime())
                                .toString()));
                jsonString.append(", \"nowTime\": ").append(
                        toJSON(new Date().toString()));

                jsonString.append(", \"suites\": [");
                boolean first = true;
                for (HTMLSuite htmlSuite : runner.getHtmlSuiteList()) {

                    if (first) {
                        first = false;
                    } else {
                        jsonString.append(", ");
                    }

                    jsonString.append("{\"suiteName\": ").append(
                            toJSON(htmlSuite.getSuiteFile().getName()));
                    jsonString.append(", \"resultPath\": ").append(
                            toJSON(AutoExecServer.CONTEXT_PATH_RESULT
                                    + htmlSuite.getResultFile().getParentFile()
                                            .getName() + "/"
                                    + htmlSuite.getResultFile().getName()));
                    jsonString.append(", \"browser\": ").append(
                            toJSON(htmlSuite.getBrowser()));

                    String suiteStatus = null;
                    switch (htmlSuite.getStatus()) {
                    case HTMLSuite.STATUS_WAIT:
                        suiteStatus = "wait";
                        break;
                    case HTMLSuite.STATUS_RUN:
                        suiteStatus = "run";
                        break;
                    case HTMLSuite.STATUS_FINISH:
                        suiteStatus = htmlSuite.isPassed() ? "passed"
                                : "failed";
                        break;
                    default:
                        break;
                    }
                    jsonString.append(", \"status\": ").append(
                            toJSON(suiteStatus));
                    jsonString.append("}");
                }
                jsonString.append("]");
            }
            jsonString.append("}");

            response.setContentType(CONTENT_TYPE_JSON);

            Writer writer = getResponceWriter(response);
            try {
                writer.append(jsonString);
            } finally {
                writer.close();
            }
        }
    }

    /**
     * @param response
     * @param text
     * @param type
     * @throws IOException
     */
    private void resultToResponse(HttpResponse response, String text,
            String type) throws IOException {

        if (type.equals(TYPE_TEXT)) {
            // text
            response.setContentType(CONTENT_TYPE_TEXT);
        } else {
            // json
            response.setContentType(CONTENT_TYPE_JSON);
        }

        Writer writer = getResponceWriter(response);

        try {
            if (type.equals(TYPE_TEXT)) {
                // text
                writer.write(text);
            } else {
                // json
                writer.write("{\"result\": " + toJSON(text) + "}");
            }
        } finally {
            writer.close();
        }
    }

    /**
     * @param response
     * @return responce writer
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
     * @param text
     * @return json string
     */
    private String toJSON(String text) {

        if (text == null) {
            return "null";
        }

        StringBuffer buffer = new StringBuffer("\"");

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            switch (ch) {
            case '\b':
                buffer.append('\\');
                buffer.append('b');
                break;
            case '\n':
                buffer.append('\\');
                buffer.append('n');
                break;
            case '\t':
                buffer.append('\\');
                buffer.append('t');
                break;
            case '\f':
                buffer.append('\\');
                buffer.append('f');
                break;
            case '\r':
                buffer.append('\\');
                buffer.append('r');
                break;
            case '"':
                buffer.append('\\');
                buffer.append('"');
                break;
            case '\\':
                buffer.append('\\');
                buffer.append('\\');
                break;
            default:
                buffer.append(ch);
                break;
            }
        }

        buffer.append("\"");
        return buffer.toString();
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

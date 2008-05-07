package com.enjoyxstudy.selenium.autoexec;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.log.LogFactory;

import com.enjoyxstudy.selenium.htmlsuite.MultiHTMLSuiteRunner;

/**
 * @author onozaty
 */
public class CommandHandler implements HttpHandler {

    /** serialVersionUID */
    private static final long serialVersionUID = -8717254990316146272L;

    /** logger */
    private static Log log = LogFactory.getLog(CommandHandler.class);

    /** context */
    private HttpContext context;

    /** is started */
    private boolean isStarted;

    /** autoExecServer */
    private AutoExecServer autoExecServer;

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
    private void command(String commandName, @SuppressWarnings("unused")
    HttpRequest request, HttpResponse response) throws HttpException,
            IOException {

        if (commandName.equals("server/stop")) {
            autoExecServer.runningStop();
            responseText(response, "success");
        } else if (commandName.equals("run")) {
            try {
                MultiHTMLSuiteRunner runner = autoExecServer.process();
                if (runner.getResult()) {
                    responseText(response, "passed");
                } else {
                    responseText(response, "failed");
                }
            } catch (Exception e) {
                throw new HttpException(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new HttpException(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * @param response
     * @param text
     * @throws IOException
     */
    private void responseText(HttpResponse response, String text)
            throws IOException {

        response.setContentType("text/plain; charset=utf-8");

        OutputStream outputStream = response.getOutputStream();
        Writer writer = new OutputStreamWriter(outputStream, response
                .getCharacterEncoding());
        try {
            writer.write(text);
        } finally {
            writer.close();
        }
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

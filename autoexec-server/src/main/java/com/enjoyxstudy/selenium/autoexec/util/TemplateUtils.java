package com.enjoyxstudy.selenium.autoexec.util;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * @author onozaty
 */
public final class TemplateUtils {

    /** velocityEngine */
    private static final VelocityEngine velocityEngine = new VelocityEngine();

    static {
        try {
            velocityEngine.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param input
     * @param context
     * @param out
     */
    public static void merge(Reader input, Map<String, Object> context,
            Writer out) {

        try {
            velocityEngine.evaluate(new VelocityContext(context), out,
                    "exec reader", input);
        } catch (Exception e) {
            throw new TemplateRuntimeException(e);
        }
    }

    /**
     * @param input
     * @param context
     * @param out 
     */
    public static void merge(String input, Map<String, Object> context,
            Writer out) {

        StringReader reader = new StringReader(input);

        merge(reader, context, out);
    }

    /**
     * @param input
     * @param context
     * @return string
     */
    public static String merge(String input, Map<String, Object> context) {

        StringWriter writer = new StringWriter();

        merge(input, context, writer);

        return writer.toString();
    }
}

/**
 * @author onozaty
 */
class TemplateRuntimeException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = -7341917516834914989L;

    /**
     * 
     */
    public TemplateRuntimeException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public TemplateRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public TemplateRuntimeException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public TemplateRuntimeException(Throwable cause) {
        super(cause);
    }
}
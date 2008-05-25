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

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.mortbay.log.LogFactory;

/**
 * @author onozaty
 */
public final class TemplateUtils {

    /** logger */
    private static Log log = LogFactory.getLog(TemplateUtils.class);

    /** velocityEngine */
    private static final VelocityEngine velocityEngine = new VelocityEngine();

    static {
        try {
            velocityEngine.init();
        } catch (Exception e) {
            log.error("Error VelocityEngine can't init.", e);
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
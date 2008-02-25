/* 
 * $Id$
 * 
 * 2007/12/28 : 新規作成
 * 
 */
package com.enjoyxstudy.selenium.autoexec.mail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author onozaty
 */
public class MailConfig {

    /** protocol */
    private String protocol = "smtp";

    /** host */
    private String host;

    /** port */
    private int port = -1;

    /** connectionTimeout */
    private int connectionTimeout = 10000;

    /** readTimeout */
    private int readTimeout = 10000;

    /** username */
    private String username;

    /** password */
    private String password;

    /** charset */
    private String charset = "iso-8859-1";

    /** to */
    private String to;

    /** from */
    private String from;

    /** subject */
    private String subject;

    /** body */
    private String body;

    /** ssl */
    private boolean ssl;

    /** debug */
    private boolean debug;

    /**
     * 
     */
    public MailConfig() {
        //
    }

    /**
     * @param properties
     * @throws IOException
     */
    public MailConfig(Properties properties) throws IOException {

        String temp;
        if ((temp = properties.getProperty("mail.protocol")) != null) {
            protocol = temp;
        }

        host = properties.getProperty("mail.host");

        if ((temp = properties.getProperty("mail.port")) != null) {
            port = Integer.parseInt(temp);
        }
        if ((temp = properties.getProperty("mail.connectionTimeout")) != null) {
            connectionTimeout = Integer.parseInt(temp);
        }
        if ((temp = properties.getProperty("mail.readTimeout")) != null) {
            readTimeout = Integer.parseInt(temp);
        }

        username = properties.getProperty("mail.username");
        password = properties.getProperty("mail.password");

        if ((temp = properties.getProperty("mail.charset")) != null) {
            charset = temp;
        }

        if ((temp = properties.getProperty("mail.to")) != null) {
            to = temp;
        }
        if ((temp = properties.getProperty("mail.from")) != null) {
            from = temp;
        }
        if ((temp = properties.getProperty("mail.subject")) != null) {
            subject = temp;
        }

        InputStream inputStream = null;
        BufferedReader reader = null;
        if ((temp = properties.getProperty("mail.bodyPath")) != null) {
            inputStream = new FileInputStream(temp);
        } else {
            inputStream = this.getClass().getResourceAsStream(
                    "mailBodyTemplate.txt");
        }
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF-8"));

            StringBuilder builder = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                builder.append(ch);
            }
            body = builder.toString();

        } finally {
            if (reader != null) {
                reader.close();
            }
            inputStream.close();
        }

        ssl = Boolean.parseBoolean(properties.getProperty("mail.ssl"));
        debug = Boolean.parseBoolean(properties.getProperty("mail.debug"));
    }

    /**
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * @return connectionTimeout
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @param connectionTimeout connectionTimeout
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @return debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return from
     */
    public String getFrom() {
        return from;
    }

    /**
     * @param from from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return readTimeout
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * @param readTimeout readTimeout
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * @return ssl
     */
    public boolean isSsl() {
        return ssl;
    }

    /**
     * @param ssl ssl
     */
    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    /**
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return to
     */
    public String getTo() {
        return to;
    }

    /**
     * @param to to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

}

package com.enjoyxstudy.selenium.autoexec.mail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.enjoyxstudy.selenium.htmlsuite.util.PropertiesUtils;

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
    private String subject = "Selenium Test Report #if($result)[passed]#else[failed]#end";

    /** body */
    private String body;

    /** ssl */
    private boolean ssl;

    /** debug */
    private boolean debug;

    /** BUF_SIZE */
    private static final int BUF_SIZE = 1024;

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

        protocol = PropertiesUtils.getString(properties, "mail.protocol",
                protocol);

        host = PropertiesUtils.getString(properties, "mail.host", host);
        port = PropertiesUtils.getInt(properties, "mail.port", port);

        connectionTimeout = PropertiesUtils.getInt(properties,
                "mail.connectionTimeout", connectionTimeout);
        readTimeout = PropertiesUtils.getInt(properties, "mail.readTimeout",
                readTimeout);

        username = PropertiesUtils.getString(properties, "mail.username");
        password = PropertiesUtils.getString(properties, "mail.password");

        charset = PropertiesUtils
                .getString(properties, "mail.charset", charset);

        to = PropertiesUtils.getString(properties, "mail.to", to);
        from = PropertiesUtils.getString(properties, "mail.from", from);
        subject = PropertiesUtils
                .getString(properties, "mail.subject", subject);

        InputStream inputStream = null;
        BufferedReader reader = null;
        String bodyPath = PropertiesUtils
                .getString(properties, "mail.bodyPath");
        if (bodyPath != null) {
            inputStream = new FileInputStream(bodyPath);
        } else {
            inputStream = this.getClass().getResourceAsStream(
                    "mailBodyTemplate.txt");
        }
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF-8"));

            StringBuilder builder = new StringBuilder();
            char[] buf = new char[BUF_SIZE];
            int bufSize = 0;

            while ((bufSize = reader.read(buf, 0, BUF_SIZE)) != -1) {
                builder.append(buf, 0, bufSize);
            }

            body = builder.toString();

        } finally {
            if (reader != null) {
                reader.close();
            }
            inputStream.close();
        }

        ssl = PropertiesUtils.getBoolean(properties, "mail.ssl", ssl);
        debug = PropertiesUtils.getBoolean(properties, "mail.debug", debug);
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

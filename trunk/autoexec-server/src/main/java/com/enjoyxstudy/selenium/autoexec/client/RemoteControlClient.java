/*
 * Copyright (c) 2008 onozaty (http://www.enjoyxstudy.com)
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
package com.enjoyxstudy.selenium.autoexec.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author onozaty
 */
public class RemoteControlClient {

    /** Selenium AutoExec Server command URL */
    private String commandUrl = "http://localhost:4444/selenium-server/autoexec/command/";

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        String url = null;
        boolean isAsync = false;

        for (String arg : args) {
            if (arg.equals("-a")) {
                isAsync = true;
            } else {
                url = arg;
            }
        }

        RemoteControlClient client = new RemoteControlClient(url);

        if (isAsync) {
            System.out.println("run async. reuslt:" + client.runAsync());
        } else {
            System.out.println("run. reuslt:" + client.run());
        }
    }

    /**
     * constructor.
     */
    public RemoteControlClient() {
        //
    }

    /**
     * @param commandUrl
     */
    public RemoteControlClient(String commandUrl) {
        if (commandUrl != null && !commandUrl.equals("")) {
            this.commandUrl = commandUrl;
        }
    }

    /**
     * call "run" command.
     * @return result
     * @throws IOException
     */
    public boolean run() throws IOException {

        String result = doCommand("run");

        if (result.equals("passed")) {
            return true;
        } else if (result.equals("failed")) {
            return false;
        } else {
            throw new IOException("illegal result.  result=[" + result + "]");
        }
    }

    /**
     * call "run async" command.
     * @return result
     * @throws IOException
     */
    public boolean runAsync() throws IOException {

        String result = doCommand("run/async");

        if (result.equals("success")) {
            return true;
        } else if (result.equals("failed")) {
            return false;
        } else {
            throw new IOException("illegal result.  result=[" + result + "]");
        }
    }

    /**
     * call remote command.
     * 
     * @param command
     * @return response
     * @throws IOException
     */
    private String doCommand(String command) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) new URL(commandUrl
                + command).openConnection();

        try {
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK
                    && responseCode != HttpURLConnection.HTTP_ACCEPTED) {
                throw new IOException("request failed.  responseCode=["
                        + responseCode + "]");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            try {
                StringWriter writer = new StringWriter();
                char[] buff = new char[128];
                int length;

                while ((length = reader.read(buff)) != -1) {
                    writer.write(buff, 0, length);
                }
                return writer.toString();

            } finally {
                reader.close();
            }

        } finally {
            connection.disconnect();
        }
    }
}

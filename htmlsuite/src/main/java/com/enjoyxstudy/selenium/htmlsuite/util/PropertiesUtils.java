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
package com.enjoyxstudy.selenium.htmlsuite.util;

import java.util.Properties;

/**
 * @author onozaty
 */
public final class PropertiesUtils {

    /**
     * @param properties
     * @param key
     * @param defaultValue
     * @return properties value
     */
    public static int getInt(Properties properties, String key, int defaultValue) {

        String value = getString(properties, key);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    /**
     * @param properties
     * @param key
     * @param defaultValue
     * @return properties value
     */
    public static boolean getBoolean(Properties properties, String key,
            boolean defaultValue) {

        String value = getString(properties, key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    /**
     * @param properties
     * @param key
     * @return properties value
     */
    public static boolean getBoolean(Properties properties, String key) {

        return getBoolean(properties, key, false);
    }

    /**
     * @param properties
     * @param key
     * @param defaultValue
     * @return properties value
     */
    public static String getString(Properties properties, String key,
            String defaultValue) {

        String value = properties.getProperty(key);
        if (value != null && !value.equals("")) {
            return value;
        }
        return defaultValue;
    }

    /**
     * @param properties
     * @param key
     * @return properties value
     */
    public static String getString(Properties properties, String key) {

        return getString(properties, key, null);
    }
}

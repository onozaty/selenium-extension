package com.enjoyxstudy.selenium.htmlsuite.util;

import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * PropertiesUtils test case.
 * 
 * @author onozaty
 */
public class PropertiesUtilsTest extends TestCase {

    /** PROPERTY_FILE */
    private static final String PROPERTY_FILE = "PropertiesUtilsTest.properties";

    /** properties */
    private Properties properties;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        super.setUp();

        properties = new Properties();
        InputStream inputStream = this.getClass().getResourceAsStream(
                PROPERTY_FILE);
        try {
            properties.load(inputStream);
        } finally {
            inputStream.close();
        }
    }

    /**
     * test {@link PropertiesUtils#getInt(Properties, String, int)}
     */
    public void testGetInt() {

        assertEquals(1, PropertiesUtils.getInt(properties, "intNone", 1));
        assertEquals(-1, PropertiesUtils.getInt(properties, "intEmpty", -1));
        assertEquals(1, PropertiesUtils.getInt(properties, "int1", 0));
        assertEquals(-100, PropertiesUtils.getInt(properties, "int-100", 0));
    }

    /**
     * test {@link PropertiesUtils#getBoolean(Properties, String, boolean)}
     */
    public void testGetBooleanPropertiesStringBoolean() {

        assertTrue(!PropertiesUtils
                .getBoolean(properties, "booleanNone", false));
        assertTrue(!PropertiesUtils.getBoolean(properties, "booleanEmpty",
                false));
        assertTrue(PropertiesUtils.getBoolean(properties, "booleanTrue", false));
        assertTrue(!PropertiesUtils
                .getBoolean(properties, "booleanFalse", true));
        assertTrue(!PropertiesUtils.getBoolean(properties, "boolean0", true));
        assertTrue(!PropertiesUtils.getBoolean(properties, "boolean1", true));
        assertTrue(!PropertiesUtils
                .getBoolean(properties, "booleanOther", true));
    }

    /**
     * test {@link PropertiesUtils#getBoolean(Properties, String)}
     */
    public void testGetBooleanPropertiesString() {

        assertTrue(!PropertiesUtils.getBoolean(properties, "booleanNone"));
        assertTrue(!PropertiesUtils.getBoolean(properties, "booleanEmpty"));
        assertTrue(PropertiesUtils.getBoolean(properties, "booleanTrue"));
        assertTrue(!PropertiesUtils.getBoolean(properties, "booleanFalse"));
        assertTrue(!PropertiesUtils.getBoolean(properties, "boolean0"));
        assertTrue(!PropertiesUtils.getBoolean(properties, "boolean1"));
        assertTrue(!PropertiesUtils.getBoolean(properties, "booleanOther"));
    }

    /**
     * test {@link PropertiesUtils#getString(Properties, String, String)}
     */
    public void testGetStringPropertiesStringString() {

        assertEquals("none", PropertiesUtils.getString(properties,
                "stringNone", "none"));
        assertEquals(null, PropertiesUtils.getString(properties, "stringEmpty",
                null));
        assertEquals("", PropertiesUtils.getString(properties, "stringEmpty",
                ""));
        assertEquals("hoge", PropertiesUtils.getString(properties,
                "stringHoge", null));
    }

    /**
     * test {@link PropertiesUtils#getString(Properties, String)}
     */
    public void testGetStringPropertiesString() {

        assertEquals(null, PropertiesUtils.getString(properties, "stringNone"));
        assertEquals(null, PropertiesUtils.getString(properties, "stringEmpty"));
        assertEquals("hoge", PropertiesUtils
                .getString(properties, "stringHoge"));
        assertEquals("1234", PropertiesUtils
                .getString(properties, "string1234"));
    }

}

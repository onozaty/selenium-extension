/* 
 * $Id$
 * 
 * 2007/12/28 : 新規作成
 * 
 */
package com.enjoyxstudy.selenium.autoexec.util;

import java.util.HashMap;

import junit.framework.TestCase;

/**
 * @author onozaty
 */
public class TemplateUtilsTest extends TestCase {

    /**
     * test
     */
    public void testMerge() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("key1", "Hello!");
        map.put("key2", "Smith");

        assertEquals("Hello! Mr. Smith.", TemplateUtils.merge(
                "${key1} Mr. ${key2}.", map));
    }
}

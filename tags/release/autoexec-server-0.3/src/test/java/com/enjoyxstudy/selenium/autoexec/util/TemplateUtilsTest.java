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

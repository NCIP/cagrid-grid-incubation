/**
 * Copyright 2010 Emory University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated  documentation files (the 
 * "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions: The above copyright notice and this permission
 * notice shall be included in all copies or substantial portions of the
 * Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package edu.emory.cci.cqlCsm.cqlCsmCql;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * This is a map that associates the name of an attribute with a set of
 * authorized attribute value strings.
 * 
 * @author Mark Grand
 */
class AuthorizedAttributeValues {
    private HashMap<String, TreeSet<String>> authorizedValueMap = new HashMap<String, TreeSet<String>>();

    /**
     * Associate the given set of values with the given attribute name. The
     * given set of values replaces any set of values previously associated with
     * the named attribute.
     * 
     * @param attributeName
     *            The name of the attribute that will be associated with the set
     *            of values.
     * @param valueSet
     *            the set of values to be associated with the named attribute.
     */
    void set(String attributeName, TreeSet<String> valueSet) {
        synchronized (authorizedValueMap) {
            authorizedValueMap.put(attributeName, valueSet);
        }
    }

    /**
     * Return the set of value strings associated with the named attribute.
     * 
     * @param attributeName
     *            The name of an attribute.
     * @return The set of value strings associated with the named attribute. The
     *         returned object will not be subsequently referenced or modified
     *         by this object. If no set of values is associated with the named
     *         attribute then the returned value is null.
     */
    @SuppressWarnings("unchecked")
    TreeSet<String> getAuthorizedValues(String attributeName) {
        TreeSet<String> valueSet;
        synchronized (authorizedValueMap) {
            valueSet = authorizedValueMap.get(attributeName);
        }
        if (valueSet == null) {
            return null;
        }
        return (TreeSet<String>) valueSet.clone();
    }
}

/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
/**
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

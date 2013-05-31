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
package edu.emory.cci.cqlCsm.cqlCsmCql;

import static org.junit.Assert.*;

import java.util.TreeSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Junit test for AuthorizedAttributeValues
 * 
 * @author Mark Grand
 */
public class AuthorizedAttributeValuesTest {
    AuthorizedAttributeValues attributeValues;

    @Before
    public void setUp() throws Exception {
        attributeValues = new AuthorizedAttributeValues();
    }

    @Test
    public void testAdd() {
        TreeSet<String> s1 = new TreeSet<String>();
        s1.add("23");
        s1.add("28");
        s1.add(null);
        attributeValues.set("foo", s1);

        TreeSet<String> s2 = new TreeSet<String>();
        s2.add("53");
        s2.add("44");
        s2.add("037");
        attributeValues.set("bar", s2);
    }

    @Test
    public void testGetAuthorizedValues() {
        TreeSet<String> s1 = new TreeSet<String>();
        s1.add("23");
        s1.add("28");
        s1.add(null);
        attributeValues.set("foo", s1);

        TreeSet<String> s2 = new TreeSet<String>();
        s2.add("53");
        s2.add("44");
        s2.add("037");
        attributeValues.set("bar", s2);

        Set<String> valueSetLaLa = attributeValues.getAuthorizedValues("lala");
        assertNull(valueSetLaLa);
        Set<String> valueSetFoo = attributeValues.getAuthorizedValues("foo");
        assertEquals(3, valueSetFoo.size());
        assertTrue(valueSetFoo.contains("23"));
        assertTrue(valueSetFoo.contains("28"));
        assertTrue(valueSetFoo.contains(null));

        s1.add("999");
        assertEquals(3, valueSetFoo.size());
        assertEquals(4, attributeValues.getAuthorizedValues("foo").size());
    }
}

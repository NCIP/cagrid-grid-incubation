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

import org.junit.Test;

import edu.emory.cci.cqlCsm.Filter;

public class FilterTest {

    @Test
    public void testFilter() {
        new Filter("aa.bb.cc","","foo", "aa.bb.cc");
        new Filter("aa.bb.cc","aa.bb.cc","foo", "aa.bb.cc");
        new Filter("aa.bb.cc","xray","foo","com.zid.sdw");
        new Filter("aa.bb.cc","xray,zonk","foo","com.zid.sdw");
        new Filter("aa.bb.cc","xray,conk,wufj","foo","com.zid.sdw");
    }

    @Test
    public void testGetClassName() {
        Filter f1 = new Filter("aa.bb.cc","","foo", "aa.bb.cc");
        assertEquals("aa.bb.cc", f1.getClassName());
    }

    @Test
    public void testGetAssociationPath() {
        Filter f1 = new Filter("aa.bb.cc","","foo", "aa.bb.cc");
        assertEquals(0, f1.getAssociationPath().length);

        Filter f2 = new Filter("aa.bb.cc","aa.bb.cc","foo", "aa.bb.cc");
        assertEquals(0, f2.getAssociationPath().length);

        Filter f3 = new Filter("aa.bb.cc","xray","foo","com.zid.sdw");
        assertEquals(1, f3.getAssociationPath().length);
        assertEquals("xray", f3.getAssociationPath()[0]);

        Filter f4 = new Filter("aa.bb.cc","xray,zonk","foo","com.zid.sdw");
        assertEquals(2, f4.getAssociationPath().length);
        assertEquals("xray", f4.getAssociationPath()[0]);
        assertEquals("zonk", f4.getAssociationPath()[1]);

        Filter f5 = new Filter("aa.bb.cc","xray,conk,wufj","foo","com.zid.sdw");
        assertEquals(3, f5.getAssociationPath().length);
        assertEquals("xray", f5.getAssociationPath()[0]);
        assertEquals("conk", f5.getAssociationPath()[1]);
        assertEquals("wufj", f5.getAssociationPath()[2]);
    }

    @Test
    public void testGetAttributeName() {
        Filter f2 = new Filter("aa.bb.cc","aa.bb.cc","foo", "aa.bb.cc");
        assertEquals("foo", f2.getAttributeName());
    }

    @Test
    public void testGetTargetClassName() {
        Filter f2 = new Filter("aa.bb.cc","aa.bb.cc","foo", "aa.bb.cc");
        assertEquals("aa.bb.cc", f2.getTargetClassName());
    }

}

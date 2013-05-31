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
package org.cagrid.redcap.cql.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RedcapSampleTest extends TestCase{
	
	 public RedcapSampleTest(){
		 super();
	 }
	 
	 public RedcapSampleTest(String name){
		 super(name);
	 }
	 
		 
	public void testSample(){
		assertTrue(Boolean.TRUE);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(RedcapSampleTest.class);
		return suite;
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
}

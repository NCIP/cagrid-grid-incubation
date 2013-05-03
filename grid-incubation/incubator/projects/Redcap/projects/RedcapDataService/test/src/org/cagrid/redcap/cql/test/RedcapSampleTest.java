/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
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

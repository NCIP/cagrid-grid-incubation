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

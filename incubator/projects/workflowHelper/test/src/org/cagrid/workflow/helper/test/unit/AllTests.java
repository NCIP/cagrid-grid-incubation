package org.cagrid.workflow.helper.test.unit;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for unitTests");
		//$JUnit-BEGIN$
		suite.addTestSuite(ServiceInvocationUtilTest.class);
		suite.addTestSuite(FileUtilTest.class);
		suite.addTestSuite(WorkflowHelperServiceImplTest.class);
		//$JUnit-END$
		return suite;
	}

	public static void main(String args[]){
		
		Test all_tests = AllTests.suite();
		TestResult result = new TestResult();
		all_tests.run(result);
	} 
	
}

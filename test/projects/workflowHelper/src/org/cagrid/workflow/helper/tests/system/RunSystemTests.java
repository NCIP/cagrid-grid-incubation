package org.cagrid.workflow.helper.tests.system;

import junit.framework.TestResult;


/** Run the system tests for WorkflowHelperService
 * 
 * NOTE Properties that must be set
 *  [1] test.dir        (point to directory upon which the test workflow's services will be)
 *  [2] testing.containers.dir     (point to directory where we have the containers' ZIP files)    
 * 
 * **/
public class RunSystemTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("BEGIN RunSystemTests");

		System.out.println("Instantiating WorkflowTestStoryBook");
		WorkflowTestStoryBook test_workflow = new WorkflowTestStoryBook();
		TestResult result = new TestResult();
		test_workflow.run(result);
		System.out.println("END RunSystemTests");
	}

}

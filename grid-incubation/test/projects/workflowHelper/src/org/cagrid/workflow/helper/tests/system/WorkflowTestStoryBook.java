package org.cagrid.workflow.helper.tests.system;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.StoryBook;

public class WorkflowTestStoryBook extends StoryBook {

	@Override
	protected void stories() {
		
		System.out.println("BEGIN WorkflowTestStoryBook");
		
		// Deploy services of workflow and WorkflowHelperService
		System.out.println("Adding DeployWorkflowServicesTest");
		DeployWorkflowServicesTest dwst = new DeployWorkflowServicesTest();
		try {
			dwst.runBare();
		} catch (Throwable e) {
			e.printStackTrace();
			return;
		}
		
		
		// Execute the test workflow
		System.out.println("Adding ExecuteWorkflowTest");
		ServiceContainer globus_container = dwst.getContainer();
		ExecuteWorkflowTest ewt = new ExecuteWorkflowTest(globus_container);
		this.addStory(ewt);
		
		System.out.println("END WorkflowTestStoryBook");

	}

}

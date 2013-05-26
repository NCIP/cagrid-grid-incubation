/**
*============================================================================
*  Copyright (c) 2008, The Ohio State University Research Foundation, Emory 
*  University, the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.workflow.helper.tests.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.apache.axis.message.addressing.EndpointReference;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;

public class CreateWorkflowInvocationHelperStep extends Step {


	private WorkflowInvocationHelperDescriptor stageDesc = null;
	private EndpointReference helperEPR = null;
	private WorkflowInvocationHelperClient stageClient = null;


	public CreateWorkflowInvocationHelperStep(WorkflowInvocationHelperDescriptor stageDesc, EndpointReference helperEPR){
		this.helperEPR = helperEPR;
		this.stageDesc = stageDesc;
	}



	public void runStep() throws Throwable {

		System.out.println("BEGIN CreateWorkflowInvocationHelperStep");

		WorkflowInstanceHelperClient helper = new WorkflowInstanceHelperClient(getHelperEPR());
		WorkflowInvocationHelperClient stage_client = helper.createWorkflowInvocationHelper(getStageDesc());

		setStageClient(stage_client);
		System.out.println("END CreateWorkflowInvocationHelperStep");
	}





	private WorkflowInvocationHelperDescriptor getStageDesc() {
		return stageDesc;
	}



	private EndpointReference getHelperEPR() {
		return helperEPR;
	}



	public WorkflowInvocationHelperClient getStageClient() {
		return stageClient;
	}





	public void setStageClient(WorkflowInvocationHelperClient stageClient) {
		this.stageClient = stageClient;
	}

}

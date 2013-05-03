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
package org.cagrid.workflow.helper.tests.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;

public class SetWorkflowHelperInstanceParametersStep extends Step {


	private WorkflowInvocationHelperClient stageClient = null;
	private InputParameter[] stageInputs = null;


	public SetWorkflowHelperInstanceParametersStep(
			WorkflowInvocationHelperClient stageClient,
			InputParameter[] stageInputs) {
		super();
		this.stageClient = stageClient;
		this.stageInputs = stageInputs;
	}




	public void runStep() throws Throwable {

		System.out.println("BEGIN SetWorkflowHelperInstanceParametersStep");
		getStageClient().setParameters(getStageInputs());
		System.out.println("END SetWorkflowHelperInstanceParametersStep");
	}




	public WorkflowInvocationHelperClient getStageClient() {
		return stageClient;
	}




	public void setStageClient(WorkflowInvocationHelperClient stageClient) {
		this.stageClient = stageClient;
	}




	public InputParameter[] getStageInputs() {
		return stageInputs;
	}




	public void setStageInputs(InputParameter[] stageInputs) {
		this.stageInputs = stageInputs;
	}




}

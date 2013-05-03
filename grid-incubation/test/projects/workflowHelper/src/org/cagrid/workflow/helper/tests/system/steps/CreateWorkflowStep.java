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

import org.apache.axis.message.addressing.EndpointReference;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;

public class CreateWorkflowStep extends Step {


	private WorkflowInvocationHelperDescriptor servicesDesc[] = null;
	private EndpointReference helperEPR = null;

	WorkflowInvocationHelperClient servicesClients[] = null;



	public CreateWorkflowStep(WorkflowInvocationHelperDescriptor[] servicesDesc,
			EndpointReference helperEPR) {
		super();
		this.servicesDesc = servicesDesc;
		this.helperEPR = helperEPR;
	}




	public void runStep() throws Throwable {

		System.out.println("BEGIN CreateWorkflowStep");

		// Add the steps for services' creation
		CreateWorkflowInvocationHelperStep services_creation[] = new CreateWorkflowInvocationHelperStep[servicesDesc.length];
		servicesClients = new WorkflowInvocationHelperClient[servicesDesc.length];
		for(int i=0; i < servicesDesc.length; i++){
			// Configure sub-step
			services_creation[i] = new CreateWorkflowInvocationHelperStep(servicesDesc[i], getHelperEPR());

			// Run sub-step and store the stage reference
			services_creation[i].runStep();
			servicesClients[i] = services_creation[i].getStageClient();
			services_creation[i].setStageClient(servicesClients[i]);
		}


		System.out.println("END CreateWorkflowStep");
	}




	public WorkflowInvocationHelperDescriptor[] getServicesDesc() {
		return servicesDesc;
	}




	public void setServicesDesc(WorkflowInvocationHelperDescriptor[] servicesDesc) {
		this.servicesDesc = servicesDesc;
	}




	public EndpointReference getHelperEPR() {
		return helperEPR;
	}




	public void setHelperEPR(EndpointReference helperEPR) {
		this.helperEPR = helperEPR;
	}




	public WorkflowInvocationHelperClient[] getServicesClients() {
		return servicesClients;
	}




	public void setServicesClients(WorkflowInvocationHelperClient[] servicesClients) {
		this.servicesClients = servicesClients;
	}


}

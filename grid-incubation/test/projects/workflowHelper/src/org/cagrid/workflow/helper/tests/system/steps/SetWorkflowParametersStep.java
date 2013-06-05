/**
*============================================================================
*  Copyright (c) 2008, The Ohio State University Research Foundation, Emory 
*  University, the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.workflow.helper.tests.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;

public class SetWorkflowParametersStep extends Step {

	private Map<WorkflowInvocationHelperClient, InputParameter[]> service2parameter = null;


	public SetWorkflowParametersStep(
			Map<WorkflowInvocationHelperClient, InputParameter[]> service2parameter) {
		super();
		this.service2parameter = service2parameter;
	}



	public void runStep() throws Throwable {

		System.out.println("BEGIN SetWorkflowParametersStep");

		Set<WorkflowInvocationHelperClient> services = service2parameter.keySet();
		Iterator<WorkflowInvocationHelperClient> iter = services.iterator();

		System.out.println("Setting each service's parameters");
		while(iter.hasNext()){

			WorkflowInvocationHelperClient cur_service = iter.next();
			InputParameter[] cur_params = service2parameter.get(cur_service);
			SetWorkflowHelperInstanceParametersStep cur_step = new SetWorkflowHelperInstanceParametersStep(cur_service, cur_params);
			cur_step.runStep();
		}
		System.out.println("END SetWorkflowParametersStep");

	}



	private Map<WorkflowInvocationHelperClient, InputParameter[]> getService2parameter() {
		return service2parameter;
	}


	private void setService2parameter(
			Map<WorkflowInvocationHelperClient, InputParameter[]> service2parameter) {
		this.service2parameter = service2parameter;
	}


}

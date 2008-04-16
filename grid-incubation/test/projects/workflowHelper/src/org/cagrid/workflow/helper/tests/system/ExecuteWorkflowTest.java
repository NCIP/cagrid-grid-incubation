package org.cagrid.workflow.helper.tests.system;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;

import java.util.Vector;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.tests.system.test.steps.CreateTestWorkflowsStep;


public class ExecuteWorkflowTest extends ServiceStoryBase {

	
	private EndpointReference helperEPR = null;
	private ServiceContainer wf_container = null;
	
	private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	private static final String HELPER_PATH_IN_CONTAINER = "/cagrid/WorkflowHelperService";
	
	
	public ExecuteWorkflowTest(ServiceContainer wf_container) {
		super();
		this.wf_container = wf_container;
		try {
			URI helper_uri = this.wf_container.getContainerBaseURI();
			helper_uri.appendPath(HELPER_PATH_IN_CONTAINER);
			this.helperEPR = new EndpointReference(helper_uri); 
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		
		return "This story is intended to create some test workflows and execute them";
	}

	@Override
	protected Vector steps() { 

		System.out.println("BEGIN ExecuteWorkflowTest");
		
		Vector steps = new Vector();
		
		// Start a container before anything else 
		StartContainerStep start_globus = new StartContainerStep(wf_container);
		try {
			start_globus.runStep();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		
		
		// Configure the workflows that are part of the system test
		CreateTestWorkflowsStep create_workflows = new CreateTestWorkflowsStep(this.getHelperEPR());
		steps.add(create_workflows);
		
		System.out.println("END ExecuteWorkflowTest");
		return steps;
	}
	
	

	private EndpointReference getHelperEPR() {
		return helperEPR;
	}

	private void setHelperEPR(EndpointReference helperEPR) {
		this.helperEPR = helperEPR;
	}

	/*@Override
	protected boolean storySetUp() throws Throwable {
		
		// Start the container so we can create and run our test workflow
		System.out.println("Starting container before executing tests");
		if( this.wf_container != null ){
			this.wf_container.startContainer();
		}
		else throw new Exception("getContainer returned null. Have the container been set?");
		return true;
	} //*/

	@Override
	protected void storyTearDown() throws Throwable {

		System.out.println("Stopping container after executing tests");
		if( this.wf_container != null ){
			this.wf_container.stopContainer();
		}
		else System.err.println("getContainer returned null. Have the container been set?");
			//throw new Exception("getContainer returned null. Have the container been set?");
	}//*/


}

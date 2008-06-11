package org.cagrid.workflow.manager.tests.system.steps;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.cagrid.workflow.manager.client.WorkflowManagerServiceClient;
import org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor;
import org.cagrid.workflow.manager.util.FileUtil;

public class RunToyWorkflowStep extends Step {

	
	private final String bpelFileName;
	private final EndpointReferenceType managerEPR;
	private final ServiceContainer serviceContainer;

	
	public RunToyWorkflowStep(String bpelFileName, EndpointReferenceType managerEPR, ServiceContainer serviceContainer) {
		this.bpelFileName = bpelFileName;
		this.managerEPR = managerEPR;
		this.serviceContainer = serviceContainer;
	}

	@Override
	public void runStep() throws Throwable {


		try{
		WorkflowManagerServiceClient client = new WorkflowManagerServiceClient(this.managerEPR);
		// place client calls here if you want to use this main as a
		// test....

		String workflowBpelFileContent = null;
		String workflowServicesURLs = null;
		try{
			URI servicesBaseURI = this.serviceContainer.getContainerBaseURI();
			String servicesBaseURL = servicesBaseURI.toString();
			
			System.out.println("container base URI: "+ servicesBaseURL);  //DEBUG
			
			workflowBpelFileContent = FileUtil.readTextFile(this.bpelFileName);
			workflowServicesURLs = 
				"http\\://first.cagrid.org/First      "+ servicesBaseURL +"cagrid/First\n" +
				"http\\://second.cagrid.org/Second    "+ servicesBaseURL +"cagrid/Second";
			        //FileUtil.readTextFile(this.serviceURLFileName);
			
			System.out.println("(propertiesFile) "+ workflowServicesURLs); //DEBUG
		}catch(IOException ioe){
			ioe.printStackTrace();
			System.exit(1);
		}
		System.out.println("File read!");

		WorkflowManagerInstanceDescriptor workflowDescriptor = new WorkflowManagerInstanceDescriptor();
		workflowDescriptor.setBpelDescription(workflowBpelFileContent);
		workflowDescriptor.setServicesURLs(workflowServicesURLs);
		System.out.println("Before create workflow");
		/* WorkflowManagerInstanceReference managerInstanceReference = */ client.createWorkflowManagerInstance(workflowDescriptor);
		
		System.out.println("Get reference");
		//WorkflowManagerInstanceClient managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceReference.getEndpointReference());
		
							
		//String[] outputs = managerInstanceClient.getOutputValues(); // TODO How will the Manager set the parameters' numeric identifier? 
		}
		catch(Throwable t){
			t.printStackTrace();
			Assert.fail(t.getMessage());
		}
		
		
		System.out.println("End client");

	}

}

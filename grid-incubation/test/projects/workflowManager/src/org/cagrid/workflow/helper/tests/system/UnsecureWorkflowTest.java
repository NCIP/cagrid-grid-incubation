package org.cagrid.workflow.helper.tests.system;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import junit.framework.Assert;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.PropertyConfigurator;
import org.cagrid.workflow.manager.tests.system.steps.RunToyWorkflowStep;

public class UnsecureWorkflowTest extends ServiceStoryBase {


	private static final String MANAGER_PATH_IN_CONTAINER = "/cagrid/WorkflowManagerService";
	private boolean enableVerboseOutput = false;  // Enable/Disable internal tasks' output to be shown at console


	public UnsecureWorkflowTest() {
		super();
		try {
			ServiceContainer cont = ServiceContainerFactory.createContainer(ServiceContainerType.GLOBUS_CONTAINER);
			setContainer(cont);
		} catch (IOException e) {

			e.printStackTrace();
			Assert.fail();
		}

		if( this.enableVerboseOutput ){
			PropertyConfigurator.configure("." + File.separator + "conf" +
					File.separator
					+ "log4j.properties"); 
		}
	}


	
	public UnsecureWorkflowTest(ServiceContainer container) {
        super(container);
        
        if( this.enableVerboseOutput ){
        	PropertyConfigurator.configure("." + File.separator + "conf" +
        			File.separator
        			+ "log4j.properties"); 
        }
    }
	
	

	public String getDescription() {

		return "Execution of a toy workflow described in a BPEL file";
	}

	@Override
	protected Vector steps() {


		Vector<Step> steps = new Vector<Step>();


		// Set workflow services' directories
		String tests_basedir = System.getProperty("resources.dir") + File.separator;
		if (tests_basedir == null) {
			System.err.println("ERROR: System property 'resources.dir' not set");
			return null;
		}
		
		File managerDir = new File(".." + File.separator + ".." + File.separatorChar + ".." + File.separator + "incubator"
				+ File.separator + "projects" + File.separator + "workflowManager"); 
		
		File services_dirs[] = new File[]{
				new File(tests_basedir + "First"),
				new File(tests_basedir + "Second"),            
				new File(".." + File.separator + ".." + File.separatorChar + ".." + File.separator + "incubator"
						+ File.separator + "projects" + File.separator + "workflowHelper"),            
				managerDir
				};

		
		// Create container
		System.out.println("Adding unpack task");
		steps.add(new UnpackContainerStep(getContainer()));

		
		// Deploy each service
		System.out.println("Adding deploy services task");
		for (int i = 0; i < services_dirs.length; i++) {
			String curr_service_dir = null;
			try {
				curr_service_dir = services_dirs[i].getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Adding deploy for service in directory '" + curr_service_dir + "'");
			steps.add(new DeployServiceStep(getContainer(), curr_service_dir));
		}
		System.out.println("END DeployWorkflowServicesTest");

		// Start container before anything else
		StartContainerStep start_step = new StartContainerStep(getContainer());
		steps.add(start_step);


		// Retrieve Manager path within the container
		EndpointReferenceType managerEPR = null;
		try {
			URI manager_uri = getContainer().getContainerBaseURI();
			manager_uri.appendPath(MANAGER_PATH_IN_CONTAINER);
			//System.out.println("[UnsecureWorkflowTest.steps] Manager URL is: "+ manager_uri.toString()); // DEBUG
	        managerEPR = new EndpointReferenceType(manager_uri);
	        
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
        



		// Configure a toy workflow to run
		String sampleFilesDir = tests_basedir + "bpelSamples"+ File.separator;
		String bpelFileName = sampleFilesDir + "first.bpel";
		String serviceURLFileName = sampleFilesDir +"first.urls.txt";


		RunToyWorkflowStep rtws = new RunToyWorkflowStep(bpelFileName, managerEPR, getContainer()); 

		steps.add(rtws);


		return steps;
	}



	@Override
	protected void storyTearDown() throws Throwable {
		
		super.storyTearDown();
		
		
		// Shutdown and delete container
		ServiceContainer myContainer = getContainer();
		
		if( myContainer.isStarted() ){
			
			myContainer.stopContainer();
		}
		
		if( myContainer.isUnpacked() ){
			
			myContainer.deleteContainer();
		}
		
		
		
	}

}

package org.cagrid.workflow.manager.tests.system;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.cagrid.workflow.manager.tests.system.steps.RunUnsecureWorkflowStepFromXML;

public class UnsecureWorkflowTest extends ServiceStoryBase {

	private Log logger = LogFactory.getLog(UnsecureWorkflowTest.class);
	
	private static final String MANAGER_PATH_IN_CONTAINER = "/cagrid/WorkflowManagerService";
	private boolean enableVerboseOutput = true;  // Enable/Disable internal tasks' output to be shown at console


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

		logger.info("Start");

		Vector<Step> steps = new Vector<Step>();
		
		// Set workflow services' directories
		String tests_basedir = System.getProperty("resources.dir");
		if (tests_basedir == null) {
			logger.error("ERROR: System property 'resources.dir' not set");
			return null;
		}
		tests_basedir = tests_basedir + File.separator;
		
		File managerDir = new File(".." + File.separator + ".." + File.separatorChar + ".." + File.separator + "incubator"
				+ File.separator + "projects" + File.separator + "workflowManager"); 
		
		File helperDir = new File(".." + File.separator + ".." + File.separatorChar + ".." + File.separator + "incubator"
				+ File.separator + "projects" + File.separator + "workflowHelper");
		
		
		String helperResourcesDir = null;
		helperResourcesDir = tests_basedir + ".."+ File.separator +".."+ File.separator +"WorkflowHelper" + File.separator + "resources" + File.separator;
		logger.info("Helper resources directory is: "+ helperResourcesDir);
		String managerResourceDir = tests_basedir + ".."+ File.separator +".."+ File.separator +"WorkflowManager" + File.separator + "resources" + File.separator;
		
		
		File services_dirs[] = new File[]{
				new File(helperResourcesDir + "CreateArrayService"),
				new File(helperResourcesDir + "ReceiveArrayService"),
				new File(helperResourcesDir + "Service1"),
				new File(helperResourcesDir + "Service2"),
				new File(helperResourcesDir + "Service3"),
				new File(helperResourcesDir + "Service4"),
				new File(helperResourcesDir + "Service5"),
				helperDir,            
				managerDir,
				
				};

		
		// Create container
		logger.info("Adding unpack task");
		steps.add(new UnpackContainerStep(getContainer()));

		
		// Deploy each service
		logger.info("Adding deploy services task");
		for (int i = 0; i < services_dirs.length; i++) {
			String curr_service_dir = null;
			try {
				curr_service_dir = services_dirs[i].getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.info("Adding deploy for service in directory '" + curr_service_dir + "'");
			steps.add(new DeployServiceStep(getContainer(), curr_service_dir));
		}
		

		// Start container before anything else
		StartContainerStep start_step = new StartContainerStep(getContainer());
		steps.add(start_step);


		// Retrieve Manager path within the container
		EndpointReferenceType managerEPR = null;
		try {
			URI manager_uri = getContainer().getContainerBaseURI();
			manager_uri.appendPath(MANAGER_PATH_IN_CONTAINER);
			logger.info("Manager URL is: "+ manager_uri.toString()); 
	        managerEPR = new EndpointReferenceType(manager_uri);
	        
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
        

		logger.info("Adding step for executing unsecure workflow test");
		Step unsecureWorkflow = null;
		try {
			File sampleXMLDirectory = new File( managerResourceDir +"workflowDescriptionSamples" );
			unsecureWorkflow = new RunUnsecureWorkflowStepFromXML(managerEPR, getContainer().getContainerBaseURI().toString(), 
					sampleXMLDirectory); 
				//new RunUnsecureWorkflowsStep(managerEPR, getContainer().getContainerBaseURI().toString());
		} catch (MalformedURIException e) {
			logger.error(e.getMessage());
		} catch(Throwable t){
			logger.error(t.getMessage());
		}
		
		steps.add(unsecureWorkflow); // */
		
		
		logger.info("END");
		return steps;
	}



	@Override
	protected void storyTearDown() throws Throwable {
		
		super.storyTearDown();
		
		logger.info("Cleaning up container before halting");
		
		
		// Shutdown and delete container
		ServiceContainer myContainer = getContainer();
		
		if( myContainer.isStarted() ){
		
			logger.info("Stopping container");
			myContainer.stopContainer();
		}
		
		if( myContainer.isUnpacked() ){
			
			logger.info("Deleting container");
			myContainer.deleteContainer();
		} 
		
		
		logger.info("END");
	}

}

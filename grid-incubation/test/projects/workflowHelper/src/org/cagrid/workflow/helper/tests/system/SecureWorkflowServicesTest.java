package org.cagrid.workflow.helper.tests.system;

import gov.nih.nci.cagrid.testing.system.deployment.ContainerException;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.TomcatSecureServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import junit.framework.Assert;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.workflow.helper.tests.system.steps.CreateTestSecureWorkflowsStep;
import org.cagrid.workflow.helper.util.CredentialHandlingUtil;
import org.globus.gsi.GlobusCredential;


/**
 * Test the system's funcionalities by creating and executing workflows
 * whose components are invoked in a secure way. A grid credential is needed
 * in order to execute the workflows.
 * 
 * 
 * **/
public class SecureWorkflowServicesTest extends ServiceStoryBase {



	private EndpointReference helperEPR = null;
	private EndpointReferenceType dorianEPR = null;
	private EndpointReferenceType cdsEPR = null;

	
	final TomcatSecureServiceContainer myContainer;

	private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	private static final String HELPER_PATH_IN_CONTAINER = "/cagrid/WorkflowHelper";



	public SecureWorkflowServicesTest(EndpointReferenceType dorianEPR, EndpointReferenceType cdsEPR) {
		super();

		
		this.dorianEPR = dorianEPR;
		this.cdsEPR = cdsEPR;
		
		// Create a secure container so the secure services can be deployed
		try {

			ServiceContainer cont = ServiceContainerFactory.createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER);
			setContainer(cont);
			
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} 

		this.myContainer = (TomcatSecureServiceContainer) getContainer();
	}


	public SecureWorkflowServicesTest(ServiceContainer container) {
		super(container);
		this.myContainer = (TomcatSecureServiceContainer) getContainer();
	}

	@Override
	public String getDescription() {

		return "System tests for execution of workflows whose components require secure invocation";
	}

	@Override
	protected Vector steps() {

		Vector steps = new Vector();


		// Set workflow services' directories
		final String tests_basedir = System.getProperty("resources.dir") + File.separator;
		if (tests_basedir == null) {
			System.err.println("ERROR: System property 'resources.dir' not set");
			return null;
		}
		


		File services_dirs[] = new File[]{ // TODO Add services that will test each possible security mechanism
				new File(tests_basedir + "Service1"),
				new File(tests_basedir + "Service2"),
				new File(tests_basedir + "Service3"),
				new File(tests_basedir + "Service4"),
				new File(tests_basedir + "Service5"),
				new File(tests_basedir + "ReceiveArrayService"),
				new File(tests_basedir + "CreateArrayService"),
				new File(tests_basedir + "ValidateOutputsService"),
				new File(".." + File.separator + ".." + File.separatorChar + ".." + File.separator + "incubator"
						+ File.separator + "projects" + File.separator + "workflowHelper")};


		
		
		
		/* (1) Obtain host credentials (from file system) and user credentials (from Dorian) */
		System.out.println("Retrieving credentials to run the workflows");

		// Get host credential
		/*GlobusCredential hostCredential = null;
		try {
			hostCredential = new GlobusCredential(new FileInputStream(new File(this.hostCredentialFilename)));
		} catch (GlobusCredentialException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} // */

		
		
		// Log user on the Grid
		/*String userID = "caOSTests";
		String userPassword = "wa2@crUnx8";
		ProxyLifetime lifetime = new ProxyLifetime(12, 0, 0);
		int delegationPathLength = 5;
		GlobusCredential userCredential = CredentialHandlingUtil.LogUserOnGrid(dorianURL, userID, userPassword, 
				delegationPathLength, lifetime);  // */


		// Get credential for the WorkflowHelper
		GlobusCredential helperCredential = CredentialHandlingUtil.LogUserOnGrid(this.dorianEPR.getAddress().toString(), 
				"caOSHelper", "cHabr4w$Ph", 2, new ProxyLifetime(12, 0, 0));;
		
		


		/* (2) Unpack a secure container */
		System.out.println("Unpacking secure container");
		steps.add(new UnpackContainerStep(getContainer()));


		/* (3) Deploy Credential Delegation Service */
		final String cds_directory = tests_basedir + File.pathSeparator + "CredentialDelegationService";
		// Should we check MySQL availability?
		steps.add(new DeployServiceStep(getContainer(), cds_directory));



		/* (4) Deploy secure services (workflow), WorkflowHelper */
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


		/* (5) Start the container */
		StartContainerStep start_step = new StartContainerStep(getContainer());
		steps.add(start_step);


		
		/* Retrieve user credential */
		GlobusCredential userCredential = null;
		try {
			File cert_dir = this.myContainer.getCertificatesDirectory();
			File credential_file = new File(cert_dir.getAbsolutePath() + File.separator + "user.proxy");
			userCredential = new GlobusCredential(new FileInputStream(credential_file));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		

		/* (6) Configure, instantiate and execute each workflow */
		try {
			URI helper_uri = getContainer().getContainerBaseURI();
			String container_base_url = "https://"+ helper_uri.getHost() + ':' + helper_uri.getPort(); 
			helper_uri.appendPath(HELPER_PATH_IN_CONTAINER);
			this.helperEPR = new EndpointReference(helper_uri);
			CreateTestSecureWorkflowsStep create_workflows = new CreateTestSecureWorkflowsStep(this.helperEPR, 
					container_base_url, userCredential, helperCredential, this.cdsEPR, this.dorianEPR);
			steps.add(create_workflows);
		} catch (MalformedURIException e) {
			e.printStackTrace();
			Assert.fail();
		}


		return steps;
	}


	@Override
	protected boolean storySetUp() throws Throwable {

		return super.storySetUp();
	}


	@Override
	protected void storyTearDown() throws Throwable {

		if (getContainer() != null) {
			try {

				System.out.println("["+ this.getClass().getName() +"] Stopping container after executing tests");
				getContainer().stopContainer();
				getContainer().deleteContainer();
			} catch (ContainerException e) {
				e.printStackTrace();
				Assert.fail();
			} 
		} else
			System.err.println("["+ this.getClass().getName() +"] getContainer returned null. Have the container been set?");
		// */
	}

}


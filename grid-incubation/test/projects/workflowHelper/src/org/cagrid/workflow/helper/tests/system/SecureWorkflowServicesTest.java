package org.cagrid.workflow.helper.tests.system;

import gov.nih.nci.cagrid.gridca.common.CA;
import gov.nih.nci.cagrid.gridca.common.CertUtil;
import gov.nih.nci.cagrid.gridca.common.Credential;
import gov.nih.nci.cagrid.gridca.common.KeyUtil;
import gov.nih.nci.cagrid.gridca.common.SecurityConstants;
import gov.nih.nci.cagrid.gridca.common.SecurityUtil;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerException;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.TomcatSecureServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Vector;

import junit.framework.Assert;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.PropertyConfigurator;
import org.cagrid.workflow.helper.tests.system.steps.ConfigureContainerSecurityStep;
import org.cagrid.workflow.helper.tests.system.steps.CreateTestSecureWorkflowsStep;
import org.globus.gsi.GlobusCredential;


/**
 * Test the system's funcionalities by creating and executing workflows
 * whose components are invoked in a secure way. A grid credential is needed
 * in order to execute the workflows.
 * 
 * 
 * **/
public class SecureWorkflowServicesTest extends ServiceStoryBase {



	private EndpointReference helperEPR;
	private EndpointReferenceType dorianEPR;
	private EndpointReferenceType cdsEPR;


	private TomcatSecureServiceContainer myContainer;
	private String containerBase;

	private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	private static final String HELPER_PATH_IN_CONTAINER = "/cagrid/WorkflowHelper";


	private final boolean enableVerboseOutput = true;
	private String cdsURL;

	public SecureWorkflowServicesTest() {
		super();


		// Create a secure container so the secure services can be deployed
		try {

			//System.out.println("Creating Secure Tomcat Container"); //DEBUG

			ServiceContainer cont = ServiceContainerFactory.createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER);
			setContainer(cont);

		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} 

		this.myContainer = (TomcatSecureServiceContainer) getContainer();
		try {
			String containerIP = this.myContainer.getContainerBaseURI().getHost();
			this.containerBase = "https://" + containerIP + ':' + this.myContainer.getContainerBaseURI().getPort() 
			+ this.myContainer.getContainerBaseURI().getPath();

			this.dorianEPR = new EndpointReferenceType(new Address("https://dorian.training.cagrid.org:8443/wsrf/services/cagrid/Dorian"));
			

			this.cdsURL = this.containerBase + "cagrid/CredentialDelegationService";
			Address cdsAddress = new Address(this.cdsURL);

			//System.out.println("cdsAddress is: "+ cdsAddress); //DEBUG

			this.cdsEPR = new EndpointReferenceType(cdsAddress);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} 


		if( this.enableVerboseOutput ){
			PropertyConfigurator.configure("." + File.separator + "conf" +
					File.separator
					+ "log4j.properties"); 
		}


	}


	public SecureWorkflowServicesTest(ServiceContainer container) {
		super(container);


		this.myContainer = (TomcatSecureServiceContainer) getContainer();
		try {
			this.containerBase = this.myContainer.getContainerBaseURI().getPath();

			this.dorianEPR = new EndpointReferenceType(new Address("https://dorian.training.cagrid.org:8443/wsrf/services/cagrid/Dorian"));
			this.cdsEPR = new EndpointReferenceType(new Address(this.containerBase + "/wsrf/services/cagrid/CredentialDelegationService"));
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		if( this.enableVerboseOutput ){
			PropertyConfigurator.configure("." + File.separator + "conf" +
					File.separator
					+ "log4j.properties"); 
		}

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


		// Directories in which the test services are found
		File services_dirs[] = new File[]{ // TODO Add services that will test each possible security mechanism
				new File(tests_basedir + "Service1"),
				new File(tests_basedir + "Service2"),
				new File(tests_basedir + "Service3"),
				new File(tests_basedir + "Service4"),
				new File(tests_basedir + "Service5"), // */
				new File(tests_basedir + "ReceiveArrayService"),
				new File(tests_basedir + "CreateArrayService"),
			    new File(tests_basedir + "ValidateOutputsService"),
				new File(tests_basedir + "CredentialDelegationService"),
				new File(".." + File.separator + ".." + File.separatorChar + ".." + File.separator + "incubator"
						+ File.separator + "projects" + File.separator + "workflowHelper")};



		/* (1) Unpack a secure container */
		Step unpack = new UnpackContainerStep(getContainer()); 
		try {
			unpack.runStep(); // We need this container unpacked for the next step 
		} catch (Throwable e2) {
			e2.printStackTrace();
		}
		
		
		
		// Instantiate a CA identified by a credential located inside the container and trusted by it  
		CA ca = null;
		GlobusCredential userCredential = null;
		File caCertFile = null;
		File caKeyFile = null;
		try {
			
			// Locate pair <key, certificate> in the unpacked container
			String containerBasedir = getContainer().getProperties().getContainerDirectory().getCanonicalPath();
			String caCertsDir = containerBasedir + File.separator + "certificates" + File.separator + "ca"; 
			caCertFile = new File(caCertsDir + File.separator + "testing_ca_cert.pem");
			caKeyFile = new File(caCertsDir + File.separator + "testing_ca_key.pem");
			
			
			// Load the pair in order to instantiate the CA 
			PrivateKey caKey = null;
			X509Certificate caCert = null;
			caKey = KeyUtil.loadPrivateKey(caKeyFile, SecurityConstants.HOSTKEY_PASSWORD);
			caCert = CertUtil.loadCertificate(caCertFile);
			ca = new CA(caCert, caKey, null);
			
			
			// Obtain a credential to be used as user's credential
			Credential userCertificate = ca.createIdentityCertificate("testUser");
			userCredential = new GlobusCredential(userCertificate.getPrivateKey(), new X509Certificate[]{ userCertificate.getCertificate()});
			
		} catch (Throwable e1) {
			e1.printStackTrace();
			Assert.fail(e1.getMessage());
		}
		

		
		/* (2) Configure container security  */
		System.out.println("Configuring container");
		File containerBasedir = getContainer().getProperties().getContainerDirectory();
		steps.add(new ConfigureContainerSecurityStep(containerBasedir, caCertFile, caKeyFile));
		
		
		/* (2) Deploy secure services (workflow), WorkflowHelper */
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



		/* (3) Start the container */
		StartContainerStep start_step = new StartContainerStep(getContainer());
		steps.add(start_step);



		/* (4) Instantiate test workflows */
		try {
			URI helper_uri = getContainer().getContainerBaseURI();
			String container_base_url = "https://"+ helper_uri.getHost() + ':' + helper_uri.getPort(); 
			helper_uri.appendPath(HELPER_PATH_IN_CONTAINER);
			this.helperEPR = new EndpointReference(helper_uri);
			CreateTestSecureWorkflowsStep create_workflows = new CreateTestSecureWorkflowsStep(this.helperEPR, this.cdsEPR, 
					container_base_url, userCredential, this.cdsURL);
			steps.add(create_workflows);
		} catch (MalformedURIException e) {
			e.printStackTrace();
			Assert.fail();
		}


		return steps;
	}


	@Override
	protected boolean storySetUp() throws Throwable {
		try{
			SecurityUtil.init();
		}
		catch(Throwable t){
			t.printStackTrace();
		}
		
		return super.storySetUp();
	}


	@Override
	protected void storyTearDown() throws Throwable {

		if (getContainer() != null) {
			try {

				System.out.println("["+ this.getClass().getName() +"] Stopping container after executing tests");
				if( getContainer().isStarted() ){

					getContainer().stopContainer();
				}

				if( getContainer().isUnpacked() ){

					getContainer().deleteContainer();
				} // */
			} catch (ContainerException e) {
				e.printStackTrace();
				Assert.fail();
			} 
		} else
			System.err.println("["+ this.getClass().getName() +"] getContainer returned null. Have the container been set?");

	}



	/**
	 * Convenience method for running all the Steps in this Story.
	 */
	public static void main(String args[]) {

		try{

			TestRunner runner = new TestRunner();
			TestResult result = runner.doRun(new TestSuite(SecureWorkflowServicesTest.class));
			System.exit(result.errorCount() + result.failureCount());
		}
		catch(Throwable t){
			t.printStackTrace();
			Assert.fail(t.getMessage());
		}  
	}


	public EndpointReference getHelperEPR() {
		return helperEPR;
	}


	public void setHelperEPR(EndpointReference helperEPR) {
		this.helperEPR = helperEPR;
	}


	public EndpointReferenceType getDorianEPR() {
		return dorianEPR;
	}


	public void setDorianEPR(EndpointReferenceType dorianEPR) {
		this.dorianEPR = dorianEPR;
	}


	public EndpointReferenceType getCdsEPR() {
		return cdsEPR;
	}


	public void setCdsEPR(EndpointReferenceType cdsEPR) {
		this.cdsEPR = cdsEPR;
	}


	public TomcatSecureServiceContainer getMyContainer() {
		return myContainer;
	}



}


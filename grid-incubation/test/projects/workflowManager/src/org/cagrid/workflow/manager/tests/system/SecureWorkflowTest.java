package org.cagrid.workflow.manager.tests.system;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.cagrid.workflow.manager.tests.system.steps.ConfigureContainerSecurityStep;
import org.cagrid.workflow.manager.tests.system.steps.RunSecureWorkflowsStep;
import org.globus.gsi.GlobusCredential;


public class SecureWorkflowTest  extends ServiceStoryBase {


	/* (non-Javadoc)
	 * @see junit.framework.TestCase#getName()
	 */
	@Override
	public String getName() {
		
		return "testManagerSecureWorkflows";
	}


	private static final String LOG4J_CONF_FILE = "." + File.separator + "conf" +
						File.separator
						+ "log4j.properties";

	private static Log logger = LogFactory.getLog(SecureWorkflowTest.class);

	private EndpointReference managerEPR;
	private EndpointReferenceType dorianEPR;
	private EndpointReferenceType cdsEPR;


	private TomcatSecureServiceContainer myContainer;
	private String containerBase;

	private static final String MANAGER_PATH_IN_CONTAINER = "cagrid/WorkflowManagerService";


	private final boolean enableVerboseOutput = false;
	private String cdsURL;

	public SecureWorkflowTest() {
		super();


		// Create a secure container so the secure services can be deployed
		try {

			logger.info("Creating Secure Tomcat Container"); 

			ServiceContainer cont = ServiceContainerFactory.createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER);
			setContainer(cont);

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			Assert.fail();
		} 

		this.myContainer = (TomcatSecureServiceContainer) getContainer();
		try {
			final URI containerBaseURI = this.myContainer.getContainerBaseURI();
			String containerIP = containerBaseURI.getHost();
			this.containerBase = containerBaseURI.getScheme() + "://" + containerIP + ':' + containerBaseURI.getPort() 
			+ containerBaseURI.getPath();

			this.dorianEPR = new EndpointReferenceType(new Address("https://dorian.training.cagrid.org:8443/wsrf/services/cagrid/Dorian"));


			this.cdsURL = this.containerBase + "cagrid/CredentialDelegationService";
			Address cdsAddress = new Address(this.cdsURL);

			logger.info("cdsAddress is: "+ cdsAddress); 

			this.cdsEPR = new EndpointReferenceType(cdsAddress);
		} catch (MalformedURIException e) {
			logger.error(e.getMessage(), e);
		} 


		if( this.enableVerboseOutput ){
			PropertyConfigurator.configure(LOG4J_CONF_FILE); 
		}
	}


	public SecureWorkflowTest(ServiceContainer container) {
		super(container);


		this.myContainer = (TomcatSecureServiceContainer) getContainer();
		try {
			this.containerBase = this.myContainer.getContainerBaseURI().getPath();

			this.dorianEPR = new EndpointReferenceType(new Address("https://dorian.training.cagrid.org:8443/wsrf/services/cagrid/Dorian"));
			this.cdsEPR = new EndpointReferenceType(new Address(this.containerBase + "/wsrf/services/cagrid/CredentialDelegationService"));
		} catch (MalformedURIException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		}

		if( this.enableVerboseOutput ){
			PropertyConfigurator.configure(LOG4J_CONF_FILE); 
		}

	}

	@Override
	public String getDescription() {

		return "System tests for execution of workflows whose components require secure invocation";
	}

	@Override
	protected Vector steps() {

		logger.info("Creating steps for secure workflows test");
		Vector<Step> steps = new Vector<Step>();


		// Set workflow services' directories
		String tests_basedir = System.getProperty("resources.dir");
		if (( tests_basedir == null) || (tests_basedir.trim().equals(""))) {
			System.err.println("ERROR: System property 'resources.dir' not set");
			return null;
		}
		else {
			tests_basedir = tests_basedir + File.separator;
		}
		
		tests_basedir = tests_basedir + File.separator;

		File managerDir = new File(".." + File.separator + ".." + File.separatorChar + ".." + File.separator + "incubator"
				+ File.separator + "projects" + File.separator + "workflowManager"); 

		File helperDir = new File(".." + File.separator + ".." + File.separatorChar + ".." + File.separator + "incubator"
				+ File.separator + "projects" + File.separator + "workflowHelper");


		String helperResourcesDir = tests_basedir + ".." + File.separator + ".." + File.separator + "workflowHelper" + File.separator + "resources" + File.separator;
		String managerResourceDir = tests_basedir + ".." + File.separator + ".." + File.separator + "workflowManager" + File.separator + "resources" + File.separator;
		try {
			logger.info("Helper resources directory is: "+ new File(helperResourcesDir).getCanonicalPath() );
		} catch (IOException e3) {
			logger.error(e3.getMessage(), e3);
		}

		// Directories in which the test services are found
		File services_dirs[] = new File[]{ // TODO Add services that will test each possible security mechanism
				new File(helperResourcesDir + "Service1"),
				new File(helperResourcesDir + "Service2"),
				new File(helperResourcesDir + "Service3"),
				new File(helperResourcesDir + "Service4"),
				new File(helperResourcesDir + "Service5"), 
				new File(helperResourcesDir + "ReceiveArrayService"),
				new File(helperResourcesDir + "CreateArrayService"),
				new File(helperResourcesDir + "AssertService"),
				new File(helperResourcesDir + "CredentialDelegationService"),
				helperDir,
				managerDir
		};



		/* (1) Unpack a secure container */
		logger.info("Unpacking container");
		Step unpack = new UnpackContainerStep(getContainer()); 
		try {
			unpack.runStep(); // We need this container unpacked for the next step 
		} catch (Throwable e2) {
			logger.error(e2.getMessage() ,e2);
		}



		// Instantiate a CA identified by a credential located inside the container and trusted by it  
		logger.info("Creating Globus credentials");
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
			logger.error(e1.getMessage() ,e1);
			Assert.fail(e1.getMessage());
		}



		/* (2) Configure container security  */
		logger.info("Configuring container");
		File containerBasedir = getContainer().getProperties().getContainerDirectory();
		steps.add(new ConfigureContainerSecurityStep(containerBasedir, caCertFile, caKeyFile));


		/* (2) Deploy secure services (workflow), WorkflowHelper */
		logger.info("Adding deploy services task");
		for (int i = 0; i < services_dirs.length; i++) {
			String curr_service_dir = null;
			try {
				curr_service_dir = services_dirs[i].getCanonicalPath();
			} catch (IOException e) {
				logger.error(e.getMessage() ,e);
			}
			logger.info("Adding deploy for service in directory '" + curr_service_dir + "'");
			steps.add(new DeployServiceStep(getContainer(), curr_service_dir));
		}



		/* (3) Start the container */
		StartContainerStep start_step = new StartContainerStep(getContainer());
		steps.add(start_step);



		/* (4) Instantiate test workflows */
		try {
			URI manager_uri = getContainer().getContainerBaseURI();
			String container_base_url = "https://"+ manager_uri.getHost() + ':' + manager_uri.getPort() 
			+ "/wsrf/services/"; 
			manager_uri.appendPath(MANAGER_PATH_IN_CONTAINER);
			this.managerEPR = new EndpointReference(manager_uri);
			File sampleXMLDirectory = new File( managerResourceDir +"workflowDescriptionSamples" );
			Step create_workflows = 
//				new RunSecureWorkflowsFromXMLStep(this.managerEPR, this.cdsEPR, container_base_url, userCredential, this.cdsURL, sampleXMLDirectory);
				new RunSecureWorkflowsStep(this.managerEPR, this.cdsEPR, container_base_url, userCredential, this.cdsURL);
			steps.add(create_workflows);
		} catch (MalformedURIException e) {
			logger.error(e.getMessage() ,e);
			Assert.fail();
		}


		return steps;
//			   new Vector(); //DEBUG
			
	}


	@Override
	protected boolean storySetUp() throws Throwable {
		try{
			SecurityUtil.init();
		}
		catch(Throwable t){
			logger.error(t.getMessage() ,t);
		}

		return super.storySetUp();
	}


	@Override
	protected void storyTearDown() throws Throwable {

		if (getContainer() != null) {
			try {

				logger.info("["+ this.getClass().getName() +"] Stopping container after executing tests");
				if( getContainer().isStarted() ){

					getContainer().stopContainer();
				}

				if( getContainer().isUnpacked() ){

					getContainer().deleteContainer();
				} // */
			} catch (ContainerException e) {
				logger.error(e.getMessage() ,e);
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
			TestResult result = runner.doRun(new TestSuite(SecureWorkflowTest.class));
			System.exit(result.errorCount() + result.failureCount());
		}
		catch(Throwable t){
			logger.error(t.getMessage() ,t);
			Assert.fail(t.getMessage());
		}  
	}


	public EndpointReference getHelperEPR() {
		return managerEPR;
	}


	public void setHelperEPR(EndpointReference helperEPR) {
		this.managerEPR = helperEPR;
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

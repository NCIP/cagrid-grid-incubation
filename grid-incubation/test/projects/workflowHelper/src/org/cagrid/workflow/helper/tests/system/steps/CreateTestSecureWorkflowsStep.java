package org.cagrid.workflow.helper.tests.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.workflow.helper.client.WorkflowHelperClient;
import org.cagrid.workflow.helper.descriptor.CDSAuthenticationMethod;
import org.cagrid.workflow.helper.descriptor.ChannelProtection;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TLSInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.helper.util.CredentialHandlingUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.NotifyCallback;

public class CreateTestSecureWorkflowsStep extends Step implements NotifyCallback {

	private EndpointReference helper_epr;
	private String containerBaseURL;


	// Synchronizes the access to variable 'isFinished' 
	private Lock isFinishedKey = new ReentrantLock();
	private Condition isFinishedCondition = isFinishedKey.newCondition();
	private Map<String, Boolean> stageIsFinished = new HashMap<String, Boolean>() ;

	private boolean isFinished = false;
	private EndpointReferenceType cdsEPR = null;

	private EndpointReferenceType dorianEPR;
	private GlobusCredential userCredential;
	private GlobusCredential helperCredential;


	final static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	final static String SOAPENCODING_NAMESPACE = "http://schemas.xmlsoap.org/soap/encoding/";
	private static final String HOST_CREDENTIAL_FILE_PROPERTY = "host.credential";
		//"C:\\Documents and Settings\\hawks\\Desktop\\caOSSecurityTests\\credential.bin";




	public CreateTestSecureWorkflowsStep(EndpointReference helperEPR, String container_base_url, GlobusCredential userCredential, 
			GlobusCredential helperCredential, EndpointReferenceType cdsEPR, EndpointReferenceType dorianEPR) {
		this.helper_epr = helperEPR;
		this.containerBaseURL  = container_base_url;
		this.cdsEPR = cdsEPR;		
		this.dorianEPR = dorianEPR;
		
		this.userCredential = userCredential;
		this.helperCredential = helperCredential;
	}

	@Override
	public void runStep() throws Throwable {
		
		System.out.println("---- BEGIN SECURE WORKFLOW TEST ----");
		

		final WorkflowHelperClient wf_helper = new WorkflowHelperClient(helper_epr); 
		final EndpointReferenceType manager_epr = null;		
		

		String cds_URL = "https://localhost:8443/wsrf/services/cagrid/CredentialDelegationService";
		String hostCredentialFile = System.getProperty(HOST_CREDENTIAL_FILE_PROPERTY);
		if( hostCredentialFile == null ) throw new Exception("System property undefined: "+ HOST_CREDENTIAL_FILE_PROPERTY);
		String hostCredentialKey = hostCredentialFile + "-key.pem";
		String hostCredentialCert = hostCredentialFile + "-cert.pem";
		
		
		
		// Manager role: Delegate user credential to the Manager 
		wf_helper.setProxy(this.helperCredential);			
		
		
		
		System.out.println("Delegating user credential to the Helper"); //DEBUG
		EndpointReferenceType proxyEPR = CredentialHandlingUtil.delegateCredential(this.userCredential, wf_helper.getIdentity(), 
				cds_URL, new ProxyLifetime(5,0,0), new ProxyLifetime(6,0,0), 3, 2);


		// Get delegated credential from the user
		System.out.println("FakeManager retrieving delegated user credential"); //DEBUG
		GlobusCredential myCredential = CredentialHandlingUtil.getDelegatedCredential(new EndpointReference(proxyEPR));
		wf_helper.setProxy(myCredential);
 


		ProxyLifetime delegationLifetime = new ProxyLifetime(4,0,0);
		ProxyLifetime issuedCredentialLifetime = new ProxyLifetime(5,0,0);
		int delegationPath = 1;
		int issuedCredentialPath = 0; 
		
		
		
		try{
			/*** Service that will gather all the output and match against the expected ones ***/
			EndpointReferenceType outputMatcherEPR = runOuputMatcher(manager_epr, wf_helper, myCredential, 
					delegationLifetime, issuedCredentialLifetime, delegationPath, issuedCredentialPath);

			/** FAN IN AND FAN OUT TEST **/
			System.out.println("BEGIN Testing fan in and fan out"); 
			runFaninFanOutTest(manager_epr, wf_helper, outputMatcherEPR, delegationLifetime, myCredential, issuedCredentialLifetime,
					delegationPath, issuedCredentialPath);
			System.out.println("END Testing fan in and fan out"); // */

			

			/*** Testing arrays as services' input ***/

			/** simple type arrays **/
			System.out.println("Simple arrays as input");
			runSimpleArrayTest(manager_epr, wf_helper, outputMatcherEPR, issuedCredentialLifetime, myCredential, delegationLifetime,
					issuedCredentialPath, delegationPath);
			System.out.println("OK");

			System.out.println("Complex arrays as input");
			runComplexArrayTest(manager_epr, wf_helper, outputMatcherEPR, issuedCredentialLifetime, delegationLifetime, myCredential,
					issuedCredentialPath, delegationPath);
			System.out.println("OK");

			System.out.println("END Testing arrays"); // */

			
		

			/** BEGIN streaming test **/
			System.out.println("BEGIN Testing streaming");

			// Streaming simple types 
			System.out.println("Streaming of simple-type arrays");
			runSimpleArrayStreaming(manager_epr, wf_helper, myCredential, issuedCredentialLifetime, delegationLifetime, issuedCredentialPath,
					delegationPath);
			System.out.println("OK");  // */



			/* Streaming complex types */
			System.out.print("Streaming of complex-type arrays");
			runComplexArrayStreaming(manager_epr, wf_helper, issuedCredentialLifetime, myCredential, delegationLifetime, 
					issuedCredentialPath, delegationPath);
			System.out.println("OK");

			System.out.println("END Testing streaming"); // */
	
			
			// Block until every stage reports either a FINISHED or an ERROR status
			this.waitUntilCompletion();
		} catch(Throwable t) {
			t.printStackTrace();
			Assert.fail();
		}

		
		System.out.println("---- END SECURE WORKFLOW TEST ----");
		
		return;
	}

	
	
	/**
	 * Instantiate the service that can match all the workflows' outputs against the expected ones 
	 * 
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_helper Client of a running WorkflowHelper service
	 * @param myCredential Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime 
	 * @param delegationPath
	 * @param issuedCredentialPath
	 * 
	 * 
	 * @return An EPR that can be used to contact the OutputMatcher
	 * 
	 * */
	private EndpointReferenceType runOuputMatcher(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper, GlobusCredential myCredential, ProxyLifetime delegationLifetime, ProxyLifetime issuedCredentialLifetime, int delegationPath, int issuedCredentialPath) 
			throws RemoteException {


		WorkflowInstanceHelperDescriptor validatorInstanceDesc = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		validatorInstanceDesc.setWorkflowID("Validator");
		validatorInstanceDesc.setWorkflowManagerEPR(manager_epr);

		String outputMatcherURI =  "http://validateoutputsservice.test.workflow.cagrid.org/ValidateOutputsService";

		WorkflowInstanceHelperClient validatorInstance = null;
		try {
			validatorInstance = wf_helper.createWorkflowInstanceHelper(validatorInstanceDesc);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		WorkflowInvocationHelperDescriptor validatorInvocationDesc = new WorkflowInvocationHelperDescriptor();
		validatorInvocationDesc.setOperationQName(
				new QName(outputMatcherURI, "SecureValidateTestOutputRequest"));
		validatorInvocationDesc.setServiceURL(containerBaseURL + "/wsrf/services/cagrid/ValidateOutputsService");
		
		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(cdsEPR);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);
		validatorInvocationDesc.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		
		

		WorkflowInvocationHelperClient validatorInvocation1 = null;
		try {
			validatorInvocation1 = validatorInstance.createWorkflowInvocationHelper(validatorInvocationDesc);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


		// Subscribe for status notifications
		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), validatorInvocation1);


		// Configure inputs
		OperationInputMessageDescriptor validatorInputDesc = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam = new InputParameterDescriptor[8];
		inputParam[0] = new InputParameterDescriptor(new QName("test1Param1"), new QName(XSD_NAMESPACE, "int"));
		inputParam[1] = new InputParameterDescriptor(new QName("test1Param2"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType[]"));
		inputParam[2] = new InputParameterDescriptor(new QName("test1Param3"), new QName(XSD_NAMESPACE, "boolean"));
		inputParam[3] = new InputParameterDescriptor(new QName("test2Param1"), new QName(XSD_NAMESPACE, "int"));
		inputParam[4] = new InputParameterDescriptor(new QName("test2Param2"), new QName(XSD_NAMESPACE, "string[]"));
		inputParam[5] = new InputParameterDescriptor(new QName("test2Param3"), new QName(XSD_NAMESPACE, "boolean"));
		inputParam[6] = new InputParameterDescriptor(new QName("test3Param1"), new QName(XSD_NAMESPACE, "string"));
		inputParam[7] = new InputParameterDescriptor(new QName("test3Param2"), new QName(XSD_NAMESPACE, "string")); // */
		
		
		validatorInputDesc.setInputParam(inputParam);
		validatorInvocation1.configureInput(validatorInputDesc);


		// Configure outputs: it has none
		OperationOutputTransportDescriptor validatorOutput = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor[] paramDescriptor = new OperationOutputParameterTransportDescriptor[0];
		validatorOutput.setParamDescriptor(paramDescriptor );
		validatorInvocation1.configureOutput(validatorOutput);

		
		
		// Set the GlobusCredential to use on InstanceHelper
		//System.out.println("Delegating helper's credential to the InstanceHelper"); //DEBUG
		EndpointReferenceType delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsEPR.toString(), delegationLifetime, issuedCredentialLifetime, 
				delegationPath, issuedCredentialPath);

		validatorInstance.addCredential(validatorInvocation1.getEndpointReference(), delegationEPR);

		//DEBUG
		//System.out.println("Credential added");
		System.out.flush();
		
		

		// Set static parameters
		validatorInvocation1.setParameter(new InputParameter("999", 0));
		validatorInvocation1.setParameter(new InputParameter("true", 2));
		validatorInvocation1.setParameter(new InputParameter("999", 3));
		validatorInvocation1.setParameter(new InputParameter("true", 5)); // */
		


		return validatorInvocation1.getEndpointReference();
	}


	
	
	/**
	 * Instantiate the workflow that will test complex array streaming
	 * 
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_helper Client of a running WorkflowHelper service
	 * @param myCredential Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime 
	 * @param delegationPath
	 * @param issuedCredentialPath
	 * 
	 * */
	private void runComplexArrayStreaming(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper, ProxyLifetime issuedCredentialLifetime, GlobusCredential myCredential, ProxyLifetime delegationLifetime, int issuedCredentialPath, int delegationPath)throws RemoteException {


		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor5 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		workflowDescriptor5.setWorkflowID("WorkFlow5");
		workflowDescriptor5.setWorkflowManagerEPR(manager_epr);


		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance5 = null;
		try {
			wf_instance5 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor5);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		// BEGIN service 4				


		// Creating client of service 4
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation4 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		java.lang.String acess_url = containerBaseURL+"/wsrf/services/cagrid/Service4";
		operation4.setWorkflowID("GeorgeliusWorkFlow");
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation4.setServiceURL(acess_url);
		
		
		
		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(cdsEPR);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);
		operation4.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		
		
		
		WorkflowInvocationHelperClient serviceClient__4 = null;
		try {
			serviceClient__4 = wf_instance5.createWorkflowInvocationHelper(operation4);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient__4);

		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams4 = new InputParameterDescriptor[2];
		inputParams4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"));
		inputParams4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"));
		inputMessage4.setInputParam(inputParams4);
		serviceClient__4.configureInput(inputMessage4);
		// End InputMessage Descriptor

		// Setting output descriptor
		OperationOutputTransportDescriptor outputDescriptor4 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor4 [] = new OperationOutputParameterTransportDescriptor[0];


		// takes the reference to no service
		outputDescriptor4.setParamDescriptor(outParameterDescriptor4);
		serviceClient__4.configureOutput(outputDescriptor4);

		// Set the GlobusCredential to use on InstanceHelper
		System.out.println("Delegating helper's credential to the InstanceHelper"); //DEBUG
		EndpointReferenceType delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsEPR.toString(), delegationLifetime, issuedCredentialLifetime, 
				delegationPath, issuedCredentialPath);

		wf_instance5.addCredential(serviceClient__4.getEndpointReference(), delegationEPR);

		//DEBUG
		System.out.println("Credential added");
		System.out.flush();


		// Setting second parameter
		serviceClient__4.setParameter(new InputParameter("complex type's streaming", 1));
		// END service 4

		
		
		
		// BEGIN CreateArrayService::getComplexArray				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation__ca = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		String access_url = containerBaseURL+"/wsrf/services/cagrid/CreateArrayService";
		operation__ca.setWorkflowID("GeorgeliusWorkFlow");
		operation__ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetComplexArrayRequest"));
		operation__ca.setServiceURL(access_url);
		operation__ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType[]"));

		
		operation__ca.setWorkflowInvocationSecurityDescriptor(secDescriptor);		
		

		// create ReceiveArrayService				
		WorkflowInvocationHelperClient serviceClient__ca = null;
		try {
			serviceClient__ca = wf_instance5.createWorkflowInvocationHelper(operation__ca);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient__ca);

		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage__ca = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams__ca = new InputParameterDescriptor[0];
		inputMessage__ca.setInputParam(inputParams__ca);
		serviceClient__ca.configureInput(inputMessage__ca);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the only service that will receive the output (ReceiveArrayService)
		OperationOutputTransportDescriptor outputDescriptor__ca = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor__ca [] = new OperationOutputParameterTransportDescriptor[1];

		// First destination: ReceiveArrayService::ReceiveComplexArray
		outParameterDescriptor__ca[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor__ca[0].setParamIndex(0);
		outParameterDescriptor__ca[0].setType(new QName(XSD_NAMESPACE ,"string"));
		outParameterDescriptor__ca[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "abc")});
		outParameterDescriptor__ca[0].setLocationQuery("/ns0:GetComplexArrayResponse/abc:ComplexType/abc:message");
		outParameterDescriptor__ca[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient__4.getEndpointReference()});


		wf_instance5.addCredential(serviceClient__ca.getEndpointReference(), delegationEPR);

		// takes the reference to ReceiveComplexArrayService
		outputDescriptor__ca.setParamDescriptor(outParameterDescriptor__ca);
		serviceClient__ca.configureOutput(outputDescriptor__ca);

		// END CreateArrayService::getComplexArray 
	}


	
	/**
	 * Instantiate the workflow that will test simple array streaming
	 * 
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_helper Client of a running WorkflowHelper service
	 * @param myCredential Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime 
	 * @param delegationPath
	 * @param issuedCredentialPath
	 * 
	 * */
	private void runSimpleArrayStreaming(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper, GlobusCredential myCredential, ProxyLifetime issuedCredentialLifetime, ProxyLifetime delegationLifetime, int issuedCredentialPath, int delegationPath) throws RemoteException {

		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor5 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		workflowDescriptor5.setWorkflowID("WorkFlow5");
		workflowDescriptor5.setWorkflowManagerEPR(manager_epr);

		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance5 = null;
		try {
			wf_instance5 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor5);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


		// BEGIN service 4
		WorkflowInvocationHelperDescriptor operation_4 = new WorkflowInvocationHelperDescriptor();
		operation_4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation_4.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/Service4");
		// operation_4.setOutputType(); // Void output expected

		
		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(cdsEPR);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);
		operation_4.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		


		// Creating client of service 4
		WorkflowInvocationHelperClient serviceClient_4 = null;
		try {
			serviceClient_4 = wf_instance5.createWorkflowInvocationHelper(operation_4);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient_4);



		// Creating Descriptor of the InputMessage
		OperationInputMessageDescriptor inputMessage_4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam_4 = new InputParameterDescriptor[2];
		inputParam_4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"));
		inputParam_4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"));
		inputMessage_4.setInputParam(inputParam_4);
		serviceClient_4.configureInput(inputMessage_4);
		// End InputMessage Descriptor

		OperationOutputTransportDescriptor outputDescriptor_4 = new OperationOutputTransportDescriptor(); 
		OperationOutputParameterTransportDescriptor[] outParameterDescriptor_4 = new OperationOutputParameterTransportDescriptor[0];

		// Setting output descriptor
		outputDescriptor_4.setParamDescriptor(outParameterDescriptor_4);
		serviceClient_4.configureOutput(outputDescriptor_4);

		EndpointReferenceType delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsEPR.toString(), delegationLifetime, issuedCredentialLifetime, 
				delegationPath, issuedCredentialPath);

		wf_instance5.addCredential(serviceClient_4.getEndpointReference(), delegationEPR);


		// Setting second parameter
		serviceClient_4.setParameter(new InputParameter("simple type's streaming", 1));
		// END service 4




		// BEGIN service 2				
		// create service 2
		WorkflowInvocationHelperDescriptor operation__2 = new WorkflowInvocationHelperDescriptor();
		operation__2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "CapitalizeRequest"));
		operation__2.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/Service2");
		operation__2.setOutputType(new QName(XSD_NAMESPACE, "string"));
		
		operation__2.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		
		WorkflowInvocationHelperClient serviceClient_2 = null;
		try {
			serviceClient_2 = wf_instance5.createWorkflowInvocationHelper(operation__2);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient_2);


		// Creating Descriptor of the InputMessage
		OperationInputMessageDescriptor inputMessage__2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam__2 = new InputParameterDescriptor[1];
		inputParam__2[0] = new InputParameterDescriptor(new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"));
		inputMessage__2.setInputParam(inputParam__2 );
		serviceClient_2.configureInput(inputMessage__2);
		// End InputMessage Descriptor


		wf_instance5.addCredential(serviceClient_2.getEndpointReference(), delegationEPR);

		// configure destination of output
		OperationOutputTransportDescriptor outputDescriptor__2 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor__2 [] = new OperationOutputParameterTransportDescriptor[1];
		
		
		// 1st destination: Service4
		outParameterDescriptor__2[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor__2[0].setParamIndex(0);
		outParameterDescriptor__2[0].setType(new QName(XSD_NAMESPACE, "string"));
		QName[] namespaces__2 = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor__2[0].setQueryNamespaces(namespaces__2);
		outParameterDescriptor__2[0].setLocationQuery("/ns0:CapitalizeResponse");
		outParameterDescriptor__2[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient_4.getEndpointReference()});
		

		outputDescriptor__2.setParamDescriptor(outParameterDescriptor__2);
		serviceClient_2.configureOutput(outputDescriptor__2);
		// END service 2



		// BEGIN CreateArrayService				
		// create CreateArrayService	
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation__cas = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		operation__cas.setWorkflowID("GeorgeliusWorkFlow");
		operation__cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetArrayRequest"));
		operation__cas.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/CreateArrayService");
		operation__cas.setOutputType(new QName(SOAPENCODING_NAMESPACE, "string[]"));
		
		operation__cas.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		
		WorkflowInvocationHelperClient serviceClient_cs = null;
		try {
			serviceClient_cs = wf_instance5.createWorkflowInvocationHelper(operation__cas);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient_cs);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage__cas = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams__cas = new InputParameterDescriptor[0];
		inputMessage__cas.setInputParam(inputParams__cas);
		serviceClient_cs.configureInput(inputMessage__cas);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the only service that will receive the output (Service2::capitalize)
		OperationOutputTransportDescriptor outputDescriptor_cs = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_cs [] = new OperationOutputParameterTransportDescriptor[1];

		// First destination: Service2::capitalize
		outParameterDescriptor_cs[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_cs[0].setParamIndex(0);
		outParameterDescriptor_cs[0].setType(new QName( SOAPENCODING_NAMESPACE, "string"));
		outParameterDescriptor_cs[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_cs[0].setLocationQuery("/ns0:GetArrayResponse");
		outParameterDescriptor_cs[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient_2.getEndpointReference()});  // takes the reference to Service2::capitalize


		outputDescriptor_cs.setParamDescriptor(outParameterDescriptor_cs);
		serviceClient_cs.configureOutput(outputDescriptor_cs);

		wf_instance5.addCredential(serviceClient_cs.getEndpointReference(), delegationEPR);
		// END CreateArrayService
	}

	
	
	/**
	 * Instantiate the workflow that will test complex array usual handling
	 * 
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_helper Client of a running WorkflowHelper service
	 * @param myCredential Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime 
	 * @param delegationPath
	 * @param issuedCredentialPath
	 * 
	 * */
	private void runComplexArrayTest(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper, EndpointReferenceType outputMatcherEPR, ProxyLifetime issuedCredentialLifetime, ProxyLifetime delegationLifetime, GlobusCredential myCredential, int issuedCredentialPath, int delegationPath) throws RemoteException{


		/** complex type arrays **/
		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor1 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		workflowDescriptor1.setWorkflowID("WorkFlow1");
		workflowDescriptor1.setWorkflowManagerEPR(manager_epr);

		
		
		
		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance1 = null;
		try {
			wf_instance1 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor1);
		} catch (MalformedURIException e1) {
			e1.printStackTrace();
		}

		// BEGIN ReceiveArrayService::ReceiveComplexArray	


		String access_url = containerBaseURL+"/wsrf/services/cagrid/ReceiveArrayService";
		WorkflowInvocationHelperDescriptor operation2 = new WorkflowInvocationHelperDescriptor();
		operation2.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "ReceiveComplexArrayRequest"));
		operation2.setServiceURL(access_url);
		//operation2.setOutput(); // This service has no output

		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(cdsEPR);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);
		operation2.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		
		

		// create ReceiveArrayService
		WorkflowInvocationHelperClient client2 = null;
		try {
			client2 = wf_instance1.createWorkflowInvocationHelper(operation2);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), client2);

		//System.out.println("Configuring invocation helper"); //DEBUG

		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ras = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ras = new InputParameterDescriptor[3];
		inputParams_ras[0] = new InputParameterDescriptor(new QName("number"), new QName(XSD_NAMESPACE, "int"));
		inputParams_ras[1] = new InputParameterDescriptor(new QName("complexArray"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType[]"));
		inputParams_ras[2] = new InputParameterDescriptor(new QName("booleanValue"), new QName(XSD_NAMESPACE, "boolean"));
		inputMessage_ras.setInputParam(inputParams_ras);
		try {
			client2.configureInput(inputMessage_ras);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		// End InputMessage Descriptor

		// Creating an empty outputDescriptor
		OperationOutputTransportDescriptor outputDescriptor_ras = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ras [] = new OperationOutputParameterTransportDescriptor[0];

		// takes the reference to no service
		outputDescriptor_ras.setParamDescriptor(outParameterDescriptor_ras);
		client2.configureOutput(outputDescriptor_ras);


		// Set the GlobusCredential to use on InstanceHelper
		//System.out.println("Delegating helper's credential to the InstanceHelper"); //DEBUG
		EndpointReferenceType delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsEPR.toString(), delegationLifetime, issuedCredentialLifetime, 
				delegationPath, issuedCredentialPath);

		wf_instance1.addCredential(client2.getEndpointReference(), delegationEPR);

		//System.out.println("Setting params"); //DEBUG

		// Set the values of its simple-type arguments
		client2.setParameter(new InputParameter("999", 0)); // number
		client2.setParameter(new InputParameter("true",2));  // booleanValue
		// END ReceiveArrayService::ReceiveComplexArray



		// BEGIN CreateArrayService::getComplexArray				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_ca = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		access_url = containerBaseURL+"/wsrf/services/cagrid/CreateArrayService";
		operation_ca.setWorkflowID("GeorgeliusWorkFlow");
		operation_ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetComplexArrayRequest"));
		operation_ca.setServiceURL(access_url);
		operation_ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType[]"));

		
		operation_ca.setWorkflowInvocationSecurityDescriptor(secDescriptor);

		// create ReceiveArrayService		
		WorkflowInvocationHelperClient client_ca = null;
		try {
			client_ca = wf_instance1.createWorkflowInvocationHelper(operation_ca);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		// Monitor status changes
		/*System.out.println("Subscribing "+ operation_ca.getOperationQName().getLocalPart() +" to be monitored. Key = "
				+ client_ca.getEndpointReference().toString()); //DEBUG */
		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), client_ca);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ca = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ca = new InputParameterDescriptor[0];
		inputMessage_ca.setInputParam(inputParams_ca);
		client_ca.configureInput(inputMessage_ca);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the only service that will receive the output (ReceiveArrayService)
		OperationOutputTransportDescriptor outputDescriptor_ca = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ca [] = new OperationOutputParameterTransportDescriptor[2];

		// First destination: ReceiveArrayService::ReceiveComplexArray
		outParameterDescriptor_ca[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_ca[0].setParamIndex(1);
		outParameterDescriptor_ca[0].setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType[]"));
		outParameterDescriptor_ca[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_ca[0].setLocationQuery("/ns0:GetComplexArrayResponse");
		outParameterDescriptor_ca[0].setDestinationEPR(new EndpointReferenceType[]{ client2.getEndpointReference() });


		// Second destination: Output matcher
		outParameterDescriptor_ca[1] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_ca[1].setParamIndex(1); // Setting 2nd argument in the output matcher 
		outParameterDescriptor_ca[1].setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType[]"));
		outParameterDescriptor_ca[1].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_ca[1].setLocationQuery("/ns0:GetComplexArrayResponse");
		outParameterDescriptor_ca[1].setDestinationEPR(new EndpointReferenceType[]{ outputMatcherEPR });


		wf_instance1.addCredential(client_ca.getEndpointReference(), delegationEPR);


		// takes the reference to ReceiveComplexArrayService
		outputDescriptor_ca.setParamDescriptor(outParameterDescriptor_ca);
		client_ca.configureOutput(outputDescriptor_ca);
		// END CreateArrayService::getComplexArray 

		return;
	}



	/**
	 * Instantiate the workflow that will test simple array usual handling
	 * 
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_helper Client of a running WorkflowHelper service
	 * @param myCredential Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime 
	 * @param delegationPath
	 * @param issuedCredentialPath
	 * 
	 * */
	private void runSimpleArrayTest(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper, EndpointReferenceType outputMatcherEPR, ProxyLifetime issuedCredentialLifetime, GlobusCredential myCredential, ProxyLifetime delegationLifetime, int issuedCredentialPath, int delegationPath) throws RemoteException{

		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor2 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		workflowDescriptor2.setWorkflowID("WorkFlow2");
		workflowDescriptor2.setWorkflowManagerEPR(manager_epr);

		
		
		
		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance2 = null;
		try {
			wf_instance2 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor2);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


		// BEGIN ReceiveArrayService::ReceiveArrayAndMore
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_ram = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		operation_ram.setWorkflowID("GeorgeliusWorkFlow");
		operation_ram.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "ReceiveArrayAndMoreRequest"));
		operation_ram.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/ReceiveArrayService");
		//operation_ram.setOutputType(); // Service has no output

		
		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(cdsEPR);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);
		operation_ram.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		
		

		// create ReceiveArrayService				
		WorkflowInvocationHelperClient serviceClient_ram = null;
		try {
			serviceClient_ram = wf_instance2.createWorkflowInvocationHelper(operation_ram);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient_ram);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ram = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ram = new InputParameterDescriptor[3];
		inputParams_ram[0] = new InputParameterDescriptor(new QName("number"), new QName(XSD_NAMESPACE, "int"));
		inputParams_ram[1] = new InputParameterDescriptor(new QName("strArray"), new QName(XSD_NAMESPACE, "string[]"));
		inputParams_ram[2] = new InputParameterDescriptor(new QName("booleanValue"), new QName(XSD_NAMESPACE, "boolean"));
		inputMessage_ram.setInputParam(inputParams_ram);
		serviceClient_ram.configureInput(inputMessage_ram);
		// End InputMessage Descriptor

		// Creating an empty outputDescriptor
		OperationOutputTransportDescriptor outputDescriptor_ram = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ram [] = new OperationOutputParameterTransportDescriptor[0];

		// takes the reference to no service
		outputDescriptor_ram.setParamDescriptor(outParameterDescriptor_ram);
		serviceClient_ram.configureOutput(outputDescriptor_ram);

		// Set the GlobusCredential to use on InstanceHelper
		//System.out.println("Delegating helper's credential to the InstanceHelper"); //DEBUG
		EndpointReferenceType delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsEPR.toString(), delegationLifetime, issuedCredentialLifetime, 
				delegationPath, issuedCredentialPath);

		wf_instance2.addCredential(serviceClient_ram.getEndpointReference(), delegationEPR);

		
		// Set the values of the two arguments of simple type
		serviceClient_ram.setParameter(new InputParameter("999", 0));
		serviceClient_ram.setParameter(new InputParameter("true",2));
		// END ReceiveArrayService::ReceiveArrayAndMore



		// BEGIN CreateArrayService				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_cas = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		String access_url = containerBaseURL+"/wsrf/services/cagrid/CreateArrayService";
		operation_cas.setWorkflowID("GeorgeliusWorkFlow");
		operation_cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetArrayRequest"));
		operation_cas.setServiceURL(access_url);
		operation_cas.setOutputType(new QName(SOAPENCODING_NAMESPACE, "string[]"));

		operation_cas.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		

		// create ReceiveArrayService				
		WorkflowInvocationHelperClient serviceClient_cas = null;
		try {
			serviceClient_cas = wf_instance2.createWorkflowInvocationHelper( operation_cas);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient_cas);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_cas = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_cas = new InputParameterDescriptor[0];
		inputMessage_cas.setInputParam(inputParams_cas);
		serviceClient_cas.configureInput(inputMessage_cas);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the only service that will receive the output (ReceiveArrayService)
		OperationOutputTransportDescriptor outputDescriptor_cas = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_cas [] = new OperationOutputParameterTransportDescriptor[2];

		// First destination: ReceiveArrayService::ReceiveArrayAndMore
		outParameterDescriptor_cas[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_cas[0].setParamIndex(1);
		outParameterDescriptor_cas[0].setType(new QName( SOAPENCODING_NAMESPACE ,"string[]"));
		outParameterDescriptor_cas[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_cas[0].setLocationQuery("/ns0:GetArrayResponse");
		outParameterDescriptor_cas[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient_ram.getEndpointReference()});

		
		// Second destination: Output matcher
		outParameterDescriptor_cas[1] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_cas[1].setParamIndex(4);
		outParameterDescriptor_cas[1].setType(new QName( SOAPENCODING_NAMESPACE ,"string[]"));
		outParameterDescriptor_cas[1].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_cas[1].setLocationQuery("/ns0:GetArrayResponse");
		outParameterDescriptor_cas[1].setDestinationEPR(new EndpointReferenceType[]{ outputMatcherEPR});
		
		
		
		wf_instance2.addCredential(serviceClient_cas.getEndpointReference(), delegationEPR);

		// takes the reference to ReceiveArrayService
		outputDescriptor_cas.setParamDescriptor(outParameterDescriptor_cas);
		serviceClient_cas.configureOutput(outputDescriptor_cas);

		// END CreateArrayService 

		return;
	}

	
	
	/**
	 * Instantiate the workflow that will test the mechanisms for receiving input from multiple stages and forwarding the outputs
	 * to multiple stages.
	 * 
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_helper Client of a running WorkflowHelper service
	 * @param myCredential Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime 
	 * @param delegationPath
	 * @param issuedCredentialPath
	 * 
	 * */
	private void runFaninFanOutTest(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper, EndpointReferenceType outputMatcherEPR, ProxyLifetime delegationLifetime, GlobusCredential myCredential, ProxyLifetime issuedCredentialLifetime, int delegationPath, int issuedCredentialPath) throws RemoteException{

		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor3 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		workflowDescriptor3.setWorkflowID("WorkFlow2");
		workflowDescriptor3.setWorkflowManagerEPR(manager_epr);

		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance3 = null;
		try {
			wf_instance3 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor3);
		}  catch (MalformedURIException e) {
			e.printStackTrace();
		}

		// BEGIN service 4				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation4 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		java.lang.String acess_url = containerBaseURL+"/wsrf/services/cagrid/Service4";
		operation4.setWorkflowID("GeorgeliusWorkFlow");
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation4.setServiceURL(acess_url);

		
		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(cdsEPR);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);
		operation4.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		

		// Creating client of service 4
		WorkflowInvocationHelperClient serviceClient4 = null;
		try {
			serviceClient4 = wf_instance3.createWorkflowInvocationHelper(operation4);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient4);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams4 = new InputParameterDescriptor[2];
		inputParams4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"));
		inputParams4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"));
		inputMessage4.setInputParam(inputParams4);
		serviceClient4.configureInput(inputMessage4);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor4 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor4 [] = new OperationOutputParameterTransportDescriptor[0];
		QName namespaces[] = null;

		// Set the GlobusCredential to use on InstanceHelper
		//System.out.println("Delegating helper's credential to the InstanceHelper"); //DEBUG
		EndpointReferenceType delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsEPR.toString(), delegationLifetime, issuedCredentialLifetime, 
				delegationPath, issuedCredentialPath);

		wf_instance3.addCredential(serviceClient4.getEndpointReference(), delegationEPR);


		// takes the reference to no service
		outputDescriptor4.setParamDescriptor(outParameterDescriptor4);
		serviceClient4.configureOutput(outputDescriptor4);
		// END service 4



		// BEGIN service 2				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_2 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service2";
		operation_2.setWorkflowID("GeorgeliusWorkFlow");
		operation_2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "CapitalizeRequest"));
		operation_2.setServiceURL(acess_url);
		operation_2.setOutputType(new QName(XSD_NAMESPACE, "string"));

		
		operation_2.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		

		// create service 2				
		WorkflowInvocationHelperClient serviceClient2 = null;
		try {
			serviceClient2 = wf_instance3.createWorkflowInvocationHelper(operation_2);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient2);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_2 = new InputParameterDescriptor[1];
		inputParams_2[0] = new InputParameterDescriptor( new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"));
		inputMessage_2.setInputParam(inputParams_2);
		serviceClient2.configureInput(inputMessage_2);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor2 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor2 [] = new OperationOutputParameterTransportDescriptor[2];
		
		// First destination
		outParameterDescriptor2[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor2[0].setParamIndex(0);
		outParameterDescriptor2[0].setType(new QName("string"));
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor2[0].setQueryNamespaces(namespaces);
		outParameterDescriptor2[0].setLocationQuery("/ns0:CapitalizeResponse");
		outParameterDescriptor2[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient4.getEndpointReference()});
		
		
		// Second destination: output matcher
		outParameterDescriptor2[1] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor2[1].setParamIndex(6);
		outParameterDescriptor2[1].setType(new QName("string"));
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor2[1].setQueryNamespaces(namespaces);
		outParameterDescriptor2[1].setLocationQuery("/ns0:CapitalizeResponse");
		outParameterDescriptor2[1].setDestinationEPR(new EndpointReferenceType[]{ outputMatcherEPR});
		
		
		
		wf_instance3.addCredential(serviceClient2.getEndpointReference(), delegationEPR);

		
		// takes the reference to the service 4
		outputDescriptor2.setParamDescriptor(outParameterDescriptor2);
		serviceClient2.configureOutput(outputDescriptor2);
		// END service 2



		// BEGIN service 3
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation3 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service3";

		// This is the greek version of my name...
		operation3.setWorkflowID("GeorgeliusWorkFlow");
		operation3.setOperationQName(new QName("http://service3.introduce.cagrid.org/Service3", "GenerateXRequest"));
		operation3.setServiceURL(acess_url);
		operation3.setOutputType(new QName(XSD_NAMESPACE, "string"));

		
		operation3.setWorkflowInvocationSecurityDescriptor(secDescriptor);

		// create service 3				
		WorkflowInvocationHelperClient serviceClient3 = null;
		try {
			serviceClient3 = wf_instance3.createWorkflowInvocationHelper(operation3	);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient3);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage3 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams3 = new InputParameterDescriptor[1];
		inputParams3[0] = new InputParameterDescriptor(new QName("str_length"), new QName(XSD_NAMESPACE, "int"));
		inputMessage3.setInputParam(inputParams3);
		serviceClient3.configureInput(inputMessage3);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor3 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor3 [] = new OperationOutputParameterTransportDescriptor[2];
		
		
		// 1st destination
		outParameterDescriptor3[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor3[0].setParamIndex(1);
		outParameterDescriptor3[0].setType(new QName(XSD_NAMESPACE, "string"));
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service3.introduce.cagrid.org/Service3", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor3[0].setQueryNamespaces(namespaces);
		outParameterDescriptor3[0].setLocationQuery("/ns0:GenerateXResponse"); 
		outParameterDescriptor3[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient4.getEndpointReference()});
		
		
		// 2nd destination: output matcher
		outParameterDescriptor3[1] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor3[1].setParamIndex(7);
		outParameterDescriptor3[1].setType(new QName(XSD_NAMESPACE, "string"));
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service3.introduce.cagrid.org/Service3", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor3[1].setQueryNamespaces(namespaces);
		outParameterDescriptor3[1].setLocationQuery("/ns0:GenerateXResponse"); 
		outParameterDescriptor3[1].setDestinationEPR(new EndpointReferenceType[]{outputMatcherEPR});
		
		
		wf_instance3.addCredential(serviceClient3.getEndpointReference(), delegationEPR);

		
		// takes the reference to the service 4
		outputDescriptor3.setParamDescriptor(outParameterDescriptor3);
		serviceClient3.configureOutput(outputDescriptor3);
		// END service 3				


		// BEGIN service 5				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation5 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service5";
		operation5.setWorkflowID("GeorgeliusWorkFlow");
		operation5.setOperationQName(new QName("http://service5.introduce.cagrid.org/Service5" , "CheckStringAndItsLengthRequest"));
		operation5.setServiceURL(acess_url);
		operation5.setOutputType(new QName(XSD_NAMESPACE, "boolean"));

		operation5.setWorkflowInvocationSecurityDescriptor(secDescriptor);

		// Creating client of service 5
		WorkflowInvocationHelperClient serviceClient5 = null;
		try {
			serviceClient5 = wf_instance3.createWorkflowInvocationHelper(operation5);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient5);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage5 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams5 = new InputParameterDescriptor[1];
		inputParams5[0] = new InputParameterDescriptor(new QName("http://service1.workflow.cagrid.org/Service1", "stringAndItsLenght"), 
				new QName("http://service1.workflow.cagrid.org/Service1", "StringAndItsLength"));
		inputMessage5.setInputParam(inputParams5);
		serviceClient5.configureInput(inputMessage5);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor5 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor5 [] = new OperationOutputParameterTransportDescriptor[1];
		outParameterDescriptor5[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor5[0].setParamIndex(0);
		outParameterDescriptor5[0].setType(new QName("string"));
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service5.introduce.cagrid.org/Service5", "ns0")};
		outParameterDescriptor5[0].setQueryNamespaces(namespaces);
		outParameterDescriptor5[0].setLocationQuery("/ns0:CheckStringAndItsLengthResponse");

		wf_instance3.addCredential(serviceClient5.getEndpointReference(), delegationEPR);


		// takes the reference to no service
		outParameterDescriptor5[0].setDestinationEPR(null);
		outputDescriptor5.setParamDescriptor(outParameterDescriptor5);
		serviceClient5.configureOutput(outputDescriptor5);

		// END service 5


		// BEGIN service 1				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation1 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service1";
		operation1.setWorkflowID("GeorgeliusWorkFlow");
		operation1.setOperationQName(new QName("GenerateDataRequest"));
		operation1.setServiceURL(acess_url);
		operation1.setOutputType(new QName(XSD_NAMESPACE, "string"));

		operation1.setWorkflowInvocationSecurityDescriptor(secDescriptor);


		// create service 1				
		WorkflowInvocationHelperClient serviceClient1 = null;
		try {
			serviceClient1 = wf_instance3.createWorkflowInvocationHelper(operation1);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		this.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), serviceClient1);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage1 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams1 = new InputParameterDescriptor[1];
		inputParams1[0] = new InputParameterDescriptor(new QName("info"), new QName(XSD_NAMESPACE, "string"));
		inputMessage1.setInputParam(inputParams1);
		serviceClient1.configureInput(inputMessage1);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter (Service2)
		OperationOutputTransportDescriptor outputDescriptor1 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor1 [] = new OperationOutputParameterTransportDescriptor[3];
		outParameterDescriptor1[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor1[0].setParamIndex(0);
		outParameterDescriptor1[0].setType(new QName("string"));
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service1.introduce.cagrid.org/Service1", "ns0"),
				new QName("http://service1.workflow.cagrid.org/Service1", "ns1")};
		outParameterDescriptor1[0].setQueryNamespaces(namespaces);
		outParameterDescriptor1[0].setLocationQuery("/ns0:GenerateDataResponse/ns1:StringAndItsLenght/ns1:str"); 
		// takes the reference to the service 2
		outParameterDescriptor1[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient2.getEndpointReference()});

		// Creating the outputDescriptor of the second filter (Service3)
		outParameterDescriptor1[1] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor1[1].setParamIndex(0);
		outParameterDescriptor1[1].setType(new QName("int"));
		outParameterDescriptor1[1].setQueryNamespaces(namespaces);
		outParameterDescriptor1[1].setLocationQuery("/ns0:GenerateDataResponse/ns1:StringAndItsLenght/ns1:length");
		// takes the reference to the service 3
		outParameterDescriptor1[1].setDestinationEPR(new EndpointReferenceType[]{serviceClient3.getEndpointReference()});

		// Creating the outputDescriptor of the second filter (Service5)
		outParameterDescriptor1[2] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor1[2].setParamIndex(0);
		outParameterDescriptor1[2].setType(new QName("http://service1.workflow.cagrid.org/Service1","StringAndItsLenght"));
		outParameterDescriptor1[2].setQueryNamespaces(namespaces);
		outParameterDescriptor1[2].setLocationQuery("/ns0:GenerateDataResponse/ns1:StringAndItsLenght");
		// takes the reference to the service 5
		outParameterDescriptor1[2].setDestinationEPR(new EndpointReferenceType[]{serviceClient5.getEndpointReference()});

		// parameters are all set at this point
		outputDescriptor1.setParamDescriptor(outParameterDescriptor1);
		serviceClient1.configureOutput(outputDescriptor1);

		wf_instance3.addCredential(serviceClient1.getEndpointReference(), delegationEPR);

		
		// set the only one parameter of this service.
		// now it have to run and set one Parameter of the service4
		String workflow_input = "george teadoro gordao que falou";
		System.out.println("Setting input for service 1: '"+workflow_input+"'");
		InputParameter inputService1 = new InputParameter(workflow_input, 0);
		serviceClient1.setParameter(inputService1);
		// END service 1 

	}


	
	
	
	private void waitUntilCompletion() {

		System.out.println("Waiting for workflow notification of FINISH status");

		this.isFinishedKey.lock();
		try {


			if( !this.isFinished ){

				try {
					this.isFinishedCondition.await();
				} catch(Throwable t){
					System.err.println("Error while waiting");
					t.printStackTrace();
				}
			}

		}
		finally {
			this.isFinishedKey.unlock();
		}

	}


	public void deliver(List arg0, EndpointReferenceType arg1, Object arg2) {
		org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType changeMessage = ((org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType) arg2)
		.getResourcePropertyValueChangeNotification();

		MessageElement actual_property = changeMessage.getNewValue().get_any()[0];
		QName message_qname = actual_property.getQName();
		boolean isStatusChange = message_qname.equals(Status.getTypeDesc().getXmlType());
		String stageKey = arg1.toString();


		// Handle status change notifications
		if(isStatusChange){
			Status status = null;;
			try {
				status = (Status) actual_property.getValueAsType(message_qname, Status.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//log.println("[deliver] Received new status value: "+ status.toString()); //DEBUG

			if(status.equals(Status.ERROR)){ // Dies when an error is reported
				Assert.fail("Received ERROR notification from "+ stageKey);
			}
			else if(status.equals(Status.FINISHED)){

				this.isFinishedKey.lock();
				try{

					if( this.stageIsFinished.containsKey(stageKey) ){

						this.stageIsFinished.remove(stageKey);
						this.stageIsFinished.put(stageKey, Boolean.TRUE);

						//printMap(this.stageIsFinished);//DEBUG

					}
					else System.err.println("[CreateTestWorkflowsStep] Unrecognized stage notified status change: "+ stageKey);
					this.isFinished  = (!this.stageIsFinished.containsValue(Boolean.FALSE));
					if(this.isFinished) 
						this.isFinishedCondition.signalAll();
				}
				finally {
					this.isFinishedKey.unlock();
				}
			}

		}
	}


	private void subscribe(QName notificationType, WorkflowInvocationHelperClient toSubscribe){


		try{

			this.stageIsFinished.put(toSubscribe.getEndpointReference().toString(), Boolean.FALSE); // Register to be monitored for status changes
			toSubscribe.subscribe(notificationType, this);
		}
		catch(Throwable t){
			t.printStackTrace();
		} 

		return;
	}

	private static void printMap( Map<String, Boolean> map ){


		System.out.println("BEGIN printMap");
		Set<Entry<String, Boolean>> entries = map.entrySet();
		Iterator<Entry<String, Boolean>> iter = entries.iterator();
		while(iter.hasNext()){

			Entry<String, Boolean> curr = iter.next();
			System.out.println("["+ curr.getKey() +", "+ curr.getValue() +"]");
		}
		System.out.println("END printMap");


		return;
	}


}

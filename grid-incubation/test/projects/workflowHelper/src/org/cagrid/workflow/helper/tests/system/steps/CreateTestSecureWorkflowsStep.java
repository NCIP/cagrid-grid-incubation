package org.cagrid.workflow.helper.tests.system.steps;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.NamespaceConstants;

import junit.framework.Assert;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.AttributedURI;
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
import org.cagrid.workflow.helper.descriptor.TLSInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.helper.util.CredentialHandlingUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.NotifyCallback;

public class CreateTestSecureWorkflowsStep extends CreateTestWorkflowsStep implements NotifyCallback {


	private EndpointReferenceType cdsEPR;
	private String cdsURL;

	private GlobusCredential userCredential; 


	final static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	final static String SOAPENCODING_NAMESPACE = "http://schemas.xmlsoap.org/soap/encoding/";


	protected final boolean validatorEnabled = true;  // Enable/Disable the output matcher. Should be true when not debugging
	

	public CreateTestSecureWorkflowsStep(EndpointReference helperEPR, EndpointReferenceType cdsEPR, String container_base_url, 
			GlobusCredential userCredential, String cdsURL) {
		super(helperEPR, container_base_url);

		//this.cdsEPR = cdsEPR;		
		this.userCredential = userCredential;
		this.cdsURL = cdsURL;
		try {
			this.cdsEPR = new EndpointReferenceType(new Address(cdsURL));
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runStep() throws Throwable {

		System.out.println("---- BEGIN SECURE WORKFLOW TEST ----");
		

		try{

			final WorkflowHelperClient wf_helper = new WorkflowHelperClient(helper_epr); 
			final EndpointReferenceType manager_epr = null;


			// Manager role: Delegate user credential to the Manager 	
			//System.out.println("[CreateTestSecureWorkflowsStep] Delegating user credential to the Helper"); //DEBUG
			AttributedURI cdsAddress = this.cdsEPR.getAddress();
			String cdsURL = cdsAddress.toString(); 
			EndpointReferenceType proxyEPR = CredentialHandlingUtil.delegateCredential(this.userCredential, wf_helper.getIdentity(), 
					cdsURL , new ProxyLifetime(5,0,0), new ProxyLifetime(6,0,0), 3, 2);
			System.out.println("[CreateTestSecureWorkflowsStep] Delegation done");


			// Get delegated credential from the user
			//System.out.println("[CreateTestSecureWorkflowsStep] FakeManager retrieving delegated user credential"); //DEBUG
			GlobusCredential myCredential = CredentialHandlingUtil.getDelegatedCredential(new EndpointReference(proxyEPR));
			//System.out.println("[CreateTestSecureWorkflowsStep] Retrieval done");



			ProxyLifetime delegationLifetime = new ProxyLifetime(4,0,0);
			ProxyLifetime issuedCredentialLifetime = new ProxyLifetime(5,0,0);
			int delegationPath = 1;
			int issuedCredentialPath = 0; 


			/*** Testing arrays as services' input ***/

			/** simple type arrays **/
			System.out.println("[CreateTestSecureWorkflowsStep] Simple arrays as input");
			runSimpleArrayTest(manager_epr, wf_helper, issuedCredentialLifetime, myCredential, delegationLifetime,
					issuedCredentialPath, delegationPath);
			System.out.println("[CreateTestSecureWorkflowsStep] OK");

			/*System.out.println("[CreateTestSecureWorkflowsStep] Complex arrays as input"); // FIXME Debug this workflow's configuration. 
			runComplexArrayTest(manager_epr, wf_helper, issuedCredentialLifetime, delegationLifetime, myCredential,
					issuedCredentialPath, delegationPath);
			System.out.println("[CreateTestSecureWorkflowsStep] OK"); // */

			System.out.println("[CreateTestSecureWorkflowsStep] END Testing arrays"); // */




			/** BEGIN streaming test **/
			System.out.println("[CreateTestSecureWorkflowsStep] BEGIN Testing streaming");

			// Streaming simple types 
			System.out.println("[CreateTestSecureWorkflowsStep] Streaming of simple-type arrays");
			runSimpleArrayStreaming(manager_epr, wf_helper, myCredential, issuedCredentialLifetime, delegationLifetime, issuedCredentialPath,
					delegationPath);
			System.out.println("[CreateTestSecureWorkflowsStep] OK");  // */



			/* Streaming complex types */
			System.out.print("[CreateTestSecureWorkflowsStep] Streaming of complex-type arrays");
			runComplexArrayStreaming(manager_epr, wf_helper, issuedCredentialLifetime, myCredential, delegationLifetime, 
					issuedCredentialPath, delegationPath);
			System.out.println("[CreateTestSecureWorkflowsStep] OK");

			System.out.println("[CreateTestSecureWorkflowsStep] END Testing streaming"); // */



			/** FAN IN AND FAN OUT TEST **/
			System.out.println("[CreateTestSecureWorkflowsStep] BEGIN Testing fan in and fan out"); 
			runFaninFanOutTest(manager_epr, wf_helper, delegationLifetime, myCredential, issuedCredentialLifetime,
					delegationPath, issuedCredentialPath);
			System.out.println("[CreateTestSecureWorkflowsStep] END Testing fan in and fan out"); // */


			// Block until every stage reports either a FINISHED or an ERROR status
			this.waitUntilCompletion();

		}
		catch(Throwable t){
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
	private EndpointReferenceType runOuputMatcher(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper, 
			GlobusCredential myCredential, ProxyLifetime delegationLifetime, ProxyLifetime issuedCredentialLifetime, 
			int delegationPath, int issuedCredentialPath) throws RemoteException {

		System.out.println("BEGIN runOuputMatcher");

		WorkflowInstanceHelperDescriptor validatorInstanceDesc = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		String workflowID = "Validator";
		validatorInstanceDesc.setWorkflowID(workflowID);
		validatorInstanceDesc.setWorkflowManagerEPR(manager_epr);

		String outputMatcherURI =  "http://validateoutputsservice.test.workflow.cagrid.org/ValidateOutputsService";

		WorkflowInstanceHelperClient validatorInstance = null;
		try {
			validatorInstance = wf_helper.createWorkflowInstanceHelper(validatorInstanceDesc);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}
		
		
		this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), validatorInstance, workflowID);
		

		WorkflowInvocationHelperDescriptor validatorInvocationDesc = new WorkflowInvocationHelperDescriptor();
		validatorInvocationDesc.setOperationQName(
				new QName(outputMatcherURI, "SecureValidateTestOutputRequest"));
		validatorInvocationDesc.setServiceURL(containerBaseURL + "/wsrf/services/cagrid/ValidateOutputsService");

		// Configure security
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
		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), validatorInvocation1
			//	, validatorInvocationDesc.getOperationQName().toString());

		// Set the GlobusCredential to use on InstanceHelper
		//System.out.println("[runOutputMatcher] Delegating helper's credential to the InstanceHelper"); //DEBUG
		EndpointReferenceType delegationEPR = null;
		try{
			delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsURL, delegationLifetime, issuedCredentialLifetime, 
					delegationPath, issuedCredentialPath);

			//System.out.println("Informing the InstanceHelper about the delegation"); //DEBUG

			validatorInstance.addCredential(validatorInvocation1.getEndpointReference(), delegationEPR);
			//System.out.println("[runOutputMatcher] Done");
		}
		catch(Throwable t){
			t.printStackTrace();
		}


		// Configure inputs
		OperationInputMessageDescriptor validatorInputDesc = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam = new InputParameterDescriptor[8];
		inputParam[0] = new InputParameterDescriptor(new QName("test1Param1"), new QName(XSD_NAMESPACE, "int"), false);
		inputParam[1] = new InputParameterDescriptor(new QName("test1Param2"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"), true);
		inputParam[2] = new InputParameterDescriptor(new QName("test1Param3"), new QName(XSD_NAMESPACE, "boolean"), false);
		inputParam[3] = new InputParameterDescriptor(new QName("test2Param1"), new QName(XSD_NAMESPACE, "int"), false);
		inputParam[4] = new InputParameterDescriptor(new QName("test2Param2"), new QName(XSD_NAMESPACE, "string"), true);
		inputParam[5] = new InputParameterDescriptor(new QName("test2Param3"), new QName(XSD_NAMESPACE, "boolean"), false);
		inputParam[6] = new InputParameterDescriptor(new QName("test3Param1"), new QName(XSD_NAMESPACE, "string"), false);
		inputParam[7] = new InputParameterDescriptor(new QName("test3Param2"), new QName(XSD_NAMESPACE, "string"), false); // */


		validatorInputDesc.setInputParam(inputParam);
		validatorInvocation1.configureInput(validatorInputDesc);


		// Configure outputs: it has none
		OperationOutputTransportDescriptor validatorOutput = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor[] paramDescriptor = new OperationOutputParameterTransportDescriptor[0];
		validatorOutput.setParamDescriptor(paramDescriptor );
		validatorInvocation1.configureOutput(validatorOutput);
		validatorInvocation1.start();



		// Set static parameters
		validatorInvocation1.setParameter(new InputParameter("999", 0));
		validatorInvocation1.setParameter(new InputParameter("true", 2));
		validatorInvocation1.setParameter(new InputParameter("999", 3));
		validatorInvocation1.setParameter(new InputParameter("true", 5)); // */


		System.out.println("END runOuputMatcher");

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

		System.out.println("BEGIN runComplexArrayStreaming");

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
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "" +
		"SecurePrintResultsRequest"));
		operation4.setServiceURL(acess_url);

		// Configure security
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

		// For now, we don't register to get notifications, because we can't determine when a "streaming session" ends 
		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), serviceClient__4);

		// Set the GlobusCredential to use on InstanceHelper
		//System.out.println("[runComplexArrayStreaming] Delegating helper's credential to the InstanceHelper"); //DEBUG
		EndpointReferenceType delegationEPR = null;
		try{
			delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsURL, delegationLifetime, issuedCredentialLifetime, 
					delegationPath, issuedCredentialPath);
			wf_instance5.addCredential(serviceClient__4.getEndpointReference(), delegationEPR);
		}
		catch(Throwable t){
			t.printStackTrace();
		}
		
		
		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams4 = new InputParameterDescriptor[2];
		inputParams4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"), false);
		inputParams4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"), false);
		inputMessage4.setInputParam(inputParams4);
		serviceClient__4.configureInput(inputMessage4);
		// End InputMessage Descriptor

		// Setting output descriptor
		OperationOutputTransportDescriptor outputDescriptor4 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor4 [] = new OperationOutputParameterTransportDescriptor[0];


		// takes the reference to no service
		outputDescriptor4.setParamDescriptor(outParameterDescriptor4);
		serviceClient__4.configureOutput(outputDescriptor4);
		serviceClient__4.start();



		// Setting second parameter
		serviceClient__4.setParameter(new InputParameter("complex type's streaming", 1));
		// END service 4

		// BEGIN CreateArrayService::getComplexArray				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation__ca = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		String access_url = containerBaseURL+"/wsrf/services/cagrid/CreateArrayService";
		operation__ca.setWorkflowID("GeorgeliusWorkFlow");
		operation__ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "SecureGetComplexArrayRequest"));
		operation__ca.setServiceURL(access_url);
		operation__ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"));
		operation__ca.setOutputIsArray(true);



		// Configure security
		operation__ca.setWorkflowInvocationSecurityDescriptor(secDescriptor);

		// create ReceiveArrayService				
		WorkflowInvocationHelperClient serviceClient__ca = null;
		try {
			serviceClient__ca = wf_instance5.createWorkflowInvocationHelper(operation__ca);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		// For now, we don't register to get notifications, because we can't determine when a "streaming session" ends
		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), serviceClient__ca);

		// Set the GlobusCredential to use on InstanceHelper
		try{
			wf_instance5.addCredential(serviceClient__ca.getEndpointReference(), delegationEPR);
		}
		catch(Throwable t){
			t.printStackTrace();
		}

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
		outParameterDescriptor__ca[0].setLocationQuery("/ns0:SecureGetComplexArrayResponse/abc:ComplexType/abc:message");
		outParameterDescriptor__ca[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient__4.getEndpointReference()});



		// takes the reference to ReceiveComplexArrayService
		outputDescriptor__ca.setParamDescriptor(outParameterDescriptor__ca);
		serviceClient__ca.configureOutput(outputDescriptor__ca);
		serviceClient__ca.start();


		System.out.println("END runComplexArrayStreaming");
		// END CreateArrayService::getComplexArray 

		System.out.println("END runComplexArrayStreaming");
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

		System.out.println("BEGIN runSimpleArrayStreaming");

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
		operation_4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "SecurePrintResultsRequest"));
		operation_4.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/Service4");
		// operation_4.setOutputType(); // Void output expected


		// Configure security
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

		// For now, we don't register to get notifications, because we can't determine when a "streaming session" ends
		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), serviceClient_4);


		// Set the GlobusCredential to use on InstanceHelper
		//System.out.println("[runSimpleArrayStreaming] Delegating helper's credential to the InstanceHelper"); //DEBUG
		EndpointReferenceType delegationEPR = null;
		try{
			delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsURL, 
					delegationLifetime, issuedCredentialLifetime, 
					delegationPath, issuedCredentialPath);
			wf_instance5.addCredential(serviceClient_4.getEndpointReference(), delegationEPR);
		}
		catch(Throwable t){
			t.printStackTrace();
		}


		// Creating Descriptor of the InputMessage
		OperationInputMessageDescriptor inputMessage_4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam_4 = new InputParameterDescriptor[2];
		inputParam_4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"), false);
		inputParam_4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"), false);
		inputMessage_4.setInputParam(inputParam_4);
		serviceClient_4.configureInput(inputMessage_4);
		// End InputMessage Descriptor

		OperationOutputTransportDescriptor outputDescriptor_4 = new OperationOutputTransportDescriptor(); 
		OperationOutputParameterTransportDescriptor[] outParameterDescriptor_4 = new OperationOutputParameterTransportDescriptor[0];

		// Setting output descriptor
		outputDescriptor_4.setParamDescriptor(outParameterDescriptor_4);
		serviceClient_4.configureOutput(outputDescriptor_4);
		serviceClient_4.start();

		// Setting second parameter
		serviceClient_4.setParameter(new InputParameter("simple type's streaming", 1));
		// END service 4




		// BEGIN service 2				
		// create service 2
		WorkflowInvocationHelperDescriptor operation__2 = new WorkflowInvocationHelperDescriptor();
		operation__2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "SecureCapitalizeRequest"));
		operation__2.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/Service2");
		operation__2.setOutputType(new QName(XSD_NAMESPACE, "string"));

		// Configure security
		operation__2.setWorkflowInvocationSecurityDescriptor(secDescriptor);


		WorkflowInvocationHelperClient serviceClient_2 = null;
		try {
			serviceClient_2 = wf_instance5.createWorkflowInvocationHelper(operation__2);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		// For now, we don't register to get notifications, because we can't determine when a "streaming session" ends
		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), serviceClient_2);


		// Set the GlobusCredential to use on InstanceHelper
		wf_instance5.addCredential(serviceClient_2.getEndpointReference(), delegationEPR);


		// Creating Descriptor of the InputMessage
		OperationInputMessageDescriptor inputMessage__2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam__2 = new InputParameterDescriptor[1];
		inputParam__2[0] = new InputParameterDescriptor(new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"), false);
		inputMessage__2.setInputParam(inputParam__2 );
		serviceClient_2.configureInput(inputMessage__2);
		// End InputMessage Descriptor


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
		outParameterDescriptor__2[0].setLocationQuery("/ns0:SecureCapitalizeResponse");
		outParameterDescriptor__2[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient_4.getEndpointReference()});


		outputDescriptor__2.setParamDescriptor(outParameterDescriptor__2);
		serviceClient_2.configureOutput(outputDescriptor__2);
		serviceClient_2.start();

		// END service 2



		// BEGIN CreateArrayService				
		// create CreateArrayService	
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation__cas = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		operation__cas.setWorkflowID("GeorgeliusWorkFlow");
		operation__cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "SecureGetArrayRequest"));
		operation__cas.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/CreateArrayService");
		operation__cas.setOutputType(new QName(SOAPENCODING_NAMESPACE, "string"));
		operation__cas.setOutputIsArray(true);

		// Configure security
		operation__cas.setWorkflowInvocationSecurityDescriptor(secDescriptor);


		WorkflowInvocationHelperClient serviceClient_cs = null;
		try {
			serviceClient_cs = wf_instance5.createWorkflowInvocationHelper(operation__cas);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		// For now, we don't register to get notifications, because we can't determine when a "streaming session" ends
		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), serviceClient_cs);

		// Set the GlobusCredential to use on InstanceHelper
		wf_instance5.addCredential(serviceClient_cs.getEndpointReference(), delegationEPR);


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
		outParameterDescriptor_cs[0].setLocationQuery("/ns0:SecureGetArrayResponse");
		outParameterDescriptor_cs[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient_2.getEndpointReference()});  // takes the reference to Service2::capitalize


		outputDescriptor_cs.setParamDescriptor(outParameterDescriptor_cs);
		serviceClient_cs.configureOutput(outputDescriptor_cs);
		serviceClient_cs.start();

		// END CreateArrayService

		System.out.println("END runSimpleArrayStreaming");
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
	private void runComplexArrayTest(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper, ProxyLifetime issuedCredentialLifetime, ProxyLifetime delegationLifetime, GlobusCredential myCredential, int issuedCredentialPath, int delegationPath) throws RemoteException{

		System.out.println("BEGIN runComplexArrayTest");

		/** complex type arrays **/
		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor1 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		String workflowID = "WorkFlow1";
		workflowDescriptor1.setWorkflowID(workflowID);
		workflowDescriptor1.setWorkflowManagerEPR(manager_epr);

		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance1 = null;
		try {
			wf_instance1 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor1);
		} catch (MalformedURIException e1) {
			e1.printStackTrace();
		}

		
		this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), wf_instance1, workflowID);
		
		
		
		
		// BEGIN ReceiveArrayService::ReceiveComplexArray	
		String access_url = containerBaseURL+"/wsrf/services/cagrid/ReceiveArrayService";
		WorkflowInvocationHelperDescriptor operation2 = new WorkflowInvocationHelperDescriptor();
		operation2.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "SecureReceiveComplexArrayRequest"));
		operation2.setServiceURL(access_url);
		//operation2.setOutput(); // This service has no output

		// Configure security
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

		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), client2
			//	, operation2.getOperationQName().toString());

		//System.out.println("Configuring invocation helper"); //DEBUG


		// Set the GlobusCredential to use on InstanceHelper
		//System.out.println("[runComplexArray] Delegating helper's credential to the InstanceHelper"); //DEBUG
		EndpointReferenceType delegationEPR = null;
		try{
			delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsURL
					, delegationLifetime, issuedCredentialLifetime, 
					delegationPath, issuedCredentialPath);
			wf_instance1.addCredential(client2.getEndpointReference(), delegationEPR);
		}
		catch(Throwable t){
			t.printStackTrace();
		}

		
		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ras = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ras = new InputParameterDescriptor[3];
		inputParams_ras[0] = new InputParameterDescriptor(new QName("number"), new QName(XSD_NAMESPACE, "int"), false);
		inputParams_ras[1] = new InputParameterDescriptor(new QName("complexArray"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"), true);
		inputParams_ras[2] = new InputParameterDescriptor(new QName("booleanValue"), new QName(XSD_NAMESPACE, "boolean"), false);
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
		client2.start();

		//System.out.println("Setting params"); //DEBUG

		// Set the values of its simple-type arguments
		client2.setParameter(new InputParameter("999", 0)); // number
		client2.setParameter(new InputParameter("true",2));  // booleanValue
		// END ReceiveArrayService::ReceiveComplexArray

		
		
		
		
		// Stage that verifies whether the workflow output matches the expected
		WorkflowInvocationHelperClient client_assert = null;
		if(this.validatorEnabled){

			// BEGIN AssertService::assertSimpleArrayEquals
			org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_assert = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
			operation_assert.setWorkflowID("GeorgeliusWorkFlow");
			operation_assert.setOperationQName(new QName("http://assertService.test.system.workflow.cagrid.org/AssertService", "AssertComplexArrayEqualsRequest"));
			operation_assert.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/AssertService");
			operation_assert.setOutputType(new QName(NamespaceConstants.NSURI_SCHEMA_XSD, "boolean"));


			
			// create ReceiveArrayService
			try {
				client_assert = wf_instance1.createWorkflowInvocationHelper(operation_assert);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			}


			// Creating Descriptor of the InputMessage
			org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_assert = new OperationInputMessageDescriptor();
			InputParameterDescriptor[] inputParams_assert = new InputParameterDescriptor[2];
			inputParams_assert[0] = new InputParameterDescriptor(new QName("complexArray1"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"), true);
			inputParams_assert[1] = new InputParameterDescriptor(new QName("complexArray2"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"), true);
			inputMessage_assert.setInputParam(inputParams_assert);
			client_assert.configureInput(inputMessage_assert);
			// End InputMessage Descriptor

			// Creating an empty outputDescriptor
			OperationOutputTransportDescriptor outputDescriptor_assert = new OperationOutputTransportDescriptor();
			OperationOutputParameterTransportDescriptor outParameterDescriptor_assert [] = new OperationOutputParameterTransportDescriptor[0];

			// takes the reference to no service
			outputDescriptor_assert.setParamDescriptor(outParameterDescriptor_assert);
			client_assert.configureOutput(outputDescriptor_assert);



			// Set the expected output
			InputParameter inputParameter = new InputParameter("<SecureGetComplexArrayResponse xmlns=\"http://createarrayservice.introduce.cagrid.org/CreateArrayService\">" +
					"<ns1:ComplexType xmlns:ns1=\"http://systemtests.workflow.cagrid.org/SystemTests\"><ns1:id>0</ns1:id><ns1:message>Element 0</ns1:message>" +
					"</ns1:ComplexType><ns2:ComplexType xmlns:ns2=\"http://systemtests.workflow.cagrid.org/SystemTests\"><ns2:id>1</ns2:id>" +
					"<ns2:message>Element 1</ns2:message></ns2:ComplexType><ns3:ComplexType xmlns:ns3=\"http://systemtests.workflow.cagrid.org/SystemTests\">" +
					"<ns3:id>2</ns3:id><ns3:message>Element 2</ns3:message></ns3:ComplexType><ns4:ComplexType xmlns:ns4=\"http://systemtests.workflow.cagrid.org/SystemTests\">" +
					"<ns4:id>3</ns4:id><ns4:message>Element 3</ns4:message></ns4:ComplexType><ns5:ComplexType xmlns:ns5=\"http://systemtests.workflow.cagrid.org/SystemTests\">" +
					"<ns5:id>4</ns5:id><ns5:message>Element 4</ns5:message></ns5:ComplexType><ns6:ComplexType xmlns:ns6=\"http://systemtests.workflow.cagrid.org/SystemTests\">" +
					"<ns6:id>5</ns6:id><ns6:message>Element 5</ns6:message></ns6:ComplexType></SecureGetComplexArrayResponse>", 1);
			client_assert.setParameter(inputParameter);

			client_assert.start();
			// END AssertService::assertSimpleArrayEquals
		}
		
		


		// BEGIN CreateArrayService::getComplexArray				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_ca = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		access_url = containerBaseURL+"/wsrf/services/cagrid/CreateArrayService";
		operation_ca.setWorkflowID("GeorgeliusWorkFlow");
		operation_ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "SecureGetComplexArrayRequest"));
		operation_ca.setServiceURL(access_url);
		operation_ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"));
		operation_ca.setOutputIsArray(true);

		// Configure security
		operation_ca.setWorkflowInvocationSecurityDescriptor(secDescriptor);


		// create ReceiveArrayService		
		WorkflowInvocationHelperClient client_ca = null;
		try {
			client_ca = wf_instance1.createWorkflowInvocationHelper(operation_ca);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

	
		// Set the GlobusCredential to use on InstanceHelper
		wf_instance1.addCredential(client_ca.getEndpointReference(), delegationEPR);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ca = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ca = new InputParameterDescriptor[0];
		inputMessage_ca.setInputParam(inputParams_ca);
		client_ca.configureInput(inputMessage_ca);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the only service that will receive the output (ReceiveArrayService)
		OperationOutputTransportDescriptor outputDescriptor_ca = new OperationOutputTransportDescriptor();
		int numDestination = this.validatorEnabled ? 2: 1;
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ca [] = new OperationOutputParameterTransportDescriptor[numDestination];

		// First destination: ReceiveArrayService::ReceiveComplexArray
		outParameterDescriptor_ca[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_ca[0].setParamIndex(1);
		outParameterDescriptor_ca[0].setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType"));
		outParameterDescriptor_ca[0].setExpectedTypeIsArray(true);
		outParameterDescriptor_ca[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_ca[0].setLocationQuery("/ns0:SecureGetComplexArrayResponse");
		outParameterDescriptor_ca[0].setDestinationEPR(new EndpointReferenceType[]{ client2.getEndpointReference() });


		// Second destination: Output matcher
		if( validatorEnabled ){

			//System.out.println("Setting 2nd param in the output matcher: "+ outputMatcherEPR); //DEBUG

			outParameterDescriptor_ca[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor_ca[1].setParamIndex(0); // Setting 2nd argument in the output matcher 
			outParameterDescriptor_ca[1].setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType"));
			outParameterDescriptor_ca[1].setExpectedTypeIsArray(true);
			outParameterDescriptor_ca[1].setQueryNamespaces(new QName[]{ 
					new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
					new QName(XSD_NAMESPACE,"xsd")});
			outParameterDescriptor_ca[1].setLocationQuery("/ns0:SecureGetComplexArrayResponse");
			outParameterDescriptor_ca[1].setDestinationEPR(new EndpointReferenceType[]{ client_assert.getEndpointReference() });
		}



		// takes the reference to ReceiveComplexArrayService
		outputDescriptor_ca.setParamDescriptor(outParameterDescriptor_ca);
		client_ca.configureOutput(outputDescriptor_ca);
		client_ca.start();

		// END CreateArrayService::getComplexArray 


		System.out.println("END runComplexArrayTest");
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
	private void runSimpleArrayTest(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper, ProxyLifetime issuedCredentialLifetime, GlobusCredential myCredential, ProxyLifetime delegationLifetime, int issuedCredentialPath, int delegationPath) throws RemoteException{

		System.out.println("BEGIN runSimpleArrayTest");

		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor2 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		String workflowID = "WorkFlow2";
		workflowDescriptor2.setWorkflowID(workflowID);
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

		this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), wf_instance2, workflowID);
		
		

		// BEGIN ReceiveArrayService::ReceiveArrayAndMore
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_ram = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		operation_ram.setWorkflowID("GeorgeliusWorkFlow");
		operation_ram.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "SecureReceiveArrayAndMoreRequest"));
		operation_ram.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/ReceiveArrayService");
		//operation_ram.setOutputType(); // Service has no output

		// Configure security
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

					
		// Set the GlobusCredential to use on InstanceHelper
		EndpointReferenceType delegationEPR = null;
		try{
			delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsURL
					, delegationLifetime, issuedCredentialLifetime, 
					delegationPath, issuedCredentialPath);
			wf_instance2.addCredential(serviceClient_ram.getEndpointReference(), delegationEPR);
			System.out.println("[runSimpleArray] Done");
		}
		catch(Throwable t){
			t.printStackTrace();
		}
		

		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ram = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ram = new InputParameterDescriptor[3];
		inputParams_ram[0] = new InputParameterDescriptor(new QName("number"), new QName(XSD_NAMESPACE, "int"), false);
		inputParams_ram[1] = new InputParameterDescriptor(new QName("strArray"), new QName(XSD_NAMESPACE, "string"), true);
		inputParams_ram[2] = new InputParameterDescriptor(new QName("booleanValue"), new QName(XSD_NAMESPACE, "boolean"), false);
		inputMessage_ram.setInputParam(inputParams_ram);
		serviceClient_ram.configureInput(inputMessage_ram);
		// End InputMessage Descriptor

		// Creating an empty outputDescriptor
		OperationOutputTransportDescriptor outputDescriptor_ram = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ram [] = new OperationOutputParameterTransportDescriptor[0];

		// takes the reference to no service
		outputDescriptor_ram.setParamDescriptor(outParameterDescriptor_ram);
		serviceClient_ram.configureOutput(outputDescriptor_ram);
		serviceClient_ram.start();


	

		// Set the values of the two arguments of simple type
		serviceClient_ram.setParameter(new InputParameter("999", 0));
		serviceClient_ram.setParameter(new InputParameter("true",2));
		// END ReceiveArrayService::ReceiveArrayAndMore



		
		// Stage that verifies whether the workflow output matches the expected
		WorkflowInvocationHelperClient serviceClient_assert = null;
		if(this.validatorEnabled){

			// BEGIN AssertService::assertSimpleArrayEquals
			org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_assert = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
			operation_assert.setWorkflowID("GeorgeliusWorkFlow");
			operation_assert.setOperationQName(new QName("http://assertService.test.system.workflow.cagrid.org/AssertService", "AssertSimpleArrayEqualsRequest"));
			operation_assert.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/AssertService");
			operation_assert.setOutputType(new QName(NamespaceConstants.NSURI_SCHEMA_XSD, "boolean"));


			// create AssertService				
			try {
				serviceClient_assert = wf_instance2.createWorkflowInvocationHelper(operation_assert);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			}


			// Creating Descriptor of the InputMessage
			org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_assert = new OperationInputMessageDescriptor();
			InputParameterDescriptor[] inputParams_assert = new InputParameterDescriptor[2];
			inputParams_assert[0] = new InputParameterDescriptor(new QName("stringArray1"), new QName(XSD_NAMESPACE, "string"), true);
			inputParams_assert[1] = new InputParameterDescriptor(new QName("stringArray2"), new QName(XSD_NAMESPACE, "string"), true);
			inputMessage_assert.setInputParam(inputParams_assert);
			serviceClient_assert.configureInput(inputMessage_assert);
			// End InputMessage Descriptor

			// Creating an empty outputDescriptor
			OperationOutputTransportDescriptor outputDescriptor_assert = new OperationOutputTransportDescriptor();
			OperationOutputParameterTransportDescriptor outParameterDescriptor_assert [] = new OperationOutputParameterTransportDescriptor[0];

			// takes the reference to no service
			outputDescriptor_assert.setParamDescriptor(outParameterDescriptor_assert);
			serviceClient_assert.configureOutput(outputDescriptor_assert);



			// Set the expected output
			InputParameter inputParameter = new InputParameter("<SecureGetArrayResponse xmlns=\"http://createarrayservice.introduce.cagrid.org/CreateArrayService\">" +
					"<response>number 0</response><response>number 1</response><response>number 2</response><response>number 3</response><response>number 4</response><response>number 5" +
					"</response></SecureGetArrayResponse>", 1);  
			serviceClient_assert.setParameter(inputParameter);

			serviceClient_assert.start();
			// END AssertService::assertSimpleArrayEquals
		}
		
		
		
		

		// BEGIN CreateArrayService				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_cas = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		String access_url = containerBaseURL+"/wsrf/services/cagrid/CreateArrayService";
		operation_cas.setWorkflowID("GeorgeliusWorkFlow");
		operation_cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "SecureGetArrayRequest"));
		operation_cas.setServiceURL(access_url);
		operation_cas.setOutputType(new QName(SOAPENCODING_NAMESPACE, "string"));
		operation_cas.setOutputIsArray(true);

		// Configure security
		operation_cas.setWorkflowInvocationSecurityDescriptor(secDescriptor);


		// create ReceiveArrayService				
		WorkflowInvocationHelperClient serviceClient_cas = null;
		try {
			serviceClient_cas = wf_instance2.createWorkflowInvocationHelper( operation_cas);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

	
		// Set the GlobusCredential to use on InstanceHelper
		wf_instance2.addCredential(serviceClient_cas.getEndpointReference(), delegationEPR);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_cas = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_cas = new InputParameterDescriptor[0];
		inputMessage_cas.setInputParam(inputParams_cas);
		serviceClient_cas.configureInput(inputMessage_cas);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the only service that will receive the output (ReceiveArrayService)
		OperationOutputTransportDescriptor outputDescriptor_cas = new OperationOutputTransportDescriptor();
		int numDestinations = this.validatorEnabled ? 2 : 1;
		OperationOutputParameterTransportDescriptor outParameterDescriptor_cas [] = new OperationOutputParameterTransportDescriptor[numDestinations];

		// First destination: ReceiveArrayService::ReceiveArrayAndMore
		outParameterDescriptor_cas[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_cas[0].setParamIndex(1);
		outParameterDescriptor_cas[0].setType(new QName( SOAPENCODING_NAMESPACE ,"string"));
		outParameterDescriptor_cas[0].setExpectedTypeIsArray(true);
		outParameterDescriptor_cas[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_cas[0].setLocationQuery("/ns0:SecureGetArrayResponse");
		outParameterDescriptor_cas[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient_ram.getEndpointReference()});


		// Second destination: Output matcher
		if(validatorEnabled){

			//System.out.println("Setting 5th param in the output matcher: "+ outputMatcherEPR); //DEBUG

			outParameterDescriptor_cas[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor_cas[1].setParamIndex(0);
			outParameterDescriptor_cas[1].setType(new QName( SOAPENCODING_NAMESPACE ,"string"));
			outParameterDescriptor_cas[1].setExpectedTypeIsArray(true);
			outParameterDescriptor_cas[1].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
					new QName(XSD_NAMESPACE,"xsd")});
			outParameterDescriptor_cas[1].setLocationQuery("/ns0:SecureGetArrayResponse");
			outParameterDescriptor_cas[1].setDestinationEPR(new EndpointReferenceType[]{ serviceClient_assert.getEndpointReference() });
		}


		// takes the reference to ReceiveArrayService
		outputDescriptor_cas.setParamDescriptor(outParameterDescriptor_cas);
		serviceClient_cas.configureOutput(outputDescriptor_cas);
		serviceClient_cas.start();
		// END CreateArrayService 

		System.out.println("END runSimpleArrayTest");
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
	private void runFaninFanOutTest(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper, ProxyLifetime delegationLifetime, GlobusCredential myCredential, ProxyLifetime issuedCredentialLifetime, int delegationPath, int issuedCredentialPath) throws RemoteException{

		System.out.println("BEGIN runFaninFanOutTest");

		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor3 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		String workflowID = "WorkFlow2";
		workflowDescriptor3.setWorkflowID(workflowID);
		workflowDescriptor3.setWorkflowManagerEPR(manager_epr);

		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance3 = null;
		try {
			wf_instance3 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor3);
		}  catch (MalformedURIException e) {
			e.printStackTrace();
		}
		
		this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), wf_instance3, workflowID);
		

		// BEGIN service 4				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation4 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		java.lang.String acess_url = containerBaseURL+"/wsrf/services/cagrid/Service4";
		operation4.setWorkflowID("GeorgeliusWorkFlow");
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "SecurePrintResultsRequest"));
		operation4.setServiceURL(acess_url);


		// Configure security
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

		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), serviceClient4
		//		, operation4.getOperationQName().toString());


		// Set the GlobusCredential to use on InstanceHelper
		//System.out.println("[runFanInFanOut] Delegating helper's credential to the InstanceHelper"); //DEBUG
		EndpointReferenceType delegationEPR = null;
		try{
			delegationEPR = CredentialHandlingUtil.delegateCredential(myCredential, wf_helper.getIdentity(), this.cdsURL
					, delegationLifetime, issuedCredentialLifetime, 
					delegationPath, issuedCredentialPath);
			wf_instance3.addCredential(serviceClient4.getEndpointReference(), delegationEPR);
		}
		catch(Throwable t){
			t.printStackTrace();
		}


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams4 = new InputParameterDescriptor[2];
		inputParams4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"), false);
		inputParams4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"), false);
		inputMessage4.setInputParam(inputParams4);
		serviceClient4.configureInput(inputMessage4);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor4 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor4 [] = new OperationOutputParameterTransportDescriptor[0];
		QName namespaces[] = null;


		// takes the reference to no service
		outputDescriptor4.setParamDescriptor(outParameterDescriptor4);
		serviceClient4.configureOutput(outputDescriptor4);
		serviceClient4.start();

		// END service 4


		
		
		// Stage that verifies whether service2's output matches the expected
		WorkflowInvocationHelperClient client_assert2 = null;
		if(this.validatorEnabled){

			// BEGIN AssertService::assertSimpleArrayEquals
			org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_assert = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
			operation_assert.setWorkflowID("GeorgeliusWorkFlow");
			operation_assert.setOperationQName(new QName("http://assertService.test.system.workflow.cagrid.org/AssertService", "AssertEqualsRequest"));
			operation_assert.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/AssertService");
			operation_assert.setOutputType(new QName(NamespaceConstants.NSURI_SCHEMA_XSD, "boolean"));

	
			try {
				client_assert2 = wf_instance3.createWorkflowInvocationHelper(operation_assert);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			}


			// Creating Descriptor of the InputMessage
			org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_assert = new OperationInputMessageDescriptor();
			InputParameterDescriptor[] inputParams_assert = new InputParameterDescriptor[2];
			inputParams_assert[0] = new InputParameterDescriptor(new QName("string1"), new QName(NamespaceConstants.NSURI_SCHEMA_XSD, "string"), false);
			inputParams_assert[1] = new InputParameterDescriptor(new QName("string2"), new QName(NamespaceConstants.NSURI_SCHEMA_XSD, "string"), false);
			inputMessage_assert.setInputParam(inputParams_assert);
			client_assert2.configureInput(inputMessage_assert);
			// End InputMessage Descriptor

			// Creating an empty outputDescriptor
			OperationOutputTransportDescriptor outputDescriptor_assert = new OperationOutputTransportDescriptor();
			OperationOutputParameterTransportDescriptor outParameterDescriptor_assert [] = new OperationOutputParameterTransportDescriptor[0];

			// takes the reference to no service
			outputDescriptor_assert.setParamDescriptor(outParameterDescriptor_assert);
			client_assert2.configureOutput(outputDescriptor_assert);



			// Set the expected output
			InputParameter inputParameter = new InputParameter("GEORGE TEADORO GORDAO QUE FALOU", 1);
			client_assert2.setParameter(inputParameter);

			client_assert2.start();
			// END AssertService::assertSimpleArrayEquals
		}

		
		
		
		

		// BEGIN service 2				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_2 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service2";
		operation_2.setWorkflowID("GeorgeliusWorkFlow");
		operation_2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "SecureCapitalizeRequest"));
		operation_2.setServiceURL(acess_url);
		operation_2.setOutputType(new QName(XSD_NAMESPACE, "string"));

		// Configure security
		operation_2.setWorkflowInvocationSecurityDescriptor(secDescriptor);


		// create service 2				
		WorkflowInvocationHelperClient serviceClient2 = null;
		try {
			serviceClient2 = wf_instance3.createWorkflowInvocationHelper(operation_2);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

	
		// Set the GlobusCredential to use on InstanceHelper
		wf_instance3.addCredential(serviceClient2.getEndpointReference(), delegationEPR);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_2 = new InputParameterDescriptor[1];
		inputParams_2[0] = new InputParameterDescriptor(new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"),false);
		inputMessage_2.setInputParam(inputParams_2);
		serviceClient2.configureInput(inputMessage_2);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor2 = new OperationOutputTransportDescriptor();
		int numDestinations = this.validatorEnabled ? 2 : 1;
		OperationOutputParameterTransportDescriptor outParameterDescriptor2 [] = new OperationOutputParameterTransportDescriptor[numDestinations];

		// First destination
		outParameterDescriptor2[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor2[0].setParamIndex(0);
		outParameterDescriptor2[0].setType(new QName("string"));
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor2[0].setQueryNamespaces(namespaces);
		outParameterDescriptor2[0].setLocationQuery("/ns0:SecureCapitalizeResponse");
		outParameterDescriptor2[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient4.getEndpointReference()});


		// Second destination: output matcher
		if(this.validatorEnabled){

			//System.out.println("Setting 7th param in the output matcher: "+ outputMatcherEPR); //DEBUG

			outParameterDescriptor2[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor2[1].setParamIndex(0);
			outParameterDescriptor2[1].setType(new QName("string"));
			namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
					new QName(XSD_NAMESPACE, "xsd")};
			outParameterDescriptor2[1].setQueryNamespaces(namespaces);
			outParameterDescriptor2[1].setLocationQuery("/ns0:SecureCapitalizeResponse");
			outParameterDescriptor2[1].setDestinationEPR(new EndpointReferenceType[]{ client_assert2.getEndpointReference() });
		}

		outputDescriptor2.setParamDescriptor(outParameterDescriptor2);
		serviceClient2.configureOutput(outputDescriptor2);
		serviceClient2.start();
		// END service 2

		
		
		// Stage that verifies whether service3's output matches the expected
		WorkflowInvocationHelperClient client_assert3 = null;
		if(this.validatorEnabled){

			// BEGIN AssertService::assertSimpleArrayEquals
			org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_assert = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
			operation_assert.setWorkflowID("GeorgeliusWorkFlow");
			operation_assert.setOperationQName(new QName("http://assertService.test.system.workflow.cagrid.org/AssertService", "AssertEqualsRequest"));
			operation_assert.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/AssertService");
			operation_assert.setOutputType(new QName(NamespaceConstants.NSURI_SCHEMA_XSD, "boolean"));

	
			try {
				client_assert3 = wf_instance3.createWorkflowInvocationHelper(operation_assert);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			}


			// Creating Descriptor of the InputMessage
			org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_assert = new OperationInputMessageDescriptor();
			InputParameterDescriptor[] inputParams_assert = new InputParameterDescriptor[2];
			inputParams_assert[0] = new InputParameterDescriptor(new QName("string1"), new QName(NamespaceConstants.NSURI_SCHEMA_XSD, "string"), false);
			inputParams_assert[1] = new InputParameterDescriptor(new QName("string2"), new QName(NamespaceConstants.NSURI_SCHEMA_XSD, "string"), false);
			inputMessage_assert.setInputParam(inputParams_assert);
			client_assert3.configureInput(inputMessage_assert);
			// End InputMessage Descriptor

			// Creating an empty outputDescriptor
			OperationOutputTransportDescriptor outputDescriptor_assert = new OperationOutputTransportDescriptor();
			OperationOutputParameterTransportDescriptor outParameterDescriptor_assert [] = new OperationOutputParameterTransportDescriptor[0];

			// takes the reference to no service
			outputDescriptor_assert.setParamDescriptor(outParameterDescriptor_assert);
			client_assert3.configureOutput(outputDescriptor_assert);



			// Set the expected output
			InputParameter inputParameter = new InputParameter("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", 1);
			client_assert3.setParameter(inputParameter);

			client_assert3.start();
			// END AssertService::assertSimpleArrayEquals
		}
		
		
		// BEGIN service 3
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation3 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service3";

		// This is the greek version of my name...
		operation3.setWorkflowID("GeorgeliusWorkFlow");
		operation3.setOperationQName(new QName("http://service3.introduce.cagrid.org/Service3", "SecureGenerateXRequest"));
		operation3.setServiceURL(acess_url);
		operation3.setOutputType(new QName(XSD_NAMESPACE, "string"));


		// Configure security
		operation3.setWorkflowInvocationSecurityDescriptor(secDescriptor);


		// create service 3				
		WorkflowInvocationHelperClient serviceClient3 = null;
		try {
			serviceClient3 = wf_instance3.createWorkflowInvocationHelper(operation3	);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), serviceClient3
			//	, operation3.getOperationQName().toString());


		// Set the GlobusCredential to use on InstanceHelper
		wf_instance3.addCredential(serviceClient3.getEndpointReference(), delegationEPR);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage3 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams3 = new InputParameterDescriptor[1];
		inputParams3[0] = new InputParameterDescriptor(new QName("str_length"), new QName(XSD_NAMESPACE, "int"), false);
		inputMessage3.setInputParam(inputParams3);
		serviceClient3.configureInput(inputMessage3);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor3 = new OperationOutputTransportDescriptor();
		numDestinations = this.validatorEnabled ? 2 : 1;
		OperationOutputParameterTransportDescriptor outParameterDescriptor3 [] = new OperationOutputParameterTransportDescriptor[numDestinations];


		// 1st destination
		outParameterDescriptor3[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor3[0].setParamIndex(1);
		outParameterDescriptor3[0].setType(new QName(XSD_NAMESPACE, "string"));
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service3.introduce.cagrid.org/Service3", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor3[0].setQueryNamespaces(namespaces);
		outParameterDescriptor3[0].setLocationQuery("/ns0:SecureGenerateXResponse"); 
		outParameterDescriptor3[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient4.getEndpointReference()});


		// 2nd destination: output matcher
		if(this.validatorEnabled){

			//System.out.println("Setting 8th param in the output matcher: "+ outputMatcherEPR); //DEBUG

			outParameterDescriptor3[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor3[1].setParamIndex(0);
			outParameterDescriptor3[1].setType(new QName(XSD_NAMESPACE, "string"));
			namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service3.introduce.cagrid.org/Service3", "ns0"),
					new QName(XSD_NAMESPACE, "xsd")};
			outParameterDescriptor3[1].setQueryNamespaces(namespaces);
			outParameterDescriptor3[1].setLocationQuery("/ns0:SecureGenerateXResponse"); 
			outParameterDescriptor3[1].setDestinationEPR(new EndpointReferenceType[]{client_assert3.getEndpointReference()});  
		}



		// takes the reference to the service 4

		outputDescriptor3.setParamDescriptor(outParameterDescriptor3);
		serviceClient3.configureOutput(outputDescriptor3);
		serviceClient3.start();

		// END service 3				


		// BEGIN service 5				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation5 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service5";
		operation5.setWorkflowID("GeorgeliusWorkFlow");
		operation5.setOperationQName(new QName("http://service5.introduce.cagrid.org/Service5" , "SecureCheckStringAndItsLengthRequest"));
		operation5.setServiceURL(acess_url);
		operation5.setOutputType(new QName(XSD_NAMESPACE, "boolean"));


		// Configure security
		operation5.setWorkflowInvocationSecurityDescriptor(secDescriptor);


		// Creating client of service 5
		WorkflowInvocationHelperClient serviceClient5 = null;
		try {
			serviceClient5 = wf_instance3.createWorkflowInvocationHelper(operation5);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), serviceClient5
			//	, operation5.getOperationQName().toString());


		// Set the GlobusCredential to use on InstanceHelper
		wf_instance3.addCredential(serviceClient5.getEndpointReference(), delegationEPR);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage5 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams5 = new InputParameterDescriptor[1];
		inputParams5[0] = new InputParameterDescriptor(new QName("http://service1.workflow.cagrid.org/Service1", "stringAndItsLenght"), 
				new QName("http://service1.workflow.cagrid.org/Service1", "StringAndItsLength"), false);
		inputMessage5.setInputParam(inputParams5);
		serviceClient5.configureInput(inputMessage5);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor5 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor5 [] = new OperationOutputParameterTransportDescriptor[0];


		// takes the reference to no service

		outputDescriptor5.setParamDescriptor(outParameterDescriptor5);
		serviceClient5.configureOutput(outputDescriptor5);
		serviceClient5.start();

		// END service 5


		// BEGIN service 1				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation1 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service1";
		operation1.setWorkflowID("GeorgeliusWorkFlow");
		operation1.setOperationQName(new QName("http://service1.introduce.cagrid.org/Service1", "SecureGenerateDataRequest"));
		operation1.setServiceURL(acess_url);
		operation1.setOutputType(new QName(XSD_NAMESPACE, "string"));

		// Configure security
		operation1.setWorkflowInvocationSecurityDescriptor(secDescriptor);


		// create service 1				
		WorkflowInvocationHelperClient serviceClient1 = null;
		try {
			serviceClient1 = wf_instance3.createWorkflowInvocationHelper(operation1);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		//this.subscribe(org.cagrid.workflow.helper.descriptor.TimestampedStatus.getTypeDesc().getXmlType(), serviceClient1
		//		, operation1.getOperationQName().toString());

		// Set the GlobusCredential to use on InstanceHelper
		wf_instance3.addCredential(serviceClient1.getEndpointReference(), delegationEPR);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage1 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams1 = new InputParameterDescriptor[1];
		inputParams1[0] = new InputParameterDescriptor(new QName("info"), new QName(XSD_NAMESPACE, "string"), false);
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
		outParameterDescriptor1[0].setLocationQuery("/ns0:SecureGenerateDataResponse/ns1:StringAndItsLenght/ns1:str"); 
		// takes the reference to the service 2
		outParameterDescriptor1[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient2.getEndpointReference()});

		// Creating the outputDescriptor of the second filter (Service3)
		outParameterDescriptor1[1] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor1[1].setParamIndex(0);
		outParameterDescriptor1[1].setType(new QName("int"));
		outParameterDescriptor1[1].setQueryNamespaces(namespaces);
		outParameterDescriptor1[1].setLocationQuery("/ns0:SecureGenerateDataResponse/ns1:StringAndItsLenght/ns1:length");
		// takes the reference to the service 3
		outParameterDescriptor1[1].setDestinationEPR(new EndpointReferenceType[]{serviceClient3.getEndpointReference()});

		// Creating the outputDescriptor of the 3rd filter (Service5)
		outParameterDescriptor1[2] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor1[2].setParamIndex(0);
		outParameterDescriptor1[2].setType(new QName("http://service1.workflow.cagrid.org/Service1","StringAndItsLenght"));
		outParameterDescriptor1[2].setQueryNamespaces(namespaces);
		outParameterDescriptor1[2].setLocationQuery("/ns0:SecureGenerateDataResponse/ns1:StringAndItsLenght");
		// takes the reference to the service 5
		outParameterDescriptor1[2].setDestinationEPR(new EndpointReferenceType[]{serviceClient5.getEndpointReference()});

		// parameters are all set at this point
		outputDescriptor1.setParamDescriptor(outParameterDescriptor1);
		serviceClient1.configureOutput(outputDescriptor1);
		serviceClient1.start();



		// set the only one parameter of this service.
		// now it have to run and set one Parameter of the service4
		String workflow_input = "george teadoro gordao que falou";
		System.out.println("Setting input for service 1: '"+workflow_input+"'");
		InputParameter inputService1 = new InputParameter(workflow_input, 0);
		serviceClient1.setParameter(inputService1);
		// END service 1 


		System.out.println("END runFaninFanOutTest");
	}


}

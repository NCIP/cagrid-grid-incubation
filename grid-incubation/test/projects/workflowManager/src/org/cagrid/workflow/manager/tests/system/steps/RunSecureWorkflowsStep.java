/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.workflow.manager.tests.system.steps;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.workflow.helper.descriptor.CDSAuthenticationMethod;
import org.cagrid.workflow.helper.descriptor.ChannelProtection;
import org.cagrid.workflow.helper.descriptor.DeliveryPolicy;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OutputReady;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TLSInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.util.CredentialHandlingUtil;
import org.cagrid.workflow.manager.client.WorkflowManagerServiceClient;
import org.cagrid.workflow.manager.descriptor.WorkflowInputParameter;
import org.cagrid.workflow.manager.descriptor.WorkflowInputParameters;
import org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowOutputParameterTransportDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowOutputTransportDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowPortionDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowPortionsDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowStageDescriptor;
import org.cagrid.workflow.manager.instance.client.WorkflowManagerInstanceClient;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.container.ContainerException;

public class RunSecureWorkflowsStep extends RunUnsecureWorkflowsStep implements NotifyCallback {



	private static Log logger = LogFactory.getLog(RunSecureWorkflowsStep.class);


	protected EndpointReferenceType cdsEPR;
	protected GlobusCredential userCredential;

	final static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	final static String SOAPENCODING_NAMESPACE = "http://schemas.xmlsoap.org/soap/encoding/";


	public RunSecureWorkflowsStep(EndpointReference managerEPR, EndpointReferenceType cdsEPR, String container_base_url,
			GlobusCredential userCredential, String cdsURL) {
		super(managerEPR, container_base_url);


		this.userCredential = userCredential;

		try {
			this.cdsEPR = new EndpointReferenceType(new Address(cdsURL));
		} catch (MalformedURIException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void runStep() throws Throwable {

		System.out.println("---- BEGIN SECURE WORKFLOW TEST ----");


		try{

			String wfManagerURL = this.containerBaseURL + "/cagrid/WorkflowManagerService";
			final EndpointReferenceType manager_epr = new EndpointReferenceType(new Address(wfManagerURL));
			WorkflowManagerServiceClient wf_manager = new WorkflowManagerServiceClient(manager_epr);


			// User role: Delegate user credential to the Manager
			logger.info("Obtaining user credential");
			AttributedURI cdsAddress = this.cdsEPR.getAddress();
			String cdsURL = cdsAddress.toString();
			EndpointReferenceType delegatedCredentialProxy = CredentialHandlingUtil.delegateCredential(this.userCredential, wf_manager.getIdentity(),
					cdsURL , new ProxyLifetime(5,0,0), new ProxyLifetime(6,0,0), 3, 2);
			logger.info("Delegation done");


			/*** Testing arrays as services' input ***/

			/** simple type arrays **/
			System.out.println("[CreateTestSecureWorkflowsStep] Simple arrays as input");
			runSimpleArrayTest(wf_manager, delegatedCredentialProxy);
			System.out.println("[CreateTestSecureWorkflowsStep] OK"); // */

			/*System.out.println("[CreateTestSecureWorkflowsStep] Complex arrays as input");
			runComplexArrayTest(wf_manager, outputMatcherID, delegatedCredentialProxy);
			System.out.println("[CreateTestSecureWorkflowsStep] OK"); // */

			System.out.println("[CreateTestSecureWorkflowsStep] END Testing arrays"); // */


			/** BEGIN streaming test **/
			logger.info("[CreateTestSecureWorkflowsStep] BEGIN Testing streaming");

			// Streaming simple types
			logger.info("[CreateTestSecureWorkflowsStep] Streaming of simple-type arrays");
			runSimpleArrayStreaming(wf_manager, delegatedCredentialProxy);
			logger.info("[CreateTestSecureWorkflowsStep] OK");  // */



			/* Streaming complex types */
			System.out.print("[CreateTestSecureWorkflowsStep] Streaming of complex-type arrays");
			runComplexArrayStreaming(wf_manager, delegatedCredentialProxy);
			logger.info("[CreateTestSecureWorkflowsStep] OK");

			logger.info("[CreateTestSecureWorkflowsStep] END Testing streaming"); // */



			/** FAN IN AND FAN OUT TEST **/
			logger.info("[CreateTestSecureWorkflowsStep] BEGIN Testing fan in and fan out");
			runFaninFanOutTest(wf_manager, delegatedCredentialProxy);
			logger.info("[CreateTestSecureWorkflowsStep] END Testing fan in and fan out"); // */


			// Block until every stage reports either a FINISHED or an ERROR status
			this.waitForCompletion();

		}
		catch(Throwable t){
			logger.error(t.getMessage(), t);
			Assert.fail();
		}

		logger.info("---- END SECURE WORKFLOW TEST ----");

		return;
	}



	/**
	 * Instantiate the workflow that will test complex array streaming
	 *
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_manager Client of a running WorkflowHelper service
	 * @param delegatedCredentialProxy Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime
	 * @param delegationPath
	 * @param issuedCredentialPath
	 *
	 * */
	protected void runComplexArrayStreaming(WorkflowManagerServiceClient wf_manager, EndpointReferenceType delegatedCredentialProxy)throws RemoteException {

		logger.info("BEGIN");


		// Create security descriptor for the stages (in this case, all of them present the same security requirements)
		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(delegatedCredentialProxy);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);


		// Create the description of the single local workflow this workflow is divided in
		WorkflowPortionDescriptor workflowParts = new WorkflowPortionDescriptor();
		String workflowHelperServiceLocation = this.containerBaseURL + "/cagrid/WorkflowHelper";
		logger.info("WorkflowHelper is located at: "+ workflowHelperServiceLocation);
		workflowParts.setWorkflowHelperServiceLocation(workflowHelperServiceLocation);



		logger.info("Creating InstanceHelper descriptor");
		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor5 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		String workflowID = "WorkFlow5";
		workflowDescriptor5.setWorkflowID(workflowID);
		workflowParts.setInstanceHelperDesc(workflowDescriptor5);

		ArrayList<WorkflowStageDescriptor> stagesDescs = new ArrayList<WorkflowStageDescriptor>();
		ArrayList<WorkflowInputParameter> workflowInputs = new ArrayList<WorkflowInputParameter>();


		// BEGIN service 4
		logger.info("Describing service 4");
		WorkflowStageDescriptor currStageDesc = new WorkflowStageDescriptor();
		int currStageID = 4;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);



		logger.info("Building basic description");
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation4 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		java.lang.String acess_url = containerBaseURL+"/cagrid/Service4";
		operation4.setWorkflowID("GeorgeliusWorkFlow");
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "SecurePrintResultsRequest"));
		operation4.setServiceURL(acess_url);
		operation4.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation4);


		// Creating Descriptor of the InputMessage
		logger.info("Building input parameters descriptor");
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams4 = new InputParameterDescriptor[2];
		inputParams4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"), false);
		inputParams4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"), false);
		inputMessage4.setInputParam(inputParams4);
		currStageDesc.setInputsDescription(inputMessage4);
		// End InputMessage Descriptor

		// Setting output descriptor
		logger.info("Building output descriptor");
		OperationOutputTransportDescriptor outputDescriptor4 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor4 [] = new OperationOutputParameterTransportDescriptor[0];
		outputDescriptor4.setParamDescriptor(outParameterDescriptor4);


		// Setting second parameter
		logger.info("Setting value for the 2nd argument");
		workflowInputs.add(new WorkflowInputParameter(new InputParameter("complex type's streaming", 1), currStageID));
		currStageDesc.setOutputTransportDescriptor(outputDescriptor4);
		stagesDescs.add(currStageDesc);
		logger.info("Done Service 4");
		// END service 4



		// BEGIN CreateArrayService::getComplexArray
		logger.info("Describing CreateArrayService");
		currStageDesc = new WorkflowStageDescriptor();
		currStageID = 0;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);


		logger.info("Building stage basic description");
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation__ca = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		String access_url = containerBaseURL+"/cagrid/CreateArrayService";
		operation__ca.setWorkflowID("GeorgeliusWorkFlow");
		operation__ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "SecureGetComplexArrayRequest"));
		operation__ca.setServiceURL(access_url);
		operation__ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"));
		operation__ca.setOutputIsArray(true);
		operation__ca.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation__ca);


		// Creating Descriptor of the InputMessage
		logger.info("Building input parameters' descriptor");
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage__ca = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams__ca = new InputParameterDescriptor[0];
		inputMessage__ca.setInputParam(inputParams__ca);
		currStageDesc.setInputsDescription(inputMessage__ca);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the only service that will receive the output (ReceiveArrayService)
		logger.info("Building output descriptor");
		OperationOutputTransportDescriptor outputDescriptor__ca = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor__ca [] = new OperationOutputParameterTransportDescriptor[1];

		// First destination: ReceiveArrayService::ReceiveComplexArray
		logger.info("Adding destination for output");
		outParameterDescriptor__ca[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor__ca[0].setDeliveryPolicy(DeliveryPolicy.ROUNDROBIN);
		outParameterDescriptor__ca[0].setParamIndex(0);
		outParameterDescriptor__ca[0].setType(new QName(XSD_NAMESPACE ,"string"));
		outParameterDescriptor__ca[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "abc")});
		outParameterDescriptor__ca[0].setLocationQuery("/ns0:SecureGetComplexArrayResponse/abc:ComplexType/abc:message");
		outParameterDescriptor__ca[0].setDestinationGlobalUniqueIdentifier(4);
		//		outParameterDescriptor__ca[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient__4.getEndpointReference()});



		// takes the reference to ReceiveComplexArrayService
		outputDescriptor__ca.setParamDescriptor(outParameterDescriptor__ca);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor__ca);
		stagesDescs.add(currStageDesc);
		logger.info("Done CreateArrayService");
		// END CreateArrayService::getComplexArray


		// Store stages' description
		logger.info("Storing stages' descriptors");
		WorkflowStageDescriptor[] invocationHelperDescs = stagesDescs.toArray(new WorkflowStageDescriptor[0]);
		workflowParts.setInvocationHelperDescs(invocationHelperDescs);


		// Store workflow inputs' settings
		logger.info("Storing workflow input data");
		WorkflowInputParameters inputs = new WorkflowInputParameters();
		WorkflowInputParameter[] parameters = workflowInputs.toArray(new WorkflowInputParameter[0]);
		inputs.setParameter(parameters);

		// Store workflow outputs' description
		logger.info("Storing workflow output output description");
		WorkflowOutputTransportDescriptor outputDesc = new WorkflowOutputTransportDescriptor();
		WorkflowOutputParameterTransportDescriptor[] paramDescriptor = new WorkflowOutputParameterTransportDescriptor[0];
		outputDesc.setParamDescriptor(paramDescriptor);


		logger.info("Creating ManagerInstance");
		WorkflowManagerInstanceDescriptor managerInstanceDesc = new WorkflowManagerInstanceDescriptor();
		managerInstanceDesc.setInputs(inputs);
		managerInstanceDesc.setOutputDesc(outputDesc);
		managerInstanceDesc.setLocalWorkflows(new WorkflowPortionsDescriptor(new WorkflowPortionDescriptor[]{ workflowParts }));

		WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(managerInstanceDesc);
		WorkflowManagerInstanceClient managerInstanceClient = null;
		try {
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());

		} catch (MalformedURIException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();

		}

		this.managerInstances.add(managerInstanceRef.getEndpointReference());



		// Initialize synchronization variables so we can handle future notifications of execution end
		logger.info("Initializing synchronization variables");
		String clientID = managerInstanceClient.getEPRString();
		this.asynchronousStartCallbackReceived.put(clientID, false);
		Lock asynchronousCallbackLock = new ReentrantLock();
		this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
		this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition());

		// Start execution
		logger.info("Subscribing to receive notifications when workflow output is ready");
		try {
			managerInstanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);
		} catch (ContainerException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		}
		System.out.println("Starting workflow execution");
		managerInstanceClient.start();


		// Wait for workflow to finish
		System.out.println("Waiting for asynchronous callback to be received");
		asynchronousCallbackLock.lock();
		try {

			if(!this.asynchronousStartCallbackReceived.get(clientID)){

				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
				//				System.out.println("Blocking until signal is received on condition variable");
				currWorkflowCondition.await();  // Blocks until execution is finished
				//				System.out.println("Condition variable was signaled");
			}

		} catch (InterruptedException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} finally {
			asynchronousCallbackLock.unlock();
		}


		logger.info("END");	}



	/**
	 * Instantiate the workflow that will test simple array streaming
	 *
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_manager Client of a running WorkflowHelper service
	 * @param delegatedCredentialProxy Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime
	 * @param delegationPath
	 * @param issuedCredentialPath
	 *
	 * */
	protected void runSimpleArrayStreaming(WorkflowManagerServiceClient wf_manager, EndpointReferenceType delegatedCredentialProxy) throws RemoteException {



		// Create security descriptor for the stages (in this case, all of them present the same security requirements)
		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(delegatedCredentialProxy);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);



		// Create descriptor of the ManagerInstance
		WorkflowManagerInstanceDescriptor wfDesc = new WorkflowManagerInstanceDescriptor();
		WorkflowPortionDescriptor workflowPart = new WorkflowPortionDescriptor();
		String workflowHelperServiceLocation = this.containerBaseURL + "/cagrid/WorkflowHelper";
		workflowPart.setWorkflowHelperServiceLocation(workflowHelperServiceLocation);


		// Create descriptor for the only InstanceHelper of this workflow
		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor instanceDesc = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		instanceDesc.setWorkflowID("WorkFlow5");
		workflowPart.setInstanceHelperDesc(instanceDesc);


		ArrayList<WorkflowStageDescriptor> stagesDescs = new ArrayList<WorkflowStageDescriptor>();
		ArrayList<WorkflowInputParameter> inputData = new ArrayList<WorkflowInputParameter>();



		// BEGIN service 4
		WorkflowStageDescriptor currStageDesc = new WorkflowStageDescriptor();
		int currGUID = 4;
		currStageDesc.setGlobalUniqueIdentifier(currGUID);



		WorkflowInvocationHelperDescriptor operation_4 = new WorkflowInvocationHelperDescriptor();
		operation_4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "SecurePrintResultsRequest"));
		operation_4.setServiceURL(containerBaseURL+"/cagrid/Service4");
		operation_4.setWorkflowInvocationSecurityDescriptor(secDescriptor);
		// operation_4.setOutputType(); // Void output expected
		currStageDesc.setBasicDescription(operation_4);


		// Creating Descriptor of the InputMessage
		OperationInputMessageDescriptor inputMessage_4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam_4 = new InputParameterDescriptor[2];
		inputParam_4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"), false);
		inputParam_4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"), false);
		inputMessage_4.setInputParam(inputParam_4);
		currStageDesc.setInputsDescription(inputMessage_4);
		// End InputMessage Descriptor

		OperationOutputTransportDescriptor outputDescriptor_4 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor[] outParameterDescriptor_4 = new OperationOutputParameterTransportDescriptor[0];

		// Setting output descriptor
		outputDescriptor_4.setParamDescriptor(outParameterDescriptor_4);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor_4);


		// Setting second parameter
		WorkflowInputParameter inputParam = new WorkflowInputParameter(new InputParameter("simple type's streaming", 1), currGUID);
		inputData.add(inputParam);
		stagesDescs.add(currStageDesc);
		// END service 4




		// BEGIN service 2
		currStageDesc = new WorkflowStageDescriptor();
		currGUID = 2;
		currStageDesc.setGlobalUniqueIdentifier(currGUID);


		WorkflowInvocationHelperDescriptor operation2 = new WorkflowInvocationHelperDescriptor();
		operation2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "SecureCapitalizeRequest"));
		operation2.setServiceURL(containerBaseURL+"/cagrid/Service2");
		operation2.setOutputType(new QName(XSD_NAMESPACE, "string"));
		operation2.setWorkflowInvocationSecurityDescriptor(secDescriptor); // Set security requirements
		currStageDesc.setBasicDescription(operation2);


		// Creating Descriptor of the InputMessage
		OperationInputMessageDescriptor inputMessage__2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam__2 = new InputParameterDescriptor[1];
		inputParam__2[0] = new InputParameterDescriptor(new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"), false);
		inputMessage__2.setInputParam(inputParam__2 );
		currStageDesc.setInputsDescription(inputMessage__2);
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
		outParameterDescriptor__2[0].setDestinationGlobalUniqueIdentifier(4);


		outputDescriptor__2.setParamDescriptor(outParameterDescriptor__2);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor__2);
		stagesDescs.add(currStageDesc);
		// END service 2



		// BEGIN CreateArrayService
		currStageDesc = new WorkflowStageDescriptor();
		currGUID = 0;
		currStageDesc.setGlobalUniqueIdentifier(currGUID);


		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation__cas = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		operation__cas.setWorkflowID("GeorgeliusWorkFlow");
		operation__cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "SecureGetArrayRequest"));
		operation__cas.setServiceURL(containerBaseURL+"/cagrid/CreateArrayService");
		operation__cas.setOutputType(new QName(SOAPENCODING_NAMESPACE, "string"));
		operation__cas.setOutputIsArray(true);
		operation__cas.setWorkflowInvocationSecurityDescriptor(secDescriptor); // Set security requirements
		currStageDesc.setBasicDescription(operation__cas);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage__cas = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams__cas = new InputParameterDescriptor[0];
		inputMessage__cas.setInputParam(inputParams__cas);
		currStageDesc.setInputsDescription(inputMessage__cas);
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
		outParameterDescriptor_cs[0].setDestinationGlobalUniqueIdentifier(2);


		outputDescriptor_cs.setParamDescriptor(outParameterDescriptor_cs);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor_cs);
		stagesDescs.add(currStageDesc);
		// END CreateArrayService




		workflowPart.setInvocationHelperDescs(stagesDescs.toArray(new WorkflowStageDescriptor[0]));
		WorkflowInputParameters inputs = new WorkflowInputParameters();
		inputs.setParameter(inputData.toArray(new WorkflowInputParameter[0]));
		wfDesc.setInputs(inputs);
		wfDesc.setOutputDesc(new WorkflowOutputTransportDescriptor());
		wfDesc.setLocalWorkflows(new WorkflowPortionsDescriptor(new WorkflowPortionDescriptor[]{ workflowPart }));

		WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
		WorkflowManagerInstanceClient managerInstanceClient = null;
		try {
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());
		} catch (MalformedURIException e) {
			logger.error(e.getMessage(), e);
		}

		this.managerInstances.add(managerInstanceRef.getEndpointReference());

		// Initialize synchronization variables so we can handle future notifications of execution end
		String clientID = managerInstanceClient.getEPRString();
		this.asynchronousStartCallbackReceived.put(clientID, false);
		ReentrantLock asynchronousCallbackLock = new ReentrantLock();
		this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
		this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition());

		// Start execution
		logger.info("Subscribing to receive notifications when workflow output is ready"); //DEBUG
		try {
			managerInstanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);
		} catch (ContainerException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		}
		System.out.println("Starting workflow execution");
		managerInstanceClient.start();


		// Wait for asynchornous method callback
		asynchronousCallbackLock.lock();
		try {

			if(!this.asynchronousStartCallbackReceived.get(clientID)){

				System.out.println("Blocking until signal is received on condition variable");
				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
				currWorkflowCondition.await();  // Blocks until execution is finished
				System.out.println("Condition variable was signaled");
			}

		} catch (InterruptedException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} finally {
			asynchronousCallbackLock.unlock();
		}
	}



	/**
	 * Instantiate the workflow that will test complex array usual handling
	 *
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_manager Client of a running WorkflowHelper service
	 * @param delegatedCredentialProxy Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime
	 * @param delegationPath
	 * @param issuedCredentialPath
	 *
	 * */
	private void runComplexArrayTest(WorkflowManagerServiceClient wf_manager, Integer outputMatcherID, EndpointReferenceType delegatedCredentialProxy) throws RemoteException{



		// Create security descriptor for the stages (in this case, all of them present the same security requirements)
		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(delegatedCredentialProxy);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);


		// Create description of the ManagerInstance
		WorkflowManagerInstanceDescriptor wfDesc = new WorkflowManagerInstanceDescriptor();
		WorkflowPortionDescriptor workflowPart = new WorkflowPortionDescriptor();
		String workflowHelperServiceLocation = this.containerBaseURL + "/cagrid/WorkflowHelper";
		workflowPart.setWorkflowHelperServiceLocation(workflowHelperServiceLocation);


		ArrayList <WorkflowStageDescriptor> stagesDescs = new ArrayList<WorkflowStageDescriptor>();
		ArrayList<WorkflowInputParameter> inputParams = new ArrayList<WorkflowInputParameter>();
		ArrayList<WorkflowOutputParameterTransportDescriptor> outputParams = new ArrayList<WorkflowOutputParameterTransportDescriptor>();



		/** complex type arrays **/
		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor1 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		String workflowID = "WorkFlow1";
		workflowDescriptor1.setWorkflowID(workflowID);
		workflowPart.setInstanceHelperDesc(workflowDescriptor1);



		// BEGIN ReceiveArrayService::ReceiveComplexArray
		WorkflowStageDescriptor currStageDesc = new WorkflowStageDescriptor();
		int currStageID = 0;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);


		String access_url = containerBaseURL+"/cagrid/ReceiveArrayService";
		WorkflowInvocationHelperDescriptor operation2 = new WorkflowInvocationHelperDescriptor();
		operation2.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "SecureReceiveComplexArrayRequest"));
		operation2.setServiceURL(access_url);
		//operation2.setOutput(); // This service has no output
		operation2.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation2);



		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ras = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ras = new InputParameterDescriptor[3];
		inputParams_ras[0] = new InputParameterDescriptor(new QName("number"), new QName(XSD_NAMESPACE, "int"), false);
		inputParams_ras[1] = new InputParameterDescriptor(new QName("complexArray"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"), false);
		inputParams_ras[2] = new InputParameterDescriptor(new QName("booleanValue"), new QName(XSD_NAMESPACE, "boolean"), false);
		inputMessage_ras.setInputParam(inputParams_ras);
		currStageDesc.setInputsDescription(inputMessage_ras);
		// End InputMessage Descriptor

		// Creating an empty outputDescriptor
		OperationOutputTransportDescriptor outputDescriptor_ras = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ras [] = new OperationOutputParameterTransportDescriptor[0];

		// takes the reference to no service
		outputDescriptor_ras.setParamDescriptor(outParameterDescriptor_ras);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor_ras);


		// Set the values of its simple-type arguments
		inputParams.add(new WorkflowInputParameter(new InputParameter("999", 0), currStageID));
		inputParams.add(new WorkflowInputParameter(new InputParameter("true",2), currStageID));
		stagesDescs.add(currStageDesc);
		// END ReceiveArrayService::ReceiveComplexArray




		// BEGIN CreateArrayService::getComplexArray
		currStageDesc = new WorkflowStageDescriptor();
		currStageID++;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);


		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_ca = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		access_url = containerBaseURL+"/cagrid/CreateArrayService";
		operation_ca.setWorkflowID("GeorgeliusWorkFlow");
		operation_ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "SecureGetComplexArrayRequest"));
		operation_ca.setServiceURL(access_url);
		operation_ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"));
		operation_ca.setOutputIsArray(true);
		operation_ca.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation_ca);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ca = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ca = new InputParameterDescriptor[0];
		inputMessage_ca.setInputParam(inputParams_ca);
		currStageDesc.setInputsDescription(inputMessage_ca);

		// End InputMessage Descriptor

		// Creating the outputDescriptor of the only service that will receive the output (ReceiveArrayService)
		OperationOutputTransportDescriptor outputDescriptor_ca = new OperationOutputTransportDescriptor();
		int numDestination = this.validatorEnabled ? 2 : 1;
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ca [] = new OperationOutputParameterTransportDescriptor[numDestination];

		// First destination: ReceiveArrayService::ReceiveComplexArray
		outParameterDescriptor_ca[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_ca[0].setParamIndex(1);
		outParameterDescriptor_ca[0].setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType"));
		outParameterDescriptor_ca[0].setExpectedTypeIsArray(true);
		outParameterDescriptor_ca[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_ca[0].setLocationQuery("/ns0:SecureGetComplexArrayResponse");
		outParameterDescriptor_ca[0].setDestinationGlobalUniqueIdentifier(0);


		// Add one output to the workflow outputs
		WorkflowOutputParameterTransportDescriptor outputParamDesc = new WorkflowOutputParameterTransportDescriptor();
		outputParamDesc.setSourceGUID(currStageID);
		OperationOutputParameterTransportDescriptor paramDescription = new OperationOutputParameterTransportDescriptor();
		paramDescription.setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType"));
		paramDescription.setExpectedTypeIsArray(true);
		paramDescription.setLocationQuery("/ns0:SecureGetComplexArrayResponse");
		paramDescription.setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outputParamDesc.setParamDescription(paramDescription);
		outputParams.add(outputParamDesc);



		// takes the reference to ReceiveComplexArrayService
		outputDescriptor_ca.setParamDescriptor(outParameterDescriptor_ca);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor_ca);
		stagesDescs.add(currStageDesc);
		// END CreateArrayService::getComplexArray



		// Set workflow outputs
		WorkflowOutputTransportDescriptor outputDesc = new WorkflowOutputTransportDescriptor();
		WorkflowOutputParameterTransportDescriptor[] paramDescriptor = outputParams.toArray(new WorkflowOutputParameterTransportDescriptor[0]);
		outputDesc.setParamDescriptor(paramDescriptor);


		// Finish creating the workflow descriptor
		workflowPart.setInvocationHelperDescs(stagesDescs.toArray(new WorkflowStageDescriptor[0]));
		WorkflowInputParameters inputParameters = new WorkflowInputParameters(inputParams.toArray(new WorkflowInputParameter[0]));
		wfDesc.setInputs(inputParameters );
		wfDesc.setOutputDesc(outputDesc );
		wfDesc.setLocalWorkflows(new WorkflowPortionsDescriptor(new WorkflowPortionDescriptor[]{ workflowPart }));


		// Instantiate the workflow
		WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
		WorkflowManagerInstanceClient managerInstanceClient = null;
		try {
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());
			this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), managerInstanceClient, workflowID);
		} catch (MalformedURIException e) {
			logger.error(e.getMessage(), e);
		}

		this.managerInstances.add(managerInstanceRef.getEndpointReference());

		// Initialize synchronization variables so we can handle future notifications of execution end
		logger.info("Initializing synchronization variables");
		String clientID = managerInstanceClient.getEPRString();
		this.asynchronousStartCallbackReceived.put(clientID, false);
		ReentrantLock asynchronousCallbackLock = new ReentrantLock();
		this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
		this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition());

		// Start execution
		logger.info("Subscribing to receive notifications when workflow output is ready");
		try {
			managerInstanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);
		} catch (ContainerException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		}


		logger.info("Executing workflow");
		managerInstanceClient.start();  // Start workflow execution

		// Wait for asynchornous method callback
		logger.info("Waiting for workflow to finish");
		asynchronousCallbackLock.lock();
		try {

			if(!this.asynchronousStartCallbackReceived.get(clientID)){

				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
				currWorkflowCondition.await();  // Blocks until execution is finished
			}

		} catch (InterruptedException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} finally {
			asynchronousCallbackLock.unlock();
		}


		logger.info("Retrieving workflow outputs");
		String[] wf_outputs = managerInstanceClient.getOutputValues();  // Retrieve workflow outputs


		for(int i = 0; i < wf_outputs.length; i++){

			logger.info("Workflow output #"+ i +" is: "+ wf_outputs[i]);
		}


		//		managerInstanceClient.destroy();

		logger.info("END");
		return;
	}



	/**
	 * Instantiate the workflow that will test simple array usual handling
	 *
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_manager Client of a running WorkflowHelper service
	 * @param delegatedCredentialProxy Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime
	 * @param delegationPath
	 * @param issuedCredentialPath
	 *
	 * */
	private void runSimpleArrayTest(WorkflowManagerServiceClient wf_manager, EndpointReferenceType delegatedCredentialProxy) throws RemoteException{



		// Create security descriptor for the stages (in this case, all of them present the same security requirements)
		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(delegatedCredentialProxy);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);


		// Instantiate ManagerDescription
		WorkflowManagerInstanceDescriptor wfDesc = new WorkflowManagerInstanceDescriptor();

		// Describe the single local workflow this workflow is divided in
		WorkflowPortionDescriptor wfPart = new WorkflowPortionDescriptor();
		String workflowHelperServiceLocation = this.containerBaseURL + "/cagrid/WorkflowHelper";
		wfPart.setWorkflowHelperServiceLocation(workflowHelperServiceLocation);

		// Describe the InstanceHelper associated with the local workflow
		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor2 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		String workflowID = "WorkFlow2";
		workflowDescriptor2.setWorkflowID(workflowID);
		wfPart.setInstanceHelperDesc(workflowDescriptor2);


		ArrayList<WorkflowStageDescriptor> stagesDescs = new ArrayList<WorkflowStageDescriptor>();
		ArrayList<WorkflowInputParameter> inputParams = new ArrayList<WorkflowInputParameter>();
		ArrayList<WorkflowOutputParameterTransportDescriptor> outputParams = new ArrayList<WorkflowOutputParameterTransportDescriptor>();



		// BEGIN ReceiveArrayService::ReceiveArrayAndMore
		WorkflowStageDescriptor currStageDesc = new WorkflowStageDescriptor();
		int currStageID = 0;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);


		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_ram = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		operation_ram.setWorkflowID("GeorgeliusWorkFlow");
		operation_ram.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "SecureReceiveArrayAndMoreRequest"));
		operation_ram.setServiceURL(containerBaseURL+"/cagrid/ReceiveArrayService");
		//operation_ram.setOutputType(); // Service has no output
		operation_ram.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation_ram);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ram = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ram = new InputParameterDescriptor[3];
		inputParams_ram[0] = new InputParameterDescriptor(new QName("number"), new QName(XSD_NAMESPACE, "int"), false);
		inputParams_ram[1] = new InputParameterDescriptor(new QName("strArray"), new QName(XSD_NAMESPACE, "string"), true);
		inputParams_ram[2] = new InputParameterDescriptor(new QName("booleanValue"), new QName(XSD_NAMESPACE, "boolean"), false);
		inputMessage_ram.setInputParam(inputParams_ram);
		currStageDesc.setInputsDescription(inputMessage_ram);
		// End InputMessage Descriptor

		// Creating an empty outputDescriptor
		OperationOutputTransportDescriptor outputDescriptor_ram = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ram [] = new OperationOutputParameterTransportDescriptor[0];

		// takes the reference to no service
		outputDescriptor_ram.setParamDescriptor(outParameterDescriptor_ram);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor_ram);
		stagesDescs.add(currStageDesc);


		// Set the values of the two arguments of simple type
		inputParams.add(new WorkflowInputParameter(new InputParameter("999", 0), currStageID));
		inputParams.add(new WorkflowInputParameter(new InputParameter("true",2), currStageID));
		// END ReceiveArrayService::ReceiveArrayAndMore




		// BEGIN CreateArrayService
		currStageDesc = new WorkflowStageDescriptor();
		currStageID++;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);


		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_cas = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		String access_url = containerBaseURL+"/cagrid/CreateArrayService";
		operation_cas.setWorkflowID("GeorgeliusWorkFlow");
		operation_cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "SecureGetArrayRequest"));
		operation_cas.setServiceURL(access_url);
		operation_cas.setOutputType(new QName(SOAPENCODING_NAMESPACE, "string"));
		operation_cas.setOutputIsArray(true);
		operation_cas.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation_cas);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_cas = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_cas = new InputParameterDescriptor[0];
		inputMessage_cas.setInputParam(inputParams_cas);
		currStageDesc.setInputsDescription(inputMessage_cas);
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
		outParameterDescriptor_cas[0].setDestinationGlobalUniqueIdentifier(0);
		//		outParameterDescriptor_cas[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient_ram.getEndpointReference()});


		// Add one output to the worklow outputs' description
		WorkflowOutputParameterTransportDescriptor outputParam = new WorkflowOutputParameterTransportDescriptor();
		OperationOutputParameterTransportDescriptor paramDescription = new OperationOutputParameterTransportDescriptor();
		paramDescription.setLocationQuery("/ns0:SecureGetArrayResponse");
		paramDescription.setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		paramDescription.setType(new QName( SOAPENCODING_NAMESPACE ,"string"));
		paramDescription.setExpectedTypeIsArray(true);
		outputParam.setParamDescription(paramDescription );
		outputParam.setSourceGUID(currStageID);
		outputParams.add(outputParam);




		// takes the reference to ReceiveArrayService
		outputDescriptor_cas.setParamDescriptor(outParameterDescriptor_cas);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor_cas);

		stagesDescs.add(currStageDesc);
		// END CreateArrayService


		logger.info("Configuring workflow inputs and outputs");
		WorkflowInputParameters inputParameters = new WorkflowInputParameters(inputParams.toArray(new WorkflowInputParameter[0]));
		wfDesc.setInputs(inputParameters);
		WorkflowOutputTransportDescriptor outputDesc = new WorkflowOutputTransportDescriptor(outputParams.toArray(new WorkflowOutputParameterTransportDescriptor[0]));
		wfDesc.setOutputDesc(outputDesc);
		wfPart.setInvocationHelperDescs(stagesDescs.toArray(new WorkflowStageDescriptor[0]));
		wfDesc.setLocalWorkflows(new WorkflowPortionsDescriptor(new WorkflowPortionDescriptor[]{ wfPart }));
		WorkflowManagerInstanceReference instanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
		WorkflowManagerInstanceClient instanceClient = null;
		try {
			instanceClient = new WorkflowManagerInstanceClient(instanceRef.getEndpointReference());
			this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), instanceClient, workflowID);
		} catch (MalformedURIException e) {
			logger.error(e.getMessage(),e );
		} catch(Throwable t){
			logger.error(t.getMessage(), t);
		}


		this.managerInstances.add(instanceRef.getEndpointReference());


		// Initialize synchronization variables so we can handle future notifications of execution end
		String clientID = instanceClient.getEPRString();
		this.asynchronousStartCallbackReceived.put(clientID, false);
		ReentrantLock asynchronousCallbackLock = new ReentrantLock();
		this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
		this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition());

		// Start execution
		logger.info("Subscribing to receive notifications when workflow output is ready");
		try {
			instanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);
		} catch (ContainerException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		}
		System.out.println("Starting workflow execution");
		instanceClient.start();



		// Wait for asynchornous method callback
		logger.info("Waiting for workflow to finish");
		asynchronousCallbackLock.lock();
		try {

			if(!this.asynchronousStartCallbackReceived.get(clientID)){

				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
//				System.out.println("Blocking until signal is received on condition variable");
				currWorkflowCondition.await();  // Blocks until execution is finished
//				System.out.println("Condition variable was signaled");
			}

		} catch (InterruptedException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} finally {
			asynchronousCallbackLock.unlock();
		}

		logger.info("Retrieving workflow outputs");
		String[] outputs = instanceClient.getOutputValues();

		for(int i=0; i < outputs.length; i++){

			logger.info("Output #"+ i +" is "+ outputs[i]);
		}


		//		this.waitUntilCompletion();
		//		instanceClient.destroy();

		logger.info("END");
		return;
	}



	/**
	 * Instantiate the workflow that will test the mechanisms for receiving input from multiple stages and forwarding the outputs
	 * to multiple stages.
	 *
	 * @param manager_epr EndpointReference of the manager so each workflow stage can invoke 'setParameter' on it (not used)
	 * @param wf_manager Client of a running WorkflowHelper service
	 * @param delegatedCredentialProxy Credential delegated by the user to be used when calling the workflows' stages
	 * @param delegationLifetime Lifetime of the delegation of the credential to the workflow stages
	 * @param issuedCredentialLifetime
	 * @param delegationPath
	 * @param issuedCredentialPath
	 *
	 * */
	private void runFaninFanOutTest(WorkflowManagerServiceClient wf_manager, EndpointReferenceType delegatedCredentialProxy) throws RemoteException{


		// Create security descriptor for the stages (in this case, all of them present the same security requirements)
		CDSAuthenticationMethod cds_auth = new CDSAuthenticationMethod(delegatedCredentialProxy);
		TLSInvocationSecurityDescriptor tlsSecDesc = new TLSInvocationSecurityDescriptor(cds_auth , null, ChannelProtection.Privacy, null);
		WorkflowInvocationSecurityDescriptor secDescriptor = new WorkflowInvocationSecurityDescriptor(tlsSecDesc , null, null);


		// Describe the ManagerInstance and the single local workflow it is responsible for
		WorkflowManagerInstanceDescriptor wfDesc = new WorkflowManagerInstanceDescriptor();
		WorkflowPortionDescriptor wfPart = new WorkflowPortionDescriptor();
		String workflowHelperServiceLocation = this.containerBaseURL + "/cagrid/WorkflowHelper";
		wfPart.setWorkflowHelperServiceLocation(workflowHelperServiceLocation);


		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor3 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		String workflowID = "WorkFlow2";
		workflowDescriptor3.setWorkflowID(workflowID);
		wfPart.setInstanceHelperDesc(workflowDescriptor3);



		ArrayList<WorkflowStageDescriptor> stagesDescs = new ArrayList<WorkflowStageDescriptor>();
		ArrayList<WorkflowInputParameter> inputParams = new ArrayList<WorkflowInputParameter>();
		ArrayList<WorkflowOutputParameterTransportDescriptor> outputParams = new ArrayList<WorkflowOutputParameterTransportDescriptor>();



		// BEGIN service 4
		WorkflowStageDescriptor currStageDesc = new WorkflowStageDescriptor();
		int currStageID = 4;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);

		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation4 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		java.lang.String acess_url = containerBaseURL+"/cagrid/Service4";
		operation4.setWorkflowID("GeorgeliusWorkFlow");
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "SecurePrintResultsRequest"));
		operation4.setServiceURL(acess_url);
		operation4.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation4);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams4 = new InputParameterDescriptor[2];
		inputParams4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"), false);
		inputParams4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"), false);
		inputMessage4.setInputParam(inputParams4);
		currStageDesc.setInputsDescription(inputMessage4);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor4 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor4 [] = new OperationOutputParameterTransportDescriptor[0];
		QName namespaces[] = null;


		// takes the reference to no service
		outputDescriptor4.setParamDescriptor(outParameterDescriptor4);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor4);
		stagesDescs.add(currStageDesc);
		// END service 4




		// BEGIN service 2
		currStageDesc = new WorkflowStageDescriptor();
		currStageID = 2;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);

		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_2 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/cagrid/Service2";
		operation_2.setWorkflowID("GeorgeliusWorkFlow");
		operation_2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "SecureCapitalizeRequest"));
		operation_2.setServiceURL(acess_url);
		operation_2.setOutputType(new QName(XSD_NAMESPACE, "string"));
		operation_2.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation_2);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_2 = new InputParameterDescriptor[1];
		inputParams_2[0] = new InputParameterDescriptor(new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"), false);
		inputMessage_2.setInputParam(inputParams_2);
		currStageDesc.setInputsDescription(inputMessage_2);
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
		outParameterDescriptor2[0].setDestinationGlobalUniqueIdentifier(4);


		// Add one output to the workflow outputs
		WorkflowOutputParameterTransportDescriptor outputParam = new WorkflowOutputParameterTransportDescriptor();
		OperationOutputParameterTransportDescriptor paramDescription = new OperationOutputParameterTransportDescriptor();
		paramDescription.setLocationQuery("/ns0:SecureCapitalizeResponse");
		paramDescription.setQueryNamespaces(new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")});
		paramDescription.setType(new QName("string"));
		outputParam.setParamDescription(paramDescription);
		outputParam.setSourceGUID(currStageID);
		outputParams.add(outputParam);


		outputDescriptor2.setParamDescriptor(outParameterDescriptor2);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor2);
		stagesDescs.add(currStageDesc);
		// END service 2



		// BEGIN service 3
		currStageDesc = new WorkflowStageDescriptor();
		currStageID = 3;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);

		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation3 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/cagrid/Service3";

		// This is the greek version of my name...
		operation3.setWorkflowID("GeorgeliusWorkFlow");
		operation3.setOperationQName(new QName("http://service3.introduce.cagrid.org/Service3", "SecureGenerateXRequest"));
		operation3.setServiceURL(acess_url);
		operation3.setOutputType(new QName(XSD_NAMESPACE, "string"));
		operation3.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation3);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage3 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams3 = new InputParameterDescriptor[1];
		inputParams3[0] = new InputParameterDescriptor(new QName("str_length"), new QName(XSD_NAMESPACE, "int"), false);
		inputMessage3.setInputParam(inputParams3);
		currStageDesc.setInputsDescription(inputMessage3);
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
		outParameterDescriptor3[0].setDestinationGlobalUniqueIdentifier(4);


		// Add one output to the workflow outputs
		outputParam = new WorkflowOutputParameterTransportDescriptor();
		paramDescription = new OperationOutputParameterTransportDescriptor();
		paramDescription.setLocationQuery("/ns0:SecureGenerateXResponse");
		paramDescription.setQueryNamespaces(new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service3.introduce.cagrid.org/Service3", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")});
		paramDescription.setType(new QName(XSD_NAMESPACE, "string"));
		outputParam.setParamDescription(paramDescription);
		outputParam.setSourceGUID(currStageID);
		outputParams.add(outputParam);


		outputDescriptor3.setParamDescriptor(outParameterDescriptor3);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor3);
		stagesDescs.add(currStageDesc);
		// END service 3



		// BEGIN service 5
		currStageDesc = new WorkflowStageDescriptor();
		currStageID = 5;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);



		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation5 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		acess_url = containerBaseURL+"/cagrid/Service5";
		operation5.setWorkflowID("GeorgeliusWorkFlow");
		operation5.setOperationQName(new QName("http://service5.introduce.cagrid.org/Service5" , "SecureCheckStringAndItsLengthRequest"));
		operation5.setServiceURL(acess_url);
		operation5.setOutputType(new QName(XSD_NAMESPACE, "boolean"));
		operation5.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation5);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage5 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams5 = new InputParameterDescriptor[1];
		inputParams5[0] = new InputParameterDescriptor(new QName("http://service1.workflow.cagrid.org/Service1", "stringAndItsLenght"),
				new QName("http://service1.workflow.cagrid.org/Service1", "StringAndItsLength"), false);
		inputMessage5.setInputParam(inputParams5);
		currStageDesc.setInputsDescription(inputMessage5);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor5 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor5 [] = new OperationOutputParameterTransportDescriptor[0];


		outputDescriptor5.setParamDescriptor(outParameterDescriptor5);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor5);
		stagesDescs.add(currStageDesc);
		// END service 5



		// BEGIN service 1
		currStageDesc = new WorkflowStageDescriptor();
		currStageID = 1;
		currStageDesc.setGlobalUniqueIdentifier(currStageID);



		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation1 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/cagrid/Service1";
		operation1.setWorkflowID("GeorgeliusWorkFlow");
		operation1.setOperationQName(new QName("http://service1.introduce.cagrid.org/Service1", "SecureGenerateDataRequest"));
		operation1.setServiceURL(acess_url);
		operation1.setOutputType(new QName(XSD_NAMESPACE, "string"));
		operation1.setWorkflowInvocationSecurityDescriptor(secDescriptor);  // Set security requirements
		currStageDesc.setBasicDescription(operation1);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage1 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams1 = new InputParameterDescriptor[1];
		inputParams1[0] = new InputParameterDescriptor(new QName("info"), new QName(XSD_NAMESPACE, "string"), false);
		inputMessage1.setInputParam(inputParams1);
		currStageDesc.setInputsDescription(inputMessage1);
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
		outParameterDescriptor1[0].setDestinationGlobalUniqueIdentifier(2);
		//		outParameterDescriptor1[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient2.getEndpointReference()});

		// Creating the outputDescriptor of the second filter (Service3)
		outParameterDescriptor1[1] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor1[1].setParamIndex(0);
		outParameterDescriptor1[1].setType(new QName("int"));
		outParameterDescriptor1[1].setQueryNamespaces(namespaces);
		outParameterDescriptor1[1].setLocationQuery("/ns0:SecureGenerateDataResponse/ns1:StringAndItsLenght/ns1:length");
		outParameterDescriptor1[1].setDestinationGlobalUniqueIdentifier(3);
		//		outParameterDescriptor1[1].setDestinationEPR(new EndpointReferenceType[]{serviceClient3.getEndpointReference()});

		// Creating the outputDescriptor of the 3rd filter (Service5)
		outParameterDescriptor1[2] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor1[2].setParamIndex(0);
		outParameterDescriptor1[2].setType(new QName("http://service1.workflow.cagrid.org/Service1","StringAndItsLenght"));
		outParameterDescriptor1[2].setQueryNamespaces(namespaces);
		outParameterDescriptor1[2].setLocationQuery("/ns0:SecureGenerateDataResponse/ns1:StringAndItsLenght");
		outParameterDescriptor1[2].setDestinationGlobalUniqueIdentifier(5);
		//		outParameterDescriptor1[2].setDestinationEPR(new EndpointReferenceType[]{serviceClient5.getEndpointReference()});



		// Add one output to the workflow outputs
		outputParam = new WorkflowOutputParameterTransportDescriptor();
		outputParam.setSourceGUID(currStageID);
		paramDescription = new OperationOutputParameterTransportDescriptor();
		paramDescription.setLocationQuery("/ns0:SecureGenerateDataResponse/ns1:StringAndItsLenght");
		paramDescription.setQueryNamespaces(namespaces);
		paramDescription.setType(new QName("http://service1.workflow.cagrid.org/Service1","StringAndItsLenght"));
		outputParam.setParamDescription(paramDescription);
		outputParams.add(outputParam);


		// parameters are all set at this point
		outputDescriptor1.setParamDescriptor(outParameterDescriptor1);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor1);
		stagesDescs.add(currStageDesc);


		// set the only one parameter of this service.
		// now it have to run and set one Parameter of the service4
		String workflow_input = "george teadoro gordao que falou";
		logger.info("Setting input for service 1: '"+workflow_input+"'");
		InputParameter inputService1 = new InputParameter(workflow_input, 0);
		inputParams.add(new WorkflowInputParameter(inputService1, currStageID));
		// END service 1



		// Build the workflow output descriptor
		logger.info("Setting workflow outputs");
		WorkflowOutputTransportDescriptor outputDesc = new WorkflowOutputTransportDescriptor();
		outputDesc.setParamDescriptor(outputParams.toArray(new WorkflowOutputParameterTransportDescriptor[0]));


		wfPart.setInvocationHelperDescs(stagesDescs.toArray(new WorkflowStageDescriptor[0]));
		WorkflowInputParameters inputParameters = new WorkflowInputParameters(inputParams.toArray(new WorkflowInputParameter[0]));
		wfDesc.setInputs(inputParameters);
		wfDesc.setOutputDesc(outputDesc);
		wfDesc.setLocalWorkflows(new WorkflowPortionsDescriptor(new WorkflowPortionDescriptor[]{ wfPart }));

		logger.info("Creating Manager Instance");
		WorkflowManagerInstanceReference instanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
		WorkflowManagerInstanceClient instanceClient = null;
		try {
			instanceClient = new WorkflowManagerInstanceClient(instanceRef.getEndpointReference());
		} catch (MalformedURIException e) {
			logger.error(e.getMessage(), e);
		}


		this.managerInstances.add(instanceRef.getEndpointReference());

		this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), instanceClient, workflowID);

		// Initialize synchronization variables so we can handle future notifications of execution end
		logger.info("Initializing synchronization variables");
		String clientID = instanceClient.getEPRString();
		this.asynchronousStartCallbackReceived.put(clientID, false);
		ReentrantLock asynchronousCallbackLock = new ReentrantLock();
		this.asynchronousStartLock.put(clientID, asynchronousCallbackLock);
		this.asynchronousStartCondition.put(clientID, asynchronousCallbackLock.newCondition());

		// Start execution
		logger.info("Subscribing to receive notifications when workflow output is ready");
		try {
			instanceClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);
		} catch (ContainerException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			logger.error(e1.getMessage(), e1);
			e1.printStackTrace();
		}
		System.out.println("Starting workflow execution");
		instanceClient.start();


		// Wait for asynchornous method callback
		System.out.println("Waiting for workflow to finish");
		asynchronousCallbackLock.lock();
		try {

			if(!this.asynchronousStartCallbackReceived.get(clientID)){

				Condition currWorkflowCondition = this.asynchronousStartCondition.get(clientID);
//				System.out.println("Blocking until signal is received on condition variable"); //DEBUG
				currWorkflowCondition.await();  // Blocks until execution is finished
//				System.out.println("Condition variable was signaled"); //DEBUG
			}

		} catch (InterruptedException e) {
			logger.error(e.getMessage() ,e);
			e.printStackTrace();
		} finally {
			asynchronousCallbackLock.unlock();
		}

		logger.info("Retrieving workflow outputs");
		String[] outputs = instanceClient.getOutputValues();
		for(int i=0; i < outputs.length; i++){


			logger.info("Output #"+ i +" is: "+ outputs[i]);
		}
		return;
	}


	public void deliver(List arg0, EndpointReferenceType arg1, Object arg2) {

		org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType changeMessage = ((org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType) arg2)
		.getResourcePropertyValueChangeNotification();

		MessageElement actual_property = changeMessage.getNewValue().get_any()[0];
		QName message_qname = actual_property.getQName();
		boolean isTimestampedStatusChange = message_qname.equals(TimestampedStatus.getTypeDesc().getXmlType());
		boolean isOutputReady = message_qname.equals(OutputReady.getTypeDesc().getXmlType());
		String stageKey = null;
		try {
			stageKey = new WorkflowManagerInstanceClient(arg1).getEPRString();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			e1.printStackTrace();
		}


		logger.trace("[RunSecureWorkflowsStep] Received message of type "+ message_qname.getLocalPart() +" from "+ stageKey);


		// Handle status change notifications
		if(isTimestampedStatusChange){
			TimestampedStatus status = null;;
			try {
				status = (TimestampedStatus) actual_property.getValueAsType(message_qname, TimestampedStatus.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


			logger.info("[RunSecureWorkflowsStep] Received new status value: "+ status.getStatus().toString() + ':' + status.getTimestamp());

			this.isFinishedKey.lock();
			try{

				boolean statusActuallyChanged = false;
				if( this.stageStatus.containsKey(stageKey) ){


					TimestampedStatus curr_status = this.stageStatus.get(stageKey);
					statusActuallyChanged = ( curr_status.getTimestamp() < status.getTimestamp() );

					if(statusActuallyChanged){

						this.stageStatus.remove(stageKey);
						this.stageStatus.put(stageKey, status);
					}

				}
				else logger.warn("[RunSecureWorkflowsStep] Unrecognized stage notified status change: "+ stageKey);


				if( statusActuallyChanged && (status.getStatus().equals(Status.FINISHED) || status.getStatus().equals(Status.ERROR)) ){


					this.isFinished  = this.hasFinished();

					if(this.isFinished){

						this.isFinishedCondition.signalAll();
						Assert.assertFalse(this.stageStatus.containsValue(Status.ERROR));

						// Destroy ManagerInstance resources
						/*Iterator<EndpointReferenceType> instances_iter = this.managerInstances.iterator();
						while( instances_iter.hasNext() ){

							EndpointReferenceType curr_managerInstance = instances_iter.next();
							WorkflowManagerInstanceClient curr_client;
							try {
								curr_client = new WorkflowManagerInstanceClient(curr_managerInstance);
								curr_client.destroy();

							} catch (MalformedURIException e) {
								logger.error(e.getMessage(), e);
								e.printStackTrace();
							} catch (RemoteException e) {
								logger.error(e.getMessage(), e);
								e.printStackTrace();
							}
						} // */
					}
				}
			}
			finally {
				this.isFinishedKey.unlock();
			}
		}

		// Handle callbacks received from just finished workflows
		else if(isOutputReady){

			stageKey = null;
			try {
				stageKey = new WorkflowManagerInstanceClient(arg1).getEPRString();

				if(stageKey == null){
					logger.error("[RunSecureWorkflowsStep::deliver] Unable to retrieve stageKey");
				}

			} catch (RemoteException e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage(), e1);
			} catch (MalformedURIException e1) {
				e1.printStackTrace();
				logger.error(e1.getMessage(), e1);
			}


			OutputReady callback = null;
			try {
				callback = (OutputReady) actual_property.getValueAsType(message_qname, OutputReady.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Boolean notificationValue = new Boolean(callback.equals(OutputReady.TRUE));


			// Store/Update the value stored internally for the current InvocationHelper
			Lock mutex = this.asynchronousStartLock.get(stageKey);
			mutex.lock();
			try {


				this.asynchronousStartCallbackReceived.put(stageKey, notificationValue);

				// If the execution is finished, report the user
				boolean allCallbacksReceived = !this.asynchronousStartCallbackReceived.containsValue(Boolean.FALSE);
				if(allCallbacksReceived){

					System.out.println("[RunSecureWorkflowsStep::deliver] All callbacks received. Execution is finished.");
					Condition workflowFinished = this.asynchronousStartCondition.get(stageKey);
					workflowFinished.signalAll();
				}


			} finally {
				mutex.unlock();
			}
		}
		else{
			logger.error("[RunSecureWorkflowsStep::deliver] Callback received from an unknown stage: "+ stageKey);
		}

	}

}

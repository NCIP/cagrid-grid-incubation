package org.cagrid.workflow.manager.tests.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.descriptor.DeliveryPolicy;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.manager.client.WorkflowManagerServiceClient;
import org.cagrid.workflow.manager.descriptor.WorkflowInputParameter;
import org.cagrid.workflow.manager.descriptor.WorkflowInputParameters;
import org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowOutputParameterTransportDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowOutputTransportDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowPortionDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowStageDescriptor;
import org.cagrid.workflow.manager.instance.client.WorkflowManagerInstanceClient;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.globus.wsrf.NotifyCallback;



public class RunUnsecureWorkflowsStep extends Step implements NotifyCallback {

	
	
	protected Log logger = LogFactory.getLog(RunUnsecureWorkflowsStep.class);
	

	protected EndpointReferenceType manager_epr = null;
	protected String containerBaseURL = null;

	// Synchronizes the access to variable 'isFinished' 
	protected Lock isFinishedKey = new ReentrantLock();
	protected Condition isFinishedCondition = isFinishedKey.newCondition();
	protected Map<String, TimestampedStatus> stageStatus = new HashMap<String, TimestampedStatus>() ;


	// Store the operation name for each service subscribed for notification 
	protected Map<String, String> EPR2OperationName = new HashMap<String, String>();

	protected boolean isFinished = false;
	protected List<EndpointReferenceType> managerInstances = new ArrayList<EndpointReferenceType>();


	final static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	final static String SOAPENCODING_NAMESPACE = "http://schemas.xmlsoap.org/soap/encoding/";

	protected final boolean validatorEnabled = false;  // Enable/Disable the output matcher. Should be true when not debugging


	private int currParamIndex = 0;



	public RunUnsecureWorkflowsStep(EndpointReferenceType manager_epr, String containerBaseURL) {
		super();
		this.manager_epr = manager_epr;
		this.containerBaseURL  = containerBaseURL;
	}


	@Override
	public void runStep() throws Throwable {



		/** Instantiate each of the workflows: Fan in-Fan out, simple-type array forwarding, complex-type array forwarding,
		 * simple-type array streaming, complex-type array streaming  
		 * */
		logger.info("-x-x-x- BEGIN NON-SECURE WORKFLOWS TESTS -x-x-x-");
		if( this.validatorEnabled ) logger.info("Running the output matcher");
		else logger.info("Not running the output matcher");


		WorkflowManagerServiceClient wf_manager = new WorkflowManagerServiceClient(this.manager_epr); 

		
		try {

			/*** Service that will gather all the output and match against the expected ones ***/
//			int outputMatcherID = this.validatorEnabled ? runOuputMatcher(wf_manager) : Integer.MAX_VALUE;



			/*** Testing arrays as services' input ***/

			/** simple type arrays **/
			logger.info("Simple arrays as input");
			runSimpleArrayTest(wf_manager);
			logger.info("OK"); // */

			logger.info("Complex arrays as input");
			runComplexArrayTest(wf_manager);
			logger.info("OK"); 

			logger.info("END Testing arrays"); // */






			/** BEGIN streaming test **/
			logger.info("BEGIN Testing streaming");

			// Streaming simple types 
			logger.info("Streaming of simple-type arrays");
			runSimpleArrayStreaming(wf_manager);
			logger.info("OK");  // */



			/* Streaming complex types */
			logger.info("Streaming of complex-type arrays");
			runComplexArrayStreaming(wf_manager);
			logger.info("OK");

			logger.info("END Testing streaming"); // */

			/** FAN IN AND FAN OUT TEST **/
			logger.info("BEGIN Testing fan in and fan out"); 
			runFaninFanOutTest(wf_manager, -1);
			logger.info("END Testing fan in and fan out"); // */

			// Block until every stage reports either a FINISHED or an ERROR status
			this.waitForCompletion();


		} catch(Throwable t) {
			t.printStackTrace();
			Assert.fail();
		}


		logger.info("-x-x-x- END NON-SECURE WORKFLOWS TESTS -x-x-x-");
		return;
	}



	protected int runOuputMatcher(WorkflowManagerServiceClient wf_manager) 
	throws RemoteException {


		WorkflowManagerInstanceDescriptor workflowDesc = new WorkflowManagerInstanceDescriptor();


		WorkflowPortionDescriptor localWfDesc = new WorkflowPortionDescriptor();
		EndpointReferenceType managerEPR = wf_manager.getEndpointReference();
//		String workflowHelperServiceLocation;
//		localWfDesc.setWorkflowHelperServiceLocation(workflowHelperServiceLocation);
//		localWfDesc.setInvocationHelperDescs(invocationHelperDescs);


		WorkflowInstanceHelperDescriptor validatorInstanceDesc = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		String workflowID = "Validator";
		validatorInstanceDesc.setWorkflowID(workflowID);
		int stageID = Integer.MAX_VALUE;
		localWfDesc.setInstanceHelperDesc(validatorInstanceDesc);


		String outputMatcherURI =  "http://validateoutputsservice.test.workflow.cagrid.org/ValidateOutputsService";
		WorkflowInvocationHelperDescriptor validatorInvocationDesc = new WorkflowInvocationHelperDescriptor();
		validatorInvocationDesc.setOperationQName(
				new QName(outputMatcherURI, "ValidateTestOutputRequest"));
		validatorInvocationDesc.setServiceURL(containerBaseURL + "/wsrf/services/cagrid/ValidateOutputsService");



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



		// Configure outputs: it has none
		OperationOutputTransportDescriptor validatorOutput = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor[] paramDescriptor = new OperationOutputParameterTransportDescriptor[0];
		validatorOutput.setParamDescriptor(paramDescriptor );


		// Set static parameters
//		validatorInvocation1.setParameter(new InputParameter("999", 0));
//		validatorInvocation1.setParameter(new InputParameter("true", 2));
//		validatorInvocation1.setParameter(new InputParameter("999", 3));
//		validatorInvocation1.setParameter(new InputParameter("true", 5)); // */


//		workflowDesc.setInputs(inputs);
//		workflowDesc.setOutputDesc(outputDesc);
//		WorkflowPortionDescriptor[] workflowParts;
//		workflowDesc.setWorkflowParts(workflowParts);


		return stageID;
	}


	protected void runComplexArrayStreaming(WorkflowManagerServiceClient wf_manager)throws RemoteException {

		
		logger.info("BEGIN");
		
		WorkflowPortionDescriptor workflowParts = new WorkflowPortionDescriptor();
		String workflowHelperServiceLocation = this.containerBaseURL + "cagrid/WorkflowHelper";
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

		java.lang.String acess_url = containerBaseURL+"cagrid/Service4";
		operation4.setWorkflowID("GeorgeliusWorkFlow");
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation4.setServiceURL(acess_url);
		currStageDesc.setBasicDescription(operation4);
		

		// Creating Descriptor of the InputMessage
		logger.info("Building input parameters descriptor");
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams4 = new InputParameterDescriptor[2];
		inputParams4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"));
		inputParams4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"));
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
		String access_url = containerBaseURL+"cagrid/CreateArrayService";
		operation__ca.setWorkflowID("GeorgeliusWorkFlow");
		operation__ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetComplexArrayRequest"));
		operation__ca.setServiceURL(access_url);
		operation__ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType[]"));
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
		outParameterDescriptor__ca[0].setLocationQuery("/ns0:GetComplexArrayResponse/abc:ComplexType/abc:message");
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
		inputs.setParameters(parameters);
		
		// Store workflow outputs' description
		logger.info("Storing workflow output output description");
		WorkflowOutputTransportDescriptor outputDesc = new WorkflowOutputTransportDescriptor();
		WorkflowOutputParameterTransportDescriptor[] paramDescriptor = new WorkflowOutputParameterTransportDescriptor[0];
		outputDesc.setParamDescriptor(paramDescriptor);
		
		
		logger.info("Creating ManagerInstance");
		WorkflowManagerInstanceDescriptor managerInstanceDesc = new WorkflowManagerInstanceDescriptor();
		managerInstanceDesc.setInputs(inputs);
		managerInstanceDesc.setOutputDesc(outputDesc);
		managerInstanceDesc.setWorkflowParts(new WorkflowPortionDescriptor[]{ workflowParts });

		WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(managerInstanceDesc);
		WorkflowManagerInstanceClient managerInstanceClient = null;
		try {
			managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());

		} catch (MalformedURIException e) {
			e.printStackTrace();
			
		} 
		
		this.managerInstances.add(managerInstanceRef.getEndpointReference());
		
		logger.info("Starting workflow execution");
		managerInstanceClient.start();
		
		logger.info("END");
	}


	protected void runSimpleArrayStreaming(WorkflowManagerServiceClient  wf_manager) throws RemoteException, MalformedURIException {

		
		WorkflowManagerInstanceDescriptor wfDesc = new WorkflowManagerInstanceDescriptor();
		WorkflowPortionDescriptor workflowPart = new WorkflowPortionDescriptor();
		String workflowHelperServiceLocation = this.containerBaseURL + "cagrid/WorkflowHelper";
		workflowPart.setWorkflowHelperServiceLocation(workflowHelperServiceLocation);
		
		
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
		operation_4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation_4.setServiceURL(containerBaseURL+"cagrid/Service4");
		// operation_4.setOutputType(); // Void output expected
		currStageDesc.setBasicDescription(operation_4);


		// Creating Descriptor of the InputMessage
		OperationInputMessageDescriptor inputMessage_4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam_4 = new InputParameterDescriptor[2];
		inputParam_4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"));
		inputParam_4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"));
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
		operation2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "CapitalizeRequest"));
		operation2.setServiceURL(containerBaseURL+"cagrid/Service2");
		operation2.setOutputType(new QName(XSD_NAMESPACE, "string"));
		currStageDesc.setBasicDescription(operation2);


		// Creating Descriptor of the InputMessage
		OperationInputMessageDescriptor inputMessage__2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam__2 = new InputParameterDescriptor[1];
		inputParam__2[0] = new InputParameterDescriptor(new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"));
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
		outParameterDescriptor__2[0].setLocationQuery("/ns0:CapitalizeResponse");
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
		operation__cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetArrayRequest"));
		operation__cas.setServiceURL(containerBaseURL+"cagrid/CreateArrayService");
		operation__cas.setOutputType(new QName(SOAPENCODING_NAMESPACE, "string[]"));
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
		outParameterDescriptor_cs[0].setLocationQuery("/ns0:GetArrayResponse");
		outParameterDescriptor_cs[0].setDestinationGlobalUniqueIdentifier(2);


		outputDescriptor_cs.setParamDescriptor(outParameterDescriptor_cs);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor_cs);
		stagesDescs.add(currStageDesc);
		// END CreateArrayService
		
		
		
		
		workflowPart.setInvocationHelperDescs(stagesDescs.toArray(new WorkflowStageDescriptor[0]));
		WorkflowInputParameters inputs = new WorkflowInputParameters();
		inputs.setParameters(inputData.toArray(new WorkflowInputParameter[0]));
		wfDesc.setInputs(inputs);
		wfDesc.setOutputDesc(new WorkflowOutputTransportDescriptor());
		wfDesc.setWorkflowParts(new WorkflowPortionDescriptor[]{ workflowPart });

		WorkflowManagerInstanceReference managerInstanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
		WorkflowManagerInstanceClient managerInstanceClient = new WorkflowManagerInstanceClient(managerInstanceRef.getEndpointReference());
		
		this.managerInstances.add(managerInstanceRef.getEndpointReference());
		
		managerInstanceClient.start();
//		managerInstanceClient.destroy();
		
	}


	protected void runComplexArrayTest(WorkflowManagerServiceClient wf_manager) 
		throws RemoteException{

		
		WorkflowManagerInstanceDescriptor wfDesc = new WorkflowManagerInstanceDescriptor();
		WorkflowPortionDescriptor workflowPart = new WorkflowPortionDescriptor();
		String workflowHelperServiceLocation = this.containerBaseURL + "cagrid/WorkflowHelper";
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
		
		
		String access_url = containerBaseURL+"cagrid/ReceiveArrayService";
		WorkflowInvocationHelperDescriptor operation2 = new WorkflowInvocationHelperDescriptor();
		operation2.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "ReceiveComplexArrayRequest"));
		operation2.setServiceURL(access_url);
		//operation2.setOutput(); // This service has no output
		currStageDesc.setBasicDescription(operation2);
		


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ras = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ras = new InputParameterDescriptor[3];
		inputParams_ras[0] = new InputParameterDescriptor(new QName("number"), new QName(XSD_NAMESPACE, "int"));
		inputParams_ras[1] = new InputParameterDescriptor(new QName("complexArray"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType[]"));
		inputParams_ras[2] = new InputParameterDescriptor(new QName("booleanValue"), new QName(XSD_NAMESPACE, "boolean"));
		inputMessage_ras.setInputParam(inputParams_ras);
		currStageDesc.setInputsDescription(inputMessage_ras);
		// End InputMessage Descriptor

		// Creating an empty outputDescriptor
		OperationOutputTransportDescriptor outputDescriptor_ras = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ras [] = new OperationOutputParameterTransportDescriptor[0];

		// takes the reference to no service
		outputDescriptor_ras.setParamDescriptor(outParameterDescriptor_ras);
		currStageDesc.setOutputTransportDescriptor(outputDescriptor_ras);
		


		//System.out.println("Setting params"); //DEBUG

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
		access_url = containerBaseURL+"cagrid/CreateArrayService";
		operation_ca.setWorkflowID("GeorgeliusWorkFlow");
		operation_ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetComplexArrayRequest"));
		operation_ca.setServiceURL(access_url);
		operation_ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType[]"));
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
		outParameterDescriptor_ca[0].setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType[]"));
		outParameterDescriptor_ca[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_ca[0].setLocationQuery("/ns0:GetComplexArrayResponse");
		outParameterDescriptor_ca[0].setDestinationGlobalUniqueIdentifier(0);



		// Second destination: Output matcher
		if( validatorEnabled ){

			//System.out.println("Setting 2nd param in the output matcher: "+ outputMatcherEPR); //DEBUG

			outParameterDescriptor_ca[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor_ca[1].setParamIndex(1); // Setting 2nd argument in the output matcher 
			outParameterDescriptor_ca[1].setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType[]"));
			outParameterDescriptor_ca[1].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
					new QName(XSD_NAMESPACE,"xsd")});
			outParameterDescriptor_ca[1].setLocationQuery("/ns0:GetComplexArrayResponse");
//			outParameterDescriptor_ca[1].setDestinationEPR(new EndpointReferenceType[]{ outputMatcherID });
		}


		// Add one output to the workflow outputs
		WorkflowOutputParameterTransportDescriptor outputParamDesc = new WorkflowOutputParameterTransportDescriptor();
		outputParamDesc.setSourceGUID(currStageID);
		OperationOutputParameterTransportDescriptor paramDescription = new OperationOutputParameterTransportDescriptor();
		paramDescription.setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType[]"));
		paramDescription.setLocationQuery("/ns0:GetComplexArrayResponse");
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
		wfDesc.setWorkflowParts(new WorkflowPortionDescriptor[]{ workflowPart });
		
		
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
		
		logger.info("Executing workflow");
		managerInstanceClient.start();  // Start workflow execution
		logger.info("Retrieving workflow outputs");
		String[] wf_outputs = managerInstanceClient.getOutputValues();  // Retrieve workflow outputs
		
		
		for(int i = 0; i < wf_outputs.length; i++){
			
			logger.info("Workflow output #"+ i +" is: "+ wf_outputs[i]);
		}
		
		
//		managerInstanceClient.destroy();
		
		logger.info("END");
		return;
	}



	protected void runSimpleArrayTest(WorkflowManagerServiceClient  wf_manager) 
	throws RemoteException{

		
		
		WorkflowManagerInstanceDescriptor wfDesc = new WorkflowManagerInstanceDescriptor();
		
		
		WorkflowPortionDescriptor wfPart = new WorkflowPortionDescriptor();
		String workflowHelperServiceLocation = this.containerBaseURL + "cagrid/WorkflowHelper";
		wfPart.setWorkflowHelperServiceLocation(workflowHelperServiceLocation);
		
		
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
		operation_ram.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "ReceiveArrayAndMoreRequest"));
		operation_ram.setServiceURL(containerBaseURL+"cagrid/ReceiveArrayService");
		//operation_ram.setOutputType(); // Service has no output
		currStageDesc.setBasicDescription(operation_ram);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ram = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ram = new InputParameterDescriptor[3];
		inputParams_ram[0] = new InputParameterDescriptor(new QName("number"), new QName(XSD_NAMESPACE, "int"));
		inputParams_ram[1] = new InputParameterDescriptor(new QName("strArray"), new QName(XSD_NAMESPACE, "string[]"));
		inputParams_ram[2] = new InputParameterDescriptor(new QName("booleanValue"), new QName(XSD_NAMESPACE, "boolean"));
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
		String access_url = containerBaseURL+"cagrid/CreateArrayService";
		operation_cas.setWorkflowID("GeorgeliusWorkFlow");
		operation_cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetArrayRequest"));
		operation_cas.setServiceURL(access_url);
		operation_cas.setOutputType(new QName(SOAPENCODING_NAMESPACE, "string[]"));
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
		outParameterDescriptor_cas[0].setType(new QName( SOAPENCODING_NAMESPACE ,"string[]"));
		outParameterDescriptor_cas[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_cas[0].setLocationQuery("/ns0:GetArrayResponse");
		outParameterDescriptor_cas[0].setDestinationGlobalUniqueIdentifier(0);
//		outParameterDescriptor_cas[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient_ram.getEndpointReference()});


		// Second destination: Output matcher
		if(validatorEnabled){

			//System.out.println("Setting 5th param in the output matcher: "+ outputMatcherEPR); //DEBUG

			outParameterDescriptor_cas[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor_cas[1].setParamIndex(4);
			outParameterDescriptor_cas[1].setType(new QName( SOAPENCODING_NAMESPACE ,"string[]"));
			outParameterDescriptor_cas[1].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
					new QName(XSD_NAMESPACE,"xsd")});
			outParameterDescriptor_cas[1].setLocationQuery("/ns0:GetArrayResponse");
//			outParameterDescriptor_cas[1].setDestinationEPR(new EndpointReferenceType[]{ outputMatcherID});
		}

		
		
		
		// Add one output to the worklow outputs' description
		WorkflowOutputParameterTransportDescriptor outputParam = new WorkflowOutputParameterTransportDescriptor();
		OperationOutputParameterTransportDescriptor paramDescription = new OperationOutputParameterTransportDescriptor();
		paramDescription.setLocationQuery("/ns0:GetArrayResponse");
		paramDescription.setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		paramDescription.setType(new QName( SOAPENCODING_NAMESPACE ,"string[]"));
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
		wfDesc.setWorkflowParts(new WorkflowPortionDescriptor[]{ wfPart });
		WorkflowManagerInstanceReference instanceRef = wf_manager.createWorkflowManagerInstanceFromObjectDescriptor(wfDesc);
		WorkflowManagerInstanceClient instanceClient = null;
		try {
			instanceClient = new WorkflowManagerInstanceClient(instanceRef.getEndpointReference());
			this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), instanceClient, workflowID);
		} catch (MalformedURIException e) {
			logger.error(e.getMessage(),e );
		} 
		
		this.managerInstances.add(instanceRef.getEndpointReference());
	
		logger.info("Starting execution");
		instanceClient.start();
		
		logger.info("Retrieving workflow outputs");
		String[] outputs = instanceClient.getOutputValues();
		
		for(int i=0; i < outputs.length; i++){
			
			logger.info("Output #"+ i +" is "+ outputs[i]);
		}
		
		
//		this.waitUntilCompletion();
//		instanceClient.destroy();
		
		
		return;
	}



	protected void runFaninFanOutTest(WorkflowManagerServiceClient  wf_manager, 
			int outputMatcherID) throws RemoteException{

		
		WorkflowManagerInstanceDescriptor wfDesc = new WorkflowManagerInstanceDescriptor();
		WorkflowPortionDescriptor wfPart = new WorkflowPortionDescriptor();
		String workflowHelperServiceLocation = this.containerBaseURL + "cagrid/WorkflowHelper";
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

		java.lang.String acess_url = containerBaseURL+"cagrid/Service4";
		operation4.setWorkflowID("GeorgeliusWorkFlow");
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation4.setServiceURL(acess_url);
		currStageDesc.setBasicDescription(operation4);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage4 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams4 = new InputParameterDescriptor[2];
		inputParams4[0] = new InputParameterDescriptor(new QName("result1"), new QName(XSD_NAMESPACE, "string"));
		inputParams4[1] = new InputParameterDescriptor(new QName("result2"), new QName(XSD_NAMESPACE, "string"));
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
		acess_url = containerBaseURL+"cagrid/Service2";
		operation_2.setWorkflowID("GeorgeliusWorkFlow");
		operation_2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "CapitalizeRequest"));
		operation_2.setServiceURL(acess_url);
		operation_2.setOutputType(new QName(XSD_NAMESPACE, "string"));
		currStageDesc.setBasicDescription(operation_2);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_2 = new InputParameterDescriptor[1];
		inputParams_2[0] = new InputParameterDescriptor( new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"));
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
		outParameterDescriptor2[0].setLocationQuery("/ns0:CapitalizeResponse");
		outParameterDescriptor2[0].setDestinationGlobalUniqueIdentifier(4);
//		outParameterDescriptor2[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient4.getEndpointReference()});


		// Second destination: output matcher
		if(this.validatorEnabled){

			//System.out.println("Setting 7th param in the output matcher: "+ outputMatcherEPR); //DEBUG

			outParameterDescriptor2[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor2[1].setParamIndex(6);
			outParameterDescriptor2[1].setType(new QName("string"));
			namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
					new QName(XSD_NAMESPACE, "xsd")};
			outParameterDescriptor2[1].setQueryNamespaces(namespaces);
			outParameterDescriptor2[1].setLocationQuery("/ns0:CapitalizeResponse");
//			outParameterDescriptor2[1].setDestinationEPR(new EndpointReferenceType[]{ outputMatcherID});
		}


		// Add one output to the workflow outputs
		WorkflowOutputParameterTransportDescriptor outputParam = new WorkflowOutputParameterTransportDescriptor();
		OperationOutputParameterTransportDescriptor paramDescription = new OperationOutputParameterTransportDescriptor();
		paramDescription.setLocationQuery("/ns0:CapitalizeResponse");
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
		acess_url = containerBaseURL+"cagrid/Service3";

		// This is the greek version of my name...
		operation3.setWorkflowID("GeorgeliusWorkFlow");
		operation3.setOperationQName(new QName("http://service3.introduce.cagrid.org/Service3", "GenerateXRequest"));
		operation3.setServiceURL(acess_url);
		operation3.setOutputType(new QName(XSD_NAMESPACE, "string"));
		currStageDesc.setBasicDescription(operation3);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage3 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams3 = new InputParameterDescriptor[1];
		inputParams3[0] = new InputParameterDescriptor(new QName("str_length"), new QName(XSD_NAMESPACE, "int"));
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
		outParameterDescriptor3[0].setLocationQuery("/ns0:GenerateXResponse"); 
		outParameterDescriptor3[0].setDestinationGlobalUniqueIdentifier(4);
//		outParameterDescriptor3[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient4.getEndpointReference()});


		// 2nd destination: output matcher
		if(this.validatorEnabled){

			//System.out.println("Setting 8th param in the output matcher: "+ outputMatcherEPR); //DEBUG

			outParameterDescriptor3[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor3[1].setParamIndex(7);
			outParameterDescriptor3[1].setType(new QName(XSD_NAMESPACE, "string"));
			namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service3.introduce.cagrid.org/Service3", "ns0"),
					new QName(XSD_NAMESPACE, "xsd")};
			outParameterDescriptor3[1].setQueryNamespaces(namespaces);
			outParameterDescriptor3[1].setLocationQuery("/ns0:GenerateXResponse"); 
//			outParameterDescriptor3[1].setDestinationEPR(new EndpointReferenceType[]{outputMatcherID});  // */
		}


		// Add one output to the workflow outputs
		outputParam = new WorkflowOutputParameterTransportDescriptor();
		paramDescription = new OperationOutputParameterTransportDescriptor();
		paramDescription.setLocationQuery("/ns0:GenerateXResponse");
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

		acess_url = containerBaseURL+"cagrid/Service5";
		operation5.setWorkflowID("GeorgeliusWorkFlow");
		operation5.setOperationQName(new QName("http://service5.introduce.cagrid.org/Service5" , "CheckStringAndItsLengthRequest"));
		operation5.setServiceURL(acess_url);
		operation5.setOutputType(new QName(XSD_NAMESPACE, "boolean"));
		currStageDesc.setBasicDescription(operation5);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage5 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams5 = new InputParameterDescriptor[1];
		inputParams5[0] = new InputParameterDescriptor(new QName("http://service1.workflow.cagrid.org/Service1", "stringAndItsLenght"), 
				new QName("http://service1.workflow.cagrid.org/Service1", "StringAndItsLength"));
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
		acess_url = containerBaseURL+"cagrid/Service1";
		operation1.setWorkflowID("GeorgeliusWorkFlow");
		operation1.setOperationQName(new QName("http://service1.introduce.cagrid.org/Service1", "GenerateDataRequest"));
		operation1.setServiceURL(acess_url);
		operation1.setOutputType(new QName(XSD_NAMESPACE, "string"));
		currStageDesc.setBasicDescription(operation1);


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage1 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams1 = new InputParameterDescriptor[1];
		inputParams1[0] = new InputParameterDescriptor(new QName("info"), new QName(XSD_NAMESPACE, "string"));
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
		outParameterDescriptor1[0].setLocationQuery("/ns0:GenerateDataResponse/ns1:StringAndItsLenght/ns1:str"); 
		outParameterDescriptor1[0].setDestinationGlobalUniqueIdentifier(2);
//		outParameterDescriptor1[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient2.getEndpointReference()});

		// Creating the outputDescriptor of the second filter (Service3)
		outParameterDescriptor1[1] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor1[1].setParamIndex(0);
		outParameterDescriptor1[1].setType(new QName("int"));
		outParameterDescriptor1[1].setQueryNamespaces(namespaces);
		outParameterDescriptor1[1].setLocationQuery("/ns0:GenerateDataResponse/ns1:StringAndItsLenght/ns1:length");
		outParameterDescriptor1[1].setDestinationGlobalUniqueIdentifier(3);
//		outParameterDescriptor1[1].setDestinationEPR(new EndpointReferenceType[]{serviceClient3.getEndpointReference()});

		// Creating the outputDescriptor of the 3rd filter (Service5)
		outParameterDescriptor1[2] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor1[2].setParamIndex(0);
		outParameterDescriptor1[2].setType(new QName("http://service1.workflow.cagrid.org/Service1","StringAndItsLenght"));
		outParameterDescriptor1[2].setQueryNamespaces(namespaces);
		outParameterDescriptor1[2].setLocationQuery("/ns0:GenerateDataResponse/ns1:StringAndItsLenght");
		outParameterDescriptor1[2].setDestinationGlobalUniqueIdentifier(5);
//		outParameterDescriptor1[2].setDestinationEPR(new EndpointReferenceType[]{serviceClient5.getEndpointReference()});

		

		// Add one output to the workflow outputs
		outputParam = new WorkflowOutputParameterTransportDescriptor();
		outputParam.setSourceGUID(currStageID);
		paramDescription = new OperationOutputParameterTransportDescriptor();
		paramDescription.setLocationQuery("/ns0:GenerateDataResponse/ns1:StringAndItsLenght");
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
		System.out.println("Setting input for service 1: '"+workflow_input+"'");
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
		wfDesc.setWorkflowParts(new WorkflowPortionDescriptor[]{ wfPart });

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
		
		logger.info("Starting execution");
		instanceClient.start();
//		instanceClient.destroy();
		
		logger.info("Retrieving workflow outputs");
		String[] outputs = instanceClient.getOutputValues();
		for(int i=0; i < outputs.length; i++){
			
			
			logger.info("Output #"+ i +" is: "+ outputs[i]);
		}
		return;
	}



	protected void waitForCompletion() {

		System.out.println("Waiting for workflow notification of FINISH status");


		this.isFinishedKey.lock();
		try {


			if( !this.isFinished ){

				try {

					boolean wasSignaled = this.isFinishedCondition.await(45, TimeUnit.SECONDS); 					
					if(wasSignaled) System.out.println("OK. Received notification of FINISH status. Exiting"); 
					else {
						String errMsg = "Timeout exceeded without any notification of FINISH status. Exiting";
						System.err.println(errMsg);
						this.printMap();
						Assert.fail(errMsg);
					}

				} catch(Throwable t){
					System.err.println("Error while waiting");
					t.printStackTrace();
				}
			}

			//this.printMap(); //DEBUG

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
		boolean isTimestampedStatusChange = message_qname.equals(TimestampedStatus.getTypeDesc().getXmlType());
		String stageKey = null;
		try {
			stageKey = new WorkflowManagerInstanceClient(arg1).getEPRString();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			e1.printStackTrace();
		}   




		//DEBUG
		//PrintStream log = System.out;
		//log.println("[CreateTestWorkflowsStep] Received message of type "+ message_qname.getLocalPart() +" from "+ stageKey);


		// Handle status change notifications
		if(isTimestampedStatusChange){
			TimestampedStatus status = null;;
			try {
				status = (TimestampedStatus) actual_property.getValueAsType(message_qname, TimestampedStatus.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


			logger.info("Received new status value: "+ status.getStatus().toString() + ':' + status.getTimestamp());

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
				else logger.warn("Unrecognized stage notified status change: "+ stageKey);


				if( statusActuallyChanged && (status.getStatus().equals(Status.FINISHED) || status.getStatus().equals(Status.ERROR)) ){


					this.isFinished  = this.hasFinished(); 

					if(this.isFinished){

						this.isFinishedCondition.signalAll();
						Assert.assertFalse(this.stageStatus.containsValue(Status.ERROR));
						
						// Destroy ManagerInstance resources
						Iterator<EndpointReferenceType> instances_iter = this.managerInstances.iterator();
						while( instances_iter.hasNext() ){
							
							EndpointReferenceType curr_managerInstance = instances_iter.next();
							WorkflowManagerInstanceClient curr_client;
							try {
								curr_client = new WorkflowManagerInstanceClient(curr_managerInstance);
//								curr_client.destroy();
							} catch (MalformedURIException e) {
								logger.error(e.getMessage(), e);
								e.printStackTrace();
							} catch (RemoteException e) {
								logger.error(e.getMessage(), e);
								e.printStackTrace();
							}
							
							
						}
						
					}
				}
			}
			finally {
				this.isFinishedKey.unlock();
			}
		}

	}


	/**
	 * Verify whether all stages have already sent a termination notification  
	 * */
	private boolean hasFinished() {

		Set<Entry<String, TimestampedStatus>> entries = this.stageStatus.entrySet();
		Iterator<Entry<String, TimestampedStatus>> entries_iter = entries.iterator();

		while( entries_iter.hasNext() ){

			Entry<String, TimestampedStatus> curr_entry = entries_iter.next();
			boolean stageEnded =   curr_entry.getValue().getStatus().equals(Status.FINISHED)
			|| curr_entry.getValue().getStatus().equals(Status.ERROR);

			if( !stageEnded )  return false;

		}

		return true;
	}


	protected void subscribe(QName notificationType, WorkflowManagerInstanceClient  toSubscribe, String stageOperationQName){
		//protected void subscribe(QName notificationType, WorkflowInvocationHelperClient toSubscribe, String stageOperationQName){

		try{

			//System.out.println("[subscribe] Subscribing service: "+ toSubscribe.getEPRString());

			this.stageStatus.put(toSubscribe.getEPRString(), new TimestampedStatus(Status.UNCONFIGURED, 0)); // Register to be monitored for status changes
			toSubscribe.subscribeWithCallback(notificationType, this);

			this.EPR2OperationName.put(toSubscribe.getEPRString(), stageOperationQName);

		}
		catch(Throwable t){
			t.printStackTrace();
		} 

		return;
	}

	protected void printMap(){


		System.out.println("BEGIN printMap");
		Set<Entry<String, TimestampedStatus>> entries = this.stageStatus.entrySet();
		Iterator<Entry<String, TimestampedStatus>> iter = entries.iterator();
		while(iter.hasNext()){

			Entry<String, TimestampedStatus> curr = iter.next();
			String operationName = this.EPR2OperationName.get(curr.getKey());

			System.out.println("["+ operationName +", "+ curr.getValue().getStatus() +"]");
		}
		System.out.println("END printMap");


		return;
	}

}
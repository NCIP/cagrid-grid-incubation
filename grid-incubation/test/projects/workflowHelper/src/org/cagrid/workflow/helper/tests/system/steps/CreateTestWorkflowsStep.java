/**
*============================================================================
*  Copyright (c) 2008, The Ohio State University Research Foundation, Emory 
*  University, the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.workflow.helper.tests.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.rmi.RemoteException;
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
import javax.xml.rpc.NamespaceConstants;

import junit.framework.Assert;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.client.WorkflowHelperClient;
import org.cagrid.workflow.helper.descriptor.EventTimePeriod;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.InstrumentationRecord;
import org.cagrid.workflow.helper.descriptor.LocalWorkflowInstrumentationRecord;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.globus.wsrf.NotifyCallback;

public class CreateTestWorkflowsStep extends Step implements NotifyCallback  {

	private static Log logger = LogFactory.getLog(CreateTestWorkflowsStep.class);

	protected EndpointReferenceType helper_epr = null;
	protected String containerBaseURL = null;

	// Synchronizes the access to variable 'isFinished'
	protected Lock isFinishedKey = new ReentrantLock();
	protected Condition isFinishedCondition = isFinishedKey.newCondition();
	protected Map<String, TimestampedStatus> stageStatus = new HashMap<String, TimestampedStatus>() ;


	// Store the operation name for each service subscribed for notification
	protected Map<String, String> EPR2OperationName = new HashMap<String, String>();

	protected boolean isFinished = false;


	final static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	final static String SOAPENCODING_NAMESPACE = "http://schemas.xmlsoap.org/soap/encoding/";

	protected final boolean validatorEnabled = true;  // Enable/Disable the output matcher. Should be true when not debugging

	private Map<String, LocalWorkflowInstrumentationRecord> localWorkflowsMeasurements = new HashMap<String, LocalWorkflowInstrumentationRecord>();
//	private org.cagrid.workflow.helper.instrumentation.InstrumentationRecord globalWorkflowMeasurements;


	public CreateTestWorkflowsStep(EndpointReferenceType helper_epr, String containerBaseURL) {
		super();
		this.helper_epr = helper_epr;
		this.containerBaseURL  = containerBaseURL;
	}


	public void runStep() throws Throwable {



		/** Instantiate each of the workflows: Fan in-Fan out, simple-type array forwarding, complex-type array forwarding,
		 * simple-type array streaming, complex-type array streaming
		 * */
		System.out.println("-x-x-x- BEGIN NON-SECURE WORKFLOWS TESTS -x-x-x-");
				WorkflowHelperClient wf_helper = new WorkflowHelperClient(this.helper_epr);


		final EndpointReference manager_epr = null;



		try {

			/*** Testing arrays as services' input ***/

			/** simple type arrays **/
			System.out.println("Simple arrays as input");
			runSimpleArrayTest(manager_epr, wf_helper);
			System.out.println("OK"); // */

			System.out.println("Complex arrays as input");
			runComplexArrayTest(manager_epr, wf_helper);
			System.out.println("OK");

			System.out.println("END Testing arrays"); // */



			/** BEGIN streaming test **/
			System.out.println("BEGIN Testing streaming");

			// Streaming simple types FIXME This is generating errors
			/*System.out.println("Streaming of simple-type arrays");
			runSimpleArrayStreaming(manager_epr, wf_helper);
			System.out.println("OK");  // */



			/* Streaming complex types */
			System.out.println("Streaming of complex-type arrays");
			runComplexArrayStreaming(manager_epr, wf_helper);
			System.out.println("OK");

			System.out.println("END Testing streaming"); // */

			/** FAN IN AND FAN OUT TEST **/
			System.out.println("BEGIN Testing fan in and fan out");
			runFaninFanOutTest(manager_epr, wf_helper);
			System.out.println("END Testing fan in and fan out"); // */

			// Block until every stage reports either a FINISHED or an ERROR status
			this.waitUntilCompletion();


		} catch(Throwable t) {
			t.printStackTrace();
			Assert.fail();
		}


		System.out.println("-x-x-x- END NON-SECURE WORKFLOWS TESTS -x-x-x-");
		return;
	}


	protected void runComplexArrayStreaming(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper)throws RemoteException {


		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor5 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		String workflowID = "WorkFlow5";
		workflowDescriptor5.setWorkflowID(workflowID);
		workflowDescriptor5.setWorkflowManagerEPR(manager_epr);


		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance5 = null;
		try {
			wf_instance5 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor5);
			this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), wf_instance5, workflowID);
			this.subscribe(LocalWorkflowInstrumentationRecord.getTypeDesc().getXmlType(), wf_instance5, workflowID);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


		// BEGIN service 4


		// Creating client of service 4
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation4 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		java.lang.String acess_url = containerBaseURL+"/wsrf/services/cagrid/Service4";
		operation4.setWorkflowID("GeorgeliusWorkFlow");
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation4.setOutputIsArray(false);
		operation4.setServiceURL(acess_url);
		WorkflowInvocationHelperClient serviceClient__4 = null;
		try {
			serviceClient__4 = wf_instance5.createWorkflowInvocationHelper(operation4);
		} catch (MalformedURIException e) {
			e.printStackTrace();
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
		operation__ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetComplexArrayRequest"));
		operation__ca.setServiceURL(access_url);
		operation__ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"));
		operation__ca.setOutputIsArray(true);


		// create ReceiveArrayService
		WorkflowInvocationHelperClient serviceClient__ca = null;
		try {
			serviceClient__ca = wf_instance5.createWorkflowInvocationHelper(operation__ca);
		} catch (MalformedURIException e) {
			e.printStackTrace();
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
		outParameterDescriptor__ca[0].setExpectedTypeIsArray(false);
		outParameterDescriptor__ca[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "abc")});
		outParameterDescriptor__ca[0].setLocationQuery("/ns0:GetComplexArrayResponse/abc:ComplexType/abc:message");
		outParameterDescriptor__ca[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient__4.getEndpointReference()});



		// takes the reference to ReceiveComplexArrayService
		outputDescriptor__ca.setParamDescriptor(outParameterDescriptor__ca);
		serviceClient__ca.configureOutput(outputDescriptor__ca);
		serviceClient__ca.start();

		// END CreateArrayService::getComplexArray
	}


	protected void runSimpleArrayStreaming(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper) throws RemoteException {

		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor5 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		String workflowID = "WorkFlow5";
		workflowDescriptor5.setWorkflowID(workflowID);
		workflowDescriptor5.setWorkflowManagerEPR(manager_epr);

		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance5 = null;
		try {
			wf_instance5 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor5);
			this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), wf_instance5, workflowID);
			this.subscribe(LocalWorkflowInstrumentationRecord.getTypeDesc().getXmlType(), wf_instance5, workflowID);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


		// BEGIN service 4
		WorkflowInvocationHelperDescriptor operation_4 = new WorkflowInvocationHelperDescriptor();
		operation_4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation_4.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/Service4");
		// operation_4.setOutputType(); // Void output expected



		// Creating client of service 4
		WorkflowInvocationHelperClient serviceClient_4 = null;
		try {
			serviceClient_4 = wf_instance5.createWorkflowInvocationHelper(operation_4);
		} catch (MalformedURIException e) {
			e.printStackTrace();
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
		operation__2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "CapitalizeRequest"));
		operation__2.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/Service2");
		operation__2.setOutputType(new QName(XSD_NAMESPACE, "string"));
		operation__2.setOutputIsArray(false);
		WorkflowInvocationHelperClient serviceClient_2 = null;
		try {
			serviceClient_2 = wf_instance5.createWorkflowInvocationHelper(operation__2);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


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
		outParameterDescriptor__2[0].setExpectedTypeIsArray(false);
		QName[] namespaces__2 = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor__2[0].setQueryNamespaces(namespaces__2);
		outParameterDescriptor__2[0].setLocationQuery("/ns0:CapitalizeResponse");
		outParameterDescriptor__2[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient_4.getEndpointReference()});


		outputDescriptor__2.setParamDescriptor(outParameterDescriptor__2);
		serviceClient_2.configureOutput(outputDescriptor__2);
		serviceClient_2.start();


		// END service 2



		// BEGIN CreateArrayService
		// create CreateArrayService
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation__cas = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		operation__cas.setWorkflowID("GeorgeliusWorkFlow");
		operation__cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetArrayRequest"));
		operation__cas.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/CreateArrayService");
		operation__cas.setOutputType(new QName(XSD_NAMESPACE, "string"));
		operation__cas.setOutputIsArray(true);
		WorkflowInvocationHelperClient serviceClient_cs = null;
		try {
			serviceClient_cs = wf_instance5.createWorkflowInvocationHelper(operation__cas);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}



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
		outParameterDescriptor_cs[0].setType(new QName( XSD_NAMESPACE, "string"));
		outParameterDescriptor_cs[0].setExpectedTypeIsArray(true);
		outParameterDescriptor_cs[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_cs[0].setLocationQuery("/ns0:GetArrayResponse");
		outParameterDescriptor_cs[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient_2.getEndpointReference()});  // takes the reference to Service2::capitalize


		outputDescriptor_cs.setParamDescriptor(outParameterDescriptor_cs);
		serviceClient_cs.configureOutput(outputDescriptor_cs);
		serviceClient_cs.start();


		// END CreateArrayService
	}


	protected void runComplexArrayTest(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper) throws RemoteException{


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


		// Monitor status changes and an instrumentation report
		this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), wf_instance1, workflowID);
		this.subscribe(LocalWorkflowInstrumentationRecord.getTypeDesc().getXmlType(), wf_instance1, workflowID);



		WorkflowInvocationHelperClient client_assert = null;


		// Stage that verifies whether the workflow output matches the expected
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
			InputParameter inputParameter = new InputParameter("<GetComplexArrayResponse xmlns=\"http://createarrayservice.introduce.cagrid.org/CreateArrayService\">" +
					"<ns1:ComplexType xmlns:ns1=\"http://systemtests.workflow.cagrid.org/SystemTests\"><ns1:id>0</ns1:id><ns1:message>Element 0</ns1:message>" +
					"</ns1:ComplexType><ns2:ComplexType xmlns:ns2=\"http://systemtests.workflow.cagrid.org/SystemTests\"><ns2:id>1</ns2:id>" +
					"<ns2:message>Element 1</ns2:message></ns2:ComplexType><ns3:ComplexType xmlns:ns3=\"http://systemtests.workflow.cagrid.org/SystemTests\">" +
					"<ns3:id>2</ns3:id><ns3:message>Element 2</ns3:message></ns3:ComplexType><ns4:ComplexType xmlns:ns4=\"http://systemtests.workflow.cagrid.org/SystemTests\">" +
					"<ns4:id>3</ns4:id><ns4:message>Element 3</ns4:message></ns4:ComplexType><ns5:ComplexType xmlns:ns5=\"http://systemtests.workflow.cagrid.org/SystemTests\">" +
					"<ns5:id>4</ns5:id><ns5:message>Element 4</ns5:message></ns5:ComplexType><ns6:ComplexType xmlns:ns6=\"http://systemtests.workflow.cagrid.org/SystemTests\">" +
					"<ns6:id>5</ns6:id><ns6:message>Element 5</ns6:message></ns6:ComplexType></GetComplexArrayResponse>", 1);
			client_assert.setParameter(inputParameter);

			client_assert.start();
			// END AssertService::assertSimpleArrayEquals
		}



		// BEGIN ReceiveArrayService::ReceiveComplexArray
		String access_url = containerBaseURL+"/wsrf/services/cagrid/ReceiveArrayService";
		WorkflowInvocationHelperDescriptor operation2 = new WorkflowInvocationHelperDescriptor();
		operation2.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "ReceiveComplexArrayRequest"));
		operation2.setServiceURL(access_url);
		//operation2.setOutput(); // This service has no output


		// create ReceiveArrayService
		WorkflowInvocationHelperClient client2 = null;
		try {
			client2 = wf_instance1.createWorkflowInvocationHelper(operation2);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURIException e) {
			e.printStackTrace();
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



		// BEGIN CreateArrayService::getComplexArray
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_ca = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		access_url = containerBaseURL+"/wsrf/services/cagrid/CreateArrayService";
		operation_ca.setWorkflowID("GeorgeliusWorkFlow");
		operation_ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetComplexArrayRequest"));
		operation_ca.setServiceURL(access_url);
		operation_ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType"));
		operation_ca.setOutputIsArray(true);


		// create ReceiveArrayService
		WorkflowInvocationHelperClient client_ca = null;
		try {
			client_ca = wf_instance1.createWorkflowInvocationHelper(operation_ca);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


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
		outParameterDescriptor_ca[0].setType(new QName( "http://systemtests.workflow.cagrid.org/SystemTests" ,"ComplexType"));
		outParameterDescriptor_ca[0].setExpectedTypeIsArray(true);
		outParameterDescriptor_ca[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_ca[0].setLocationQuery("/ns0:GetComplexArrayResponse");
		outParameterDescriptor_ca[0].setDestinationEPR(new EndpointReferenceType[]{ client2.getEndpointReference() });


		// Second destination: Output matcher
		if( validatorEnabled ){

			outParameterDescriptor_ca[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor_ca[1].setParamIndex(0); // Setting 2nd argument in the output matcher
			outParameterDescriptor_ca[1].setType(new QName( "http://systemtests.workflow.cagrid.org/SystemTests" ,"ComplexType"));
			outParameterDescriptor_ca[1].setExpectedTypeIsArray(true);
			outParameterDescriptor_ca[1].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
					new QName(XSD_NAMESPACE,"xsd")});
			outParameterDescriptor_ca[1].setLocationQuery("/ns0:GetComplexArrayResponse");
			outParameterDescriptor_ca[1].setDestinationEPR(new EndpointReferenceType[]{ client_assert.getEndpointReference() });
		}



		// takes the reference to ReceiveComplexArrayService
		outputDescriptor_ca.setParamDescriptor(outParameterDescriptor_ca);
		client_ca.configureOutput(outputDescriptor_ca);
		client_ca.start();
		// END CreateArrayService::getComplexArray

		return;
	}



	protected void runSimpleArrayTest(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper) throws RemoteException{

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


		// Monitor status changes and an instrumentation report
		this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), wf_instance2, workflowID);
		this.subscribe(LocalWorkflowInstrumentationRecord.getTypeDesc().getXmlType(), wf_instance2, workflowID);


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
			InputParameter inputParameter = new InputParameter("<GetArrayResponse xmlns=\"http://createarrayservice.introduce.cagrid.org/CreateArrayService\">" +
					"<response>number 0</response><response>number 1</response><response>number 2</response><response>number 3</response><response>number 4</response><response>number 5" +
					"</response></GetArrayResponse>", 1);
			serviceClient_assert.setParameter(inputParameter);




			serviceClient_assert.start();
			// END AssertService::assertSimpleArrayEquals
		}




		// BEGIN ReceiveArrayService::ReceiveArrayAndMore
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_ram = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		operation_ram.setWorkflowID("GeorgeliusWorkFlow");
		operation_ram.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "ReceiveArrayAndMoreRequest"));
		operation_ram.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/ReceiveArrayService");
		//operation_ram.setOutputType(); // Service has no output


		// create ReceiveArrayService
		WorkflowInvocationHelperClient serviceClient_ram = null;
		try {
			serviceClient_ram = wf_instance2.createWorkflowInvocationHelper(operation_ram);
		} catch (MalformedURIException e) {
			e.printStackTrace();
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



		// BEGIN CreateArrayService
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_cas = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		String access_url = containerBaseURL+"/wsrf/services/cagrid/CreateArrayService";
		operation_cas.setWorkflowID("GeorgeliusWorkFlow");
		operation_cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetArrayRequest"));
		operation_cas.setServiceURL(access_url);
		operation_cas.setOutputType(new QName(SOAPENCODING_NAMESPACE, "string"));
		operation_cas.setOutputIsArray(true);


		// create ReceiveArrayService
		WorkflowInvocationHelperClient serviceClient_cas = null;
		try {
			serviceClient_cas = wf_instance2.createWorkflowInvocationHelper( operation_cas);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


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
		outParameterDescriptor_cas[0].setLocationQuery("/ns0:GetArrayResponse");
		outParameterDescriptor_cas[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient_ram.getEndpointReference()});


		// Second destination: Output matcher
		if(this.validatorEnabled){

			outParameterDescriptor_cas[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor_cas[1].setParamIndex(0);
			outParameterDescriptor_cas[1].setType(new QName( NamespaceConstants.NSURI_SCHEMA_XSD ,"string"));
			outParameterDescriptor_cas[1].setExpectedTypeIsArray(true);
			outParameterDescriptor_cas[1].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
					new QName(XSD_NAMESPACE,"xsd")});
			outParameterDescriptor_cas[1].setLocationQuery("/ns0:GetArrayResponse");
			outParameterDescriptor_cas[1].setDestinationEPR(new EndpointReferenceType[]{ serviceClient_assert.getEndpointReference() });
		}


		// takes the reference to ReceiveArrayService
		outputDescriptor_cas.setParamDescriptor(outParameterDescriptor_cas);
		serviceClient_cas.configureOutput(outputDescriptor_cas);
		serviceClient_cas.start();

		// END CreateArrayService

		return;
	}



	protected void runFaninFanOutTest(EndpointReferenceType manager_epr, WorkflowHelperClient wf_helper) throws RemoteException{

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


		// Monitor status changes and an instrumentation report
		this.subscribe(TimestampedStatus.getTypeDesc().getXmlType(), wf_instance3, workflowID);
		this.subscribe(LocalWorkflowInstrumentationRecord.getTypeDesc().getXmlType(), wf_instance3, workflowID);


		// BEGIN service 4
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation4 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		java.lang.String acess_url = containerBaseURL+"/wsrf/services/cagrid/Service4";
		operation4.setWorkflowID("GeorgeliusWorkFlow");
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation4.setServiceURL(acess_url);


		// Creating client of service 4
		WorkflowInvocationHelperClient serviceClient4 = null;
		try {
			serviceClient4 = wf_instance3.createWorkflowInvocationHelper(operation4);
		} catch (MalformedURIException e) {
			e.printStackTrace();
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
		operation_2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "CapitalizeRequest"));
		operation_2.setServiceURL(acess_url);
		operation_2.setOutputType(new QName(XSD_NAMESPACE, "string"));
		operation_2.setOutputIsArray(false);


		// create service 2
		WorkflowInvocationHelperClient serviceClient2 = null;
		try {
			serviceClient2 = wf_instance3.createWorkflowInvocationHelper(operation_2);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_2 = new InputParameterDescriptor[1];
		inputParams_2[0] = new InputParameterDescriptor(new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"), false);
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
		outParameterDescriptor2[0].setExpectedTypeIsArray(false);
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor2[0].setQueryNamespaces(namespaces);
		outParameterDescriptor2[0].setLocationQuery("/ns0:CapitalizeResponse");
		outParameterDescriptor2[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient4.getEndpointReference()});


		// Second destination: output matcher
		if(this.validatorEnabled){

			outParameterDescriptor2[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor2[1].setParamIndex(0);
			outParameterDescriptor2[1].setType(new QName("string"));
			outParameterDescriptor2[1].setExpectedTypeIsArray(false);
			namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
					new QName(XSD_NAMESPACE, "xsd")};
			outParameterDescriptor2[1].setQueryNamespaces(namespaces);
			outParameterDescriptor2[1].setLocationQuery("/ns0:CapitalizeResponse");
			outParameterDescriptor2[1].setDestinationEPR(new EndpointReferenceType[]{ client_assert2.getEndpointReference()});
		}

		// takes the reference to the service 4
		outputDescriptor2.setParamDescriptor(outParameterDescriptor2);
		serviceClient2.configureOutput(outputDescriptor2);
		serviceClient2.start();
		// END service 2



		// BEGIN service 3
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation3 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service3";

		// This is the greek version of my name...
		operation3.setWorkflowID("GeorgeliusWorkFlow");
		operation3.setOperationQName(new QName("http://service3.introduce.cagrid.org/Service3", "GenerateXRequest"));
		operation3.setServiceURL(acess_url);
		operation3.setOutputType(new QName(XSD_NAMESPACE, "string"));
		operation3.setOutputIsArray(false);




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




		// create service 3
		WorkflowInvocationHelperClient serviceClient3 = null;
		try {
			serviceClient3 = wf_instance3.createWorkflowInvocationHelper(operation3	);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


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
		outParameterDescriptor3[0].setExpectedTypeIsArray(false);
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service3.introduce.cagrid.org/Service3", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor3[0].setQueryNamespaces(namespaces);
		outParameterDescriptor3[0].setLocationQuery("/ns0:GenerateXResponse");
		outParameterDescriptor3[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient4.getEndpointReference()});


		// 2nd destination: output matcher
		if(this.validatorEnabled){

			outParameterDescriptor3[1] = new OperationOutputParameterTransportDescriptor();
			outParameterDescriptor3[1].setParamIndex(0);
			outParameterDescriptor3[1].setType(new QName(XSD_NAMESPACE, "string"));
			outParameterDescriptor3[1].setExpectedTypeIsArray(false);
			namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service3.introduce.cagrid.org/Service3", "ns0"),
					new QName(XSD_NAMESPACE, "xsd")};
			outParameterDescriptor3[1].setQueryNamespaces(namespaces);
			outParameterDescriptor3[1].setLocationQuery("/ns0:GenerateXResponse");
			outParameterDescriptor3[1].setDestinationEPR(new EndpointReferenceType[]{client_assert3.getEndpointReference()});
		}

		outputDescriptor3.setParamDescriptor(outParameterDescriptor3);
		serviceClient3.configureOutput(outputDescriptor3);
		serviceClient3.start();
		// END service 3




		// BEGIN service 5
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation5 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service5";
		operation5.setWorkflowID("GeorgeliusWorkFlow");
		operation5.setOperationQName(new QName("http://service5.introduce.cagrid.org/Service5" , "CheckStringAndItsLengthRequest"));
		operation5.setServiceURL(acess_url);
		operation5.setOutputType(new QName(XSD_NAMESPACE, "boolean"));
		operation5.setOutputIsArray(false);


		// Creating client of service 5
		WorkflowInvocationHelperClient serviceClient5 = null;
		try {
			serviceClient5 = wf_instance3.createWorkflowInvocationHelper(operation5);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage5 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams5 = new InputParameterDescriptor[1];
		inputParams5[0] = new InputParameterDescriptor(new QName("http://service1.workflow.cagrid.org/Service1", "stringAndItsLenght"),
				new QName("http://service1.workflow.cagrid.org/Service1", "StringAndItsLength"), false);
		inputMessage5.setInputParam(inputParams5);
		serviceClient5.configureInput(inputMessage5);
		// End InputMessage Descriptor

		// Creating the outputDescriptor
		OperationOutputTransportDescriptor outputDescriptor5 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor5 [] = new OperationOutputParameterTransportDescriptor[0];
		outputDescriptor5.setParamDescriptor(outParameterDescriptor5);
		serviceClient5.configureOutput(outputDescriptor5);
		serviceClient5.start();
		// END service 5




		// BEGIN service 1
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation1 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service1";
		operation1.setWorkflowID("GeorgeliusWorkFlow");
		operation1.setOperationQName(new QName("http://service1.introduce.cagrid.org/Service1", "GenerateDataRequest"));
		operation1.setServiceURL(acess_url);
		operation1.setOutputType(new QName(XSD_NAMESPACE, "string"));
		operation1.setOutputIsArray(false);


		// create service 1
		WorkflowInvocationHelperClient serviceClient1 = null;
		try {
			serviceClient1 = wf_instance3.createWorkflowInvocationHelper(operation1);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}


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
		outParameterDescriptor1[0].setExpectedTypeIsArray(false);
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
		outParameterDescriptor1[1].setExpectedTypeIsArray(false);
		outParameterDescriptor1[1].setQueryNamespaces(namespaces);
		outParameterDescriptor1[1].setLocationQuery("/ns0:GenerateDataResponse/ns1:StringAndItsLenght/ns1:length");
		// takes the reference to the service 3
		outParameterDescriptor1[1].setDestinationEPR(new EndpointReferenceType[]{serviceClient3.getEndpointReference()});

		// Creating the outputDescriptor of the 3rd filter (Service5)
		outParameterDescriptor1[2] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor1[2].setParamIndex(0);
		outParameterDescriptor1[2].setType(new QName("http://service1.workflow.cagrid.org/Service1","StringAndItsLenght"));
		outParameterDescriptor1[2].setExpectedTypeIsArray(false);
		outParameterDescriptor1[2].setQueryNamespaces(namespaces);
		outParameterDescriptor1[2].setLocationQuery("/ns0:GenerateDataResponse/ns1:StringAndItsLenght");
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

	}



	protected void waitUntilCompletion() {

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
		boolean isInstrumentationReport = message_qname.equals(LocalWorkflowInstrumentationRecord.getTypeDesc().getXmlType());
		String stageKey = null;
		try {
			stageKey = new WorkflowInstanceHelperClient(arg1).getEPRString();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			e1.printStackTrace();
		}


		// Handle status change notifications
		if(isTimestampedStatusChange){
			TimestampedStatus status = null;;
			try {
				status = (TimestampedStatus) actual_property.getValueAsType(message_qname, TimestampedStatus.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


			logger.debug("[deliver] Received new status value: "+ status.getStatus().toString() + ':' + status.getTimestamp() +" from "+ this.EPR2OperationName.get(stageKey)); //DEBUG

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
				else System.err.println("[CreateTestWorkflowsStep] Unrecognized stage notified status change: "+ stageKey);


				if( statusActuallyChanged && (status.getStatus().equals(Status.FINISHED) || status.getStatus().equals(Status.ERROR)) ){


					this.isFinished  = this.hasFinished();

					if(this.isFinished){

						this.isFinishedCondition.signalAll();
						Assert.assertFalse(this.stageStatus.containsValue(Status.ERROR));

						// Print the received instrumentation reports
						this.printInstrumentationReport();
					}
				}
			}
			finally {
				this.isFinishedKey.unlock();
			}
		}


		// Handle instrumentation reports
		else if(isInstrumentationReport){


			// Extract data
			LocalWorkflowInstrumentationRecord instrumentation_data = null;;
			try {
				instrumentation_data = (LocalWorkflowInstrumentationRecord) actual_property.getValueAsType(message_qname, LocalWorkflowInstrumentationRecord.class);
			} catch (Exception e) {
				e.printStackTrace();
			}

//			logger.info("Received instrumentation report from "+ instrumentation_data.getIdentifier());

			// Store data in the list of local workflows' instrumentation reports
			this.localWorkflowsMeasurements.put(instrumentation_data.getIdentifier(), instrumentation_data);
		}

	}



	// Print the instrumentation data received
	private void printInstrumentationReport() {


		// Iterate over the list of reports, printing each one of them
		Set<Entry<String,LocalWorkflowInstrumentationRecord>> entries = this.localWorkflowsMeasurements.entrySet();
		Iterator<Entry<String, LocalWorkflowInstrumentationRecord>> iter = entries.iterator();
		System.out.println("======================================================================");
		System.out.println("Instrumentation reports");
		while(iter.hasNext()){


			Entry<String, LocalWorkflowInstrumentationRecord> curr_entry = iter.next();
			LocalWorkflowInstrumentationRecord curr_report = curr_entry.getValue();

			System.out.println("Report for local workflow identified by "+ curr_report.getIdentifier());

			// Print data for the a local workflow as a whole
			InstrumentationRecord localWorkflowData = curr_report.getLocalWorkflowRecord();
			System.out.println("------------- BEGIN Local workflow -------------");
			EventTimePeriod[] localWorkflowRecords = localWorkflowData.getRecords();
			for(int i=0; i < localWorkflowRecords.length; i++){

				String stage_event = localWorkflowData.getRecords(i).getEvent();
				long elapsedTimeInNanos = localWorkflowData.getRecords(i).getEndTime() - localWorkflowData.getRecords(i).getStartTime();

				System.out.println("EVENT: "+ stage_event +" ; ELAPSED TIME: "+ (elapsedTimeInNanos/1000) + " microseconds");
			}
			System.out.println("------------- END Local workflow -------------");


			// Print data for each component of the current local workflow
			int num_stages_records = curr_report.getStagesRecords().length;
			for(int i=0; i < num_stages_records; i++){


				InstrumentationRecord curr_record = curr_report.getStagesRecords(i);
				System.out.println("------------- BEGIN Local workflow stage identified by "+ curr_record.getIdentifier() +" -------------");
				int num_events = curr_record.getRecords().length;
				for(int j=0; j < num_events; j++){

					EventTimePeriod curr_event = curr_record.getRecords(j);
					String stage_event = curr_event.getEvent();
					long elapsedTimeInNanos = curr_event.getEndTime() - curr_event.getStartTime();
					System.out.println("EVENT: "+ stage_event +" ; ELAPSED TIME: "+ (elapsedTimeInNanos/1000) + " microseconds");

				}
				System.out.println("------------- BEGIN Local workflow stage  -------------");
			}


		}
		System.out.println("======================================================================");
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


	protected void subscribe(QName notificationType, WorkflowInstanceHelperClient toSubscribe, String stageOperationQName){

		try{

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

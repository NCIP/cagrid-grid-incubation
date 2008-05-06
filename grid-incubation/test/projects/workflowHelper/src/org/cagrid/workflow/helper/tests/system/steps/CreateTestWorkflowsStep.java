package org.cagrid.workflow.helper.tests.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.PrintStream;
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
import org.cagrid.workflow.helper.client.WorkflowHelperClient;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.NotifyCallback;

public class CreateTestWorkflowsStep extends Step implements NotifyCallback  {
	
	
	private EndpointReferenceType helper_epr = null;
	private String containerBaseURL = null;
	
	// Synchronizes the access to variable 'isFinished' 
	private Lock isFinishedKey = new ReentrantLock();
	private Condition isFinishedCondition = isFinishedKey.newCondition();
	private Map<String, Boolean> stageIsFinished = new HashMap<String, Boolean>() ;
	
	private boolean isFinished = false;
	
	
	public CreateTestWorkflowsStep(EndpointReferenceType helper_epr, String containerBaseURL) {
		super();
		this.helper_epr = helper_epr;
		this.containerBaseURL  = containerBaseURL;
	}


	@Override
	public void runStep() throws Throwable {


		
		/** Instantiate each of the workflows: Fan in-Fan out, simple-type array forwarding, complex-type array forwarding,
		 * simple-type array streaming, complex-type array streaming  
		 * */

		final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
		final String SOAPENCODING_NAMESPACE = "http://schemas.xmlsoap.org/soap/encoding/";

		
		WorkflowHelperClient wf_helper = new WorkflowHelperClient(this.helper_epr); 
		
		// TODO Fill the variables below
		final GlobusCredential proxy = null;
		final EndpointReference manager_epr = null; 

		
		
		
		
		/*** BEGIN Service that will gather all the output and match against the expected ones ***/
		/*WorkflowInstanceHelperDescriptor validatorInstanceDesc = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();
		validatorInstanceDesc.setWorkflowID("Validator");
		validatorInstanceDesc.setWorkflowManagerEPR(manager_epr);
		
		
		WorkflowInstanceHelperClient validatorInstance = wf_helper.createWorkflowInstanceHelper(validatorInstanceDesc);
		
		WorkflowInvocationHelperDescriptor validatorInvocationDesc = new WorkflowInvocationHelperDescriptor();
		validatorInvocationDesc.setOperationQName(new QName("http://validateoutputsservice.test.workflow.cagrid.org/ValidateOutputsService", "ValidateTestOutputRequest"));
		validatorInvocationDesc.setServiceURL(containerBaseURL + "/wsrf/services/cagrid/ValidateOutputsService");

		
		WorkflowInvocationHelperClient validatorInvocation = validatorInstance.createWorkflowInvocationHelper(validatorInvocationDesc);
		
		
		// Configure inputs
		OperationInputMessageDescriptor validatorInputDesc = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam = new InputParameterDescriptor[3];
		inputParam[0] = new InputParameterDescriptor(new QName("test1Param1"), new QName(XSD_NAMESPACE, "int"));
		inputParam[1] = new InputParameterDescriptor(new QName("test1Param2"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType[]"));
		inputParam[2] = new InputParameterDescriptor(new QName("test1Param3"), new QName(XSD_NAMESPACE, "boolean"));
		
		validatorInputDesc.setInputParam(inputParam);
		validatorInvocation.configureInput(validatorInputDesc);
		
		
		
	
		// Configure outputs: it has none
		OperationOutputTransportDescriptor validatorOutput = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor[] paramDescriptor = new OperationOutputParameterTransportDescriptor[0];
		validatorOutput.setParamDescriptor(paramDescriptor );
		validatorInvocation.configureOutput(validatorOutput);
		
		
		// Subscribe for status notifications
		try{
			validatorInvocation.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), this);
		}
		catch(Throwable t){
			t.printStackTrace();
			Assert.fail(t.getMessage());
		} // */
		/* END Workflow outputs validator service  */
		
		
		

		/*** Testing arrays as services' input ***/
		System.out.println("BEGIN Testing arrays");
		
		/** complex type arrays **/
		System.out.println("Complex arrays as input...");
		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor1 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		workflowDescriptor1.setWorkflowID("WorkFlow1");
		workflowDescriptor1.setWorkflowManagerEPR(manager_epr);
		
		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance1 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor1);

		// BEGIN ReceiveArrayService::ReceiveComplexArray	

		
		String access_url = containerBaseURL+"/wsrf/services/cagrid/ReceiveArrayService";
		WorkflowInvocationHelperDescriptor operation2 = new WorkflowInvocationHelperDescriptor();
		operation2.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "ReceiveComplexArrayRequest"));
		operation2.setServiceURL(access_url);
		//operation2.setOutput(); // This service has no output

		
		// create ReceiveArrayService
		WorkflowInvocationHelperClient client2 = wf_instance1.createWorkflowInvocationHelper(operation2);
		
		/*System.out.println("------------------------------");
		System.out.println(client2.getEndpointReference().toString());
		System.out.println(client2.getEndpointReference().getAddress());
		System.out.println(client2.getEndpointReference().getPortType());
		System.out.println(client2.getEndpointReference().getServiceName());
		System.out.println("------------------------------"); // */
		
		//this.stageIsFinished.put(client2.getEndpointReference().toString(), Boolean.FALSE); // Register to be monitored for status changes
		//System.out.println("Put "+ operation2.getOperationQName().getLocalPart() +" in hash: "+ client2.getEndpointReference().toString()); //DEBUG
		
		
		System.out.println("Configuring invocation helper"); //DEBUG

		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ras = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ras = new InputParameterDescriptor[3];
		inputParams_ras[0] = new InputParameterDescriptor(new QName("number"), new QName(XSD_NAMESPACE, "int"));
		inputParams_ras[1] = new InputParameterDescriptor(new QName("complexArray"), new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType[]"));
		inputParams_ras[2] = new InputParameterDescriptor(new QName("booleanValue"), new QName(XSD_NAMESPACE, "boolean"));
		inputMessage_ras.setInputParam(inputParams_ras);
		client2.configureInput(inputMessage_ras);
		// End InputMessage Descriptor

		// Creating an empty outputDescriptor
		OperationOutputTransportDescriptor outputDescriptor_ras = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ras [] = new OperationOutputParameterTransportDescriptor[0];

		// takes the reference to no service
		outputDescriptor_ras.setParamDescriptor(outParameterDescriptor_ras);
		client2.configureOutput(outputDescriptor_ras);

		// Set user proxy
		client2.setProxy(proxy);
		
		
		// Subscribe for status notifications
		try{
			this.stageIsFinished.put(client2.getEndpointReference().toString(), Boolean.FALSE); // Register to be monitored for status changes
			System.out.println("Put "+ operation2.getOperationQName().getLocalPart() +" in hash: "+ client2.getEndpointReference().toString()); //DEBUG

			System.out.println("Subscribing to "+ Status.getTypeDesc().getXmlType() +" notifications"); //DEBUG			
			client2.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), this);

			System.out.println("Subscription OK"); //DEBUG
		}
		catch(Throwable t){
			t.printStackTrace();
			return;
		} // */
		
		
		System.out.println("Setting params"); //DEBUG
		
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

		
		// create ReceiveArrayService		
		WorkflowInvocationHelperClient client_ca = wf_instance1.createWorkflowInvocationHelper(operation_ca);
		
		// Monitor status changes
		try{
			this.stageIsFinished.put(client_ca.getEndpointReference().toString(), Boolean.FALSE); // Register to be monitored for status changes
			System.out.println("Put "+ operation_ca.getOperationQName().getLocalPart() +" in hash: "+ client_ca.getEndpointReference().toString()); //DEBUG

			System.out.println("Subscribing to "+ Status.getTypeDesc().getXmlType() +" notifications"); //DEBUG			
			client_ca.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), this);

			System.out.println("Subscription OK"); //DEBUG
		}
		catch(Throwable t){
			t.printStackTrace();
			return;
		} // */


		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_ca = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_ca = new InputParameterDescriptor[0];
		inputMessage_ca.setInputParam(inputParams_ca);
		client_ca.configureInput(inputMessage_ca);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the only service that will receive the output (ReceiveArrayService)
		OperationOutputTransportDescriptor outputDescriptor_ca = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_ca [] = new OperationOutputParameterTransportDescriptor[1];// [2];

		// First destination: ReceiveArrayService::ReceiveComplexArray
		outParameterDescriptor_ca[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_ca[0].setParamIndex(1);
		outParameterDescriptor_ca[0].setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType[]"));
		outParameterDescriptor_ca[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_ca[0].setLocationQuery("/ns0:GetComplexArrayResponse");
		outParameterDescriptor_ca[0].setDestinationEPR(new EndpointReferenceType[]{ client2.getEndpointReference() });

		
		// Second destination: ValidateOutputsService::ValidateTestOutputs
		/*outParameterDescriptor_ca[1] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_ca[1].setParamIndex(1);
		outParameterDescriptor_ca[1].setType(new QName( SOAPENCODING_NAMESPACE ,"ComplexType[]"));
		outParameterDescriptor_ca[1].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_ca[1].setLocationQuery("/ns0:GetComplexArrayResponse");
		outParameterDescriptor_ca[1].setDestinationEPR(new EndpointReferenceType[]{ validatorInvocation.getEndpointReference() });// */
		
		// Set user proxy
		client_ca.setProxy(proxy);

		// takes the reference to ReceiveComplexArrayService
		outputDescriptor_ca.setParamDescriptor(outParameterDescriptor_ca);
		client_ca.configureOutput(outputDescriptor_ca);

		// Subscribe to receive status change notifications
		//client_ca.subscribe(org.cagrid.workflow.helper.descriptor.Status.getTypeDesc().getXmlType(), this);
		
		
		// END CreateArrayService::getComplexArray 
		System.out.println("OK"); // */
		
		this.waitUntilCompletion();
		

		//Assert.fail("You need to check the output and be sure that workflow actually worked.  When i run this i see alot of error on the server side.");
		
		
		
		/** simple type arrays **/
		/*System.out.print("Simple arrays as input...");
		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor2 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		workflowDescriptor2.setWorkflowID("WorkFlow2");
		workflowDescriptor2.setWorkflowManagerEPR(manager_epr);
		
		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance2 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor2);

		
		// BEGIN ReceiveArrayService::ReceiveArrayAndMore
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_ram = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		operation_ram.setWorkflowID("GeorgeliusWorkFlow");
		operation_ram.setOperationQName(new QName("http://receivearrayservice.introduce.cagrid.org/ReceiveArrayService", "ReceiveArrayAndMoreRequest"));
		operation_ram.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/ReceiveArrayService");
		//operation_ram.setOutputType(); // Service has no output


		// create ReceiveArrayService				
		WorkflowInvocationHelperClient serviceClient_ram = wf_instance2.createWorkflowInvocationHelper(operation_ram);
		 

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

		// Set user proxy
		serviceClient_ram.setProxy(proxy);

		// Set the values of the two arguments of simple type
		serviceClient_ram.setParameter(new InputParameter("999", 0));
		serviceClient_ram.setParameter(new InputParameter("true",2));
		// END ReceiveArrayService::ReceiveArrayAndMore

		
		
		// BEGIN CreateArrayService				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_cas = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		access_url = containerBaseURL+"/wsrf/services/cagrid/CreateArrayService";
		operation_cas.setWorkflowID("GeorgeliusWorkFlow");
		operation_cas.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetArrayRequest"));
		operation_cas.setServiceURL(access_url);
		operation_cas.setOutputType(new QName(SOAPENCODING_NAMESPACE, "string[]"));


		// create ReceiveArrayService				
		WorkflowInvocationHelperClient serviceClient_cas = wf_instance2.createWorkflowInvocationHelper( operation_cas);
		 

		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_cas = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_cas = new InputParameterDescriptor[0];
		inputMessage_cas.setInputParam(inputParams_cas);
		serviceClient_cas.configureInput(inputMessage_cas);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the only service that will receive the output (ReceiveArrayService)
		OperationOutputTransportDescriptor outputDescriptor_cas = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor_cas [] = new OperationOutputParameterTransportDescriptor[1];

		// First destination: ReceiveArrayService::ReceiveArrayAndMore
		outParameterDescriptor_cas[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor_cas[0].setParamIndex(1);
		outParameterDescriptor_cas[0].setType(new QName( SOAPENCODING_NAMESPACE ,"string[]"));
		outParameterDescriptor_cas[0].setQueryNamespaces(new QName[]{ new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "ns0"),
				new QName(XSD_NAMESPACE,"xsd")});
		outParameterDescriptor_cas[0].setLocationQuery("/ns0:GetArrayResponse");
		outParameterDescriptor_cas[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient_ram.getEndpointReference()});

		// Set user proxy
		serviceClient_cas.setProxy(proxy);

		// takes the reference to ReceiveArrayService
		outputDescriptor_cas.setParamDescriptor(outParameterDescriptor_cas);
		serviceClient_cas.configureOutput(outputDescriptor_cas);

		// END CreateArrayService 
		System.out.println("OK");
		System.out.println("END Testing arrays"); // */
		/** END test arrays **/ 

		
		
		
		
		/** FAN IN AND FAN OUT TEST **/
		/*System.out.println("BEGIN Testing fan in and fan out");
		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor3 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		workflowDescriptor3.setWorkflowID("WorkFlow2");
		workflowDescriptor3.setWorkflowManagerEPR(manager_epr);
		
		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance3 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor3);

		// BEGIN service 4				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation4 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();

		java.lang.String acess_url = containerBaseURL+"/wsrf/services/cagrid/Service4";
		operation4.setWorkflowID("GeorgeliusWorkFlow");
		operation4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation4.setServiceURL(acess_url);


		// Creating client of service 4
		WorkflowInvocationHelperClient serviceClient4 = wf_instance3.createWorkflowInvocationHelper(operation4);
		

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

		// Set user proxy
		serviceClient4.setProxy(proxy);

		// takes the reference to no service
		outputDescriptor4.setParamDescriptor(outParameterDescriptor4);
		serviceClient4.configureOutput(outputDescriptor4);

		// TODO Get the output of this service
		// END service 4

		
		
		// BEGIN service 2				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation_2 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service2";
		operation_2.setWorkflowID("GeorgeliusWorkFlow");
		operation_2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "CapitalizeRequest"));
		operation_2.setServiceURL(acess_url);
		operation_2.setOutputType(new QName(XSD_NAMESPACE, "string"));


		// create service 2				
		WorkflowInvocationHelperClient serviceClient2 = wf_instance3.createWorkflowInvocationHelper(operation_2);
		 

		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage_2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams_2 = new InputParameterDescriptor[1];
		inputParams_2[0] = new InputParameterDescriptor( new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"));
		inputMessage_2.setInputParam(inputParams_2);
		serviceClient2.configureInput(inputMessage_2);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor2 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor2 [] = new OperationOutputParameterTransportDescriptor[1];
		outParameterDescriptor2[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor2[0].setParamIndex(0);
		outParameterDescriptor2[0].setType(new QName("string"));
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service2.introduce.cagrid.org/Service2", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor2[0].setQueryNamespaces(namespaces);
		outParameterDescriptor2[0].setLocationQuery("/ns0:CapitalizeResponse");

		// Set user proxy
		serviceClient2.setProxy(proxy);

		// takes the reference to the service 4
		outParameterDescriptor2[0].setDestinationEPR(new EndpointReferenceType[]{ serviceClient4.getEndpointReference()});
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
		

		// create service 3				
		WorkflowInvocationHelperClient serviceClient3 = wf_instance3.createWorkflowInvocationHelper(operation3	);
		

		// Creating Descriptor of the InputMessage
		org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor inputMessage3 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParams3 = new InputParameterDescriptor[1];
		inputParams3[0] = new InputParameterDescriptor(new QName("str_length"), new QName(XSD_NAMESPACE, "int"));
		inputMessage3.setInputParam(inputParams3);
		serviceClient3.configureInput(inputMessage3);
		// End InputMessage Descriptor

		// Creating the outputDescriptor of the first Filter
		OperationOutputTransportDescriptor outputDescriptor3 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor3 [] = new OperationOutputParameterTransportDescriptor[1];
		outParameterDescriptor3[0] = new OperationOutputParameterTransportDescriptor();
		outParameterDescriptor3[0].setParamIndex(1);
		outParameterDescriptor3[0].setType(new QName(XSD_NAMESPACE, "string"));
		namespaces = new QName[]{ new QName(XSD_NAMESPACE, "xsd"), new QName("http://service3.introduce.cagrid.org/Service3", "ns0"),
				new QName(XSD_NAMESPACE, "xsd")};
		outParameterDescriptor3[0].setQueryNamespaces(namespaces);
		outParameterDescriptor3[0].setLocationQuery("/ns0:GenerateXResponse"); 

		// Set user proxy
		serviceClient3.setProxy(proxy);

		// takes the reference to the service 4
		outParameterDescriptor3[0].setDestinationEPR(new EndpointReferenceType[]{serviceClient4.getEndpointReference()});
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


		// Creating client of service 5
		WorkflowInvocationHelperClient serviceClient5 = wf_instance3.createWorkflowInvocationHelper(operation5);
		

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

		// Set user proxy
		serviceClient5.setProxy(proxy);

		// takes the reference to no service
		outParameterDescriptor5[0].setDestinationEPR(null);
		outputDescriptor5.setParamDescriptor(outParameterDescriptor5);
		serviceClient5.configureOutput(outputDescriptor5);

		// TODO Get the output of this service
		// END service 5

		
		// BEGIN service 1				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation1 = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		acess_url = containerBaseURL+"/wsrf/services/cagrid/Service1";
		operation1.setWorkflowID("GeorgeliusWorkFlow");
		operation1.setOperationQName(new QName("GenerateDataRequest"));
		operation1.setServiceURL(acess_url);
		operation1.setOutputType(new QName(XSD_NAMESPACE, "string"));


		// create service 1				
		WorkflowInvocationHelperClient serviceClient1 = wf_instance3.createWorkflowInvocationHelper(operation1);
		 

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

		// Set user proxy
		serviceClient1.setProxy(proxy);

		// set the only one parameter of this service.
		// now it have to run and set one Parameter of the service4
		String workflow_input = "george teadoro gordao que falou";
		System.out.println("Setting input for service 1: '"+workflow_input+"'");
		InputParameter inputService1 = new InputParameter(workflow_input, 0);
		serviceClient1.setParameter(inputService1);
		// END service 1 
		System.out.println("END Testing fan in and fan out"); // */
		/** END FAN IN AND FAN OUT TEST **/

		
		
		
		
		/** BEGIN streaming test **/
		/* System.out.println("BEGIN Testing streaming");

		// Streaming simple types 
		System.out.print("Streaming of simple-type arrays...");
		org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor workflowDescriptor5 = new org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor();

		workflowDescriptor5.setWorkflowID("WorkFlow5");
		workflowDescriptor5.setWorkflowManagerEPR(manager_epr);
		
		// Get helper client so we can create the invocation helpers
		WorkflowInstanceHelperClient wf_instance5 = wf_helper.createWorkflowInstanceHelper(workflowDescriptor5);

		
		// BEGIN service 4
		WorkflowInvocationHelperDescriptor operation_4 = new WorkflowInvocationHelperDescriptor();
		operation_4.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		operation_4.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/Service4");
		// operation_4.setOutputType(); // Void output expected
		
		
		
		// Creating client of service 4
		WorkflowInvocationHelperClient serviceClient_4 = wf_instance5.createWorkflowInvocationHelper(operation_4);
		

		
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

		// Set user proxy
		serviceClient_4.setProxy(proxy);

		// Setting second parameter
		serviceClient_4.setParameter(new InputParameter("simple type's streaming", 1));
		// END service 4

		
		
		
		// BEGIN service 2				
		// create service 2
		WorkflowInvocationHelperDescriptor operation__2 = new WorkflowInvocationHelperDescriptor();
		operation__2.setOperationQName(new QName("http://service2.introduce.cagrid.org/Service2", "CapitalizeRequest"));
		operation__2.setServiceURL(containerBaseURL+"/wsrf/services/cagrid/Service2");
		operation__2.setOutputType(new QName(XSD_NAMESPACE, "string"));
		WorkflowInvocationHelperClient serviceClient_2 = wf_instance5.createWorkflowInvocationHelper(operation__2);
		 

		
		// Creating Descriptor of the InputMessage
		OperationInputMessageDescriptor inputMessage__2 = new OperationInputMessageDescriptor();
		InputParameterDescriptor[] inputParam__2 = new InputParameterDescriptor[1];
		inputParam__2[0] = new InputParameterDescriptor(new QName("uncapitalized"), new QName(XSD_NAMESPACE, "string"));
		inputMessage__2.setInputParam(inputParam__2 );
		serviceClient_2.configureInput(inputMessage__2);
		// End InputMessage Descriptor

		
		// Set user proxy
		serviceClient_2.setProxy(proxy);

		// takes the reference to Service4
		OperationOutputTransportDescriptor outputDescriptor__2 = new OperationOutputTransportDescriptor();
		OperationOutputParameterTransportDescriptor outParameterDescriptor__2 [] = new OperationOutputParameterTransportDescriptor[1];
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
		WorkflowInvocationHelperClient serviceClient_cs = wf_instance5.createWorkflowInvocationHelper(operation__cas);
		 

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

		// Set user proxy
		serviceClient_cs.setProxy(proxy);

		outputDescriptor_cs.setParamDescriptor(outParameterDescriptor_cs);
		serviceClient_cs.configureOutput(outputDescriptor_cs);
		
		
		// END CreateArrayService
		System.out.print("OK");  // */

		

		/* Streaming complex types */
		/*System.out.print("Streaming of complex-type arrays...");
		// BEGIN service 4				
		// Creating client of service 4
		WorkflowInvocationHelperClient serviceClient__4 = wf_instance5.createWorkflowInvocationHelper(operation4);
		

		// Creating Descriptor of the InputMessage
		serviceClient__4.configureInput(inputMessage4);
		// End InputMessage Descriptor

		// Setting output descriptor
		outputDescriptor4.setParamDescriptor(outParameterDescriptor4);
		serviceClient__4.configureOutput(outputDescriptor4);

		// Set user proxy
		serviceClient__4.setProxy(proxy);

		// Setting second parameter
		serviceClient__4.setParameter(new InputParameter("complex type's streaming", 1));
		// END service 4

		// BEGIN CreateArrayService::getComplexArray				
		org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor operation__ca = new org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor();
		access_url = containerBaseURL+"/wsrf/services/cagrid/CreateArrayService";
		operation__ca.setWorkflowID("GeorgeliusWorkFlow");
		operation__ca.setOperationQName(new QName("http://createarrayservice.introduce.cagrid.org/CreateArrayService", "GetComplexArrayRequest"));
		operation__ca.setServiceURL(access_url);
		operation__ca.setOutputType(new QName("http://systemtests.workflow.cagrid.org/SystemTests", "ComplexType[]"));


		// create ReceiveArrayService				
		WorkflowInvocationHelperClient serviceClient__ca = wf_instance5.createWorkflowInvocationHelper(operation__ca);
		

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
		

		// Set user proxy
		serviceClient__ca.setProxy(proxy);

		// takes the reference to ReceiveComplexArrayService
		outputDescriptor__ca.setParamDescriptor(outParameterDescriptor__ca);
		serviceClient__ca.configureOutput(outputDescriptor__ca);

		// END CreateArrayService::getComplexArray 
		System.out.println("OK");

		System.out.println("END Testing streaming"); // */
		/** END streaming test **/

		System.out.println("END ALL TESTS");
		return;
	}


	private void waitUntilCompletion() {
		
		System.out.println("Waiting for workflow notification of FINISH status");//DEBUG
		
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
		
		
		//DEBUG
		PrintStream log = null;
		/*try {
			log = new PrintStream(new File("C:\\createTest_Deliver.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} // */
		log = System.out;
		log.println("[CreateTestWorkflowsStep] Received message of type "+ message_qname +" from "+ stageKey);
		
		
		// Handle status change notifications
		if(isStatusChange){
			Status status = null;;
			try {
				status = (Status) actual_property.getValueAsType(message_qname, Status.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			log.println("[deliver] Received new status value: "+ status.toString()); //DEBUG
			
			if(status.equals(Status.ERROR)){ // Dies when an error is reported
				Assert.fail("Received ERROR notification from "+ stageKey);
			}
			else if(status.equals(Status.FINISHED)){
				
				this.isFinishedKey.lock();
				try{
				
					if( this.stageIsFinished.containsKey(stageKey) ){
						
						this.stageIsFinished.remove(stageKey);
						this.stageIsFinished.put(stageKey, Boolean.TRUE);
						
						printMap(this.stageIsFinished);//DEBUG
						
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

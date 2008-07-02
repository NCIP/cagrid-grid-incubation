package org.cagrid.workflow.manager.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlException;
import org.cagrid.workflow.helper.client.WorkflowHelperClient;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.manager.descriptor.WorkflowPortionDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowStageDescriptor;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResource;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResourceHome;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.cagrid.workflow.manager.service.parser.bpel.BpelParser;
import org.cagrid.workflow.manager.service.parser.workflowDescriptor.WorkflowDescriptorParser;
import org.oasisOpen.docs.wsbpel.x20.process.executable.ProcessDocument;
import org.oasisOpen.docs.wsbpel.x20.process.executable.TCopy;
import org.oasisOpen.docs.wsbpel.x20.process.executable.TInvoke;
import org.oasisOpen.docs.wsbpel.x20.process.executable.TQuery;
import org.oasisOpen.docs.wsbpel.x20.process.executable.TTo;
import org.tempuri.xmlSchema.OperationsDescriptorDocument;
import org.w3c.dom.Node;

/**
 * I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class WorkflowManagerServiceImpl extends WorkflowManagerServiceImplBase {

	private static final Log logger = LogFactory.getLog(WorkflowManagerServiceImpl.class);

	public WorkflowManagerServiceImpl() throws RemoteException {
		super();
	}


	public org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference createWorkflowManagerInstanceFromBpel(java.lang.String bpelDescription,java.lang.String operationsDescription,org.apache.axis.message.addressing.EndpointReferenceType managerEPR) throws RemoteException {

		logger.debug("Manager Service muito doido"); 

		org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
		WorkflowManagerInstanceResourceHome home;
		// BaseResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext
		.getCurrentContext();
		String servicePath = ctx.getTargetService();

		final int workflowID = new Random(System.currentTimeMillis()).nextInt();;

		// workflowManagerInstanceResourceHome
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME
		+ servicePath + "/" + "workflowManagerInstanceHome";
		System.out.println("homeName = " + homeName);
		WorkflowManagerInstanceResource thisResource = null;
		try {
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (WorkflowManagerInstanceResourceHome) initialContext
			.lookup(homeName);
			resourceKey = home.createResource();
			System.out.println("resourceKey = " + resourceKey);

			// Grab the newly created resource
			thisResource = (WorkflowManagerInstanceResource) home.find(resourceKey);

			// set the workflow descriptor on the helper instance
//			thisResource
//			.setWorkflowManagerInstanceDescriptor(workflowManagerInstanceDescriptor);

			String transportURL = (String) ctx
			.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
			transportURL += "WorkflowManagerInstance";

			System.out.println("transportURL = " + transportURL);
			epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL, resourceKey);

			// Set the resource identifier, that is derived from its EPR
			thisResource.setEPRString(new EndpointReference(epr).toString());

		} catch (Exception e) {
			throw new RemoteException("Error looking up WorkflowManagerInstance  home:"
					+ e.getMessage(), e);
		}

		/** Parse BPEL description */  
		Reader bpelReader = new StringReader(bpelDescription);
		ProcessDocument bpelDoc = null;
		try {
			bpelDoc = ProcessDocument.Factory.parse(bpelReader);
		} catch (XmlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Throwable t){
			t.printStackTrace();
		} // */



		/** Parse additional description file */ 
		Reader operationsDescReader = new StringReader(operationsDescription);
		OperationsDescriptorDocument operationsDesc = null;
		try {
//			operationsDesc = OperationsDescriptorParser.parse(operationsDescReader);
		} catch (Exception e) {
			throw new RemoteException(e.getMessage(), e);
		} // */

		// Necessary information for establishing links between stages: <stageName, EPR> and <stageName, input parameters> associations
		Map<String, EndpointReferenceType> stageToEPR = new HashMap<String, EndpointReferenceType>();
		Map<String, OperationInputMessageDescriptor> stageToInputParams = new HashMap<String, OperationInputMessageDescriptor>();

		/* Obtain the list of InstanceHelpers and corresponding InvocationHelpers to create, grouped by InstanceHelper */
		WorkflowPortionDescriptor[] wfPortions = BpelParser.extractWorkflowPortions(bpelDoc, operationsDesc, String.valueOf(workflowID)); 

		/* Instantiate each portion of the workflow */
		for(int i = 0; i < wfPortions.length; i++){    

			WorkflowPortionDescriptor curr_portion = wfPortions[i];

			try{

				// Get HelperService location
				String helperServiceURL = curr_portion.getWorkflowHelperServiceLocation();
				EndpointReferenceType helperServiceEPR = new EndpointReference(new Address(helperServiceURL));
				WorkflowHelperClient helperClient = new WorkflowHelperClient(helperServiceEPR);

				// Create InstanceHelper
				WorkflowInstanceHelperDescriptor instanceDesc = new WorkflowInstanceHelperDescriptor();
				instanceDesc.setWorkflowID(String.valueOf(workflowID));
				instanceDesc.setWorkflowManagerEPR(epr);
				WorkflowInstanceHelperClient instanceHelperClient = helperClient.createWorkflowInstanceHelper(instanceDesc);

				// Create corresponding InvocationHelpers
				WorkflowStageDescriptor[] invocationHelperDescs = curr_portion.getInvocationHelperDescs();
				for(int j=0 ; j < invocationHelperDescs.length; j++){

					// Get necessary information from current InvocationHelper
					WorkflowStageDescriptor curr_desc = invocationHelperDescs[j];
					QName operationQName = curr_desc.getBasicDescription().getOperationQName();
					QName outputType = curr_desc.getBasicDescription().getOutputType();
					String serviceURL = curr_desc.getBasicDescription().getServiceURL();

					// Instantiate workflow stage
					WorkflowInvocationHelperDescriptor invocationDesc = new WorkflowInvocationHelperDescriptor();
					invocationDesc.setOperationQName(operationQName);
					invocationDesc.setOutputType(outputType);
					invocationDesc.setServiceURL(serviceURL);
					invocationDesc.setWorkflowID(String.valueOf(workflowID));
					invocationDesc.setWorkflowManagerEPR(epr);
					//invocationDesc.setWorkflowInvocationSecurityDescriptor(workflowInvocationSecurityDescriptor); // Coming soon..
					WorkflowInvocationHelperClient currInvocationClient = instanceHelperClient.createWorkflowInvocationHelper(invocationDesc);

					// Configure stage's input (the output configuration can't be done yet, since we don't know the links between stages)
					OperationInputMessageDescriptor inputDesc = curr_desc.getInputsDescription();
					currInvocationClient.configureInput(inputDesc);
					//currInvocationClient.setParameters(inputParameters); // TODO Set static parameters

					// Store operation's EPR and input parameters for further use
					EndpointReferenceType stageEPR = currInvocationClient.getEndpointReference();
					stageToEPR.put(operationQName.toString(), stageEPR);
					stageToInputParams.put(operationQName.toString(), inputDesc);
				}

			} catch (MalformedURIException e) {
				e.printStackTrace();
				throw new RemoteException(e.getMessage(), e);
			}
		}

		// Retrieve all namespace declarations
		QName[] documentNamespaces = null;
		try {
			documentNamespaces = BpelParser.extractNamespaces(bpelDoc);  
		} catch (Exception e1) {
			e1.printStackTrace();
		}  

		/* Establish links between the stages */
		Map<String, ArrayList<TCopy>> sourceVariableToCopyCommand = BpelParser.extractCopyOperations(bpelDoc);  // Retrieve all copy statements
		Map<String, List<TInvoke>> operationToInvokeCommand = null;
		try {
			operationToInvokeCommand = BpelParser.extractInvokeOperations(bpelDoc, documentNamespaces); // Retrieve all invoke commands
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new RemoteException(e1.getMessage(), e1);
		}   

		Map<String, Integer> numberOfCopiesPerDestinationVariable = new HashMap<String, Integer>();

		/* Configure output transport between stages */
		Set<Entry<String, EndpointReferenceType>> stageEPRs = stageToEPR.entrySet();
		Iterator<Entry<String, EndpointReferenceType>> stageEPR_iter = stageEPRs.iterator();
		while( stageEPR_iter.hasNext() ){

			Entry<String, EndpointReferenceType> curr_entry = stageEPR_iter.next();
			String operation = curr_entry.getKey();
			EndpointReferenceType EPR = curr_entry.getValue();

			// Get input variable for the stage that executes the current operation
			// TODO Enable support for multiple stages that perform the same operation
			if( operationToInvokeCommand.containsKey(operation) ){

				List<TInvoke> stages = operationToInvokeCommand.get(operation);
				Iterator<TInvoke> stageIter = stages.iterator();
				while( stageIter.hasNext() ){

					// Get output variable for current stage
					TInvoke currStage = stageIter.next();
					String outputVar = currStage.getOutputVariable(); 

					// Get all copy commands that depend on the current source variable and configure the output transport for the current stage
					List<TCopy> copiesToPerform = sourceVariableToCopyCommand.get(outputVar);

					OperationOutputTransportDescriptor outputDescriptor = createOperationOutputTransportDescriptor( copiesToPerform,  
							documentNamespaces, numberOfCopiesPerDestinationVariable, operationToInvokeCommand, stageToEPR ); 
					WorkflowInvocationHelperClient stageClient = null;
					try {
						stageClient = new WorkflowInvocationHelperClient(EPR);
					} catch (MalformedURIException e) {
						e.printStackTrace();
					}
					stageClient.configureOutput(outputDescriptor);

					break; // This break disables the configuration of more than one stage performing the same operation for now
				}
			}
		}

		// return the typed EPR
		WorkflowManagerInstanceReference ref = new WorkflowManagerInstanceReference();
		ref.setEndpointReference(epr);

		// DEBUG
		System.out.println("END ManagerService");

		return ref;
	}

	/** Create description of parameters transport for a workflow */
	private OperationOutputTransportDescriptor createOperationOutputTransportDescriptor(
			List<TCopy> copiesToPerform, QName[] documentNamespaces, Map<String, Integer> numberOfCopiesPerDestinationVariable,
			Map<String, List<TInvoke>> operationToInvokeCommand, Map<String, EndpointReferenceType>  operationToEPR) {

		OperationOutputTransportDescriptor outDesc = new OperationOutputTransportDescriptor();
		ArrayList<OperationOutputParameterTransportDescriptor> paramDescs = new ArrayList<OperationOutputParameterTransportDescriptor>();

		Iterator<TCopy> copiesIter = copiesToPerform.iterator();
		while(copiesIter.hasNext()){

			OperationOutputParameterTransportDescriptor nextItem = new OperationOutputParameterTransportDescriptor();
			TCopy currParamDesc = copiesIter.next();

			// Retrieve XPath query
			TTo elementTo = currParamDesc.getTo();
			TQuery query = elementTo.getQuery();

			if( query != null ){

				Node queryNode = query.getDomNode();
				Node queryTextNode = queryNode.getFirstChild();
				String xpathQuery = queryTextNode.getNodeValue();
				nextItem.setLocationQuery(xpathQuery);
			}

			// Retrieve type of data that will be transferred
			// FIXME BPEL doesn't support type declaration within a copy operation. How would we do streaming between stages if we can't infer the output type of a stage?
			nextItem.setType(new QName("string"));  //nextItem.setType(transferredDataQName);

			// Set query namespaces
			nextItem.setQueryNamespaces(documentNamespaces);

			// We assume each part of a particular destination variable are set in the same order as they 
			// appear in the corresponding operation's arguments list
			String destinationVariable = elementTo.getVariable();
			int alreadySetParameters = numberOfCopiesPerDestinationVariable.containsKey(destinationVariable) ? 
					numberOfCopiesPerDestinationVariable.get(destinationVariable) : 0;
					numberOfCopiesPerDestinationVariable.put(destinationVariable, alreadySetParameters + 1); // Update number of already set parameters
					nextItem.setParamIndex(alreadySetParameters);

					// Get EPR of service which input is found in the destination variable
					EndpointReferenceType destinationEPR = null;
					try {
						destinationEPR = findStageEPRForInputVariable( destinationVariable, 
								operationToInvokeCommand,  operationToEPR);
					} catch (Exception e) {
						e.printStackTrace();
					}

					nextItem.setDestinationEPR(new EndpointReferenceType[]{ destinationEPR });
					paramDescs.add(nextItem);
		}

		OperationOutputParameterTransportDescriptor[] paramDescriptor = paramDescs.toArray(new OperationOutputParameterTransportDescriptor[0]);
		outDesc.setParamDescriptor(paramDescriptor);

		return outDesc;
	}

	/* Retrieve the Endpoint Reference corresponding to the stage that is associated with the received input variable  */
	private EndpointReferenceType findStageEPRForInputVariable(
			String destinationVariable,
			Map<String, List<TInvoke>> operationToInvokeCommand,
			Map<String, EndpointReferenceType> operationToEPR) throws Exception {

		EndpointReferenceType stageEPR;

		// Iterate over the invoke commands looking for the stage referring to the received input variable
		String stageName = null;
		Set<Entry<String, List<TInvoke>>> entries = operationToInvokeCommand.entrySet();
		Iterator<Entry<String, List<TInvoke>>> entries_iter = entries.iterator();

		boolean stageFound = false;
		while( entries_iter.hasNext() ){

			Entry<String, List<TInvoke>> curr_entry = entries_iter.next();
			List<TInvoke> invokeCmds = curr_entry.getValue();

			// Examine the invoke commands looking for the one that uses the received variable as input
			Iterator<TInvoke> invokeIter = invokeCmds.iterator();
			while(invokeIter.hasNext()){

				TInvoke curr_invoke = invokeIter.next();
				String curr_inputVar = curr_invoke.getInputVariable();

				// If this is the stage we're looking for, store it and stop the search
				if( curr_inputVar.equals(destinationVariable) ){

					stageName = curr_entry.getKey();
					stageFound = true;
					break;
				}
			}

			if( stageFound ){
				break;
			}
		}

		if(stageName == null){
			throw new Exception("Received variable is not referenced in any invoke commands. (variable name="
					+ destinationVariable +")");
		}

		// Get the EPR associated with the stage
		if( operationToEPR.containsKey(stageName)){
			stageEPR = operationToEPR.get(stageName);
		}
		else throw new Exception("No EndpointReference found for stage '"+ stageName +"'");

		return stageEPR;
	}

	// Transform a name in the from prefix:name to a QName with the corresponding namespace URI
	private QName retrieveQNameForType(String transferredDataType, QName[] documentNamespaces) throws RemoteException {

		String namespacePrefix;
		int colonIndex = transferredDataType.indexOf(':');

		String unprefixedType = null;
		// If no prefix is found, the type is supposed to belong to the document's target namespace
		if( colonIndex == -1 ){

			namespacePrefix = "tns";
		}
		// Otherwise, we extract the prefix from the string
		else {

			namespacePrefix = transferredDataType.substring(0, colonIndex);
			unprefixedType  = transferredDataType.substring(colonIndex);
		}

		// Look for the prefix among known namespaces
		String namespace = null;
		for(int i=0 ; i < documentNamespaces.length; i++){

			if( documentNamespaces[i].getPrefix().equals(namespacePrefix) ){

				namespace = documentNamespaces[i].getNamespaceURI();
				break;
			}
		}

		// Build QName for namespace
		QName output = null;
		if( namespace != null ){

			output = new QName(namespace, unprefixedType);
		}
		else throw new RemoteException("Namespace for '"+ transferredDataType +"' not found");

		return output;
	}

	/**
	 * Instantiate a workflow and give the caller a means of interacting with it
	 * 
	 * @param workflowDesc Description of the workflow to instantiate
	 * @return a handler to communicate with the newly created resource
	 * 
	 * */
	public org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference createWorkflowManagerInstance(java.lang.String xmlWorkflowDescription) throws RemoteException {


		org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor workflowDesc = WorkflowDescriptorParser.parseWorkflowDescriptor(xmlWorkflowDescription);

		return this.createWorkflowManagerInstanceFromObjectDescriptor(workflowDesc);
	}

	
	public org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference createWorkflowManagerInstanceFromObjectDescriptor(org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor workflowDesc) throws RemoteException {


		org.apache.axis.message.addressing.EndpointReferenceType managerInstanceEpr = new org.apache.axis.message.addressing.EndpointReferenceType();
		WorkflowManagerInstanceResourceHome home;
		// BaseResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext
		.getCurrentContext();
		String servicePath = ctx.getTargetService();

		final int workflowID = new Random(System.currentTimeMillis()).nextInt();;

		// workflowManagerInstanceResourceHome
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME
		+ servicePath + "/" + "workflowManagerInstanceHome";
		System.out.println("homeName = " + homeName);
		WorkflowManagerInstanceResource thisResource = null;
		try {
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (WorkflowManagerInstanceResourceHome) initialContext
			.lookup(homeName);
			resourceKey = home.createResource();
			System.out.println("resourceKey = " + resourceKey);

			// Grab the newly created resource
			thisResource = (WorkflowManagerInstanceResource) home.find(resourceKey);

			// set the workflow descriptor on the helper instance
			thisResource.setWorkflowManagerInstanceDescriptor(workflowDesc);

			String transportURL = (String) ctx
			.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
			transportURL += "WorkflowManagerInstance";

			System.out.println("transportURL = " + transportURL);
			managerInstanceEpr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL, resourceKey);

			// Set the resource identifier, that is derived from its EPR
			thisResource.setEPRString(new EndpointReference(managerInstanceEpr).toString());

		} catch (Exception e) {
			throw new RemoteException("Error looking up WorkflowManagerInstance  home:"
					+ e.getMessage(), e);
		}

		HashMap<Integer, EndpointReferenceType> stageID2EPR = new HashMap<Integer, EndpointReferenceType>(); // Stages' EPR are stored here
		HashMap<Integer, OperationOutputTransportDescriptor> stageID2OutputDesc = new HashMap<Integer, OperationOutputTransportDescriptor>();   // Partial output description for stages of the workflow

		// Retrieve the description of operations that will be executed (grouped by container) 
		WorkflowPortionDescriptor[] workflowParts = workflowDesc.getWorkflowParts();

		for(int i=0; i < workflowParts.length; i++){

			try {

				WorkflowPortionDescriptor currPart = workflowParts[i];  

				// Create a client to communicate with the WorkflowHelper
				String helperServiceURL = currPart.getWorkflowHelperServiceLocation();
				WorkflowHelperClient helperClient = new WorkflowHelperClient(helperServiceURL);

				// Instantiate an InstanceHelper for the current part of the workflow
				WorkflowInstanceHelperDescriptor instanceDesc = currPart.getInstanceHelperDesc();
				instanceDesc.setWorkflowManagerEPR(managerInstanceEpr);

				WorkflowInstanceHelperClient instanceHelperClient;
				instanceHelperClient = helperClient.createWorkflowInstanceHelper(instanceDesc);

				// Instantiate each one of the stages that are supposed to execute in the same container 
				WorkflowStageDescriptor[] stagesDesc = currPart.getInvocationHelperDescs();
				for(int j=0; j < stagesDesc.length; j++){

					// Instantiate current stage
					WorkflowStageDescriptor curr_stageDesc = stagesDesc[j];
					WorkflowInvocationHelperDescriptor basicDesc = curr_stageDesc.getBasicDescription();
					WorkflowInvocationHelperClient currInvocationClient = instanceHelperClient.createWorkflowInvocationHelper(basicDesc);

					// Configure input
					OperationInputMessageDescriptor inputDesc = curr_stageDesc.getInputsDescription();
					currInvocationClient.configureInput(inputDesc);

					// Output can't be configured here because we don't have the EPR of each workflow stage, 
					// so we store current EPR for using later
					int stageID = curr_stageDesc.getGlobalUniqueIdentifier();
					EndpointReferenceType currStageEpr = currInvocationClient.getEndpointReference();
					if( stageID2EPR.containsKey(stageID) ){
						logger.warn("Stage identified by key '"+ stageID +"' found more than once. Overwriting previous EndpointReference");
					}
					stageID2EPR.put(Integer.valueOf(stageID), currStageEpr);
					stageID2OutputDesc.put(Integer.valueOf(stageID), curr_stageDesc.getOutputTransportDescriptor());
				}


				// Register current WorkflowInstanceHelper in the current ManagerInstance
				thisResource.registerInstanceHelper(instanceHelperClient.getEndpointReference(), helperServiceURL);


			} catch (MalformedURIException e) {
				e.printStackTrace();
			}
		}


		// Configure the outputs of each workflow stage
		Set<Entry<Integer,OperationOutputTransportDescriptor>> entries = stageID2OutputDesc.entrySet();
		Iterator<Entry<Integer, OperationOutputTransportDescriptor>> iter = entries.iterator();
		while( iter.hasNext() ){

			// Retrieve required information to configure the output of a workflow stage
			Entry<Integer, OperationOutputTransportDescriptor> currEntry = iter.next();
			Integer stageID = currEntry.getKey();
			OperationOutputTransportDescriptor outputDesc = currEntry.getValue();
			OperationOutputParameterTransportDescriptor[] outputParams = outputDesc.getParamDescriptor();

			// Configure the forwarding of current stage's output to every stage that needs it
			if( outputParams != null ){
				for(int i=0; i < outputParams.length; i++){

					// Retrieve the destination stage's EPR
					OperationOutputParameterTransportDescriptor curr_outParam = outputParams[i];
					int destinationStageID = curr_outParam.getDestinationGlobalUniqueIdentifier();
					EndpointReferenceType destinationEPR = stageID2EPR.get(Integer.valueOf(destinationStageID));

					outputParams[i].setDestinationEPR(new EndpointReferenceType[]{ destinationEPR });
				}
			}

			// For the current workflow stage, all output destination's EPRs are set. So, we can configure its output transport
			EndpointReferenceType currStageEPR = stageID2EPR.get(stageID);
			WorkflowInvocationHelperClient currStageClient = null;
			try {
				currStageClient = new WorkflowInvocationHelperClient(currStageEPR);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			}

			currStageClient.configureOutput(outputDesc);
		}


		// Store stages' EPRs so we can start each one of them when asked by the user
		thisResource.storeStagesEPRs(stageID2EPR);




		// return the typed EPR
		WorkflowManagerInstanceReference ref = new WorkflowManagerInstanceReference();
		ref.setEndpointReference(managerInstanceEpr);

		return ref;  
	}
	
}


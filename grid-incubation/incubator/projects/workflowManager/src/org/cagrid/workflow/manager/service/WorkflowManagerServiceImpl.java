package org.cagrid.workflow.manager.service;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.workflow.helper.client.WorkflowHelperClient;
import org.cagrid.workflow.helper.descriptor.CDSAuthenticationMethod;
import org.cagrid.workflow.helper.descriptor.DeliveryPolicy;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.SecureConversationInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.descriptor.SecureMessageInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.descriptor.TLSInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationHelperDescriptor;
import org.cagrid.workflow.helper.descriptor.WorkflowInvocationSecurityDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.helper.util.CredentialHandlingUtil;
import org.cagrid.workflow.manager.descriptor.WorkflowInputParameter;
import org.cagrid.workflow.manager.descriptor.WorkflowInputParameters;
import org.cagrid.workflow.manager.descriptor.WorkflowOutputParameterTransportDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowOutputTransportDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowPortionDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowPortionsDescriptor;
import org.cagrid.workflow.manager.descriptor.WorkflowStageDescriptor;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResource;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResourceHome;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.cagrid.workflow.manager.util.WorkflowDescriptorParser;
import org.globus.gsi.GlobusCredential;

/**
 * I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class WorkflowManagerServiceImpl extends WorkflowManagerServiceImplBase {

	private static final Log logger = LogFactory.getLog(WorkflowManagerServiceImpl.class);
	private static final String CDS_PATH_IN_CONTAINER = "/wsrf/services/cagrid/CredentialDelegationService";

	public WorkflowManagerServiceImpl() throws RemoteException {
		super();
	}

	/*

	// Create description of parameters transport for a workflow 
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

	// Retrieve the Endpoint Reference corresponding to the stage that is associated with the received input variable  
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

	// */

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
  public org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference createWorkflowManagerInstanceFromObjectDescriptor(org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor workflowDesc) throws RemoteException {

		org.apache.axis.message.addressing.EndpointReferenceType managerInstanceEpr = new org.apache.axis.message.addressing.EndpointReferenceType();
		WorkflowManagerInstanceResourceHome home;
		// BaseResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext
		.getCurrentContext();
		String servicePath = ctx.getTargetService();

		final int workflowID = new Random(System.currentTimeMillis()).nextInt();

		// workflowManagerInstanceResourceHome
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME
		+ servicePath + "/" + "workflowManagerInstanceHome";
		logger.info("homeName = " + homeName);
		WorkflowManagerInstanceResource thisResource = null;
		try {
			// Add GLOBUS_LOCATION to the system properties
			String globus_location = System.getenv("GLOBUS_LOCATION");
			Properties sys_properties = System.getProperties();
			sys_properties.setProperty("GLOBUS_LOCATION", globus_location);
			System.setProperties(sys_properties);
			
			
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (WorkflowManagerInstanceResourceHome) initialContext
			.lookup(homeName);
			resourceKey = home.createResource();
			logger.info("resourceKey = " + resourceKey);

			// Grab the newly created resource
			thisResource = (WorkflowManagerInstanceResource) home.find(resourceKey);

			// set the workflow descriptor on the helper instance
			thisResource.setWorkflowID(workflowID);
			thisResource.startWallTimeMeasurement();
			thisResource.setWorkflowManagerInstanceDescriptor(workflowDesc);

			String transportURL = (String) ctx
			.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
			transportURL += "WorkflowManagerInstance";

			logger.info("transportURL = " + transportURL);
			managerInstanceEpr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL, resourceKey);

			// Set the resource identifier, that is derived from its EPR
			thisResource.setEPRString(new EndpointReference(managerInstanceEpr).toString());

		} catch (Exception e) {
			throw new RemoteException("Error looking up WorkflowManagerInstance  home:"
					+ e.getMessage(), e);
		}

		HashMap<Integer, EndpointReferenceType> stageID2EPR = new HashMap<Integer, EndpointReferenceType>(); // Stages' EPR are stored here
		HashMap<Integer, OperationOutputTransportDescriptor> stageID2OutputDesc = new HashMap<Integer, OperationOutputTransportDescriptor>();   // Partial output description for stages of the workflow

		Set<String> alreadyRetrievedCredentials = new HashSet<String>();

		// Retrieve the description of operations that will be executed (grouped by container) 
		logger.info("Start creating local workflows");
		final WorkflowPortionsDescriptor localWorkflows = workflowDesc.getLocalWorkflows();
		WorkflowPortionDescriptor[] workflowParts = localWorkflows.getLocalWorkflowDesc();

		for(int i=0; i < workflowParts.length; i++){

			try {

				logger.info("Creating local workflow "+ (i+1) +" of " + workflowParts.length);
				WorkflowPortionDescriptor currPart = workflowParts[i];  

				// Create a client to communicate with the WorkflowHelper
				String helperServiceURL = currPart.getWorkflowHelperServiceLocation();
				WorkflowHelperClient helperClient = new WorkflowHelperClient(helperServiceURL);

				// Instantiate an InstanceHelper for the current part of the workflow
				WorkflowInstanceHelperDescriptor instanceDesc = currPart.getInstanceHelperDesc();
				instanceDesc.setWorkflowManagerEPR(managerInstanceEpr);

				WorkflowInstanceHelperClient instanceHelperClient;
				instanceHelperClient = helperClient.createWorkflowInstanceHelper(instanceDesc);
				String helperDN = helperClient.getIdentity();

				// Instantiate each one of the stages that are supposed to execute in the same container 
				logger.info("Creating stages");
				WorkflowStageDescriptor[] stagesDesc = currPart.getInvocationHelperDescs();
				for(int j=0; j < stagesDesc.length; j++){

					// Instantiate current stage
					logger.info("Creating stage "+ (j+1) +" of "+ stagesDesc.length);
					WorkflowStageDescriptor curr_stageDesc = stagesDesc[j];
					WorkflowInvocationHelperDescriptor basicDesc = curr_stageDesc.getBasicDescription();
					logger.info("Stage name ID: ("+ curr_stageDesc.getGlobalUniqueIdentifier() +", "
							+ basicDesc.getOperationQName()+')');
					WorkflowInvocationHelperClient currInvocationClient = instanceHelperClient.createWorkflowInvocationHelper(basicDesc);  // Resource creation  
					thisResource.registerInvocationHelper(currInvocationClient.getEPRString(), basicDesc.getOperationQName());   // Create association from current stage to its operation name 

					// If necessary, retrieve delegated credential for current stage and register it in the WorkflowInstanceHelper 
					WorkflowInvocationSecurityDescriptor securityDesc = basicDesc.getWorkflowInvocationSecurityDescriptor();
					if( securityDesc != null ){

						logger.info("Configuring security mechanisms");
						EndpointReferenceType proxyEPR = null;
						SecureConversationInvocationSecurityDescriptor secConversation = securityDesc.getSecureConversationInvocationSecurityDescriptor();
						SecureMessageInvocationSecurityDescriptor secMsg = securityDesc.getSecureMessageInvocationSecurityDescriptor();
						TLSInvocationSecurityDescriptor tlsInvocation = securityDesc.getTLSInvocationSecurityDescriptor();

						// Try to find the credential in any of the objects retrieved above
						CDSAuthenticationMethod cdsMethod = null;
						if( secConversation != null ){
							cdsMethod = secConversation.getCDSAuthenticationMethod();
						}
						else if( secMsg != null ){
							cdsMethod = secMsg.getCDSAuthenticationMethod();
						}
						else if( tlsInvocation != null ){
							cdsMethod = tlsInvocation.getCDSAuthenticationMethod();
						}

						if( cdsMethod != null ){
							proxyEPR = cdsMethod.getProxyEPR();
						}
						else throw new RemoteException("Secure conversation scheme found, but no CDS Authentication method found");

						// Retrieve delegated credential if there's any to be retrieved
						if( proxyEPR != null ){

							// Tell the instance helper to manage this credential and associate it with the current stage
							instanceHelperClient.addCredential(currInvocationClient.getEndpointReference(), proxyEPR);

							// Verify whether the credential wasn't already retrieved
							boolean alreadyRetrieved = alreadyRetrievedCredentials.contains(proxyEPR.toString());

							if( !alreadyRetrieved ){

								// Retrieve delegated credential
								GlobusCredential credential = null;
								String cdsURL = "this address was not initialized";
								try {
									credential = CredentialHandlingUtil.getDelegatedCredential(new EndpointReference(proxyEPR));
									AttributedURI cdsAddress = proxyEPR.getAddress(); 
									cdsURL = cdsAddress.getScheme() + "://" + cdsAddress.getHost() + ':' + cdsAddress.getPort()
									+ CDS_PATH_IN_CONTAINER; 
									
									if( credential == null ){
										String errMsg = "Null credential received, but a valid credential was expected";
										throw new RemoteException(errMsg);
									}
									
								} catch (Exception e) {
									logger.error(e.getMessage(), e);
									throw new RemoteException(e.getMessage(), e);
								}

								// Delegate the credential to the InstanceHelper
								logger.info("Delegating credential to the WorkflowInstanceHelper");
								long secondsToExpire = credential.getTimeLeft();  // Delegate the credential for almost as long as it is valid
								int hoursLeft = (int)(secondsToExpire / 3600);
								int minsLeft = (int)((secondsToExpire % 3600)/ 60);
								minsLeft -= 5;
								int delegationPathLength = 1;
								int issuedCredentialPathLenght = 0;
								ProxyLifetime delegationLifetime = new ProxyLifetime(hoursLeft, minsLeft, 0);
								ProxyLifetime issuedCredentialLifetime = delegationLifetime;
								CredentialHandlingUtil.delegateCredential(credential, helperDN, cdsURL, delegationLifetime, 
										issuedCredentialLifetime, delegationPathLength, issuedCredentialPathLenght);

								// Register the delegated credential's EPR to avoid retrieving it multiple times
								alreadyRetrievedCredentials.add(proxyEPR.toString());
							}
						}

					}
					else logger.info("No security mechanisms required");

					// Configure input
					logger.info("Configuring stage input");
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
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			} catch(Throwable t){
				logger.error(t.getMessage(), t);
				t.printStackTrace();
			}

		}

		// Get the output transport description for the workflow as a whole and update the referred stages' descriptions
		logger.info("Searching for workflow outputs");
		WorkflowOutputTransportDescriptor wfOutputDesc = workflowDesc.getOutputDesc();
		HashMap<Integer, List<OperationOutputParameterTransportDescriptor> > stagesExtraOutputTransport =
			new HashMap<Integer, List<OperationOutputParameterTransportDescriptor>>();
		if( wfOutputDesc != null ){
			int numWfOutputs = (wfOutputDesc.getParamDescriptor() != null)? 
					wfOutputDesc.getParamDescriptor().length : 0 ;
					for(int i=0; i < numWfOutputs; i++){

						// Associate the current output with the stage responsible for sending it back
						WorkflowOutputParameterTransportDescriptor curr_output = wfOutputDesc.getParamDescriptor(i);
						int sourceStageID = curr_output.getSourceGUID();
						OperationOutputParameterTransportDescriptor paramDesc = curr_output.getParamDescription();

						// Update the list associated with the source of the current output
						List<OperationOutputParameterTransportDescriptor> curr_list;
						if( stagesExtraOutputTransport.containsKey(sourceStageID) ){
							curr_list = stagesExtraOutputTransport.get(sourceStageID);
						}
						else {
							curr_list = new ArrayList<OperationOutputParameterTransportDescriptor>();
						}
						curr_list.add(paramDesc);
						stagesExtraOutputTransport.put(sourceStageID, curr_list);
					}
		}

		// Configure the outputs of each workflow stage
		logger.info("Configuring stages' outputs");
		Set<Entry<Integer,OperationOutputTransportDescriptor>> entries = stageID2OutputDesc.entrySet();
		Iterator<Entry<Integer, OperationOutputTransportDescriptor>> iter = entries.iterator();
		while( iter.hasNext() ){

			// Retrieve required information to configure the output of a workflow stage
			Entry<Integer, OperationOutputTransportDescriptor> currEntry = iter.next();
			Integer stageID = currEntry.getKey();
			OperationOutputTransportDescriptor outputDesc = currEntry.getValue();
			OperationOutputParameterTransportDescriptor[] outputParams = outputDesc.getParamDescriptor();

			ArrayList<OperationOutputParameterTransportDescriptor> fullTransportDesc = new ArrayList<OperationOutputParameterTransportDescriptor>();

			// Configure the forwarding of current stage's output to every stage that needs it
			if( outputParams != null ){

				logger.info("Configuring transport between stages within the workflow");
				for(int i=0; i < outputParams.length; i++){

					// Retrieve the destination stage's EPR
					OperationOutputParameterTransportDescriptor curr_outParam = outputParams[i];
					int destinationStageID = curr_outParam.getDestinationGlobalUniqueIdentifier();
					EndpointReferenceType destinationEPR = stageID2EPR.get(Integer.valueOf(destinationStageID));

					outputParams[i].setDestinationEPR(new EndpointReferenceType[]{ destinationEPR });
					fullTransportDesc.add(outputParams[i]);  // Add to transport description list, that will receive workflow outputs' descs too
				}
			}

			EndpointReferenceType currStageEPR = stageID2EPR.get(stageID);

			// Configure any outputs required by the user to be sent back to the ManagerInstance
			if( stagesExtraOutputTransport.containsKey(stageID) ){

				logger.info("Configuring transport of workflow outputs");
				List<OperationOutputParameterTransportDescriptor> extraOutputs = stagesExtraOutputTransport.get(stageID);
				Iterator<OperationOutputParameterTransportDescriptor> outputs_iter = extraOutputs.iterator();
				while( outputs_iter.hasNext() ){

					OperationOutputParameterTransportDescriptor currOutput = outputs_iter.next();
					currOutput.setDestinationEPR(new EndpointReferenceType[]{ managerInstanceEpr });
					currOutput.setDeliveryPolicy(DeliveryPolicy.ROUNDROBIN);
					int paramIndex = thisResource.registerOutputValue(currOutput, currStageEPR);
					currOutput.setParamIndex(paramIndex);
					fullTransportDesc.add(currOutput);
				}
			}		

			// Update output transport description for current stage
			outputDesc.setParamDescriptor(fullTransportDesc.toArray(new OperationOutputParameterTransportDescriptor[0]));

			// For the current workflow stage, all output destination's EPRs are set. So, we can configure its output transport
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

		// Get static input values and send them to the associated InvocationHelper
		logger.info("Setting stages' static input parameter values");
		WorkflowInputParameters workflowInput = workflowDesc.getInputs();
		WorkflowInputParameter[] inputValues = workflowInput.getParameter();;
		for(int i=0; i < inputValues.length; i++){

			WorkflowInputParameter currInputValue = inputValues[i];
			InputParameter param = currInputValue.getParamDescription();
			int destinationID = currInputValue.getParamDestinationGUID();

			logger.info("Sending parameter value to stage identified by "+ destinationID +": ("
					+ param.getData() + ", " + param.getParamIndex() + ')');

			// Get EPR associated with current destination to send the input value to the appropriate location
			EndpointReferenceType destinationEPR = stageID2EPR.get(Integer.valueOf(destinationID));
			WorkflowInvocationHelperClient destinationClient = null;
			try {
				destinationClient = new WorkflowInvocationHelperClient(destinationEPR);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			}
			destinationClient.setParameter(param);
		}

		// return the typed EPR
		WorkflowManagerInstanceReference ref = new WorkflowManagerInstanceReference();
		ref.setEndpointReference(managerInstanceEpr);

		logger.info("END");
		return ref;  
	}

  public java.lang.String getIdentity() throws RemoteException {
		// TODO Retrieve the Manager's ID dynamically
		String managerIdentity = null;
		try {
			managerIdentity = org.cagrid.workflow.manager.service.WorkflowManagerServiceConfiguration.getConfiguration().getManagerIdentity();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return managerIdentity;
	}	
}


package org.cagrid.workflow.manager.service;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.client.WorkflowHelperClient;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.manager.descriptor.WorkflowStageDescriptor;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResource;
import org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResourceHome;
import org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference;
import org.cagrid.workflow.manager.service.bpelParser.BpelParser;
import org.cagrid.workflow.manager.service.bpelParser.WorkflowProcessLayout;
import org.cagrid.workflow.manager.service.conversion.WorkflowProcessLayoutConverter;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.container.ContainerException;

/**
 * I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class WorkflowManagerServiceImpl extends WorkflowManagerServiceImplBase implements NotifyCallback {

	
	
	private static final String SOAPENCODING_NAMESPACE = "http://schemas.xmlsoap.org/soap/encoding/";
	private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	private static final Log logger = LogFactory.getLog(WorkflowManagerServiceImpl.class);
	
	
	public WorkflowManagerServiceImpl() throws RemoteException {
		super();
	}



	public org.cagrid.workflow.manager.instance.stubs.types.WorkflowManagerInstanceReference createWorkflowManagerInstance(org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor workflowManagerInstanceDescriptor) throws RemoteException {
		
		
		logger.debug("Manager Service muito doido"); 
		
		
		org.apache.axis.message.addressing.EndpointReferenceType epr = new org.apache.axis.message.addressing.EndpointReferenceType();
		WorkflowManagerInstanceResourceHome home;
		// BaseResourceHome home = null;
		org.globus.wsrf.ResourceKey resourceKey = null;
		org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext
		.getCurrentContext();
		String servicePath = ctx.getTargetService();

		// workflowManagerInstanceResourceHome
		String homeName = org.globus.wsrf.Constants.JNDI_SERVICES_BASE_NAME
		+ servicePath + "/" + "workflowManagerInstanceHome";
		System.out.println("homeName = " + homeName);
		try {
			javax.naming.Context initialContext = new javax.naming.InitialContext();
			home = (WorkflowManagerInstanceResourceHome) initialContext
			.lookup(homeName);
			resourceKey = home.createResource();
			System.out.println("resourceKey = " + resourceKey);

			// Grab the newly created resource
			WorkflowManagerInstanceResource thisResource = (WorkflowManagerInstanceResource) home.find(resourceKey);

			// set the workflow descriptor on the helper instance
			thisResource
			.setWorkflowManagerInstanceDescriptor(workflowManagerInstanceDescriptor);

			String transportURL = (String) ctx
			.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
			transportURL += "WorkflowManagerInstance";

			System.out.println("transportURL = " + transportURL);
			epr = org.globus.wsrf.utils.AddressingUtils.createEndpointReference(transportURL, resourceKey);
		} catch (Exception e) {
			throw new RemoteException(
					"Error looking up WorkflowManagerInstance  home:"
					+ e.getMessage(), e);
		}



		/** Parse BPEL description */
		BpelParser parserBpelWorkflowFile = new BpelParser();

		WorkflowProcessLayout workflowLayout = parserBpelWorkflowFile.parseString(workflowManagerInstanceDescriptor.getBpelDescription(),
				resourceKey.toString());
		// Parser Bpel done
		logger.info("End bpel parser");

		/* Get the URL of the workflow's services */
		HashMap<String, URL> servicesURLs = WorkflowProcessLayoutConverter.getServicesURL(
				workflowManagerInstanceDescriptor.getServicesURLs()); 


		/* Create as many helpers as containers we are supposed to contact. 
		 * For now, we are assuming all InvocationHelpers in the same container
		 * to be managed by the same InstanceHelper */
		Map<String, List<String>> instancesServices = WorkflowProcessLayoutConverter.
		getInstanceHelpersForInvocationHelpers(workflowManagerInstanceDescriptor.getServicesURLs());

		WorkflowProcessLayoutConverter converter = new WorkflowProcessLayoutConverter(workflowLayout);
		List<WorkflowStageDescriptor> invocation_descs = converter.extractWorkflowInvocationHelperDescriptors(
				servicesURLs);


		/** Create all InstanceHelpers and its underlying InvocationHelpers */
		logger.info("Creating workflow");
		// TODO: the id just as the workflow name is not a strong name
		logger.info("ID = " + workflowLayout.getName());

		Set<Entry<String, List<String>>> instanceHelpers = instancesServices.entrySet();
		Iterator<Entry<String, List<String>>> instanceHelpersIter = instanceHelpers.iterator();


		while( instanceHelpersIter.hasNext() ){

			
			Entry<String, List<String>> curr_entry = instanceHelpersIter.next();
			
			
			
			// Retrieve WorkflowHelper's address
			String curr_helperURL = curr_entry.getKey(); 
			List<String> curr_invocationHelperURLs = curr_entry.getValue();

			logger.info("Current helper URL is: "+ curr_helperURL);
			System.out.println("Helper URL = "+ curr_helperURL); // DEBUG
			WorkflowHelperClient helper_client = null;
			try {
				EndpointReference helper_epr = new EndpointReference(curr_helperURL);
				helper_client = new WorkflowHelperClient(helper_epr);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			}

			
			
			// Create InstanceHelper
			WorkflowInstanceHelperDescriptor wfi_desc = new WorkflowInstanceHelperDescriptor();
			wfi_desc.setWorkflowID(workflowLayout.getName());
			wfi_desc.setWorkflowManagerEPR(epr);

			WorkflowInstanceHelperClient wfi_client = null;
			try {
				wfi_client = helper_client.createWorkflowInstanceHelper(wfi_desc);
				wfi_client.subscribeWithCallback(TimestampedStatus.getTypeDesc().getXmlType(), this);
			} catch (MalformedURIException e) {
				e.printStackTrace();
			} catch (ContainerException e) {
				e.printStackTrace();
			}

			
			// list of clients related to a certain operation. This is needed to be
			// used during the next step, when we set the Input/OutputParameterDescriptors for each
			// operation
			List<WorkflowInvocationHelperClient> operations_clients = new ArrayList<WorkflowInvocationHelperClient>();
			HashMap<QName, EndpointReferenceType> serviceEPR = new HashMap<QName, EndpointReferenceType>(); 

			if (invocation_descs.size() > 0) {
				System.out.println("WorkflowID = "+ invocation_descs.get(0).getBasicDescription().getWorkflowID());
			}
			System.out.println("Before create infocation services");

			// Create all the InvocationHelpers assigned to the InstanceHelper created above  
			for(int i = 0; i < invocation_descs.size(); i++){

				
				WorkflowStageDescriptor curr_desc = invocation_descs.get(i);
				String curr_url = invocation_descs.get(i).getBasicDescription().getServiceURL(); 
				
				if( curr_invocationHelperURLs.contains(curr_url) ) continue; 
						
				
				WorkflowInvocationHelperClient curr_invocation_client = null;
				System.out.println("Creating operation");
				try {
					curr_invocation_client = wfi_client.createWorkflowInvocationHelper(curr_desc.getBasicDescription());
				} catch (MalformedURIException e) {
					System.out.println("Error creating helperInstance Client. Operation =  "+ curr_desc.getBasicDescription().getOperationQName());
					e.printStackTrace();
				}
				catch(RemoteException e){
					System.out.println("Error creating helperInstance Client. Operation =  "+ curr_desc.getBasicDescription().getOperationQName());
					e.printStackTrace();
				}

				operations_clients.add(curr_invocation_client);
				serviceEPR.put(curr_desc.getBasicDescription().getOperationQName(), curr_invocation_client.getEndpointReference());
			}
			System.out.println("after");

			// Configure stages' inputs, outputs and TODO security requirements
			HashMap<String, QName> inputVariables = converter.getInputVariables();
			HashMap<QName, String> outputVariables = converter.getOutputVariables();
			HashMap<String, String> copyOperations = converter.getAssignOperations();
			for (int i = invocation_descs.size() -1; i >= 0; i--) {

				
				WorkflowStageDescriptor curr_desc = invocation_descs.get(i);

				
				logger.debug("[createWorkflowManagerInstance] operationName = " + curr_desc.getBasicDescription().getOperationQName());
				logger.debug("[createWorkflowManagerInstance] outputType = " + curr_desc.getBasicDescription().getOutputType());
				logger.debug("[createWorkflowManagerInstance] serviceURL = " + curr_desc.getBasicDescription().getServiceURL()); 

				
				QName[] namespaces = workflowLayout.getAllNamespaces();

				// invocation_descs.
				WorkflowInvocationHelperClient curr_invocation_client = operations_clients.get(i);

				if(i == 1){

					// Configuring input
					OperationInputMessageDescriptor inputMessageDescSecondService = new OperationInputMessageDescriptor();
					InputParameterDescriptor[] inputParamsSecondService = new InputParameterDescriptor[1];
					// configure each input parameter (variable name and namespace)
					inputParamsSecondService[0] = new InputParameterDescriptor(new QName("input"), new QName(XSD_NAMESPACE, "string"));
					System.out.println("paramQName = "+inputParamsSecondService[0].getParamQName());
					inputMessageDescSecondService.setInputParam(inputParamsSecondService);
					curr_invocation_client.configureInput(inputMessageDescSecondService);
				}

				if (i == 0) {
					System.out.println("Creating operation");

					OperationInputMessageDescriptor inputMessage_ras = new OperationInputMessageDescriptor();
					InputParameterDescriptor[] inputParams_ras = new InputParameterDescriptor[0];
					inputMessage_ras.setInputParam(inputParams_ras);
					curr_invocation_client.configureInput(inputMessage_ras);

				}

				// Create an empty outputDescriptor
				OperationOutputTransportDescriptor outputDescriptor_ras = new OperationOutputTransportDescriptor();
				List<OperationOutputParameterTransportDescriptor> outParameterDescriptor_ras = new ArrayList<OperationOutputParameterTransportDescriptor>();
				System.out.println("before set input");

				// Configure each destination for the output				
				OperationOutputTransportDescriptor outputTransportDescriptor = curr_desc.getOutputTransportDescriptor();
				int num_destinations = outputTransportDescriptor.getParamDescriptor().length;			

				for(int curr_dest_index = 0; curr_dest_index < num_destinations; curr_dest_index++){

					// Create current parameter forwarding descriptor
					OperationOutputParameterTransportDescriptor curr_dest = new OperationOutputParameterTransportDescriptor();

					curr_dest.setParamIndex(0); // TODO Get param's index
					curr_dest.setType(new QName(SOAPENCODING_NAMESPACE, "string")); // TODO Get type
					curr_dest.setQueryNamespaces(namespaces);

					OperationOutputParameterTransportDescriptor paramDescriptor = outputTransportDescriptor.getParamDescriptor(curr_dest_index);

					String locationQuery = paramDescriptor.getLocationQuery();
					curr_dest.setLocationQuery(locationQuery);

					// Retrieve current destination's EPR
					QName operationName = curr_desc.getBasicDescription().getOperationQName();
					String currOutputVariable = outputVariables.get(operationName);
					String currInputVariable  = copyOperations.get(currOutputVariable);
					QName nextOperation = inputVariables.get(currInputVariable);

					EndpointReferenceType nextOperationEPR = serviceEPR.get(nextOperation); 

					System.out.println("before endpoint ref");
					curr_dest.setDestinationEPR(new EndpointReferenceType[] { nextOperationEPR });
					System.out.println("after endpoint ref");

					// Add new parameter forwarding descriptor to stage's list
					outParameterDescriptor_ras.add(curr_dest); 
				}				

				outputDescriptor_ras.setParamDescriptor(outParameterDescriptor_ras.toArray(
						new OperationOutputParameterTransportDescriptor[outParameterDescriptor_ras.size()]));
				curr_invocation_client.configureOutput(outputDescriptor_ras);

				// Set static parameter, if any is supposed to be statically set
				InputParameter[] staticParameters = converter.getInputParameters(curr_desc.getInputsDescription());
				for(int j=0; j < staticParameters.length; j++){

					InputParameter currParam = staticParameters[j];
					curr_invocation_client.setParameter(currParam);
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

	
	
	public void deliver(List arg0, EndpointReferenceType arg1, Object arg2) {
		// TODO Auto-generated method stub
		//return 0;
	}

}


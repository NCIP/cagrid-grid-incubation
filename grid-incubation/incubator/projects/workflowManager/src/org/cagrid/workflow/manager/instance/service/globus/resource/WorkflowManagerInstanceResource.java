package org.cagrid.workflow.manager.instance.service.globus.resource;

import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;
import javax.xml.rpc.NamespaceConstants;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.client.WorkflowHelperClient;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.InputParameterDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationInputMessageDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OperationOutputTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.descriptor.WorkflowInstanceHelperDescriptor;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.manager.descriptor.WorkflowStageDescriptor;
import org.cagrid.workflow.manager.service.bpelParser.WorkflowProcessLayout;
import org.cagrid.workflow.manager.service.conversion.WorkflowProcessLayoutConverter;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.container.ContainerException;

import sun.awt.windows.WPopupMenuPeer;


/** 
 * The implementation of this WorkflowManagerInstanceResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowManagerInstanceResource extends WorkflowManagerInstanceResourceBase implements NotifyCallback {


	private String eprString; // GUID for this resource

	private List<String> invocationHelperEPRString = new ArrayList<String>();   // Associate parameter indices and its corresponding sources
	private HashMap<Integer, String> outputData = new HashMap<Integer, String>();  // Associate parameter indices and its value


	// Synchronization of the access to the output values
	Lock outputDataLock = new ReentrantLock();
	Condition allValuesSetCondition = outputDataLock.newCondition();
	boolean allValuesSet = false;


	// Data to be set for each InvocationHelper // TODO Store data to be set later while executing a 'startWorkflow' method
	HashMap<String, ArrayList<InputParameter> > workflowData = new HashMap<String, ArrayList<InputParameter>>();
	HashMap<String, EndpointReferenceType> string2EPR = new HashMap<String, EndpointReferenceType>(); // Map the keys from 'workflowData' to EPR



	// Status of each InstanceHelper managed by this ManagerInstance
	private Map<String, TimestampedStatus> stageStatus = new HashMap<String, TimestampedStatus>() ;
	private int timestamp = 0; // Logical time of a status notification created by this resource


	// Store the name for each service subscribed for notification. Useful while debugging 
	private Map<String, String> EPR2Name = new HashMap<String, String>();
	private Set<EndpointReferenceType> invocationHelperEPR = new HashSet<EndpointReferenceType>();


	// Synchronization of the access to variable 'isFinished' 
	protected Lock isFinishedKey = new ReentrantLock();
	private boolean isFinished = false; // Indication of whether the stages have all finished their execution



	/**
	 * Receive the output of a WorkflowInvocationHelper and store it internally. The source of the received
	 * value is identified by the paramater index, that is present in exactly one pair <integer, InvocationHelper>
	 * within this resource.
	 * 
	 * @param inputParameter Parameter one wants to send to the ManagerInstance
	 * */
	public void setParameter(InputParameter inputParameter) throws RemoteException {

		int paramIndex = inputParameter.getParamIndex();
		if(paramIndex >= this.invocationHelperEPRString.size()){
			throw new RemoteException("Parameter index ("+ paramIndex +") is greater than the " +
					"number of expected parameters ("+ this.invocationHelperEPRString.size() +").");
		}

		try{
			this.outputDataLock.lock();


			this.outputData.put(paramIndex, inputParameter.getData());
			this.allValuesSet = (this.outputData.size() == this.invocationHelperEPRString.size());

			if(this.allValuesSet){
				this.allValuesSetCondition.signalAll();
			}
		}
		finally {
			this.outputDataLock.unlock();
		}
	}


	/**
	 * Register a workflow output value to be retrieved. Creates an integer identifier for it in order
	 * to further associate a received value with its source.
	 * 
	 * @return The integer identifier of the registered parameter
	 * 
	 * */
	public int registerOutputValue(EndpointReferenceType invocationHelperEPR) throws RemoteException{

		// Retrieve the InvocationHelper identifier
		String invID = null;
		try {
			WorkflowInvocationHelperClient client = new WorkflowInvocationHelperClient(invocationHelperEPR);
			invID = client.getEPRString();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}

		// Add one more expected output value and use its index in the list as its identifier
		this.invocationHelperEPRString.add(invID);
		int outputValueID = this.invocationHelperEPRString.size() - 1; 

		if( !(this.invocationHelperEPRString.get(outputValueID).equals(invID)) ) 
			throw new RemoteException("Output value associated with the current ID does not match the one just added");

		return outputValueID;
	}


	/**
	 * Retrieve workflow outputs. Does not return until all outputs are set.
	 * */
	public String[] getWorkflowOutputs(){


		String[] retval = null;

		try{

			// Ensure all values are ready to be read
			this.outputDataLock.lock();
			if(!this.allValuesSet){
				this.allValuesSetCondition.await();
			}


			// Copy all output values to an array 
			int numOutputs = this.outputData.size();
			retval = new String[numOutputs];

			Set<Entry<Integer, String>> entrySet = this.outputData.entrySet();
			Iterator<Entry<Integer, String>> iter = entrySet.iterator();
			while( iter.hasNext() ){

				Entry<Integer, String> curr_entry = iter.next();
				retval[curr_entry.getKey().intValue()] = curr_entry.getValue();
			}




		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally{
			this.outputDataLock.unlock();
		}

		return retval;
	}



	/**
	 * Register an InvocationHelper in order to monitor its status changes
	 * */
	public void registerInstanceHelper(EndpointReferenceType epr, String string){

		//System.out.println("[registerInvocationHelper] Registering "+ name); //DEBUG

		WorkflowInstanceHelperClient instanceHelper;
		try {

			// Request notification from the InvocationHelper
			instanceHelper = new WorkflowInstanceHelperClient(epr);
			instanceHelper.subscribeWithCallback(TimestampedStatus.getTypeDesc().getXmlType(), this);

			// Store reference to the InvocationHelper internally
			String key = instanceHelper.getEPRString();
			this.stageStatus.put(key, new TimestampedStatus(Status.UNCONFIGURED, 0));
			this.EPR2Name.put(key, string.toString());

		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ContainerException e) {
			e.printStackTrace();
		}

		//System.out.println("[registerInvocationHelper] END"); //DEBUG

	}



	public void deliver(List arg0, EndpointReferenceType arg1, Object arg2) {

		org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType changeMessage = ((org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType) arg2)
		.getResourcePropertyValueChangeNotification();

		MessageElement actual_property = changeMessage.getNewValue().get_any()[0];
		QName message_qname = actual_property.getQName();
		boolean isTimestampedStatusChange = message_qname.equals(TimestampedStatus.getTypeDesc().getXmlType());
		String stageKey = null;
		try {
			stageKey = new WorkflowInstanceHelperClient(arg1).getEPRString(); 
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (MalformedURIException e1) {
			e1.printStackTrace();
		}   




		//DEBUG
		PrintStream log = System.out;
		//log.println("[WorkflowManagerInstanceResource] Received message of type "+ message_qname.getLocalPart() +" from "+ stageKey);


		// Handle status change notifications
		if(isTimestampedStatusChange){
			TimestampedStatus status = null;;
			try {
				status = (TimestampedStatus) actual_property.getValueAsType(message_qname, TimestampedStatus.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


//			log.println("[WorkflowManagerInstanceResource::deliver] Received new status value: "+ status.getStatus().toString() 
//					+ ':' + status.getTimestamp() +" from "+ this.EPR2Name.get(stageKey)); //DEBUG

			this.isFinishedKey.lock();
			try{

				boolean statusActuallyChanged = false;
				if( this.stageStatus.containsKey(stageKey) ){


					TimestampedStatus curr_status = this.stageStatus.get(stageKey);
					statusActuallyChanged = (!curr_status.getStatus().equals(status.getStatus())) && ( curr_status.getTimestamp() < status.getTimestamp() ); 										

					if(statusActuallyChanged){

						this.stageStatus.remove(stageKey);
						this.stageStatus.put(stageKey, status);
					}

				}
				else System.err.println("[WorkflowManagerInstanceResource] Unrecognized stage notified status change: "+ stageKey);



				// Calculate new status value
				if(statusActuallyChanged){

					Status new_status = this.calculateStatus();
					if( new_status.equals(Status.FINISHED) || new_status.equals(Status.ERROR)){

						System.out.println("[WorkflowManagerInstanceResource.deliver] EXECUTION HAS FINISHED");
						this.isFinished = true;
					}


					TimestampedStatus nextStatus = new TimestampedStatus(new_status, ++this.timestamp);					
					this.setTimestampedStatus(nextStatus);		
					
					System.out.println("[WorkflowManagerInstanceResource] Current workflow status is "+ nextStatus.getStatus() +
							':' + nextStatus.getTimestamp());
				}


			} catch (ResourceException e) {
				e.printStackTrace();
			}
			finally {
				this.isFinishedKey.unlock();
			}
			
			
		}

	}


	/**
	 * Calculate the status of the workflow portion managed by this InstanceHelper
	 * */
	private Status calculateStatus() {

		Status next_status;



		if( this.stagesPresentStatus(Status.ERROR) ){
			next_status = Status.ERROR;
		}

		// Starting here, we consider a workflow portion only reach some state 
		// when all the stages within that portion have already done so. 
		else if(this.stagesPresentStatus(Status.UNCONFIGURED)){			
			next_status = Status.UNCONFIGURED;
		}
		else if(this.stagesPresentStatus(Status.INPUTCONFIGURED)){
			next_status = Status.INPUTCONFIGURED;
		}
		else if( this.stagesPresentStatus(Status.INPUTOUTPUTCONFIGURED) ){
			next_status = Status.INPUTOUTPUTCONFIGURED;
		}
		else if( this.stagesPresentStatus(Status.WAITING)){
			next_status = Status.WAITING;
		}
		else if( this.stagesPresentStatus(Status.RUNNING)){
			next_status = Status.RUNNING;
		}
		else {
			next_status = Status.FINISHED;
		}

		return next_status;
	}



	/**
	 * Search for any InstanceHelper that present the wanted status
	 * 
	 * @param wanted The value to search for
	 * */
	private boolean stagesPresentStatus(Status wanted){

		Set<Entry<String, TimestampedStatus>> entries = this.stageStatus.entrySet();
		Iterator<Entry<String, TimestampedStatus>> entries_iter = entries.iterator();

		while( entries_iter.hasNext() ){

			Entry<String, TimestampedStatus> curr_entry = entries_iter.next();
			boolean found = curr_entry.getValue().getStatus().equals(wanted);

			if(found) return true;
		}

		return false;		
	}


	public String getEPRString() {

		return this.eprString;
	}


	public void setEPRString(String eprString) {
		this.eprString = eprString;
	}



	/** Associate an input parameter with a stage */
	public void addParameterForStage(EndpointReferenceType stageEPR, InputParameter param){


		// Retrieve GUID for the stage
		WorkflowInvocationHelperClient stageClient = null;
		String stageKey = null;
		try {
			stageClient = new WorkflowInvocationHelperClient(stageEPR);
			stageKey = stageClient.getEPRString();
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}



		// Associate received parameter with received state
		ArrayList<InputParameter> currParamSet;
		if( this.workflowData.containsKey(stageKey) ){

			currParamSet = this.workflowData.get(stageKey);
		}
		else {
			currParamSet = new ArrayList<InputParameter>();
			this.string2EPR.put(stageKey, stageEPR);
		}

		currParamSet.add(param);
		this.workflowData.put(stageKey, currParamSet);
	}



	/** Start workflow execution */
	public void start() {

		
		Iterator<EndpointReferenceType> invocationHelpersIter = this.invocationHelperEPR.iterator();
		
		while( invocationHelpersIter.hasNext() ){
			
			
			EndpointReferenceType currInvHelp = invocationHelpersIter.next();
			WorkflowInvocationHelperClient currInvHelpClient;
			try {
				currInvHelpClient = new WorkflowInvocationHelperClient(currInvHelp);
				
				System.out.println("[WorkflowManagerInstanceResource.start] Starting stage "+ currInvHelpClient.getEPRString());
				currInvHelpClient.start();
				
			} catch (MalformedURIException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
		}
		
	}



	/** Create an InstanceHelper and associate it with this resource */
	public void createWorkflowInstanceHelper(String helperURL, List<String> invocationHelperURLs, 
			List<WorkflowStageDescriptor> invocation_descs, EndpointReferenceType managerEPR,
			WorkflowProcessLayout workflowLayout) throws RemoteException {


		WorkflowProcessLayoutConverter converter = new WorkflowProcessLayoutConverter(workflowLayout);
		final String workflowID = workflowLayout.getName();

		logger.info("Current helper URL is: "+ helperURL);
		System.out.println("Helper URL = "+ helperURL); // DEBUG
		WorkflowHelperClient helper_client = null;
		try {
			EndpointReference helper_epr = new EndpointReference(helperURL);
			helper_client = new WorkflowHelperClient(helper_epr);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		}



		// Create InstanceHelper
		WorkflowInstanceHelperDescriptor wfi_desc = new WorkflowInstanceHelperDescriptor();
		wfi_desc.setWorkflowID(workflowID);
		wfi_desc.setWorkflowManagerEPR(managerEPR);

		WorkflowInstanceHelperClient wfi_client = null;
		try {
			wfi_client = helper_client.createWorkflowInstanceHelper(wfi_desc);
			this.registerInstanceHelper(wfi_client.getEndpointReference(), workflowID + '@' + invocationHelperURLs);
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

			if( invocationHelperURLs.contains(curr_url) ) continue; 


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
				inputParamsSecondService[0] = new InputParameterDescriptor(new QName("input"), new QName(NamespaceConstants.NSURI_SCHEMA_XSD, "string"));
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
				curr_dest.setType(new QName(NamespaceConstants.NSPREFIX_SOAP_ENCODING, "string")); // TODO Get type
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
			
			this.invocationHelperEPR.add(curr_invocation_client.getEndpointReference()); // Store EPR so we can start workflow execution later

			// Set static parameter, if any is supposed to be statically set
			InputParameter[] staticParameters = converter.getInputParameters(curr_desc.getInputsDescription());
			for(int j=0; j < staticParameters.length; j++){

				InputParameter currParam = staticParameters[j];
				curr_invocation_client.setParameter(currParam);
				//this.addParameterForStage(curr_invocation_client.getEndpointReference(), currParam);
			}
		}	

	}



}

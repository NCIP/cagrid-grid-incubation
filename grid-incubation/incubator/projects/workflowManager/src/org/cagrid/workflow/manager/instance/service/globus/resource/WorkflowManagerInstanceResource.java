package org.cagrid.workflow.manager.instance.service.globus.resource;

import java.rmi.RemoteException;
import java.util.ArrayList;
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

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.InstrumentationRecord;
import org.cagrid.workflow.helper.descriptor.LocalWorkflowInstrumentationRecord;
import org.cagrid.workflow.helper.descriptor.OperationOutputParameterTransportDescriptor;
import org.cagrid.workflow.helper.descriptor.OutputReady;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.container.ContainerException;


/**
 * The implementation of this WorkflowManagerInstanceResource type.
 *
 * @created by Introduce Toolkit version 1.2
 *
 */
public class WorkflowManagerInstanceResource extends WorkflowManagerInstanceResourceBase implements NotifyCallback {


	private static Log logger = LogFactory.getLog(WorkflowManagerInstanceResource.class);


	private String eprString; // GUID for this resource

	private List<String> invocationHelperEPRString = new ArrayList<String>();   // Associate parameter indices and its corresponding sources
	private HashMap<Integer, String> outputData = new HashMap<Integer, String>();  // Associate parameter indices and its value


	// Synchronization of the access to the output values
	private Lock outputDataLock = new ReentrantLock();
	private Condition allValuesSetCondition = outputDataLock.newCondition();
	private boolean allValuesSet = true;


	// Associate a stage's EPR to its operation name
	private Map<String, QName> eprToOperationName = new HashMap<String, QName>();



	// Data to be set for each InvocationHelper
	private HashMap<String, ArrayList<InputParameter> > workflowData = new HashMap<String, ArrayList<InputParameter>>();
	private HashMap<String, EndpointReferenceType> string2EPR = new HashMap<String, EndpointReferenceType>(); // Map the keys from 'workflowData' to EPR


	// Status of each InstanceHelper managed by this ManagerInstance
	private Map<String, TimestampedStatus> stageStatus = new HashMap<String, TimestampedStatus>() ;
	private int timestamp = 0; // Logical time of a status notification created by this resource


	// Store the name for each service subscribed for notification. Useful while debugging
	private Map<String, String> EPR2Name = new HashMap<String, String>();


	// Synchronization of the access to variable 'isFinished'
	protected Lock isFinishedKey = new ReentrantLock();


	private HashMap<Integer, EndpointReferenceType> stageID2EPR;  // EPR of each InvocationHelper associated with this resource
	private List<EndpointReferenceType> instanceHelperEPRs = new ArrayList<EndpointReferenceType>();    // EPR of each InstanceHelper associated with this resource


	// TODO Instrumentation data for this workflow


	// Synchronization for when output is ready
	private Map<String, Boolean>  asynchronous_callback_arrived = new HashMap<String, Boolean>();   // True when the asynchronous callback is received from an InvocationHelper
	private Map<String, Condition>  asynchronous_call_condition = new HashMap<String, Condition>();;   // Condition variable used to signal that a callback was received
	private Map<String, Lock>  asynchronous_call_key = new HashMap<String, Lock>();              // Provides exclusive access to the 2 variables above


	private boolean alreadyStarted = false;



	/**
	 * Receive the output of a WorkflowInvocationHelper and store it internally. The source of the received
	 * value is identified by the paramater index, that is present in exactly one pair <integer, InvocationHelper>
	 * within this resource.
	 *
	 * @param inputParameter Parameter one wants to send to the ManagerInstance
	 * */
	public void setParameter(InputParameter inputParameter) throws RemoteException {

		int paramIndex = inputParameter.getParamIndex();

		logger.info("Manager receiving "+ paramIndex +"th parameter ");




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

		logger.info("END SetParameter");
	}


	/**
	 * Register a workflow output value to be retrieved. Creates an integer identifier for it in order
	 * to further associate a received value with its source.
	 *
	 * @return The integer identifier of the registered parameter
	 *
	 * */
	public int registerOutputValue(OperationOutputParameterTransportDescriptor paramDesc, EndpointReferenceType invocationHelperEPR)
		throws RemoteException{


		// At this point, we now for sure that the user expects output
		try{
			outputDataLock.lock();
			this.allValuesSet = false;  // Setting false here means we will wait until the outputs are retrieved
		}
		finally {
			outputDataLock.unlock();
		}


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
	public void registerInstanceHelper(EndpointReferenceType epr, String instanceHelperName){

		logger.info("Registering "+ instanceHelperName);

		WorkflowInstanceHelperClient instanceHelper;
		try {

			// Request notification from the InvocationHelper
			instanceHelper = new WorkflowInstanceHelperClient(epr);
			instanceHelper.subscribeWithCallback(TimestampedStatus.getTypeDesc().getXmlType(), this);  // Monitor status changes
			instanceHelper.subscribeWithCallback(LocalWorkflowInstrumentationRecord.getTypeDesc().getXmlType(), this);  // Monitor instrumentation reports

			// Store reference to the InstanceHelper internally
			String key = instanceHelper.getEPRString();
			this.stageStatus.put(key, new TimestampedStatus(Status.UNCONFIGURED, 0));
			this.EPR2Name.put(key, instanceHelperName);

			this.instanceHelperEPRs.add(epr);  // Store EPR of the InstanceHelper


		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ContainerException e) {
			e.printStackTrace();
		}

		logger.info("END");

	}



	public void deliver(List arg0, EndpointReferenceType arg1, Object arg2) {

//		logger.info("BEGIN ManagerInstance::deliver");


		org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType changeMessage = ((org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType) arg2)
		.getResourcePropertyValueChangeNotification();

		MessageElement actual_property = changeMessage.getNewValue().get_any()[0];
		QName message_qname = actual_property.getQName();
		boolean isTimestampedStatusChange = message_qname.equals(TimestampedStatus.getTypeDesc().getXmlType());
		boolean isInstrumentationReport = message_qname.equals(LocalWorkflowInstrumentationRecord.getTypeDesc().getXmlType());
		boolean isOutputReady = message_qname.equals(OutputReady.getTypeDesc().getXmlType());


		logger.debug("Received message of type "+ message_qname.getLocalPart());


		// Handle status change notifications
		if(isTimestampedStatusChange){

			String stageKey = null;
			try {
				stageKey = new WorkflowInstanceHelperClient(arg1).getEPRString();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (MalformedURIException e1) {
				e1.printStackTrace();
			}


			TimestampedStatus status = null;
			try {
				status = (TimestampedStatus) actual_property.getValueAsType(message_qname, TimestampedStatus.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


			logger.debug("[WorkflowManagerInstanceResource::deliver] Received new status value: "+ status.getStatus().toString()
					+ ':' + status.getTimestamp() +" from "+ this.EPR2Name.get(stageKey));

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

						logger.info("[WorkflowManagerInstanceResource.deliver] EXECUTION HAS FINISHED");
						//this.isFinished = true;
					}


					TimestampedStatus nextStatus = new TimestampedStatus(new_status, ++this.timestamp);
					this.setTimestampedStatus(nextStatus);

					logger.info("[WorkflowManagerInstanceResource] Current workflow status is "+ nextStatus.getStatus() +
							':' + nextStatus.getTimestamp());
				}


			} catch (ResourceException e) {
				e.printStackTrace();
			}
			finally {
				this.isFinishedKey.unlock();
			}
		}


		// TODO Handle instrumentation reports
		else if(isInstrumentationReport){


			String stageKey = null;
			try {
				stageKey = new WorkflowInstanceHelperClient(arg1).getEPRString();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (MalformedURIException e1) {
				e1.printStackTrace();
			}

			LocalWorkflowInstrumentationRecord intrumentation_data = null;
			try {
				intrumentation_data = (LocalWorkflowInstrumentationRecord) actual_property.getValueAsType(message_qname, LocalWorkflowInstrumentationRecord.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


			logger.debug("[WorkflowManagerInstanceResource::deliver] Received new status value: "+ intrumentation_data.getIdentifier()
					+" from "+ this.EPR2Name.get(stageKey));


			//.......

		}


		// Handle signaling that output is ready
		else if(isOutputReady){

			logger.debug("[WorkflowManagerInstanceResource::deliver] Received message of type "+ message_qname.getLocalPart());

			String stageKey = null;
			try {
				stageKey = new WorkflowInvocationHelperClient(arg1).getEPRString();

				if(stageKey == null){
					logger.error("[WorkflowManagerInstanceResource::deliver] Unable to retrieve stage ID");
				}

			} catch (RemoteException e1) {
				logger.error(e1.getMessage(), e1);
				e1.printStackTrace();
			} catch (MalformedURIException e1) {
				logger.error(e1.getMessage(), e1);
				e1.printStackTrace();
			}


			OutputReady callback = null;
			try {
				callback = (OutputReady) actual_property.getValueAsType(message_qname, OutputReady.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


			Boolean outputReady = new Boolean(callback.equals(OutputReady.TRUE));
			if( outputReady ){

				try {
					System.out.println("[WorkflowManagerInstanceResource::deliver] Execution finished for "+ this.getOperationNameForInvocationHelper(stageKey));
				} catch (RemoteException e) {
					logger.error(e.getMessage(), e);
					e.printStackTrace();
				}
			}

			// Store/Update the value stored internally for the current InvocationHelper
			if( this.asynchronous_call_key.containsKey(stageKey) ){
				Lock mutex = this.asynchronous_call_key.get(stageKey);
				mutex.lock();
				try {

//					System.out.println("Updating OutputReady value in internal map"); //DEBUG
					this.asynchronous_callback_arrived.put(stageKey, outputReady);

					// If the execution is finished, report the user
					boolean allCallbacksReceived = !this.asynchronous_callback_arrived.containsValue(Boolean.FALSE);
					if(allCallbacksReceived){

						System.out.println("[WorkflowManagerInstanceResource::deliver] All callbacks received. Execution is finished."); //DEBUG
						this.setOutputReady(OutputReady.TRUE);  // Report to the user that all callbacks were received
//						System.out.println("[WorkflowManagerInstanceResource::deliver] Report regarding workflow end was sent"); //DEBUG
					}
					else {

//						System.out.println("OutputReady notification delivering finished"); // DEBUG
					}


				} catch (ResourceException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
				finally {
					mutex.unlock();
				}

			}
			else{
				logger.error("[WorkflowManagerInstanceResource::deliver] Callback received from an unknown stage: "+ stageKey);
			}

		}

//		System.out.println("END ManagerInstance::deliver");
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
		else if( this.stagesPresentStatus(Status.READY) ){
			next_status = Status.READY;
		}
		else if( this.stagesPresentStatus(Status.RUNNING)){
			next_status = Status.RUNNING;
		}
		else if( this.stagesPresentStatus(Status.GENERATING_OUTPUT)){
			next_status = Status.GENERATING_OUTPUT;
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
			Status curr_status = curr_entry.getValue().getStatus();
			boolean found = curr_status.equals(wanted);

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



	/** Start workflow execution
	 * @throws RemoteException */
	public void start() throws RemoteException {


		if(this.alreadyStarted){
			throw new RemoteException("[WorkflowManagerInstanceResource::start] Workflow excecution already started");
		}


		logger.info("STARTING stages' execution");

		Set<Entry<Integer, EndpointReferenceType>> stagesEPRs = this.stageID2EPR.entrySet();
		Iterator<Entry<Integer, EndpointReferenceType>> stages_iter = stagesEPRs.iterator();
		while( stages_iter.hasNext() ){

			Entry<Integer, EndpointReferenceType> curr_entry = stages_iter.next();
			EndpointReferenceType currStageEPR = curr_entry.getValue();

			WorkflowInvocationHelperClient currStageClient;
			try {
				currStageClient = new WorkflowInvocationHelperClient(currStageEPR);
				String clientID = currStageClient.getEPRString();


				// Initialize the synchronization variables related to the "start" method's callback
				this.asynchronous_callback_arrived.put(clientID, Boolean.FALSE);
				Lock key = new ReentrantLock();
				Condition condition = key.newCondition();
				this.asynchronous_call_key.put(clientID, key);
				this.asynchronous_call_condition.put(clientID, condition);

				// Subscribe to receive a callback generated by an asynchronous call to 'start'
				currStageClient.subscribeWithCallback(OutputReady.getTypeDesc().getXmlType(), this);

				// Asynchronous call to start current stage's execution
				System.out.println("Starting workflow execution");
				currStageClient.start();
				logger.debug("Workflow executing asynchronously");

				// Note: There is no need to wait for a callback here. In fact, we should *never* wait for a callback at this point because that could cause deadlock situations.

			} catch (MalformedURIException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			} catch (RemoteException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			} catch (ContainerException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}

		}

		this.alreadyStarted = true;

		logger.info("Stages' execution STARTED");
	}


	public void storeStagesEPRs(
			HashMap<Integer, EndpointReferenceType> stageID2EPR) {
		this.stageID2EPR = stageID2EPR;
	}


	/* (non-Javadoc)
	 * @see org.cagrid.workflow.manager.instance.service.globus.resource.WorkflowManagerInstanceResourceBase#remove()
	 */
	public void remove() throws ResourceException {

		logger.info("Destroying ManagerInstance");


		super.remove();


		Iterator<EndpointReferenceType> instanceHelper_iter = this.instanceHelperEPRs.iterator();
		while( instanceHelper_iter.hasNext() ){


			EndpointReferenceType curr_epr = instanceHelper_iter.next();
			WorkflowInstanceHelperClient curr_client;
			try {
				curr_client = new WorkflowInstanceHelperClient(curr_epr);

				String instanceID = curr_client.getEPRString();
				//this.stageStatus.remove(instanceID);  // Stop monitoring the current instance
				//this.EPR2Name.remove(instanceID);
				curr_client.destroy();  // Destroy the resource associated with the current instance

			} catch (MalformedURIException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		logger.info("Done");
		return;
	}


	public void registerInvocationHelper(String eprString, QName operationQName) {

		this.eprToOperationName.put(eprString, operationQName);
	}


	private String getOperationNameForInvocationHelper(String epr) throws RemoteException{

		if(!this.eprToOperationName.containsKey(epr)){

			throw new RemoteException("Stage record not found for: "+ epr);
		}

		return this.eprToOperationName.get(epr).toString();
	}

}















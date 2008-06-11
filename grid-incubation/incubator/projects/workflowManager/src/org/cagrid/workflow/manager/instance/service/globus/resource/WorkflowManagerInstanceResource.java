package org.cagrid.workflow.manager.instance.service.globus.resource;

import java.io.PrintStream;
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
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.instance.client.WorkflowInstanceHelperClient;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.ResourceException;


/** 
 * The implementation of this WorkflowManagerInstanceResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowManagerInstanceResource extends WorkflowManagerInstanceResourceBase implements NotifyCallback {


	private List<String> invocationHelperEPR = new ArrayList<String>();   // Associate parameter indices and its corresponding sources
	private HashMap<Integer, String> outputData = new HashMap<Integer, String>();  // Associate parameter indices and its value


	// Synchronization of the access to the output values
	Lock outputDataLock = new ReentrantLock();
	Condition allValuesSetCondition = outputDataLock.newCondition();
	boolean allValuesSet = false;

	
	
	
	
	// Status of each InstanceHelper managed by this ManagerInstance
	private Map<String, TimestampedStatus> stageStatus = new HashMap<String, TimestampedStatus>() ;

	// Store the name for each service subscribed for notification 
	private Map<String, String> EPR2Name = new HashMap<String, String>();


	private boolean isFinished = false; // Indication of whether the stages have all finished their execution   

	// Synchronizes the access to variable 'isFinished' 
	protected Lock isFinishedKey = new ReentrantLock();
	//protected Condition isFinishedCondition = isFinishedKey.newCondition();


	//private String eprString;

	// Logical time of a message notifying 
	private int timestamp = 0;


	/**
	 * Receive the output of a WorkflowInvocationHelper and store it internally. The source of the received
	 * value is identified by the paramater index, that is present in exactly one pair <integer, InvocationHelper>
	 * within this resource.
	 * 
	 * @param inputParameter Parameter one wants to send to the ManagerInstance
	 * */
	public void setParameter(InputParameter inputParameter) throws RemoteException {

		int paramIndex = inputParameter.getParamIndex();
		if(paramIndex >= this.invocationHelperEPR.size()){
			throw new RemoteException("Parameter index ("+ paramIndex +") is greater than the " +
					"number of expected parameters ("+ this.invocationHelperEPR.size() +").");
		}

		try{
			this.outputDataLock.lock();


			this.outputData.put(paramIndex, inputParameter.getData());
			this.allValuesSet = (this.outputData.size() == this.invocationHelperEPR.size());

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
		this.invocationHelperEPR.add(invID);
		int outputValueID = this.invocationHelperEPR.size() - 1; 

		if( !(this.invocationHelperEPR.get(outputValueID).equals(invID)) ) 
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
		//log.println("[CreateTestWorkflowsStep] Received message of type "+ message_qname.getLocalPart() +" from "+ stageKey);


		// Handle status change notifications
		if(isTimestampedStatusChange){
			TimestampedStatus status = null;;
			try {
				status = (TimestampedStatus) actual_property.getValueAsType(message_qname, TimestampedStatus.class);
			} catch (Exception e) {
				e.printStackTrace();
			}


			//log.println("[InstanceHelper::deliver] Received new status value: "+ status.getStatus().toString() 
			//	+ ':' + status.getTimestamp() +" from "+ this.EPR2OperationName.get(stageKey)); //DEBUG

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
				else System.err.println("[WorkflowInstanceHelperResource] Unrecognized stage notified status change: "+ stageKey);



				// Calculate new status value
				if(statusActuallyChanged){

					Status new_status = this.calculateStatus();
					if( new_status.equals(Status.FINISHED) || new_status.equals(Status.ERROR)){

						this.isFinished = true;
					}


					TimestampedStatus nextStatus = new TimestampedStatus(new_status, ++this.timestamp);					
					this.setTimestampedStatus(nextStatus);					
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
	
}

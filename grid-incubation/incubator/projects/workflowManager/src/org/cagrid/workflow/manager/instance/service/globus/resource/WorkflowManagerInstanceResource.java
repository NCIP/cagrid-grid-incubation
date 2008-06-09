package org.cagrid.workflow.manager.instance.service.globus.resource;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.descriptor.InputParameter;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;


/** 
 * The implementation of this WorkflowManagerInstanceResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowManagerInstanceResource extends WorkflowManagerInstanceResourceBase {


	private List<String> invocationHelperEPR = new ArrayList<String>();   // Associate parameter indices and its corresponding sources
	private HashMap<Integer, String> outputData = new HashMap<Integer, String>();  // Associate parameter indices and its value


	// Synchronization of the access to the output values
	Lock outputDataLock = new ReentrantLock();
	Condition allValuesSetCondition = outputDataLock.newCondition();
	boolean allValuesSet = false;



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

}

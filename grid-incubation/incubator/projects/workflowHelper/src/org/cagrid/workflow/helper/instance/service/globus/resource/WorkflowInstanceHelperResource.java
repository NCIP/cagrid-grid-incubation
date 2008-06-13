package org.cagrid.workflow.helper.instance.service.globus.resource;

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
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.descriptor.Status;
import org.cagrid.workflow.helper.descriptor.TimestampedStatus;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.helper.util.CredentialHandlingUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.container.ContainerException;


/** 
 * The implementation of this WorkflowInstanceHelperResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowInstanceHelperResource extends WorkflowInstanceHelperResourceBase implements CredentialAccess, NotifyCallback {

	// Associations between services' EPRs and the credential to be used when invoking service-operation
	private HashMap<String, GlobusCredential> servicesCredentials = new HashMap<String, GlobusCredential>();


	// Associations between Globus credentials and the EPR they came from
	private HashMap<EndpointReferenceType, GlobusCredential> eprCredential = new HashMap<EndpointReferenceType, GlobusCredential>();


	// Synchronization structure: list of GlobusCredentials with retrieval pending [serviceOperationEPR, Condition]
	private HashMap<String, Condition> serviceConditionVariable = new HashMap<String, Condition>();
	private HashMap<String, Lock> servicelLock = new HashMap<String, Lock>();

	// Set of IDs of services that should not be invoked securely
	private List<String> unsecureInvocations = new ArrayList<String>();


	// Status of each InvocationHelper managed by this InstanceHelper
	private Map<String, TimestampedStatus> stageStatus = new HashMap<String, TimestampedStatus>() ;

	// Store the operation name for each service subscribed for notification 
	private Map<String, String> EPR2OperationName = new HashMap<String, String>();


	private boolean isFinished = false; // Indication of whether the stages have all finished their execution   

	// Synchronizes the access to variable 'isFinished' 
	protected Lock isFinishedKey = new ReentrantLock();
	//protected Condition isFinishedCondition = isFinishedKey.newCondition();


	private String eprString;

	// Logical time of a message notifying 
	private int timestamp = 0;






	/**
	 * Set the GlobusCredential to be used by a specific InvocationHelper
	 * 
	 * @param serviceOperationEPR EPR of the InvocationHelper
	 * @param proxyEPR EPR of the delegated credential to retrieve
	 * 
	 * */
	public void addCredential(EndpointReference serviceOperationEPR , EndpointReference proxyEPR){

		// DEBUG
		//System.out.println("BEGIN addCredential");

		// Check whether this credential hasn't already been retrieved
		boolean credentialAlreadyRetrieved = this.eprCredential.containsKey(proxyEPR);

		if( !credentialAlreadyRetrieved ){
			this.replaceCredential(serviceOperationEPR, proxyEPR);
		}

		// DEBUG
		//System.out.println("END addCredential");

		return;
	}


	/**
	 * Retrieve the GlobusCredential associated with a service-operation. 
	 * If credential retrieval is pending, caller is blocked until the credential becomes available. 
	 * 
	 * @param serviceOperationEPR EPR of the InvocationHelper
	 * @return The associated GlobusCredential, or null if no such association does exist  
	 * 
	 * */
	public GlobusCredential getCredential(EndpointReference serviceOperationEPR){


		//this.printCredentials(); //DEBUG

		String eprStr = null;
		try {
			WorkflowInvocationHelperClient client = new WorkflowInvocationHelperClient(serviceOperationEPR);
			eprStr = client.getEPRString();

		} catch (MalformedURIException e1) {
			e1.printStackTrace();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} 



		GlobusCredential credential = null;
		boolean serviceIsUnsecure = this.unsecureInvocations.contains(eprStr);


		if( serviceIsUnsecure ){
			//System.out.println("[WorkflowInstanceHelperResource.getCredential] Service is unsecure, returning null credential");
			return null;
		}

		else {

			//System.out.println("[getCredential] Service is secure"); // DEBUG

			// If credential is unavailable, block until it is available
			boolean credentialIsNotSet = (!this.servicesCredentials.containsKey(eprStr));
			//if( credentialIsNotSet ){

			Lock key = this.servicelLock.get(eprStr);
			Condition credentialAvailability = this.serviceConditionVariable.get(eprStr);


			// Mutual exclusive access session: we can only return the credential if it was already retrieved from the
			// Credential Delegation Service
			key.lock();
			try{

				if( credentialIsNotSet ){
					credentialAvailability.await();
				}
				credential = this.servicesCredentials.get(eprStr);

				//System.out.println("[getCredential] Retrieved credential: "+ credential.getIdentity()); //DEBUG

			} catch (InterruptedException e) {
				System.err.println("[getCredential] Error retrieving credential");
				e.printStackTrace();
			}
			finally {
				key.unlock();
			}


			return credential;
		}
	}


	/**
	 * Associate an InvocationHelper with a GlobusCredential, removing previous associations if any
	 * 
	 * @param serviceOperationEPR EPR of the InvocationHelper
	 * @param proxyEPR the credential to be used by the InvocationHelper
	 * 
	 * */
	public void replaceCredential(EndpointReference serviceOperationEPR , EndpointReference proxyEPR){


		//DEBUG
		//System.out.println("BEGIN replaceCredential");

		String eprStr = null;
		try {
			WorkflowInvocationHelperClient client = new WorkflowInvocationHelperClient(serviceOperationEPR);
			eprStr = client.getEPRString();

		} catch (MalformedURIException e1) {
			e1.printStackTrace();
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}



		// Initializes mutual exclusion key that denotes the availability of the delegated credential
		Lock key;
		Condition credentialAvailability;
		if( this.serviceConditionVariable.containsKey(eprStr) ){

			key = this.servicelLock.get(eprStr);
			credentialAvailability = this.serviceConditionVariable.get(eprStr);
		}
		else {

			key = new ReentrantLock();
			credentialAvailability = key.newCondition();
			this.servicelLock.put(eprStr, key);
			this.serviceConditionVariable.put(eprStr, credentialAvailability);
		}


		//printCredentials(); //DEBUG


		// Delete old credential (if any) from the associations 
		boolean serviceExists = this.servicesCredentials.containsKey(eprStr);
		if( serviceExists ){

			this.servicesCredentials.remove(eprStr);
			this.eprCredential.remove(proxyEPR);
		}


		// Retrieve the delegated credential
		GlobusCredential credential = null;

		if( proxyEPR != null ){
			try {
				credential = CredentialHandlingUtil.getDelegatedCredential(proxyEPR);
			} catch (Exception e) {
				System.err.println("Error retrieving delegated credential");
				e.printStackTrace();
			}
		}


		// Add the new credential
		this.servicesCredentials.put(eprStr, credential);
		this.eprCredential.put(proxyEPR, credential);


		// Signal any waiting threads that the credential is available
		key.lock();
		try{
			credentialAvailability.signalAll();
		}
		finally {
			key.unlock();
		}

		//DEBUG
		//System.out.println("END replaceCredential");

		return;
	}


	/**
	 * Remove all associations between InvocationHelpers and the given GlobusCredential
	 * 
	 * @param proxyEPR EPR of GlobusCredential that we want to be associated with no service anymore
	 * */
	public void removeCredential(EndpointReferenceType proxyEPR){


		GlobusCredential credential = this.eprCredential.get(proxyEPR);

		// First, remove Credential from the [credential, EPR] association
		this.eprCredential.remove(credential);

		// Verify if the received credential is associated with any service
		boolean credentialIsKnown = this.servicesCredentials.containsValue(credential);

		// If any service is associated with the received credential, remove each of these associations
		if( credentialIsKnown ){
			Set<Entry<String, GlobusCredential>> entries = this.servicesCredentials.entrySet();
			Iterator<Entry<String, GlobusCredential>> entries_iter = entries.iterator();

			// Iterate over the associations, removing those in which the received credential is present 
			while( entries_iter.hasNext() ){

				Entry<String, GlobusCredential> curr_pair = entries_iter.next();
				GlobusCredential curr_value = curr_pair.getValue();

				if( curr_value.equals(credential)){  

					String curr_key = curr_pair.getKey();
					this.servicesCredentials.remove(curr_key);
				}
			}
		}
	}


	/** Register a service-operation either as secure or unsecure
	 * 
	 * @param serviceOperationEPR EndpointReference for the target service-operation
	 * @param isSecure Must be true if the service-operation is secure and false otherwise 
	 *  */
	public void setIsInvocationHelperSecure(EndpointReference serviceOperationEPR, boolean isSecure) {


		WorkflowInvocationHelperClient client = null;
		String EPRStr = null;
		try {
			client = new WorkflowInvocationHelperClient(serviceOperationEPR);
			EPRStr = client.getEPRString();

		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} 



		if( isSecure ){

			//System.out.println("Creating locks"); // DEBUG 

			// Initializes mutual exclusion key that denotes the availability of the delegated credential
			Lock key = new ReentrantLock();
			Condition credentialAvailability = key.newCondition();
			this.serviceConditionVariable.put(EPRStr, credentialAvailability);
			this.servicelLock.put(EPRStr, key);


			//this.printCredentials();//DEBUG

		}
		else{

			//System.out.println("Adding service to unsecure list"); //DEBUG

			this.unsecureInvocations.add(EPRStr); 
		}
		//System.out.println("Done"); //DEBUG
	}


	/** Print the associations found in each map. Useful for debugging  */
	private void printCredentials() {

		System.out.println();
		System.out.println("-------------------------------------------------------------------------");


		/** Credentials' condition variables */
		Set<Entry<String, Condition>> entries = this.serviceConditionVariable.entrySet();
		Iterator<Entry<String, Condition>> entries_iter = entries.iterator();

		System.out.println("BEGIN Condition variables");
		while(entries_iter.hasNext()){

			Entry<String, Condition> curr_entry = entries_iter.next();
			System.out.println("\t["+ curr_entry.getKey() +", "+ curr_entry.getValue() +"]");
		}
		System.out.println("END Condition variables");



		/** Credentials' condition variables **/
		Set<Entry<String, Lock>> entries2 = this.servicelLock.entrySet();
		Iterator<Entry<String, Lock>> entries_iter2 = entries2.iterator();

		System.out.println();
		System.out.println("BEGIN locks");
		while(entries_iter2.hasNext()){

			Entry<String, Lock> curr_entry = entries_iter2.next();
			System.out.println("\t["+ curr_entry.getKey() +", "+ curr_entry.getValue() +"]");
		}
		System.out.println("END locks");




		/** Credentials' EPR **/
		Set<Entry<EndpointReferenceType, GlobusCredential>> entries3 = this.eprCredential.entrySet();
		Iterator<Entry<EndpointReferenceType, GlobusCredential>> entries_iter3 = entries3.iterator();

		System.out.println();
		System.out.println("BEGIN Credentials' EPR");
		while(entries_iter3.hasNext()){

			Entry<EndpointReferenceType, GlobusCredential> curr_entry = entries_iter3.next();
			System.out.println("\t["+ curr_entry.getKey() +", "+ curr_entry.getValue() +"]");
		}
		System.out.println("END Credentials' EPR");



		/** Services' credentials **/
		Set<Entry<String, GlobusCredential>> entries4 = this.servicesCredentials.entrySet();
		Iterator<Entry<String, GlobusCredential>> entries_iter4 = entries4.iterator();

		System.out.println();
		System.out.println("BEGIN services' credentials");
		while(entries_iter4.hasNext()){

			Entry<String, GlobusCredential> curr_entry = entries_iter4.next();
			System.out.println("\t["+ curr_entry.getKey() +", "+ curr_entry.getValue() +"]");
		}
		System.out.println("END services' credentials");


		System.out.println("-------------------------------------------------------------------------");
		return;
	}



	/**
	 * Register an InvocationHelper in order to monitor its status changes
	 * */
	public void registerInvocationHelper(EndpointReferenceType epr, QName name){

		//System.out.println("[registerInvocationHelper] Registering "+ name); //DEBUG

		WorkflowInvocationHelperClient invocationHelper;
		try {

			// Request notification from the InvocationHelper
			invocationHelper = new WorkflowInvocationHelperClient(epr);
			invocationHelper.subscribeWithCallback(TimestampedStatus.getTypeDesc().getXmlType(), this);

			// Store reference to the InvocationHelper internally
			String key = invocationHelper.getEPRString();
			this.stageStatus.put(key, new TimestampedStatus(Status.UNCONFIGURED, 0));
			this.EPR2OperationName.put(key, name.toString());

		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ContainerException e) {
			e.printStackTrace();
		}

		//System.out.println("[registerInvocationHelper] END"); //DEBUG

	} 
	


	/**
	 * Process a received notification
	 * */
	public void deliver(List arg0, EndpointReferenceType arg1, Object arg2) {

		org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType changeMessage = ((org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType) arg2)
		.getResourcePropertyValueChangeNotification();

		MessageElement actual_property = changeMessage.getNewValue().get_any()[0];
		QName message_qname = actual_property.getQName();
		boolean isTimestampedStatusChange = message_qname.equals(TimestampedStatus.getTypeDesc().getXmlType());
		String stageKey = null;
		try {
			stageKey = new WorkflowInvocationHelperClient(arg1).getEPRString();  //arg1.toString();
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
	 * Search for any InvocationHelper that present the wanted status
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


	public String getEPRString() {
		return this.eprString;
	}


	public void setEprString(EndpointReference epr) {
		this.eprString = epr.toString();
	}

	
}

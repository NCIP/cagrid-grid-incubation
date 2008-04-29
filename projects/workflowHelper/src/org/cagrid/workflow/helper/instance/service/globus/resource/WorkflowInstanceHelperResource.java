package org.cagrid.workflow.helper.instance.service.globus.resource;

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

import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.workflow.helper.invocation.client.WorkflowInvocationHelperClient;
import org.cagrid.workflow.helper.util.ServiceInvocationUtil;
import org.globus.gsi.GlobusCredential;


/** 
 * The implementation of this WorkflowInstanceHelperResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class WorkflowInstanceHelperResource extends WorkflowInstanceHelperResourceBase implements CredentialAccess {

	// Associations between services' EPRs and the credential to be used when invoking service-operation
	private HashMap<String, GlobusCredential> servicesCredentials = new HashMap<String, GlobusCredential>();


	// Associations between Globus credentials and the EPR they came from
	private HashMap<EndpointReferenceType, GlobusCredential> eprCredential = new HashMap<EndpointReferenceType, GlobusCredential>();


	// Synchronization structure: list of GlobusCredentials with retrieval pending [serviceOperationEPR, Condition]
	private HashMap<String, Condition> serviceConditionVariable = new HashMap<String, Condition>();
	private HashMap<String, Lock> servicelLock = new HashMap<String, Lock>();


	private List<String> unsecureInvocations = new ArrayList<String>();



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
			System.out.println("[WorkflowInstanceHelperResource.getCredential] Service is unsecure, returning null credential");
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
				credential = ServiceInvocationUtil.getDelegatedCredential(proxyEPR);
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

}

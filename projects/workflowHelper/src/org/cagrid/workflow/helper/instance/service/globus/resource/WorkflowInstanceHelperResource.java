package org.cagrid.workflow.helper.instance.service.globus.resource;

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
	private HashMap<EndpointReference, GlobusCredential> servicesCredentials = new HashMap<EndpointReference, GlobusCredential>();


	// Associations between Globus credentials and the EPR they came from
	private HashMap<EndpointReference, GlobusCredential> eprCredential = new HashMap<EndpointReference, GlobusCredential>();


	// Synchronization structure: list of GlobusCredentials with retrieval pending [serviceOperationEPR, Condition]
	private HashMap<EndpointReference, Condition> pendingCredentials = new HashMap<EndpointReference, Condition>();
	private HashMap<EndpointReference, Lock> pendingCredentials2 = new HashMap<EndpointReference, Lock>();


	private List<EndpointReference> unsecureInvocations = new ArrayList<EndpointReference>();



	/**
	 * Set the GlobusCredential to be used by a specific InvocationHelper
	 * 
	 * @param serviceOperationEPR EPR of the InvocationHelper
	 * @param proxyEPR EPR of the delegated credential to retrieve
	 * 
	 * */
	public void addCredential(EndpointReference serviceOperationEPR , EndpointReference proxyEPR){

		// DEBUG
		System.out.println("Adding credential");

		// Check whether this credential hasn't already been retrieved
		boolean credentialAlreadyRetrieved = this.eprCredential.containsKey(proxyEPR);

		if( !credentialAlreadyRetrieved ){
			this.replaceCredential(serviceOperationEPR, proxyEPR);
		}

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


		GlobusCredential credential = null;
		boolean serviceIsUnsecure = this.unsecureInvocations.contains(serviceOperationEPR);
		

		if( serviceIsUnsecure ){
			System.out.println("[getCredential] Service is unsecure, returning null credential");
			return null;
		}

		else {

			System.out.println("[getCredential] Service is secure"); // DEBUG
			
			// If credential is unavailable, block until it is available
			boolean credentialIsNotSet = (!this.servicesCredentials.containsKey(serviceOperationEPR));
			if( credentialIsNotSet ){

				Lock key = this.pendingCredentials2.get(serviceOperationEPR);
				Condition credentialAvailability = this.pendingCredentials.get(serviceOperationEPR);

				// Exclusive access session: we can only return the credential if it was already retrieved from the
				// Credential Delegation Service
				key.lock();
				try{

					credentialAvailability.await();
					credential = this.servicesCredentials.get(serviceOperationEPR);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finally {
					key.unlock();
				}

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


		// Initializes mutual exclusion key that denotes the availability of the delegated credential
		Lock key;
		Condition credentialAvailability;
		if( this.pendingCredentials.containsKey(serviceOperationEPR) ){

			key = this.pendingCredentials2.get(serviceOperationEPR);
			credentialAvailability = this.pendingCredentials.get(serviceOperationEPR);
		}
		else {

			key = new ReentrantLock();
			credentialAvailability = key.newCondition();
			this.pendingCredentials2.put(serviceOperationEPR, key);
			this.pendingCredentials.put(serviceOperationEPR, credentialAvailability);
		}


		// Delete old credential (if any) from the associations 
		boolean serviceExists = this.servicesCredentials.containsKey(serviceOperationEPR);
		if( serviceExists ){

			this.servicesCredentials.remove(serviceOperationEPR);
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
		this.servicesCredentials.put(serviceOperationEPR, credential);
		this.eprCredential.put(proxyEPR, credential);
		this.pendingCredentials.remove(serviceOperationEPR);
		this.pendingCredentials2.remove(serviceOperationEPR);


		// Signal any waiting threads that the credential is available
		key.lock();
		try{
			credentialAvailability.signalAll();
		}
		finally {
			key.unlock();
		}


		return;
	}


	/**
	 * Remove all associations between InvocationHelpers and the given GlobusCredential
	 * 
	 * @param proxyEPR EPR of GlobusCredential that we want to be associated with no service anymore
	 * */
	public void removeCredential(EndpointReference proxyEPR){


		GlobusCredential credential = this.eprCredential.get(proxyEPR);

		// First, remove Credential from the [credential, EPR] association
		this.eprCredential.remove(credential);

		// Verify if the received credential is associated with any service
		boolean credentialIsKnown = this.servicesCredentials.containsValue(credential);

		// If any service is associated with the received credential, remove each of these associations
		if( credentialIsKnown ){
			Set<Entry<EndpointReference, GlobusCredential>> entries = this.servicesCredentials.entrySet();
			Iterator<Entry<EndpointReference, GlobusCredential>> entries_iter = entries.iterator();

			// Iterate over the associations, removing those in which the received credential is present 
			while( entries_iter.hasNext() ){

				Entry<EndpointReference, GlobusCredential> curr_pair = entries_iter.next();
				GlobusCredential curr_value = curr_pair.getValue();

				if( curr_value.equals(credential)){  

					EndpointReference curr_key = curr_pair.getKey();
					this.servicesCredentials.remove(curr_key);
				}
			}
		}
	}


	public void setIsInvocationHelperSecure(EndpointReference serviceOperationEPR, boolean isSecure) {

		if( isSecure ){

			System.out.println("Creating locks"); //DEBUG
			
			// Initializes mutual exclusion key that denotes the availability of the delegated credential
			Lock key = new ReentrantLock();
			Condition credentialAvailability = key.newCondition();
			this.pendingCredentials.put(serviceOperationEPR, credentialAvailability);
			this.pendingCredentials2.put(serviceOperationEPR, key);
		}
		else{
			
			System.out.println("Adding service to unsecure list"); //DEBUG
			
			this.unsecureInvocations.add(serviceOperationEPR); 
		}
	}

}

package org.cagrid.workflow.helper.instance.service.globus.resource;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.axis.message.addressing.EndpointReference;
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
public class WorkflowInstanceHelperResource extends WorkflowInstanceHelperResourceBase {

	// Associations between services' EPRs and the credential to be used when invoking service-operation
	private HashMap<EndpointReference, GlobusCredential> servicesCredentials = new HashMap<EndpointReference, GlobusCredential>();
	
	
	// Associations between Globus credentials and the EPR they came from
	private HashMap<EndpointReference, GlobusCredential> eprCredential = new HashMap<EndpointReference, GlobusCredential>();


	/**
	 * Set the GlobusCredential to be used by a specific InvocationHelper
	 * 
	 * @param serviceOperationEPR EPR of the InvocationHelper
	 * @param proxyEPR EPR of the delegated credential to retrieve
	 * 
	 * */
	public void addCredential(EndpointReference serviceOperationEPR , EndpointReference proxyEPR){

		
		// Check whether this credential hasn't already been retrieved
		boolean credentialAlreadyRetrieved = this.eprCredential.containsKey(proxyEPR);
		
		if( !credentialAlreadyRetrieved ){
			this.replaceCredential(serviceOperationEPR, proxyEPR);
		}

		return;
	}


	/**
	 * Retrieve the GlobusCredential associated with a service-operation
	 * 
	 * @param serviceOperationEPR EPR of the InvocationHelper
	 * @return The associated GlobusCredential, or null if no such association does exist  
	 * 
	 * */
	public GlobusCredential getCredential(EndpointReference serviceOperationEPR){

		return this.servicesCredentials.get(serviceOperationEPR);
	}


	/**
	 * Associate an InvocationHelper with a GlobusCredential, removing previous associations if any
	 * 
	 * @param serviceOperationEPR EPR of the InvocationHelper
	 * @param proxyEPR the credential to be used by the InvocationHelper
	 * 
	 * */
	public void replaceCredential(EndpointReference serviceOperationEPR , EndpointReference proxyEPR){

		// Retrieve the delegated credential
		final GlobusCredential credential = ServiceInvocationUtil.getDelegatedCredential(proxyEPR);
		
		
		// Delete old credential (if any) from the associations 
		boolean serviceExists = this.servicesCredentials.containsKey(serviceOperationEPR );
		if( serviceExists ){
			
			this.servicesCredentials.remove(serviceOperationEPR);
			this.eprCredential.remove(proxyEPR);
		}
		
		
		// And add the new credential
		this.servicesCredentials.put(serviceOperationEPR, credential);
		this.eprCredential.put(proxyEPR, credential);
		
		// Sent the new credential to the corresponding InvocationHelper
		this.updateCredentialOnInvocationHelper(serviceOperationEPR, credential);
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
	
	
	/** Send to the InvocationHelper the credential he is supposed to use from now on
	 * 
	 * @param serviceOperationEPR EndpointReference of the InvocationHelper
	 * @param proxy the Globus credential
	 *  */
	private void updateCredentialOnInvocationHelper(EndpointReference serviceOperationEPR, GlobusCredential proxy ){
		
		try {
			WorkflowInvocationHelperClient serviceOperationClient = new WorkflowInvocationHelperClient(serviceOperationEPR);
			serviceOperationClient.setProxy(proxy);
			
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} 
		
	}
	

}

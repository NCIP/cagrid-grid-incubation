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


	HashMap<EndpointReference, GlobusCredential> servicesCredentials = new HashMap<EndpointReference, GlobusCredential>();


	/**
	 * Set the GlobusCredential to be used by a specific InvocationHelper
	 * 
	 * @param serviceOperationEPR EPR of the InvocationHelper
	 * @param proxyEPR EPR of the delegated credential to retrieve
	 * 
	 * */
	public void addCredential(EndpointReference serviceOperationEPR , EndpointReference proxyEPR){

		GlobusCredential credential = ServiceInvocationUtil.getCredential(new EndpointReference(proxyEPR));
		this.replaceCredential(serviceOperationEPR, credential);
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
	 * @param credential the credential to be used by the InvocationHelper
	 * 
	 * */
	public void replaceCredential(EndpointReference serviceOperationEPR , GlobusCredential credential){

		// Delete old credential (if any) and add the new one
		boolean serviceExists = this.servicesCredentials.containsKey(serviceOperationEPR );
		if( serviceExists ){
			this.servicesCredentials.remove(serviceOperationEPR);
		}
		this.servicesCredentials.put(serviceOperationEPR, credential);
		
		// Sent the new credential to the corresponding InvocationHelper
		this.updateCredentialOnInvocationHelper(serviceOperationEPR, credential);
		return;
	}


	/**
	 * Remove all associations between InvocationHelpers and the given GlobusCredential
	 * 
	 * @param credential GlobusCredential that is supposed not to be associated with any service anymore
	 * */
	public void removeCredential(GlobusCredential credential){

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

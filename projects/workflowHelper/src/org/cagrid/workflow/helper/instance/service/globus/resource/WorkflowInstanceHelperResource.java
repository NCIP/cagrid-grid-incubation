package org.cagrid.workflow.helper.instance.service.globus.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.axis.message.addressing.EndpointReference;
import org.cagrid.workflow.helper.util.ServiceInvocationUtil;
import org.globus.gsi.GlobusCredential;


/** 
 * The implementation of this WorkflowInstanceHelperResource type.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
// TODO Update the WorkflowInvocationHelpers' proxies in the methods below
public class WorkflowInstanceHelperResource extends WorkflowInstanceHelperResourceBase {


	HashMap<EndpointReference, GlobusCredential> servicesCredentials = new HashMap<EndpointReference, GlobusCredential>();


	public void addCredential(EndpointReference serviceOperationEPR , EndpointReference proxyEPR){

		GlobusCredential credential = ServiceInvocationUtil.getCredential(new EndpointReference(proxyEPR));
		this.replaceCredential(serviceOperationEPR, credential);
		return;
	}


	public GlobusCredential getCredential(EndpointReference serviceOperationEPR){

		return this.servicesCredentials.get(serviceOperationEPR);
	}


	public void replaceCredential(EndpointReference serviceOperationEPR , GlobusCredential credential){

		boolean serviceExists = this.servicesCredentials.containsKey(serviceOperationEPR );
		if( serviceExists ){
			this.servicesCredentials.remove(serviceOperationEPR );
		}

		this.servicesCredentials.put(serviceOperationEPR, credential);
		return;
	}


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

}

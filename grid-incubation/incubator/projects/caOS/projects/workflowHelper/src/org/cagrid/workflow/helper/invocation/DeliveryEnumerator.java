/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package org.cagrid.workflow.helper.invocation;

import java.util.Iterator;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.workflow.helper.descriptor.DeliveryPolicy;


public class DeliveryEnumerator implements Iterator<EndpointReferenceType> {

	
	DeliveryPolicy policy;
	EndpointReferenceType[] possibleEPRs;
	
	// Information required to enumerate following a Round Robin algorithm
	int currEPRIndex;
	
	
	public DeliveryEnumerator(DeliveryPolicy deliveryPolicy,
			EndpointReferenceType[] destinationEPR) {
		this.policy = (deliveryPolicy != null)? deliveryPolicy : DeliveryPolicy.ROUNDROBIN; // Round Robin is the default policy
		this.possibleEPRs = destinationEPR;
		
		
		/* Set the initial information according to the delivery policy */
		if( this.policy.getValue().equals(DeliveryPolicy._BROADCAST) ){
			// TODO Not implemented for now, implement it
		}
		else if( this.policy.getValue().equals(DeliveryPolicy._DEMANDDRIVEN) ){
			// TODO Create WS-Enumeration and let the InvocationHelpers ask for the data elements 
		}
		else if( this.policy.getValue().equals(DeliveryPolicy._ROUNDROBIN) ){
			this.currEPRIndex = 0;  
		}
		
	}

	
	
	
	public boolean hasNext() {
		
		boolean hasNext = false;
		
		/* Verify if there is a next destination to consider */
		if( this.policy.getValue().equals(DeliveryPolicy._BROADCAST) ){
			// TODO Not implemented for now, implement it
		}
		else if( this.policy.getValue().equals(DeliveryPolicy._DEMANDDRIVEN) ){
			// TODO Create WS-Enumeration and let the InvocationHelpers ask for the data elements 
		}
		else if( this.policy.getValue().equals(DeliveryPolicy._ROUNDROBIN) ){
			hasNext = true;  
		}
		
		return hasNext;
	}

	
	public EndpointReferenceType next() {

		EndpointReferenceType epr = null;
		
		
		/* Verify if there is a next destination to consider */
		if( this.policy.getValue().equals(DeliveryPolicy._BROADCAST) ){
			// TODO Not implemented for now, implement it
		}
		else if( this.policy.getValue().equals(DeliveryPolicy._DEMANDDRIVEN) ){
			// TODO Create WS-Enumeration and let the InvocationHelpers ask for the data elements 
		}
		else if( this.policy.getValue().equals(DeliveryPolicy._ROUNDROBIN) ){
			int arrayIndex = this.currEPRIndex % this.possibleEPRs.length;
			epr = this.possibleEPRs[ arrayIndex ];
			this.currEPRIndex = (this.currEPRIndex+1) % this.possibleEPRs.length; 
		}
		
		return epr;
	}

	
	public void remove() {
		// Do nothing
		System.err.println("["+ this.getClass().getCanonicalName() +"] Remove operation not implemented by this class. Sorry.");
		return;
	}

}

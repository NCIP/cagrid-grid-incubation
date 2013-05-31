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
package org.cagrid.gaards.cds.service;

import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationSigningRequest;
import org.cagrid.gaards.cds.common.DelegationSigningResponse;

public class DelegatedCredential {
	private DelegationSigningRequest signingRequest;
	private DelegationSigningResponse signingResponse;

	public DelegatedCredential(DelegationSigningRequest req,
			DelegationSigningResponse res) {
		this.signingRequest = req;
		this.signingResponse = res;
	}

	public DelegationSigningRequest getSigningRequest() {
		return signingRequest;
	}

	public DelegationSigningResponse getSigningResponse() {
		return signingResponse;
	}
	
	public DelegationIdentifier getDelegationIdentifier(){
		return signingRequest.getDelegationIdentifier();
	}

}

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
package org.cagrid.workflow.helper.instance.service.globus.resource;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReference;
import org.globus.gsi.GlobusCredential;

public interface CredentialAccess {
	
	public GlobusCredential getCredential(EndpointReference serviceOperationEPR) throws RemoteException;
}

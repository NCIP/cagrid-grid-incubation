package org.cagrid.workflow.helper.instance.service.globus.resource;

import java.rmi.RemoteException;

import org.apache.axis.message.addressing.EndpointReference;
import org.globus.gsi.GlobusCredential;

public interface CredentialAccess {
	
	public GlobusCredential getCredential(EndpointReference serviceOperationEPR) throws RemoteException;
}

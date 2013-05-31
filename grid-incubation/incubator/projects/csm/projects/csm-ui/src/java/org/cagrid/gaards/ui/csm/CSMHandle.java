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
package org.cagrid.gaards.ui.csm;

import gov.nih.nci.cagrid.common.Utils;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.csm.client.CSM;
import org.cagrid.gaards.ui.common.ServiceHandle;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;


public class CSMHandle extends ServiceHandle {

    public CSMHandle(ServiceDescriptor des) {
        super(des);
    }


    public CSM getClient() throws MalformedURIException, RemoteException {
        return getClient(null);
    }


    public CSM getClient(GlobusCredential credential) throws MalformedURIException, RemoteException {
        CSM client = null;
        if (credential == null) {
            client = new CSM(getServiceDescriptor().getServiceURL());
        } else {
            client = new CSM(getServiceDescriptor().getServiceURL(), credential);
        }
        if (Utils.clean(getServiceDescriptor().getServiceIdentity()) != null) {
            IdentityAuthorization auth = new IdentityAuthorization(getServiceDescriptor().getServiceIdentity());
            client.setAuthorization(auth);
        }
        return client;
    }
}

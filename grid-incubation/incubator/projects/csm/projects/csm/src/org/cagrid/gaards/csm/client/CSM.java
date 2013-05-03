/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.csm.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;
import org.cagrid.gaards.csm.bean.PrivilegeSearchCriteria;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;


public class CSM {

    private String serviceURL;
    private GlobusCredential credential;
    private CSMClient client;


    public CSM(String serviceURL) throws MalformedURIException, RemoteException {
        this(serviceURL, null);
    }


    public CSM(String serviceURL, GlobusCredential credential) throws MalformedURIException, RemoteException {
        this.serviceURL = serviceURL;
        this.credential = credential;
        this.client = new CSMClient(this.serviceURL, this.credential);
        this.client.setAnonymousPrefered(false);
    }


    protected CSMClient getClient() {
        return client;
    }


    /**
     * This method specifies an authorization policy that the client should use
     * for authorizing the server that it connects to.
     * 
     * @param authorization
     *            The authorization policy to enforce
     */

    public void setAuthorization(Authorization authorization) {
        client.setAuthorization(authorization);
    }


    /**
     * This method get a list of application that the CSM Web Service manages
     * the access control policy for.
     * 
     * @param criteria
     *            The search criteria used to refine the application returned.
     * @return The list of application meeting the search criteria.
     * @throws RemoteException
     * @throws org.cagrid.gaards.csm.stubs.types.CSMInternalFault
     */

    public List<Application> getApplications(ApplicationSearchCriteria criteria) throws RemoteException,
        org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
        List<Application> list = new ArrayList<Application>();
        org.cagrid.gaards.csm.bean.Application[] result = getClient().getApplications(criteria);
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                list.add(new Application(this, result[i]));
            }
        }
        return list;
    }


    /**
     * This method enables one to create an application in CSM such that CSM may
     * manage the access control policy for that application.
     * 
     * @param name
     *            The name of the application
     * @param description
     *            the description of the application.
     * @return The remote application object representing the application.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */
    public Application createApplication(String name, String description) throws RemoteException, CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        org.cagrid.gaards.csm.bean.Application app = new org.cagrid.gaards.csm.bean.Application();
        app.setName(name);
        app.setDescription(description);
        return new Application(this, getClient().createApplication(app));
    }


    /**
     * This method removes an application from CSM, in which case all the access
     * control policy for that application will be removed.
     * 
     * @param applicationId
     *            The id of the application to remove.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void removeApplication(long applicationId) throws RemoteException, CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        getClient().removeApplication(applicationId);
    }


    /**
     * This method removes an application from CSM, in which case all the access
     * control policy for that application will be removed.
     * 
     * @param applicatio
     *            The application to remove.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void removeApplication(Application application) throws RemoteException, CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        if (application != null) {
            removeApplication(application.getId());
        }
    }


    /**
     * This method allow an admin to create a privilege.
     * 
     * @param name
     *            The name of the privilege to create.
     * @param description
     *            A description of the privilege to create.
     * @return The privilege created.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public Privilege createPrivilege(String name, String description) throws RemoteException, CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        org.cagrid.gaards.csm.bean.Privilege priv = new org.cagrid.gaards.csm.bean.Privilege();
        priv.setName(name);
        priv.setDescription(description);
        return new Privilege(this, getClient().createPrivilege(priv));
    }


    /**
     * This method allows one to search for existing privileges in CSM.
     * 
     * @param criteria
     *            The search criteria to limit the search results.
     * @return The list of existing privileges meeting the search criteria.
     * @throws RemoteException
     * @throws org.cagrid.gaards.csm.stubs.types.CSMInternalFault
     */
    public List<Privilege> getPrivileges(PrivilegeSearchCriteria criteria) throws RemoteException,
        org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
        List<Privilege> list = new ArrayList<Privilege>();
        org.cagrid.gaards.csm.bean.Privilege[] result = getClient().getPrivileges(criteria);
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                list.add(new Privilege(this, result[i]));
            }
        }
        return list;
    }


    /**
     * This method allows a CSM Web Service admin to remove a privilege.
     * 
     * @param privilegeId
     *            The id of the privilege to remove.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void removePrivilege(long privilegeId) throws RemoteException, CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        getClient().removePrivilege(privilegeId);
    }


    /**
     * This method allows a CSM Web Service admin to remove a privilege.
     * 
     * @param priv
     *            The privilege to remove
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */
    public void removePrivilege(Privilege priv) throws RemoteException, CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        if (priv != null) {
            removePrivilege(priv.getId());
        }
    }
    
    
}

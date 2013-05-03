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
import java.util.List;

import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public class LocalGroup extends Group {

    public LocalGroup(Application app, org.cagrid.gaards.csm.bean.LocalGroup grp) {
        super(app, grp);
    }


    /**
     * Set the description of the group
     * 
     * @param description
     *            The description of the group.
     */
    public void setDescription(String description) {
        super.setDescription(description);
    }
    
   
    public void setName(String name){
        super.setName(name);
    }


    /**
     * This method will commit/modify the group to the CSM Web Service.
     * 
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void modify() throws RemoteException, CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        org.cagrid.gaards.csm.bean.LocalGroup bean = new org.cagrid.gaards.csm.bean.LocalGroup();
        bean.setApplicationId(getApplication().getId());
        bean.setDescription(getDescription());
        bean.setId(getId());
        bean.setLastUpdated(getLastUpdated());
        bean.setName(getName());
        org.cagrid.gaards.csm.bean.LocalGroup updated = getApplication().getCSM().getClient().modifyGroup(bean);
        this.setName(updated.getName());
        this.setDescription(updated.getDescription());
    }


    /**
     * This method allows an application admin to add a list of users to the
     * group.
     * 
     * @param users
     *            The list of user identities to add to the group.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws CSMTransactionFault
     * @throws AccessDeniedFault
     */
    public void addMembers(List<String> users) throws RemoteException, CSMInternalFault, CSMTransactionFault,
        AccessDeniedFault {
        String[] result = new String[users.size()];
        result = users.toArray(result);
        getApplication().getCSM().getClient().addUsersToGroup(getId(), result);
    }


    /**
     * This method allows and application admin to add a user to this group.
     * 
     * @param user
     *            The identity of the user to add to this group.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws CSMTransactionFault
     * @throws AccessDeniedFault
     */

    public void addMember(String user) throws RemoteException, CSMInternalFault, CSMTransactionFault, AccessDeniedFault {
        String[] result = new String[1];
        result[0] = user;
        getApplication().getCSM().getClient().addUsersToGroup(getId(), result);
    }


    /**
     * This method allows an application admin to remove a list of users from
     * this group.
     * 
     * @param users
     *            The list of user identities to remove from the group
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws CSMTransactionFault
     * @throws AccessDeniedFault
     */
    public void removeMembers(List<String> users) throws RemoteException, CSMInternalFault, CSMTransactionFault,
        AccessDeniedFault {
        String[] result = new String[users.size()];
        result = users.toArray(result);
        getApplication().getCSM().getClient().removeUsersFromGroup(getId(), result);
    }


    /**
     * This method allows and application admin to remove a user from the group.
     * 
     * @param user
     *            The identity of the user to remove from the group.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws CSMTransactionFault
     * @throws AccessDeniedFault
     */
    public void removeMember(String user) throws RemoteException, CSMInternalFault, CSMTransactionFault,
        AccessDeniedFault {
        String[] result = new String[1];
        result[0] = user;
        getApplication().getCSM().getClient().removeUsersFromGroup(getId(), result);
    }
}

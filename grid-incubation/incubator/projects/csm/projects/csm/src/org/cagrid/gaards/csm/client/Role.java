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
import java.util.Calendar;
import java.util.List;

import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public class Role {

    private Application application;
    private long id;
    private String name;
    private String description;
    private Calendar lastUpdated;


    public Role(Application application, org.cagrid.gaards.csm.bean.Role bean) {
        setApplication(application);
        fromBean(bean);

    }


    private void fromBean(org.cagrid.gaards.csm.bean.Role bean) {
        if (bean.getId() != null) {
            setId(bean.getId().longValue());
        }
        setName(bean.getName());
        setDescription(bean.getDescription());
        setLastUpdated(bean.getLastUpdated());
    }


    /**
     * This method returns the application that the role belongs to.
     * 
     * @return The application that the role belongs to.
     */

    public Application getApplication() {
        return application;
    }


    private void setApplication(Application application) {
        this.application = application;
    }


    /**
     * This method returns the id of the role.
     * 
     * @return The id of the role.
     */

    public long getId() {
        return id;
    }


    private void setId(long id) {
        this.id = id;
    }


    /**
     * This method returns the name of the role.
     * 
     * @return The name of the role.
     */
    public String getName() {
        return name;
    }


    /**
     * This method sets the name of the role.
     * 
     * @param name
     *            The name of the role.
     */

    public void setName(String name) {
        this.name = name;
    }


    /**
     * This method gets the description of the role.
     * 
     * @return The description of the role.
     */
    public String getDescription() {
        return description;
    }


    /**
     * This method sets the description of the role.
     * 
     * @param description
     *            The description of the role.
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * This method return the date the role was last updated.
     * 
     * @return The date the role was last updated.
     */
    public Calendar getLastUpdated() {
        return lastUpdated;
    }


    private void setLastUpdated(Calendar lastUpdate) {
        this.lastUpdated = lastUpdate;
    }


    /**
     * This method will commit/modify the role to the CSM Web Service.
     * 
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void modify() throws RemoteException, CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        org.cagrid.gaards.csm.bean.Role bean = new org.cagrid.gaards.csm.bean.Role();
        bean.setApplicationId(getApplication().getId());
        bean.setDescription(getDescription());
        bean.setId(getId());
        bean.setLastUpdated(getLastUpdated());
        bean.setName(getName());
        fromBean(getApplication().getCSM().getClient().modifyRole(bean));
    }


    /**
     * This method sets the privileges associated with the role.
     * 
     * @param privs
     *            The privileges to associate with the role.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */
    public void setPrivileges(List<Privilege> privs) throws RemoteException, CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        if (privs != null) {
            long[] list = new long[privs.size()];
            for (int i = 0; i < privs.size(); i++) {
                list[i] = privs.get(i).getId();
            }
            this.getApplication().getCSM().getClient().setPrivilegesForRole(getId(), list);
        }
    }


    /**
     * This method gets the set of privileges associated with the role.
     * 
     * @return The set of privileges associated with the role
     * @throws RemoteException
     * @throws org.cagrid.gaards.csm.stubs.types.CSMInternalFault
     */

    public List<Privilege> getPrivileges() throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
        List<Privilege> list = new ArrayList<Privilege>();
        org.cagrid.gaards.csm.bean.Privilege[] result = getApplication().getCSM().getClient().getPrivilegesForRole(
            getId());
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                list.add(new Privilege(getApplication().getCSM(), result[i]));
            }
        }
        return list;
    }

}

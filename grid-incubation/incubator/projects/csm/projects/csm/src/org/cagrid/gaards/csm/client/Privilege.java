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
package org.cagrid.gaards.csm.client;

import java.rmi.RemoteException;
import java.util.Calendar;

import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public class Privilege {
    private CSM csm;
    private long id;
    private String name;
    private String description;
    private Calendar lastUpdated;


    public Privilege(CSM csm, org.cagrid.gaards.csm.bean.Privilege bean) {
        setCSM(csm);
        fromBean(bean);
    }


    private void fromBean(org.cagrid.gaards.csm.bean.Privilege bean) {
        if (bean.getId() != null) {
            setId(bean.getId().longValue());
        }
        setName(bean.getName());
        setDescription(bean.getDescription());
        setLastUpdated(bean.getLastUpdated());
    }


    /**
     * This method get the CSM that the privilege is associated with.
     * 
     * @return The CSM that the privilege is associate with.
     */

    public CSM getCSM() {
        return csm;
    }


    private void setCSM(CSM csm) {
        this.csm = csm;
    }


    /**
     * This method get the name of the privilege.
     * 
     * @return The name of the privilege.
     */

    public String getName() {
        return name;
    }


    /**
     * This method sets the name of the privilege.
     * 
     * @param name
     *            The name of the privilege
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * This method gets the description of the privilege
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }


    /**
     * This method sets the description of the privilege.
     * 
     * @param description
     *            The description of the privilege
     */

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * This method gets the id of the privilege.
     * 
     * @return The id of the privilege
     */
    public long getId() {
        return id;
    }


    private void setId(long id) {
        this.id = id;
    }


    /**
     * This method get the date the privilege was last update.
     * 
     * @return The date the privilege was last updated.
     */
    public Calendar getLastUpdated() {
        return lastUpdated;
    }


    private void setLastUpdated(Calendar lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


    /**
     * This method will commit/modify the privilege to the CSM Web Service.
     * 
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void modify() throws RemoteException, CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        org.cagrid.gaards.csm.bean.Privilege bean = new org.cagrid.gaards.csm.bean.Privilege();
        bean.setDescription(getDescription());
        bean.setId(getId());
        bean.setLastUpdated(getLastUpdated());
        bean.setName(getName());
        fromBean(getCSM().getClient().modifyPrivilege(bean));
    }

}

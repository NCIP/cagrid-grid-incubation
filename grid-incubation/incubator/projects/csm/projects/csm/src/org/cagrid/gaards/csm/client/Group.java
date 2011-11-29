package org.cagrid.gaards.csm.client;

import gov.nih.nci.cagrid.common.Utils;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;

import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public abstract class Group {

    private Application application;
    private long id;
    private String name;
    private String description;
    private Calendar lastUpdated;


    public Group(Application app, org.cagrid.gaards.csm.bean.Group grp) {
        this.application = app;
        this.id = grp.getId();
        this.name = grp.getName();
        this.description = grp.getDescription();
        this.lastUpdated = grp.getLastUpdated();
    }


    /**
     * This method return the date the group was last updated.
     * 
     * @return The date the group was last updated.
     */

    public Calendar getLastUpdated() {
        return this.lastUpdated;
    }


    /**
     * Return the application the group belongs to.
     * 
     * @return
     */
    public Application getApplication() {
        return application;
    }


    /**
     * Gets the unique identifier assigned to the group by CSM.
     * 
     * @return The unique unique identifier assigned to the group by CSM.
     */
    public long getId() {
        return id;
    }


    /**
     * Gets the name of the group.
     * 
     * @return The name of the group.
     */

    public String getName() {
        return name;
    }


    /**
     * Sets the name of the group
     * 
     * @param name
     *            The name of the group
     */
    protected void setName(String name) {
        this.name = name;
    }


    /**
     * Gets the description of the group
     * 
     * @return The description of the group.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Set the description of the group
     * 
     * @param description
     *            The description of the group.
     */
    protected void setDescription(String description) {
        this.description = description;
    }


    /**
     * This method returns the list of users who are members of the group.
     * 
     * @return The list of users that are members of the group.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws CSMTransactionFault
     * @throws AccessDeniedFault
     */
    public List<String> getMembers() throws RemoteException, CSMInternalFault, CSMTransactionFault, AccessDeniedFault {
        String[] result = getApplication().getCSM().getClient().getUsersInGroup(getId());
        return Utils.asList(result);
    }

}

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public class ProtectionGroup {

    private Application application;
    private long id;
    private String name;
    private String description;
    private Calendar lastUpdated;
    private boolean largeElementCount;


    public ProtectionGroup(Application application, org.cagrid.gaards.csm.bean.ProtectionGroup bean) {
        setApplication(application);
        fromBean(bean);

    }


    private void fromBean(org.cagrid.gaards.csm.bean.ProtectionGroup bean) {
        if (bean.getId() != null) {
            setId(bean.getId().longValue());
        }
        setName(bean.getName());
        setDescription(bean.getDescription());
        setLastUpdated(bean.getLastUpdated());
        setLargeElementCount(bean.isLargeElementCount());
    }


    /**
     * Specifies whether or not the protection group has a large protection
     * element count.
     * 
     * @return Whether or not the protection group has a large protection
     *         element count.
     */

    public boolean hasLargeElementCount() {
        return largeElementCount;
    }


    /**
     * Sets whether or not the protection group has a large protection element
     * count.
     * 
     * @param largeElementCount
     *            Whether or not the protection group has a large protection
     *            element count.
     */

    public void setLargeElementCount(boolean largeElementCount) {
        this.largeElementCount = largeElementCount;
    }


    /**
     * This method returns the application that the protection group belongs to.
     * 
     * @return The application that the protection group belongs to.
     */

    public Application getApplication() {
        return application;
    }


    private void setApplication(Application application) {
        this.application = application;
    }


    /**
     * This method returns the id of the protection group.
     * 
     * @return The id of the protection group.
     */

    public long getId() {
        return id;
    }


    private void setId(long id) {
        this.id = id;
    }


    /**
     * This method returns the name of the protection group.
     * 
     * @return The name of the protection group.
     */
    public String getName() {
        return name;
    }


    /**
     * This method sets the name of the protection group
     * 
     * @param name
     *            The name of the protection group.
     */

    public void setName(String name) {
        this.name = name;
    }


    /**
     * This method gets the description of the protection group.
     * 
     * @return The description of the protection group.
     */
    public String getDescription() {
        return description;
    }


    /**
     * This method sets the description of the protection group.
     * 
     * @param description
     *            The description of the protection group.
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * This method return the date the protection group was last updated.
     * 
     * @return The date the protection group was last updated.
     */
    public Calendar getLastUpdated() {
        return lastUpdated;
    }


    private void setLastUpdated(Calendar lastUpdate) {
        this.lastUpdated = lastUpdate;
    }


    /**
     * This method will commit/modify the protection group to the CSM Web
     * Service.
     * 
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void modify() throws RemoteException, CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        org.cagrid.gaards.csm.bean.ProtectionGroup bean = new org.cagrid.gaards.csm.bean.ProtectionGroup();
        bean.setApplicationId(getApplication().getId());
        bean.setDescription(getDescription());
        bean.setId(getId());
        bean.setLastUpdated(getLastUpdated());
        bean.setName(getName());
        bean.setLargeElementCount(hasLargeElementCount());
        fromBean(getApplication().getCSM().getClient().modifyProtectionGroup(bean));
    }


    /**
     * This method assigns a list of protection elements to this protection
     * group.
     * 
     * @param protectionElements
     *            The list of protection elements to assign to this protection
     *            group.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void assignProtectionElements(List<ProtectionElement> protectionElements) throws RemoteException,
        CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        if (protectionElements != null) {
            long[] list = new long[protectionElements.size()];
            for (int i = 0; i < protectionElements.size(); i++) {
                list[i] = protectionElements.get(i).getId();
            }
            getApplication().getCSM().getClient().assignProtectionElements(getId(), list);
        }
    }


    /**
     * This method unassigns a list of protection elements from this protection
     * group.
     * 
     * @param protectionElements
     *            The list of protection elements to unassign from this
     *            protection group.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void unassignProtectionElements(List<ProtectionElement> protectionElements) throws RemoteException,
        CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        if (protectionElements != null) {
            long[] list = new long[protectionElements.size()];
            for (int i = 0; i < protectionElements.size(); i++) {
                list[i] = protectionElements.get(i).getId();
            }
            getApplication().getCSM().getClient().unassignProtectionElements(getId(), list);
        }
    }


    /**
     * This method get the list of protection elements that are assigned to this
     * protection group.
     * 
     * @return The list of protection elements that are assigned to this
     *         protection group.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public List<ProtectionElement> getProtectionElements() throws RemoteException, CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        org.cagrid.gaards.csm.bean.ProtectionElement[] result = getApplication().getCSM().getClient()
            .getProtectionElementsAssignedToProtectionGroup(getId());
        List<ProtectionElement> list = new ArrayList<ProtectionElement>();
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                list.add(new ProtectionElement(getApplication(), result[i]));
            }
        }
        return list;
    }


    /**
     * This method assigns a protection group to this protection group as a
     * child
     * 
     * @param grp
     *            The protection group to assign as a child.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void assignProtectionGroup(ProtectionGroup grp) throws RemoteException, CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
        if (grp != null) {
            getApplication().getCSM().getClient().assignProtectionGroup(getId(), grp.getId());
        }
    }


    /**
     * This method unassigns a protection group from this protection group as a
     * child
     * 
     * @param grp
     *            The protection group to unassign as a child.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void unassignProtectionGroup(ProtectionGroup grp) throws RemoteException, CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        if (grp != null) {
            getApplication().getCSM().getClient().unassignProtectionGroup(getId(), grp.getId());
        }
    }


    /**
     * This method obtains a list of protection groups that have been assigned
     * as children to this protection group.
     * 
     * @return The list of protection groups that have been assigned as children
     *         to this protection group.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */
    public List<ProtectionGroup> getChildProtectionGroups() throws RemoteException, CSMInternalFault,
        AccessDeniedFault, CSMTransactionFault {
        org.cagrid.gaards.csm.bean.ProtectionGroup[] result = getApplication().getCSM().getClient()
            .getChildProtectionGroups(getId());
        List<ProtectionGroup> list = new ArrayList<ProtectionGroup>();
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                list.add(new ProtectionGroup(getApplication(), result[i]));
            }
        }
        return list;
    }


    /**
     * This method returns the parent protection group for this protection
     * group.
     * 
     * @return The parent protection group of this protection group.
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public ProtectionGroup getParentProtectionGroup() throws RemoteException, CSMInternalFault, AccessDeniedFault,
        CSMTransactionFault {
    	org.cagrid.gaards.csm.bean.ProtectionGroup bean = getApplication().getCSM().getClient().getParentProtectionGroup(
                getId());
    	if(bean!=null){
    		return new ProtectionGroup(getApplication(), bean);
    	}else {
        	return null;
        }
    }

}

package org.cagrid.gaards.csm.client;

import java.rmi.RemoteException;
import java.util.Calendar;

import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;


public class ProtectionElement {

    private Application application;
    private long id;
    private String name;
    private String description;
    private Calendar lastUpdated;
    private String attribute;
    private String attributeValue;
    private String type;
    private String objectId;


    public ProtectionElement(Application application, org.cagrid.gaards.csm.bean.ProtectionElement bean) {
        setApplication(application);
        fromBean(bean);

    }


    private void fromBean(org.cagrid.gaards.csm.bean.ProtectionElement bean) {
        if (bean.getId() != null) {
            setId(bean.getId().longValue());
        }
        setName(bean.getName());
        setDescription(bean.getDescription());
        setLastUpdated(bean.getLastUpdated());
        setAttribute(bean.getAttribute());
        setAttributeValue(bean.getAttributeValue());
        setObjectId(bean.getObjectId());
        setType(bean.getType());
    }


    /**
     * This method returns the attribute associated with the protection element.
     * 
     * @return The attribute associated with the protection element.
     */
    public String getAttribute() {
        return attribute;
    }


    /**
     * This method allows one to set the attribute of a protection element.
     * 
     * @param attribute
     *            The attribute associated with a protection element.
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }


    public String getAttributeValue() {
        return attributeValue;
    }


    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getObjectId() {
        return objectId;
    }


    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    public Application getApplication() {
        return application;
    }


    private void setApplication(Application application) {
        this.application = application;
    }


    public long getId() {
        return id;
    }


    private void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Calendar getLastUpdated() {
        return lastUpdated;
    }


    private void setLastUpdated(Calendar lastUpdate) {
        this.lastUpdated = lastUpdate;
    }


    /**
     * This method will commit/modify the protection element to the CSM Web
     * Service.
     * 
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void modify() throws RemoteException, CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        org.cagrid.gaards.csm.bean.ProtectionElement bean = new org.cagrid.gaards.csm.bean.ProtectionElement();
        bean.setApplicationId(getApplication().getId());
        bean.setAttribute(getAttribute());
        bean.setAttributeValue(getAttributeValue());
        bean.setDescription(getDescription());
        bean.setId(getId());
        bean.setLastUpdated(getLastUpdated());
        bean.setName(getName());
        bean.setObjectId(getObjectId());
        bean.setType(getType());
        fromBean(getApplication().getCSM().getClient().modifyProtectionElement(bean));
    }

}

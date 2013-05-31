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


public class FilterClause {

    private Application application;
    private long id;
    private String filterChain;
    private String sqlForUser;
    private String sqlForGroup;
    private String className;
    private String targetClassName;
    private String targetClassAttributeName;
    private String targetClassAttributeType;
    private String targetClassAlias;
    private String targetClassAttributeAlias;
    private Calendar lastUpdated;
    

    public FilterClause(Application application, org.cagrid.gaards.csm.bean.FilterClause bean) {
        setApplication(application);
        fromBean(bean);
    }


    private void fromBean(org.cagrid.gaards.csm.bean.FilterClause bean) {
        if (bean.getId() != null) {
            setId(bean.getId().longValue());
        }
        setFilterChain(bean.getFilterChain());
        setSQLForGroup(bean.getGeneratedSQLForGroup());
        setSQLForUser(bean.getGeneratedSQLForUser());
        setClassName(bean.getClassName());
        setTargetClassName(bean.getTargetClassName());
        setTargetClassAttributeName(bean.getTargetClassAttributeName());
        setTargetClassAttributeType(bean.getTargetClassAttributeType());
        setTargetClassAlias(bean.getTargetClassAlias());
        setTargetClassAttributeAlias(bean.getTargetClassAttributeAlias());
        setLastUpdated(bean.getLastUpdated());
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


    public String getFilterChain() {
        return filterChain;
    }


    private void setFilterChain(String filterChain) {
        this.filterChain = filterChain;
    }


    public String getSQLForUser() {
        return sqlForUser;
    }


    public void setSQLForUser(String sqlForUser) {
        this.sqlForUser = sqlForUser;
    }


    public String getSQLForGroup() {
        return sqlForGroup;
    }


    public void setSQLForGroup(String sqlForGroup) {
        this.sqlForGroup = sqlForGroup;
    }


    public String getClassName() {
        return className;
    }


    private void setClassName(String className) {
        this.className = className;
    }


    public String getTargetClassName() {
        return targetClassName;
    }


    private void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }


    public String getTargetClassAttributeName() {
        return targetClassAttributeName;
    }


    private void setTargetClassAttributeName(String targetClassAttributeName) {
        this.targetClassAttributeName = targetClassAttributeName;
    }


    public String getTargetClassAttributeType() {
        return targetClassAttributeType;
    }


    private void setTargetClassAttributeType(String targetClassAttributeType) {
        this.targetClassAttributeType = targetClassAttributeType;
    }


    public String getTargetClassAlias() {
        return targetClassAlias;
    }


    private void setTargetClassAlias(String targetClassAlias) {
        this.targetClassAlias = targetClassAlias;
    }


    public String getTargetClassAttributeAlias() {
        return targetClassAttributeAlias;
    }


    private void setTargetClassAttributeAlias(String targetClassAttributeAlias) {
        this.targetClassAttributeAlias = targetClassAttributeAlias;
    }


    /**
     * This method will commit/modify the filter clause to the CSM Web Service.
     * 
     * @throws RemoteException
     * @throws CSMInternalFault
     * @throws AccessDeniedFault
     * @throws CSMTransactionFault
     */

    public void modify() throws RemoteException, CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
        fromBean(getApplication().getCSM().getClient().modifyFilterClause(getId(), getSQLForGroup(), getSQLForUser()));
    }

}

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
package org.cagrid.gaards.csm.service.hibernate;

import java.util.Set;

import org.cagrid.gaards.csm.bean.RemoteGroupSynchronizationRecord;


public class RemoteGroupDescriptor {

    private long groupId;
    private String gridGrouperURL;
    private String gridGrouperGroupName;
    private long applicationId;
    private Set<RemoteGroupSynchronizationRecord> synchronizationRecords;


    public RemoteGroupDescriptor() {
    }


    public Set<RemoteGroupSynchronizationRecord> getSynchronizationRecords() {
        return synchronizationRecords;
    }


    public void setSynchronizationRecords(Set<RemoteGroupSynchronizationRecord> synchronizationRecords) {
        this.synchronizationRecords = synchronizationRecords;
    }


    public long getGroupId() {
        return groupId;
    }


    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }


    public String getGridGrouperURL() {
        return gridGrouperURL;
    }


    public void setGridGrouperURL(String gridGrouperURL) {
        this.gridGrouperURL = gridGrouperURL;
    }


    public String getGridGrouperGroupName() {
        return gridGrouperGroupName;
    }


    public void setGridGrouperGroupName(String gridGrouperGroupName) {
        this.gridGrouperGroupName = gridGrouperGroupName;
    }


    public long getApplicationId() {
        return applicationId;
    }


    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }


    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (!(obj instanceof RemoteGroupDescriptor))) {
            return false;
        } else {
            RemoteGroupDescriptor grp = (RemoteGroupDescriptor) obj;
            if ((this.getGroupId() == grp.getGroupId()) && (getGridGrouperURL().equals(grp.getGridGrouperURL()))
                && (getGridGrouperGroupName().equals(grp.getGridGrouperGroupName()))
                && (getApplicationId() == grp.getApplicationId())) {
                return true;
            } else {
                return false;
            }
        }
    }
}

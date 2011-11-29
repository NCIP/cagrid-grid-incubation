package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.AuthorizationManager;

import org.cagrid.gaards.csm.bean.RemoteGroupSynchronizationRecord;
import org.cagrid.gaards.csm.service.hibernate.RemoteGroupDescriptor;


public interface RemoteGroupSynchronizer {

    public RemoteGroupSynchronizationRecord synchronizeRemoteGroup(AuthorizationManager auth, RemoteGroupDescriptor grp);
}

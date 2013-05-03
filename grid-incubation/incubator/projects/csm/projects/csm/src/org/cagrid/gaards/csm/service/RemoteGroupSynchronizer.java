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
package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.AuthorizationManager;

import org.cagrid.gaards.csm.bean.RemoteGroupSynchronizationRecord;
import org.cagrid.gaards.csm.service.hibernate.RemoteGroupDescriptor;


public interface RemoteGroupSynchronizer {

    public RemoteGroupSynchronizationRecord synchronizeRemoteGroup(AuthorizationManager auth, RemoteGroupDescriptor grp);
}

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
package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.AuthorizationManager;

import org.cagrid.gaards.csm.bean.RemoteGroupSynchronizationRecord;
import org.cagrid.gaards.csm.service.hibernate.RemoteGroupDescriptor;

public class DoNothingRemoteGroupSynchronizer implements RemoteGroupSynchronizer {

    public RemoteGroupSynchronizationRecord synchronizeRemoteGroup(AuthorizationManager auth, RemoteGroupDescriptor grp) {
        // TODO Auto-generated method stub
        return null;
    }

}

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

public class CSMProperties {
    private DatabaseProperties databaseProperties;
    private long secondsBetweenRemoteGroupSyncs;


    public DatabaseProperties getDatabaseProperties() {
        return databaseProperties;
    }


    public void setDatabaseProperties(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }


    public long getSecondsBetweenRemoteGroupSyncs() {
        return secondsBetweenRemoteGroupSyncs;
    }


    public void setSecondsBetweenRemoteGroupSyncs(long secondsBetweenRemoteGroupSyncs) {
        this.secondsBetweenRemoteGroupSyncs = secondsBetweenRemoteGroupSyncs;
    }

}

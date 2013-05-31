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

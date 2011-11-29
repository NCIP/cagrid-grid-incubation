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

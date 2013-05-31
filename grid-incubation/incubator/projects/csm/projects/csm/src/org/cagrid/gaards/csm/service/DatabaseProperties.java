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

public class DatabaseProperties {
    private String connectionURL;
    private String userId;
    private String password;
    private String hibernateDialect;
    private String driver;
    private String remoteGroupDatabaseCreationPolicy;


    public String getRemoteGroupDatabaseCreationPolicy() {
        return remoteGroupDatabaseCreationPolicy;
    }


    public void setRemoteGroupDatabaseCreationPolicy(String remoteGroupDatabaseCreationPolicy) {
        this.remoteGroupDatabaseCreationPolicy = remoteGroupDatabaseCreationPolicy;
    }


    public String getConnectionURL() {
        return connectionURL;
    }


    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }


    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getHibernateDialect() {
        return hibernateDialect;
    }


    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }


    public String getDriver() {
        return driver;
    }


    public void setDriver(String driver) {
        this.driver = driver;
    }

}

package org.cagrid.i2b2.ontomapper.style.wizard.config;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.i2b2.ontomapper.processor.I2B2QueryProcessor;

/**
 * DatabaseConfigurationManager
 * Manages configuration of the database for the i2b2/ontomapper 
 * dataservice wizard
 * 
 * @author David
 */
public class DatabaseConfigurationManager extends BaseConfigurationManager {
    
    private static final Log LOG = LogFactory.getLog(DatabaseConfigurationManager.class);
    
    private String driverJarFilename = null;
    private String driverClassname = null;
    private String connection = null;
    private String user = null;
    private String password = null;
    private String tablePrefix = null;

    public DatabaseConfigurationManager(ServiceExtensionDescriptionType extensionDescription,
        ServiceInformation serviceInformation) {
        super(extensionDescription, serviceInformation);
    }


    public void applyConfigration() throws Exception {
        // copy the selected JDBC driver jar into the service
        File driverJar = new File(getDriverJarFilename());
        File copyJar = new File(getServiceInformation().getBaseDirectory(),
            "lib" + File.separator + driverJar.getName());
        LOG.debug("Copying JDBC driver jar from " + driverJar.getAbsolutePath() + " to " + copyJar.getName());
        try {
            Utils.copyFile(driverJar, copyJar);
        } catch (IOException ex) {
            LOG.error("Error copying JDBC driver jar into service: " + ex.getMessage(), ex);
            throw ex;
        }
        
        // set a bunch of service properties
        setServiceProperty(I2B2QueryProcessor.JDBC_DRIVER_NAME, getDriverClassname());
        setServiceProperty(I2B2QueryProcessor.JDBC_CONNECT_STRING, getConnection());
        setServiceProperty(I2B2QueryProcessor.JDBC_USERNAME, getUser());
        setServiceProperty(I2B2QueryProcessor.JDBC_PASSWORD, getPassword());
        setServiceProperty(I2B2QueryProcessor.TABLE_NAME_PREFIX, getTablePrefix());
    }


    public String getConnection() {
        return connection;
    }


    public void setConnection(String connection) {
        this.connection = connection;
    }


    public String getDriverClassname() {
        return driverClassname;
    }


    public void setDriverClassname(String driverClassname) {
        this.driverClassname = driverClassname;
    }


    public String getDriverJarFilename() {
        return driverJarFilename;
    }


    public void setDriverJarFilename(String driverJarFilename) {
        this.driverJarFilename = driverJarFilename;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getUser() {
        return user;
    }


    public void setUser(String user) {
        this.user = user;
    }
    
    
    public String getTablePrefix() {
        return tablePrefix;
    }
    
    
    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }
}

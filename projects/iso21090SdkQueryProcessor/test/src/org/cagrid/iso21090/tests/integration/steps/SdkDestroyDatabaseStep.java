package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.iso21090.tests.integration.DatabaseProperties;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.database.DatabaseException;

public class SdkDestroyDatabaseStep extends Step {
    
    private static Log LOG = LogFactory.getLog(SdkDestroyDatabaseStep.class);
    
    public SdkDestroyDatabaseStep() {
        super();
    }
    
    
    protected Database getDatabase() {
        // get DB configuration parameters
        String host = null;
        int port = 0;
        String user = null;
        String passwd = null;
        String database = null;
        try {
            host = DatabaseProperties.getServer();
            port = Integer.parseInt(DatabaseProperties.getPort());
            user = DatabaseProperties.getUsername();
            passwd = DatabaseProperties.getPassword();
            database = DatabaseProperties.getSchemaName();
        } catch (IOException ex) {
            String message = "Error determining database configuration: " + ex.getMessage();
            LOG.error(message, ex);
            fail(message);
        }
        // set up the DB instance
        Database db = null;
        try {
            db = new Database(host, port, user, passwd, database);
        } catch (DatabaseException ex) {
            String message = "Error instantiating database: " + ex.getMessage();
            LOG.error(message, ex);
            fail(message);
        }
        return db;
    }
    
    
    public void runStep() {
        try {
            LOG.debug("DESTROYING caCORE SDK DATABASE");
            getDatabase().destroyDatabase();
        } catch (Exception ex) {
            String message = "Error performing database destroy operation: " + ex.getMessage();
            LOG.error(message, ex);
            fail(message);
        }
    }
}

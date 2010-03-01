package org.cagrid.redcap.processor;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DatabaseConnectionSource
 * Simple interface to return JDBC database connections to 
 * the Redcap query processor
 * 
 */
public interface DatabaseConnectionSource {

    public Connection getConnection() throws SQLException;
    
    public void shutdown() throws SQLException;
}

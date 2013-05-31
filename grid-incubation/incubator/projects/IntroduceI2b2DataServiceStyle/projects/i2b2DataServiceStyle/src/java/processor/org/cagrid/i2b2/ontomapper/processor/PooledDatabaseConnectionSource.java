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
package org.cagrid.i2b2.ontomapper.processor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * PooledDatabaseConnectionSource
 * Utility to get JDBC connections for the i2b2 query processor
 * using Apache's DBCP
 * 
 * @author David
 */
public class PooledDatabaseConnectionSource implements DatabaseConnectionSource {

    public static final String DBCP_DRIVER_NAME = "jdbc:apache:commons:dbcp:";
    public static final String POOL_NAME_PREFIX = "i2b2_dbcp_pool";

    private static final Log LOG = LogFactory.getLog(PooledDatabaseConnectionSource.class);
    
    private String uniquePoolId = null;
    private boolean isShutdown = false;
    
    private String jdbcDriverClassname = null;
    private String jdbcConnectionString = null;
    private String username = null;
    private String password = null;
    
    public PooledDatabaseConnectionSource(String jdbcDriverClassname, 
        String jdbcConnectionString, String username, String password) throws Exception {
        this.uniquePoolId = POOL_NAME_PREFIX + "_" + UUID.randomUUID().toString();
        this.jdbcDriverClassname = jdbcDriverClassname;
        this.jdbcConnectionString = jdbcConnectionString;
        this.username = username;
        this.password = password;
        setupDriver();
    }
    
    
    /**
     * Gets a connection from the DBCP connection pool.
     * 
     * <b>REMEMBER TO CLOSE YOUR CONNECTION!</b>
     * 
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DBCP_DRIVER_NAME + uniquePoolId);
    }
    
    
    public void shutdown() throws SQLException {
        if (!isShutdown()) {
            PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(DBCP_DRIVER_NAME);
            driver.closePool(uniquePoolId);
            isShutdown = true;
        }
    }
    
    
    public boolean isShutdown() {
        return isShutdown;
    }
    
    
    private void setupDriver() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        // load and init the "inner" JDBC driver
        LOG.debug("Loading and registering JDBC driver class " + jdbcDriverClassname);
        Class.forName(jdbcDriverClassname, true, loader);
        
        // generic object pool to hold connections
        LOG.debug("Creating connection pool");
        ObjectPool connectionPool = new GenericObjectPool(null);

        // connection factory gets passed the jdbc connect string,
        // username, and password
        LOG.debug("Creating connection factory");
        ConnectionFactory connectionFactory = 
            new DriverManagerConnectionFactory(jdbcConnectionString, username, password);

        // a poolable connection factory wraps the "real" JDBC connections from
        // the connection factory with pooling implementation
        PoolableConnectionFactory poolableConnectionFactory = 
            new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

        // Finally, we create the PoolingDriver itself...
        LOG.debug("Loading and registering pooling DBCP driver");
        Class.forName("org.apache.commons.dbcp.PoolingDriver", true, loader);
        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(DBCP_DRIVER_NAME);

        // ...and register our pool with it.
        LOG.debug("Registering connection pool with DBCP driver");
        driver.registerPool(uniquePoolId, connectionPool);
    }
    
    
    public int getActiveConnectionCount() throws SQLException {
        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(DBCP_DRIVER_NAME);
        ObjectPool connectionPool = driver.getConnectionPool(uniquePoolId);
        return connectionPool.getNumActive();
    }
    
    
    public int getIdleConnectionCount() throws SQLException {
        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(DBCP_DRIVER_NAME);
        ObjectPool connectionPool = driver.getConnectionPool(uniquePoolId);
        return connectionPool.getNumIdle();
    }
}

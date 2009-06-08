package org.cagrid.i2b2.ontomapper.processor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility to get JDBC connections for the i2b2 query processor
 * using Apache's DBCP
 * 
 * @author David
 */
public class I2B2DatabaseConnectionSource {

    public static final String DBCP_DRIVER_NAME = "jdbc:apache:commons:dbcp:";
    public static final String POOL_NAME = "i2b2_dbcp_pool";

    private static final Log LOG = LogFactory.getLog(I2B2DatabaseConnectionSource.class);
    
    private String jdbcDriverClassname = null;
    private String jdbcConnectionString = null;
    private String username = null;
    private String password = null;
    
    public I2B2DatabaseConnectionSource(String jdbcDriverClassname, String jdbcConnectionString, String username, String password) {
        this.jdbcDriverClassname = jdbcDriverClassname;
        this.jdbcConnectionString = jdbcConnectionString;
        this.username = username;
        this.password = password;
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
        return DriverManager.getConnection(DBCP_DRIVER_NAME + POOL_NAME);
    }
    
    
    public void shutdown() throws SQLException {
        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(DBCP_DRIVER_NAME);
        driver.closePool(POOL_NAME);
    }
    
    
    public void setupDriver() throws Exception {
        // load and init the "inner" JDBC driver
        LOG.debug("Loading and registering JDBC driver class " + jdbcDriverClassname);
        Class.forName(jdbcDriverClassname);
        
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
        Class.forName("org.apache.commons.dbcp.PoolingDriver");
        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(DBCP_DRIVER_NAME);

        // ...and register our pool with it.
        LOG.debug("Registering connection pool with DBCP driver");
        driver.registerPool(POOL_NAME, connectionPool);
    }

    
    public static void printDriverStats() throws Exception {
        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver(DBCP_DRIVER_NAME);
        ObjectPool connectionPool = driver.getConnectionPool(POOL_NAME);
        
        System.out.println("NumActive: " + connectionPool.getNumActive());
        System.out.println("NumIdle: " + connectionPool.getNumIdle());
    }
}

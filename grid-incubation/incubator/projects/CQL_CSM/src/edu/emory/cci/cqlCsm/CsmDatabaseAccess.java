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
/**
 */

package edu.emory.cci.cqlCsm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Singleton to manage access to CSM policy objects.
 * 
 * @author Mark Grand
 */
public class CsmDatabaseAccess {
    private static final Logger myLogger = LogManager.getLogger(CsmDatabaseAccess.class);
    private static CsmDatabaseAccess myInststance = null;

    private final CsmProperties csmProperties = new CsmProperties();

    // private static ObjectCache<String, UserAuthorization> cache = new
    // ObjectCache<String, UserAuthorization>();
    private static final ArrayList<Connection> connectionPool = new ArrayList<Connection>();

    // Cache that associated retrieved filters with a class name.
    private final HashMap<String, List<Filter>> filterCache = new HashMap<String, List<Filter>>();

    private long appId;

    /**
     * Private constructor exists the that no public constructor is created by
     * the compiler.
     * 
     * @throws RuntimeException
     *             if it is unable to determine the CSM application ID. This can
     *             be caused by misconfiguration in csm.Properties or a database
     *             problem.
     */
    private CsmDatabaseAccess() {
        appId = fetchApplicationId();
    }

    /**
     * Return the single instance of this class
     */
    public static CsmDatabaseAccess getInstance() {
        if (myInststance == null) {
            synchronized (CsmDatabaseAccess.class) {
                if (myInststance == null) {
                    myInststance = new CsmDatabaseAccess();
                }
            }
        }
        return myInststance;
    }

    /**
     * Return a database connection, either getting one from the connection pool
     * or creating a new connection.
     */
    private Connection getDbConnection() {
        synchronized (connectionPool) {
            while (true) {
                if (connectionPool.isEmpty()) {
                    String url = csmProperties.getUrl();
                    String userName = csmProperties.getUserId();
                    String password = csmProperties.getUserPassword();
                    try {
                        Class.forName(csmProperties.getDriver());
                    } catch (ClassNotFoundException e) {
                        String msg = "Failed to load postgresql database driver";
                        myLogger.fatal(msg, e);
                        throw new RuntimeException(msg, e);
                    }
                    try {
                        return DriverManager.getConnection(url, userName, password);
                    } catch (SQLException e) {
                        String msg = "Error establishing database connection.  URL=" + url + "  jdbc.drivers="
                                + System.getProperty("jdbc.drivers");
                        myLogger.error(msg, e);
                        throw new RuntimeException(msg, e);
                    }
                } else {
                    Connection thisConn = connectionPool.remove(0);
                    try {
                        if (!thisConn.isClosed() /*
                                                  * && thisConn.isValid(5) /*
                                                  * isValid is not available
                                                  * before Java 1.6
                                                  */) {
                            thisConn.setAutoCommit(false);
                            return thisConn;
                        }
                    } catch (Exception e) {
                        String msg = "Error while checking state of connection";
                        myLogger.error(msg, e);
                    }
                }
            }
        }
    }

    /**
     * Return the given connection to the connection pool.
     * 
     * @param conn
     *            the connection to return to the connection pool.
     */
    private void returnToConnectionPool(Connection conn) {
        connectionPool.add(conn);
    }

    /**
     * Fetch the ID for the named application from the CSM database. The
     * application name is the value of the csm.appName property in the
     * csm.properties file.
     * 
     * @return the numeric id that the database associates with the named
     *         application or null if the application name is not found in the
     *         database.
     */
    public long fetchApplicationId() {
        String appName = csmProperties.getAppName();
        String sql = "SELECT APPLICATION_ID FROM CSM_APPLICATION WHERE APPLICATION_NAME=?";
        Connection conn = getDbConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, appName);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                rs.close();
                insertApplicationId(conn, appName);
                rs = pstmt.executeQuery();
                if (!rs.next()) {
                    String msg = "Attempt to get app ID for " + appName + " failed.  Query is: " + sql;
                    myLogger.error(msg);
                    throw new RuntimeException(msg);
                }
            }
            long id = rs.getLong(1);
            if (rs.wasNull()) {
                String msg = "Application_id associated with application_name \'" + appName + "\" is null";
                myLogger.fatal(msg);
                throw new RuntimeException(msg);
            }
            if (myLogger.isInfoEnabled()) {
                myLogger.info("Application ID for " + appName + " is " + id);
            }
            return id;
        } catch (SQLException e) {
            String msg = "Error fetching application ID for " + appName + ".  Query is: " + sql;
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        } finally {
            returnToConnectionPool(conn);
        }
    }

    private void insertApplicationId(Connection conn, String appName) throws SQLException {
        String sql = "INSERT INTO CSM_APPLICATION (APPLICATION_NAME,APPLICATION_DESCRIPTION) VALUES (?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, appName);
            pstmt.setString(2, " ");
            int rc = pstmt.executeUpdate();
            if (rc != 1) {
                conn.rollback();
                String msg = "Expected insert to affect exactl 1 row, but it effected " + rc + " rows. Sql was: " + sql;
                myLogger.error(msg);
                throw new RuntimeException(msg);
            }
            if (myLogger.isInfoEnabled()) {
                myLogger.info("Added application to CSM database: " + appName);
            }
            conn.commit();
        } catch (SQLException e) {
            String msg = "Error fetching application ID for " + appName + ".  Query is: " + sql;
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Add the given combination of identity, protection element properties and
     * privilege to the cache.
     * 
     * @param ua
     *            The UserAuthorization object under which of the authorization
     *            information is to be cached.
     * @param protectionElementId
     *            The id of the protection element whose properties are being
     *            cached.
     * @param objectId
     *            The value of the protection element's objectId property.
     * @param attributeName
     *            The value of the protection element's attribute property.
     * @param attributeValue
     *            The value of the protection element's attributeValue property.
     * @param protectionElementType
     *            The value of the protection element's protectionElementType
     *            property.
     * @param privName
     *            The name of the privilege that is associated with the
     *            protection element.
     */
    // private static void cacheAuthorization(UserAuthorization ua, long
    // protectionElementId, String objectId,
    // String attributeName, String attributeValue, String
    // protectionElementType, String privName) {
    // ProtectionElement pe;
    // pe = new ProtectionElement(protectionElementId, objectId, attributeName,
    // attributeValue, protectionElementType);
    //
    // // Assume that the attributeValue property is a good key to use for
    // // finding cached protection elements.
    // ProtectionElementAndPrivilegesCombination protPrivCombo =
    // ua.addProtectionElement(pe.getAttributeValue(), pe);
    // protPrivCombo.addPrivilege(privName);
    // }

    private static void appendAsStringLiteral(StringBuilder builder, String literal) {
        builder.append('\'');
        for (int i = 0; i < literal.length(); i++) {
            char c = literal.charAt(i);
            if (c == '\'') {
                // double '
                builder.append(c);
            }
            builder.append(c);
        }
        builder.append('\'');
    }

    /**
     * Return a list of filters associated with the named class. This list may
     * come from the database or from a cache.
     * 
     * @param className
     *            The name of the class that the fetched filters should be
     *            associated
     * @return a list of filters associated with the named class.
     * @throws RuntimeException
     *             If there is a problem getting the requested filters. The
     *             rationale for throwing an exception is that if we cannot
     *             determine what, if any, filters are associated with a class
     *             then we should prevent access.
     */
    public List<Filter> getFiltersForClass(String className) {
        if (myLogger.isDebugEnabled()) {
            myLogger.debug("Filters requested for class: " + className);
        }
        List<Filter> filters = filterCache.get(className);
        if (filters != null) {
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("Found filters for " + className + " in cache.");
            }
        } else {
            try {
                filters = fetchFiltersForClass(className);
            } catch (SQLException e) {
                String msg = "Problem fetching filters from database for class: " + className;
                myLogger.error(msg, e);
                throw new RuntimeException(msg, e);
            }
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("Fetched filters for " + className + ".");
            }
            filterCache.put(className, filters);
        }
        return filters;
    }

    /**
     * Return a list of the filters associated with a named class.
     * 
     * @param className
     *            The name of the class for which associated filters are to be
     *            fetched and returned.
     */
    private List<Filter> fetchFiltersForClass(String className) throws SQLException {
        String template = "select class_name,filter_chain,target_class_name,target_class_attribute_name"
                + " from csm_filter_clause where class_name=";
        StringBuilder builder = new StringBuilder(template);
        appendAsStringLiteral(builder, className);
        String sql = builder.toString();
        ArrayList<Filter> filters = new ArrayList<Filter>();
        Connection conn = getDbConnection();
        try {
            Statement stmt = conn.createStatement();
            try {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    Filter thisFilter = new Filter(rs.getString(1), rs.getString(2), rs.getString(4), rs.getString(3));
                    filters.add(thisFilter);
                }
            } finally {
                stmt.close();
            }
        } finally {
            returnToConnectionPool(conn);
        }
        return filters;
    }

    /**
     * Return a set of value strings that may be the value of a named attribute
     * of a named class. The set of values represented by these strings are just
     * those values of the named attribute that imply authorization for the
     * given identity to read the associated object/record.
     * 
     * @param identity
     *            The identity being authorized.
     * @param targetClassName
     *            The name of the class that the attribute is associated with.
     * @param attributeName
     *            The name of the attribute.
     * @return possibly empty set of value strings.
     * @throws RuntimeException
     *             If there is a problem getting the attribute values.
     */
    public TreeSet<String> getAuthorizedAttributeValues(String identity, String targetClassName, String attributeName) {
        StringBuilder builder = new StringBuilder("SELECT ATTRIBUTE_VALUE FROM CSM_USER_PROTECTION_ELEMENTS WHERE APPLICATION_ID=");
        builder.append(appId);
        builder.append(" AND LOGIN_NAME=");
        appendAsStringLiteral(builder, identity);
        builder.append(" AND OBJECT_ID=");
        appendAsStringLiteral(builder, targetClassName);
        builder.append(" AND ATTRIBUTE=");
        appendAsStringLiteral(builder, attributeName);
        builder.append("AND PRIVILEGE_NAME ='READ'");
        String sql = builder.toString();

        TreeSet<String> valueSet = new TreeSet<String>();
        Connection conn = getDbConnection();
        try {
            Statement stmt = conn.createStatement();
            try {
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    valueSet.add(rs.getString(1));
                }
            } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            String msg = "Problem retrieving authorized attributes values from database.  SQL: " + sql;
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        } finally {
            returnToConnectionPool(conn);
        }
        return valueSet;
    }
    
    public CsmProperties getCsmProperties() {
        return csmProperties ;
    }
}

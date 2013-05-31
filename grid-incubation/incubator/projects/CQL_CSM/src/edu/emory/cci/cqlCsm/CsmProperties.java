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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.globus.wsrf.Constants;

import gov.nih.nci.cagrid.introduce.servicetools.ServiceConfiguration;

/**
 * Access configuration properties related to csm from a properties file named
 * csm.properties. This file is expected to be found either in the class path or
 * in the container's etc directory.
 * 
 * @author Mark Grand
 */
public class CsmProperties {
    private static final Logger myLogger = LogManager.getLogger(CsmProperties.class);

    private static final String CSM_PROPERTIES = "csm.properties";

    // Names of properties required in file.
    private static final String CSM_DRIVER = "csm.database.driver";
    private static final String CSM_HOST = "csm.database.host";
    private static final String CSM_PORT = "csm.database.port";
    private static final String CSM_URL = "csm.database.url";
    private static final String CSM_DBNAME = "csm.database.dbname";
    private static final String CSM_USER = "csm.database.user";
    private static final String CSM_PASSWORD = "csm.database.password";
    private static final String CSM_APPNAME = "csm.appName";
    private static final String CSM_OWNER_USER = "csm.dbowner.user";
    private static final String CSM_OWNER_PASSWORD = "csm.dbowner.password";

    // Names of properties needed only for testing.
    private static final String CSM_ADMIN = "csm.dbadmin.user";
    private static final String CSM_ADMIN_PSWD = "csm.dbadmin.password";

    // Names of properties with default values
    private static final String CSM_DEFAULT_TARGET_ATTRIBUTE = "csm.default.target.attribute";

    private Properties csmProps = new Properties();

    /**
     * Constructor.
     */
    public CsmProperties() {
        getCsmProperties();
    }

    /**
     * Return a stream to read the CSM properties file.
     * 
     * @throws RuntimeException
     *             if the resource that contains the CSM properties cannot be
     *             found.
     */
    private BufferedInputStream csmPropertiesAsStream() {
        URL csmPropertiesUrl = getClass().getResource("/" + CSM_PROPERTIES);
        if (csmPropertiesUrl == null) {
            // CSM_PROPERTIES not in classpath; ask globus where to look.

            MessageContext msgContext = MessageContext.getCurrentContext();
            if (msgContext == null) {
                String msg = "Unable to find resource: " + CSM_PROPERTIES + "\nClassPath is \"" + System.getProperty("java.class.path");
                myLogger.fatal(msg);
                throw new RuntimeException(msg);
            }
            String servicePath = msgContext.getTargetService();

            String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
            File propertiesFile;
            try {
                javax.naming.Context initialContext = new InitialContext();
                ServiceConfiguration configuration = (ServiceConfiguration) initialContext.lookup(jndiName);
                String etcDirectory = configuration.getEtcDirectoryPath();
                propertiesFile = new File(etcDirectory, CSM_PROPERTIES);
            } catch (Exception e) {
                String msg = "Error determining directory in which to look for csm.properties.";
                myLogger.error(msg, e);
                throw new RuntimeException(msg, e);
            }
            try {
                return new BufferedInputStream(new FileInputStream(propertiesFile));
            } catch (Exception e) {
                String msg = "Unable to find resource: " + CSM_PROPERTIES + "\nClassPath is \"" + System.getProperty("java.class.path")
                        + "\"\nconfigFile: " + propertiesFile.getAbsolutePath();
                msg += "\nmessageContext properties are {";
                Iterator<String> messageContextPropertyNameIterator = getMessageContextPropertyNameIterator(msgContext);
                if (messageContextPropertyNameIterator.hasNext()) {
                    String propertyName = messageContextPropertyNameIterator.next();
                    msg += propertyName + "=" + msgContext.getProperty(propertyName);
                    while (messageContextPropertyNameIterator.hasNext()) {
                        propertyName = messageContextPropertyNameIterator.next();
                        msg += "\n, " + propertyName + "=" + msgContext.getProperty(propertyName);
                    }
                }
                msg += "}";
                myLogger.fatal(msg);
                throw new RuntimeException(msg);
            }
        } else {
            if (myLogger.isInfoEnabled()) {
                myLogger.info("Reading CSM properties from URL: " + csmPropertiesUrl.toExternalForm());
            }
            InputStream in;
            try {
                in = csmPropertiesUrl.openStream();
            } catch (Exception e) {
                String msg = "Error opening CSM properties file" + csmPropertiesUrl.toExternalForm();
                myLogger.error(msg);
                throw new RuntimeException(msg);
            }
            BufferedInputStream bin = new BufferedInputStream(in);
            return bin;
        }
    }

    /**
     * @param msgContext
     * @return
     */
    @SuppressWarnings("unchecked")
    private Iterator<String> getMessageContextPropertyNameIterator(MessageContext msgContext) {
        return msgContext.getPropertyNames();
    }

    /**
     * Read properties from the csm.properties file.
     */
    private Properties getCsmProperties() {
        BufferedInputStream bin = csmPropertiesAsStream();
        try {
            try {
                csmProps.load(bin);
            } finally {
                bin.close();
            }
        } catch (IOException e) {
            String msg = "Error reading " + CSM_PROPERTIES + " file";
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        if (myLogger.isInfoEnabled()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println("Read " + CSM_PROPERTIES + " contents are");
            csmProps.list(pw);
            pw.flush();
            myLogger.info(sw.toString());
        }
        if (!csmProps.containsKey(CSM_URL) || !csmProps.containsKey(CSM_USER) || !csmProps.containsKey(CSM_PASSWORD)
                || !csmProps.containsKey(CSM_DRIVER) || !csmProps.containsKey(CSM_APPNAME)) {
            String msg = CSM_PROPERTIES + " must specify all of " + CSM_DRIVER + ", " + CSM_URL + ", " + CSM_USER + ", " + CSM_PASSWORD
                    + " and " + CSM_APPNAME;
            myLogger.error(msg);
            throw new RuntimeException(msg);
        }
        return csmProps;
    }

    /**
     * Return the name of the host that the database is running on.
     */
    public String getDatabaseHost() {
        return csmProps.getProperty(CSM_HOST);
    }

    /**
     * Return the port number for connecting to the database manager.
     */
    public String getDatabasePort() {
        return csmProps.getProperty(CSM_PORT);
    }

    /**
     * Return the name of the database to connect to.
     */
    public String getDatabaseName() {
        return csmProps.getProperty(CSM_DBNAME);
    }

    /**
     * Return the user id that the data service should use for connecting to the
     * database manager.
     */
    public String getUserId() {
        return csmProps.getProperty(CSM_USER);
    }

    /**
     * Return the password that the data service should use for connecting to
     * the database manager.
     */
    public String getUserPassword() {
        return csmProps.getProperty(CSM_PASSWORD);
    }

    /**
     * Return the user ID to use for connecting to the database manager as the
     * owner of the csm tables..
     */
    public String getOwnerId() {
        return csmProps.getProperty(CSM_OWNER_USER);
    }

    /**
     * Return the password to use for connecting to the database manager as the
     * owner of the csm tables..
     */
    public String getOwnerPassword() {
        return csmProps.getProperty(CSM_OWNER_PASSWORD);
    }

    /**
     * Return the user ID to use for connecting to the database manager to
     * administer the contents of the csm tables..
     */
    public String getAdminId() {
        return csmProps.getProperty(CSM_ADMIN);
    }

    /**
     * Return the password to use for connecting to the database manager to
     * administer the contents of the csm tables..
     */
    public String getAdminPassword() {
        String password = csmProps.getProperty(CSM_ADMIN_PSWD);
        myLogger.debug("csmProps.getProperty(\"" + CSM_ADMIN_PSWD + "\") = " + password);
        return password;
    }

    /**
     * Return the application name that is used to identify the data service in
     * the csm tables.
     */
    public String getAppName() {
        return csmProps.getProperty(CSM_APPNAME);
    }

    /**
     * Return the name of the class of the JDBC driver to be used to work with
     * the database,
     */
    public String getDriver() {
        return csmProps.getProperty(CSM_DRIVER);
    }

    /**
     * Return the JDBC URL to use for connecting to the database.
     */
    public String getUrl() {
        return csmProps.getProperty(CSM_URL);
    }

    /**
     * To generate a valid constraint that will prevent a query from retrieving
     * and results, the CQL preprocessor needs to use the name of an attribute
     * associated with the CQL query's target class if the target class has no
     * attributes defined in the data service's domain model. If this returns
     * the empty string, then there is no such attribute name. If there is no
     * such attribute name, when the pre-processor detects that the user has no
     * authorization for retrieving the requested objects, it will throw an
     * exception instead of generating an unsatisfiable constraint.
     * 
     * If no value is specified for this property in csm.properties, then its
     * value defaults to "_value".
     * 
     * @return The default attribute name to use or the empty string.
     */
    public String getDefaultTargetAttributeName() {
        String attributeName = csmProps.getProperty(CSM_DEFAULT_TARGET_ATTRIBUTE);
        if (attributeName == null) {
            return "_value";
        }
        return attributeName.trim();
    }
}

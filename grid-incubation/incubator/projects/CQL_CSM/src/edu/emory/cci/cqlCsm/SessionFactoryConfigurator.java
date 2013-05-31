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

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import edu.emory.cci.cqlCsm.cqlCsmHibernate.ApplicationServiceImpl;

/**
 * Configure a hibernate session factory that knows about application specify
 * and common CSM objects and tables.
 * 
 * @author Mark Grand
 */
public class SessionFactoryConfigurator {
    private static Logger myLogger = Logger.getLogger(ApplicationServiceImpl.class.getName());

    private static String CSM_FILE_NAME_SUFFIX = ".csm.new.hibernate.cfg.xml";
    private static String APP_HIBERNATE_CONFIGURATION_FILE_NAME = "hibernate.cfg.xml";
    private static Map<String, SessionFactory> appToSessionFactoryMap = new HashMap<String, SessionFactory>();

    /**
     * This method is intended for applications that use CSM instance-level
     * security to control access to relational database tables.
     * <p>
     * Return the session factory for the given application. If no session
     * factory exists for the given application, a session factory is created.
     * <p>
     * Session factories are configured by combining hibernate mapping
     * information from two sources. There is mapping information for CSM tables
     * which is obtained from a file that may be located in in a .jar file. If
     * configuring CSM this way does not work, some hard-wired information is
     * used instead.
     * <p>
     * After the CSM information is configured, application-specific hibernate
     * mapping information is configured from a file. Because
     * application-specific information is configured after the CSM information,
     * the application-specific information can override information supplied
     * for CSM.
     * 
     * @param applicationName
     * @return a session factory that is based on mappings for both
     *         application-specific and CSM tables.
     */
    public static SessionFactory getCsmAndAppSessionFactory(String applicationName) {
        SessionFactory sf = appToSessionFactoryMap.get(applicationName);
        if (sf == null) {
            Configuration config = new Configuration();
            doCsmConfiguration(applicationName, config);
            doAppConfiguration(config);
            sf = config.buildSessionFactory();
            appToSessionFactoryMap.put(applicationName, sf);
        }
        return sf;
    }

    /**
     * This method is intended for applications that use CSM instance-level
     * security to control access to data stored outside of a relational
     * database.
     * <p>
     * Return the session factory for the given application to access CSM data.
     * If no session factory exists for the given application, a session factory
     * is created.
     * <p>
     * Session factories are configured from mapping information for CSM tables
     * which is obtained from a file that may be located in in a .jar file. If
     * configuring CSM this way does not work, some hard-wired information is
     * used instead.
     * 
     * @param applicationName
     * @return a session factory that is based on mappings for CSM tables.
     */
    public static SessionFactory getCsmSessionFactory(String applicationName) {
        SessionFactory sf = appToSessionFactoryMap.get(applicationName);
        if (sf == null) {
            Configuration config = new Configuration();
            doCsmConfiguration(applicationName, config);
            sf = config.buildSessionFactory();
            appToSessionFactoryMap.put(applicationName, sf);
        }
        return sf;
    }

    /**
     * @param config
     */
    private static void doAppConfiguration(Configuration config) {
        try {
            config.configure(getFileAsURL(APP_HIBERNATE_CONFIGURATION_FILE_NAME));
        } catch (Exception e) {
            String msg = "Unable to locate hibernate configuration file for application as a resource: "
                    + APP_HIBERNATE_CONFIGURATION_FILE_NAME;
            myLogger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * @param applicationName
     * @param config
     */
    private static void doCsmConfiguration(String applicationName, Configuration config) {
        try {
            config.configure(getCsmHibernateMappingUrl(applicationName));
        } catch (Exception e) {
            String msg = "Hibernate configuration for CSM tables failed for application: " + applicationName
                    + "; using hard-wired configuration information";
            myLogger.info(msg, e);
            hardWiredCsmHibernateConfiguration(config);
        }
    }

    /**
     * Supply hard-wired hibernate configuration information for CSM tables.
     * This is intended to be used as a fall-back when configuration using an
     * externally supplied file fails.
     * 
     * @param config
     */
    private static void hardWiredCsmHibernateConfiguration(Configuration config) {
        try {
            config.addResource("gov/nih/nci/security/authorization/domainobjects/Privilege.hbm.xml");
            config.addResource("gov/nih/nci/security/authorization/domainobjects/Application.hbm.xml");
            config.addResource("gov/nih/nci/security/authorization/domainobjects/FilterClause.hbm.xml");
            config.addResource("gov/nih/nci/security/authorization/domainobjects/Role.hbm.xml");
            config.addResource("gov/nih/nci/security/dao/hibernate/RolePrivilege.hbm.xml");
            config.addResource("gov/nih/nci/security/dao/hibernate/UserGroup.hbm.xml");
            config.addResource("gov/nih/nci/security/dao/hibernate/ProtectionGroupProtectionElement.hbm.xml");
            config.addResource("gov/nih/nci/security/authorization/domainobjects/Group.hbm.xml");
            config.addResource("gov/nih/nci/security/authorization/domainobjects/User.hbm.xml");
            config.addResource("gov/nih/nci/security/authorization/domainobjects/ProtectionGroup.hbm.xml");
            config.addResource("gov/nih/nci/security/authorization/domainobjects/ProtectionElement.hbm.xml");
            config.addResource("gov/nih/nci/security/authorization/domainobjects/UserGroupRoleProtectionGroup.hbm.xml");
            config.addResource("gov/nih/nci/security/authorization/domainobjects/UserProtectionElement.hbm.xml");
            config.setProperty("hibernate.c3p0.min_size", "5");
            config.setProperty("hibernate.c3p0.max_size", "20");
            config.setProperty("hibernate.c3p0.timeout", "300");
            config.setProperty("hibernate.c3p0.max_statements", "50");
            config.setProperty("hibernate.c3p0.idle_test_period", "3000");
        } catch (RuntimeException e) {
            String msg = "Failure to apply hard-wired configuration for CSM tables.";
            myLogger.error(msg, e);
            throw e;
        }
    }

    public static URL getCsmHibernateMappingUrl(String name) throws FileNotFoundException {
        return getFileAsURL(name + CSM_FILE_NAME_SUFFIX);
    }

    /**
     * Return the URL where the named file is found.
     * 
     * @param fileName
     *            The name of the file to look for.
     * @return a URL to the named file which may be inside of a .jar file., i.e.
     *         if name is <tt>app.csm.new.hibernate.cfg.xml</tt> this might
     *         return
     *         <tt>java:file:/path/app-config.jar!/app.csm.new.hibernate.cfg.xml</tt>
     * @throws FileNotFoundException
     *             If the named file cannot be found.
     */
    public static URL getFileAsURL(final String fileName) throws FileNotFoundException {
        return getFileAsURL(fileName, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Return the URL where the named file is found.
     * 
     * @param fileName
     *            The name of the file to look for.
     * @param loader
     *            The classloader to use for searching.
     * @return a URL to the named file which may be inside of a .jar file., i.e.
     *         if name is <tt>app.csm.new.hibernate.cfg.xml</tt> this might
     *         return
     *         <tt>java:file:/path/app-config.jar!/app.csm.new.hibernate.cfg.xml</tt>
     * @throws FileNotFoundException
     *             If the named file cannot be found.
     */
    public static URL getFileAsURL(String fileName, ClassLoader loader) throws IllegalArgumentException,
            FileNotFoundException {
        if (fileName == null)
            throw new IllegalArgumentException("null input: name");

        if (fileName.startsWith("/"))
            fileName = fileName.substring(1);

        URL url = null;
        try {
            if (loader == null)
                loader = ClassLoader.getSystemClassLoader();
            // name = name.replace('.', '/');

            // returns null on lookup failures:
            url = loader.getResource(fileName);
        } catch (Exception e) {
            String msg = "Got unexpected exception while trying to find " + fileName;
            myLogger.error(msg, e);
            FileNotFoundException excpt = new FileNotFoundException(msg);
            excpt.initCause(e);
            throw excpt;
        }

        if (url == null) {
            throw new FileNotFoundException(fileName);
        }
        return url;
    }
}

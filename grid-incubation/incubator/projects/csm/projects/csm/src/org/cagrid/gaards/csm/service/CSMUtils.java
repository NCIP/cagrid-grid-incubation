package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.FilterClause;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;
import gov.nih.nci.security.authorization.domainobjects.ProtectionGroup;
import gov.nih.nci.security.authorization.domainobjects.Role;
import gov.nih.nci.security.dao.ApplicationSearchCriteria;
import gov.nih.nci.security.dao.FilterClauseSearchCriteria;
import gov.nih.nci.security.dao.GroupSearchCriteria;
import gov.nih.nci.security.dao.PrivilegeSearchCriteria;
import gov.nih.nci.security.dao.ProtectionElementSearchCriteria;
import gov.nih.nci.security.dao.ProtectionGroupSearchCriteria;
import gov.nih.nci.security.dao.RoleSearchCriteria;
import gov.nih.nci.security.exceptions.CSException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.csm.bean.LocalGroup;
import org.cagrid.gaards.csm.bean.RemoteGroup;
import org.cagrid.gaards.csm.service.hibernate.RemoteGroupDescriptor;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.tools.database.Database;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;

public class CSMUtils {
    public static final String CSM_CONFIGURATION = "csm-configuration.xml";
    public static final String CSM_PROPERTIES = "csm.properties";

    private static Log log = LogFactory.getLog(CSMUtils.class.getName());

    public static AuthorizationManager getAuthorizationManager(AuthorizationManager auth, DatabaseProperties db,
            long applicationId) throws CSMInternalFault {
        try {

            gov.nih.nci.security.authorization.domainobjects.Application app = auth.getApplicationById(String
                    .valueOf(applicationId));
            return getAuthorizationManager(db, app);

        } catch (CSException e) {
            logError(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString("Unexpected error obtaining the authorization manager for the application "
                    + applicationId + ".");
            throw fault;
        }
    }

    public static AuthorizationManager getAuthorizationManager(DatabaseProperties db, Application app)
            throws CSMInternalFault {

        HashMap<String, String> connectionProperties = new HashMap<String, String>();
        if (gov.nih.nci.cagrid.common.Utils.clean(app.getDatabaseURL()) != null) {
            connectionProperties.put("hibernate.connection.url", app.getDatabaseURL());
        } else {
            connectionProperties.put("hibernate.connection.url", db.getConnectionURL());
        }

        if (gov.nih.nci.cagrid.common.Utils.clean(app.getDatabaseUserName()) != null) {
            connectionProperties.put("hibernate.connection.username", app.getDatabaseUserName());
        } else {
            connectionProperties.put("hibernate.connection.username", db.getUserId());
        }

        if (gov.nih.nci.cagrid.common.Utils.clean(app.getDatabasePassword()) != null) {
            connectionProperties.put("hibernate.connection.password", app.getDatabasePassword());
        } else {
            connectionProperties.put("hibernate.connection.password", db.getPassword());
        }

        if (gov.nih.nci.cagrid.common.Utils.clean(app.getDatabaseDialect()) != null) {
            connectionProperties.put("hibernate.dialect", app.getDatabaseDialect());
        } else {
            connectionProperties.put("hibernate.dialect", db.getHibernateDialect());
        }
        if (gov.nih.nci.cagrid.common.Utils.clean(app.getDatabaseDriver()) != null) {
            connectionProperties.put("hibernate.connection.driver_class", app.getDatabaseDriver());
        } else {
            connectionProperties.put("hibernate.connection.driver_class", db.getDriver());
        }

        try {
            AuthorizationManager auth = (AuthorizationManager) SecurityServiceProvider.getUserProvisioningManager(app
                    .getApplicationName(), connectionProperties);

            return auth;
        } catch (CSException e) {
            logError(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString("Unexpected error obtaining the authorization manager for the application "
                    + app.getApplicationName() + ".");
            throw fault;
        }
    }

    public static RemoteGroup convert(Group grp, RemoteGroupDescriptor des) {
        RemoteGroup rg = new RemoteGroup();
        rg.setApplicationId(grp.getApplication().getApplicationId());
        rg.setDescription(grp.getGroupDesc());
        rg.setRemoteGroupName(des.getGridGrouperGroupName());
        rg.setGridGrouperURL(des.getGridGrouperURL());
        rg.setId(grp.getGroupId());
        rg.setName(grp.getGroupName());
        if (grp.getUpdateDate() != null) {
            Calendar c = new GregorianCalendar();
            c.setTime(grp.getUpdateDate());
            rg.setLastUpdated(c);
        }
        return rg;
    }

    public static LocalGroup convert(Group grp) {
        LocalGroup lg = new LocalGroup();
        lg.setApplicationId(grp.getApplication().getApplicationId());
        lg.setDescription(grp.getGroupDesc());
        lg.setId(grp.getGroupId());
        lg.setName(grp.getGroupName());
        if (grp.getUpdateDate() != null) {
            Calendar c = new GregorianCalendar();
            c.setTime(grp.getUpdateDate());
            lg.setLastUpdated(c);
        }
        return lg;
    }

    public static Group convert(LocalGroup grp) {
        Group g = new Group();
        g.setGroupDesc(grp.getDescription());
        if (grp.getId() != null) {
            g.setGroupId(grp.getId().longValue());
        }
        g.setGroupName(grp.getName());
        if (grp.getLastUpdated() != null) {
            g.setUpdateDate(grp.getLastUpdated().getTime());
        }
        return g;
    }

    public static Privilege convert(org.cagrid.gaards.csm.bean.Privilege p) {
        Privilege priv = new Privilege();
        if (p.getId() != null) {
            priv.setId(p.getId().longValue());
        }
        priv.setName(p.getName());
        priv.setDesc(p.getDescription());
        if (p.getLastUpdated() != null) {
            priv.setUpdateDate(p.getLastUpdated().getTime());
        }
        return priv;
    }

    public static org.cagrid.gaards.csm.bean.Privilege convert(Privilege p) {
        org.cagrid.gaards.csm.bean.Privilege priv = new org.cagrid.gaards.csm.bean.Privilege();
        if (p.getId() != null) {
            priv.setId(p.getId().longValue());
        }
        priv.setName(p.getName());
        priv.setDescription(p.getDesc());
        if (p.getUpdateDate() != null) {
            Calendar c = new GregorianCalendar();
            c.setTime(p.getUpdateDate());
            priv.setLastUpdated(c);
        }

        return priv;
    }

    public static PrivilegeSearchCriteria convert(org.cagrid.gaards.csm.bean.PrivilegeSearchCriteria criteria) {
        Privilege priv = new Privilege();
        if (criteria != null) {
            priv.setName(criteria.getName());
        }
        return new PrivilegeSearchCriteria(priv);
    }

    public static FilterClause convert(org.cagrid.gaards.csm.bean.FilterClause f) {
        FilterClause filter = new FilterClause();
        if (f.getId() != null) {
            filter.setId(f.getId().longValue());
        }
        filter.setClassName(f.getClassName());
        filter.setFilterChain(f.getFilterChain());
        filter.setGeneratedSQLForGroup(f.getGeneratedSQLForGroup());
        filter.setGeneratedSQLForUser(f.getGeneratedSQLForUser());
        filter.setTargetClassAlias(f.getTargetClassAlias());
        filter.setTargetClassAttributeAlias(f.getTargetClassAttributeAlias());
        filter.setTargetClassAttributeName(f.getTargetClassAttributeName());
        filter.setTargetClassAttributeType(f.getTargetClassAttributeType());
        filter.setTargetClassName(f.getTargetClassName());
        if (f.getLastUpdated() != null) {
            filter.setUpdateDate(f.getLastUpdated().getTime());
        }
        return filter;
    }

    public static org.cagrid.gaards.csm.bean.FilterClause convert(FilterClause f) {
        org.cagrid.gaards.csm.bean.FilterClause filter = new org.cagrid.gaards.csm.bean.FilterClause();
        if (f.getId() != null) {
            filter.setId(f.getId().longValue());
        }
        filter.setClassName(f.getClassName());
        filter.setFilterChain(f.getFilterChain());
        filter.setGeneratedSQLForGroup(f.getGeneratedSQLForGroup());
        filter.setGeneratedSQLForUser(f.getGeneratedSQLForUser());
        filter.setTargetClassAlias(f.getTargetClassAlias());
        filter.setTargetClassAttributeAlias(f.getTargetClassAttributeAlias());
        filter.setTargetClassAttributeName(f.getTargetClassAttributeName());
        filter.setTargetClassAttributeType(f.getTargetClassAttributeType());
        filter.setTargetClassName(f.getTargetClassName());

        if (f.getApplication() != null) {
            filter.setApplicationId(f.getApplication().getApplicationId().longValue());
        }
        if (f.getUpdateDate() != null) {
            Calendar c = new GregorianCalendar();
            c.setTime(f.getUpdateDate());
            filter.setLastUpdated(c);
        }
        return filter;
    }

    public static ProtectionGroup convert(org.cagrid.gaards.csm.bean.ProtectionGroup pg) {
        ProtectionGroup grp = new ProtectionGroup();
        if (pg.getId() != null) {
            grp.setProtectionGroupId(pg.getId().longValue());
        }
        grp.setProtectionGroupName(pg.getName());
        grp.setProtectionGroupDescription(pg.getDescription());
        if (pg.getLastUpdated() != null) {
            grp.setUpdateDate(pg.getLastUpdated().getTime());
        }
        byte largeCount = 0;
        if (pg.isLargeElementCount()) {
            largeCount = 1;
        }
        grp.setLargeElementCountFlag(largeCount);
        return grp;
    }

    public static org.cagrid.gaards.csm.bean.ProtectionGroup convert(ProtectionGroup pg) {
        org.cagrid.gaards.csm.bean.ProtectionGroup grp = new org.cagrid.gaards.csm.bean.ProtectionGroup();
        if (pg.getProtectionGroupId() != null) {
            grp.setId(pg.getProtectionGroupId().longValue());
        }
        grp.setName(pg.getProtectionGroupName());
        grp.setDescription(pg.getProtectionGroupDescription());
        if (pg.getApplication() != null) {
            grp.setApplicationId(pg.getApplication().getApplicationId().longValue());
        }
        if (pg.getUpdateDate() != null) {
            Calendar c = new GregorianCalendar();
            c.setTime(pg.getUpdateDate());
            grp.setLastUpdated(c);
        }

        if (pg.getLargeElementCountFlag() == 1) {
            grp.setLargeElementCount(true);
        } else {
            grp.setLargeElementCount(false);
        }
        return grp;
    }

    public static Role convert(org.cagrid.gaards.csm.bean.Role role) {
        Role r = new Role();
        if (role.getId() != null) {
            r.setId(role.getId().longValue());
        }
        r.setName(role.getName());
        r.setDesc(role.getDescription());
        if (role.getLastUpdated() != null) {
            r.setUpdateDate(role.getLastUpdated().getTime());
        }
        return r;
    }

    public static org.cagrid.gaards.csm.bean.Role convert(Role role) {
        org.cagrid.gaards.csm.bean.Role r = new org.cagrid.gaards.csm.bean.Role();
        if (role.getId() != null) {
            r.setId(role.getId().longValue());
        }
        r.setName(role.getName());
        r.setDescription(role.getDesc());
        if (role.getApplication() != null) {
            r.setApplicationId(role.getApplication().getApplicationId().longValue());
        }
        if (role.getUpdateDate() != null) {
            Calendar c = new GregorianCalendar();
            c.setTime(role.getUpdateDate());
            r.setLastUpdated(c);
        }
        return r;
    }

    public static ProtectionElement convert(org.cagrid.gaards.csm.bean.ProtectionElement pe) {
        ProtectionElement elem = new ProtectionElement();
        if (pe.getId() != null) {
            elem.setProtectionElementId(pe.getId().longValue());
        }
        elem.setProtectionElementName(pe.getName());
        elem.setProtectionElementDescription(pe.getDescription());
        elem.setObjectId(pe.getObjectId());
        elem.setProtectionElementType(pe.getType());
        elem.setAttribute(pe.getAttribute());
        elem.setValue(pe.getAttributeValue());
        if (pe.getLastUpdated() != null) {
            elem.setUpdateDate(pe.getLastUpdated().getTime());
        }
        return elem;
    }

    public static org.cagrid.gaards.csm.bean.ProtectionElement convert(ProtectionElement pe) {
        org.cagrid.gaards.csm.bean.ProtectionElement elem = new org.cagrid.gaards.csm.bean.ProtectionElement();
        if (pe.getProtectionElementId() != null) {
            elem.setId(pe.getProtectionElementId().longValue());
        }
        elem.setName(pe.getProtectionElementName());
        elem.setDescription(pe.getProtectionElementDescription());
        elem.setObjectId(pe.getObjectId());
        elem.setType(pe.getProtectionElementType());
        elem.setAttribute(pe.getAttribute());
        elem.setAttributeValue(pe.getValue());
        if (pe.getApplication() != null) {
            elem.setApplicationId(pe.getApplication().getApplicationId().longValue());
        }
        if (pe.getUpdateDate() != null) {
            Calendar c = new GregorianCalendar();
            c.setTime(pe.getUpdateDate());
            elem.setLastUpdated(c);
        }
        return elem;
    }

    public static ApplicationSearchCriteria convert(org.cagrid.gaards.csm.bean.ApplicationSearchCriteria criteria) {
        Application app = new Application();
        if (criteria != null) {
            /*
             * if (criteria.getId() != null) { app.setApplicationId(criteria.getId().longValue()); }
             */
            app.setApplicationName(criteria.getName());
            // app.setApplicationDescription(criteria.getDescription());
        }
        return new ApplicationSearchCriteria(app);
    }

    public static FilterClauseSearchCriteria convert(org.cagrid.gaards.csm.bean.FilterClauseSearchCriteria criteria) {
        FilterClause f = new FilterClause();
        if (criteria != null) {
            f.setClassName(criteria.getClassName());
        }
        return new FilterClauseSearchCriteria(f);
    }

    public static ProtectionElementSearchCriteria convert(
            org.cagrid.gaards.csm.bean.ProtectionElementSearchCriteria criteria) {
        ProtectionElement pe = new ProtectionElement();
        if (criteria != null) {
            pe.setAttribute(criteria.getAttribute());
            pe.setObjectId(criteria.getObjectId());
            pe.setProtectionElementName(criteria.getName());
            pe.setProtectionElementType(criteria.getType());
            pe.setValue(criteria.getAttributeValue());
        }
        return new ProtectionElementSearchCriteria(pe);
    }

    public static ProtectionGroupSearchCriteria convert(
            org.cagrid.gaards.csm.bean.ProtectionGroupSearchCriteria criteria) {
        ProtectionGroup pg = new ProtectionGroup();
        if (criteria != null) {
            pg.setProtectionGroupName(criteria.getName());
        }
        return new ProtectionGroupSearchCriteria(pg);
    }

    public static RoleSearchCriteria convert(org.cagrid.gaards.csm.bean.RoleSearchCriteria criteria) {
        Role role = new Role();
        if (criteria != null) {
            role.setName(criteria.getName());
        }
        return new RoleSearchCriteria(role);
    }

    public static GroupSearchCriteria convert(org.cagrid.gaards.csm.bean.GroupSearchCriteria criteria) {
        Group pg = new Group();
        if (criteria != null) {
            pg.setGroupName(criteria.getName());
        }
        return new GroupSearchCriteria(pg);
    }

    public static org.cagrid.gaards.csm.bean.Application convert(Application app) {
        org.cagrid.gaards.csm.bean.Application a = new org.cagrid.gaards.csm.bean.Application();
        if (app.getApplicationId() != null) {
            a.setId(app.getApplicationId());
        }
        a.setName(app.getApplicationName());
        a.setDescription(app.getApplicationDescription());
        return a;
    }

    public static Application convert(org.cagrid.gaards.csm.bean.Application app) {
        Application a = new Application();
        if (app.getId() != null) {
            a.setApplicationId(app.getId().longValue());
        }
        a.setApplicationName(app.getName());
        a.setApplicationDescription(app.getDescription());
        return a;
    }

    public static List<org.cagrid.gaards.csm.bean.Application> convert(List<Application> apps) {
        List<org.cagrid.gaards.csm.bean.Application> list = new ArrayList<org.cagrid.gaards.csm.bean.Application>();
        for (int i = 0; i < apps.size(); i++) {
            list.add(convert(apps.get(i)));
        }
        return list;
    }

    public static Group getAdminGroup(AuthorizationManager auth, String applicationName) throws CSMInternalFault {
        try {
            Application webService = auth.getApplication(Constants.CSM_WEB_SERVICE_CONTEXT);
            Group group = new Group();
            group.setApplication(webService);
            group.setGroupName(applicationName + " " + Constants.ADMIN_GROUP_SUFFIX);
            List<Group> groups = auth.getObjects(new GroupSearchCriteria(group));
            return groups.get(0);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CSMInternalFault fault = new CSMInternalFault();
            fault.setFaultString("An unexpected error occurred loading the admin group for the application "
                    + applicationName + ".");
            throw fault;
        }
    }

    public static Group getWebServiceAdminGroup(AuthorizationManager auth) throws CSMInternalFault {
        return getAdminGroup(auth, Constants.CSM_WEB_SERVICE_CONTEXT);
    }

    private static void logInfo(String s) {
        System.out.println(s);
        log.info(s);
    }

    private static void logError(String s, Exception e) {
        log.error(s, e);
    }

    public static BeanUtils getBeanUtils() throws Exception {
        ClassPathResource properties = new ClassPathResource(CSM_PROPERTIES);
        return getBeanUtils(properties);
    }

    public static BeanUtils getBeanUtils(AbstractResource properties) throws Exception {
        ClassPathResource cpr = new ClassPathResource(CSM_CONFIGURATION);
        return new BeanUtils(cpr, properties);
    }

    public static CSMProperties getCSMProperties() throws Exception {
        return getBeanUtils().getCSMProperties();
    }

    /**
     * Returns a org.cagrid.tools.database.Database object that has been initialized using connection info specified in
     * the csm properties file specified by getCSMProperties().
     * 
     * @return Database
     * @throws Exception
     */
    private static Database getMysqlDatabaseConnection() throws Exception {
        CSMProperties props = getCSMProperties();
        DatabaseProperties dbprops = props.getDatabaseProperties();
        String url = dbprops.getConnectionURL();
        int beginHost = url.indexOf("//");
        int beginPort = url.indexOf(":", beginHost);
        int beginDB = url.indexOf("/", beginPort);
        String host = url.substring(beginHost + 2, beginPort);
        String port = url.substring(beginPort + 1, beginDB);
        String dbName = url.substring(beginDB + 1);
        Database db = new Database(host, Integer.valueOf(port).intValue(), dbprops.getUserId(), dbprops.getPassword(),
                dbName);
        return db;
    }

    public static void dropMysqlDatabaseTables() throws Exception {
        dropMysqlDatabaseTables(getMysqlDatabaseConnection());
    }

    /**
     * Drops all CSM tables present in the database specified by the getCSMProperties() method.
     * 
     * @throws Exception
     */
    public static void dropMysqlDatabaseTables(Database db) throws Exception {
        try {
            db.update("SET foreign_key_checks = 0;");
            db.update("DROP TABLE IF EXISTS CSM_USER_PE;");
            db.update("DROP TABLE IF EXISTS CSM_USER_GROUP_ROLE_PG;");
            db.update("DROP TABLE IF EXISTS CSM_USER_GROUP;");
            db.update("DROP TABLE IF EXISTS CSM_USER;");
            db.update("DROP TABLE IF EXISTS CSM_ROLE_PRIVILEGE;");
            db.update("DROP TABLE IF EXISTS CSM_ROLE;");
            db.update("DROP TABLE IF EXISTS CSM_REMOTE_GROUP_SYNC_RECORD;");
            db.update("DROP TABLE IF EXISTS CSM_REMOTE_GROUP;");
            db.update("DROP TABLE IF EXISTS CSM_PROTECTION_GROUP;");
            db.update("DROP TABLE IF EXISTS CSM_PRIVILEGE;");
            db.update("DROP TABLE IF EXISTS CSM_PROTECTION_ELEMENT;");
            db.update("DROP TABLE IF EXISTS CSM_PG_PE;");
            db.update("DROP TABLE IF EXISTS CSM_GROUP;");
            db.update("DROP TABLE IF EXISTS CSM_FILTER_CLAUSE;");
            db.update("DROP TABLE IF EXISTS CSM_APPLICATION;");
        } finally {
            // need to make sure that foreign key checks gets turned back on even if there was an exception during the
            // drop table block
            db.update("SET foreign_key_checks = 1;");
        }
    }

    public static void createCSMTables() throws Exception {
        Database db = getMysqlDatabaseConnection();
        dropMysqlDatabaseTables(db);
        createTables(db);
    }

    /**
     * Creates and initializes a CSM database using connection info specified in the CSM_PROPERTIES file.
     * 
     * @throws Exception
     */
    public static void createCSMDatabase() throws Exception {
        Database db = getMysqlDatabaseConnection();
        db.destroyDatabase();
        db.createDatabaseIfNeeded();

        createTables(db);
    }

    /**
     * Creates and initializes a CSM database using connection info specified in the CSM_PROPERTIES file.
     * 
     * @throws Exception
     */
    public static void createTables(Database db) throws Exception {

        String csmApplication = "CREATE TABLE CSM_APPLICATION (" + "APPLICATION_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "APPLICATION_NAME VARCHAR(255) NOT NULL," + "APPLICATION_DESCRIPTION VARCHAR(200) NOT NULL,"
                + "DECLARATIVE_FLAG BOOL NOT NULL DEFAULT 0," + "ACTIVE_FLAG BOOL NOT NULL DEFAULT 0,"
                + "UPDATE_DATE DATE DEFAULT '0000-00-00'," + "DATABASE_URL VARCHAR(100),"
                + "DATABASE_USER_NAME VARCHAR(100)," + "DATABASE_PASSWORD VARCHAR(100),"
                + "DATABASE_DIALECT VARCHAR(100)," + "DATABASE_DRIVER VARCHAR(100)," + "PRIMARY KEY(APPLICATION_ID)"
                + ")Type=InnoDB;";

        db.update(csmApplication);

        String csmGroup = "CREATE TABLE CSM_GROUP (" + "GROUP_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "GROUP_NAME VARCHAR(255) NOT NULL," + "GROUP_DESC VARCHAR(200),"
                + "UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00'," + "APPLICATION_ID BIGINT NOT NULL,"
                + "PRIMARY KEY(GROUP_ID)" + ")Type=InnoDB;";

        db.update(csmGroup);

        String csmPrivilege = "CREATE TABLE CSM_PRIVILEGE (" + "PRIVILEGE_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "PRIVILEGE_NAME VARCHAR(100) NOT NULL," + "PRIVILEGE_DESCRIPTION VARCHAR(200),"
                + "UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00'," + "PRIMARY KEY(PRIVILEGE_ID)" + ")Type=InnoDB;";

        db.update(csmPrivilege);

        String filterClause = "CREATE TABLE CSM_FILTER_CLAUSE (" + "FILTER_CLAUSE_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "CLASS_NAME VARCHAR(100) NOT NULL," + "FILTER_CHAIN VARCHAR(2000) NOT NULL,"
                + "TARGET_CLASS_NAME VARCHAR (100) NOT NULL," + "TARGET_CLASS_ATTRIBUTE_NAME VARCHAR (100) NOT NULL,"
                + "TARGET_CLASS_ATTRIBUTE_TYPE VARCHAR (100) NOT NULL," + "TARGET_CLASS_ALIAS VARCHAR (100),"
                + "TARGET_CLASS_ATTRIBUTE_ALIAS VARCHAR (100)," + "GENERATED_SQL_USER VARCHAR (4000) NOT NULL,"
                + "GENERATED_SQL_GROUP VARCHAR (4000) NOT NULL," + "APPLICATION_ID BIGINT NOT NULL,"
                + "UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00'," + "PRIMARY KEY(FILTER_CLAUSE_ID)    "
                + ")Type=InnoDB;";

        db.update(filterClause);

        String protectionElement = "CREATE TABLE CSM_PROTECTION_ELEMENT ("
                + "PROTECTION_ELEMENT_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "PROTECTION_ELEMENT_NAME VARCHAR(100) NOT NULL," + "PROTECTION_ELEMENT_DESCRIPTION VARCHAR(200),"
                + "OBJECT_ID VARCHAR(100) NOT NULL," + "ATTRIBUTE VARCHAR(100)," + "ATTRIBUTE_VALUE VARCHAR(100) ,"
                + "PROTECTION_ELEMENT_TYPE VARCHAR(100)," + "APPLICATION_ID BIGINT NOT NULL,"
                + "UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00'," + "PRIMARY KEY(PROTECTION_ELEMENT_ID)"
                + ")Type=InnoDB;";

        db.update(protectionElement);

        String protectionGroup = "CREATE TABLE CSM_PROTECTION_GROUP ("
                + "PROTECTION_GROUP_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "PROTECTION_GROUP_NAME VARCHAR(100) NOT NULL," + "PROTECTION_GROUP_DESCRIPTION VARCHAR(200),"
                + "APPLICATION_ID BIGINT NOT NULL," + "LARGE_ELEMENT_COUNT_FLAG BOOL NOT NULL,"
                + "UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00'," + "PARENT_PROTECTION_GROUP_ID BIGINT,"
                + "PRIMARY KEY(PROTECTION_GROUP_ID)" + ")Type=InnoDB;";

        db.update(protectionGroup);

        String pgtope = "CREATE TABLE CSM_PG_PE (" + "PG_PE_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "PROTECTION_GROUP_ID BIGINT NOT NULL," + "PROTECTION_ELEMENT_ID BIGINT NOT NULL,"
                + "UPDATE_DATE DATE DEFAULT '0000-00-00'," + "PRIMARY KEY(PG_PE_ID)" + ")Type=InnoDB;";

        db.update(pgtope);
        String role = "CREATE TABLE CSM_ROLE (" + "ROLE_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "ROLE_NAME VARCHAR(100) NOT NULL," + "ROLE_DESCRIPTION VARCHAR(200),"
                + "APPLICATION_ID BIGINT NOT NULL," + "ACTIVE_FLAG BOOL NOT NULL,"
                + "UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00'," + "PRIMARY KEY(ROLE_ID)" + ")Type=InnoDB;";

        db.update(role);
        String rp = "CREATE TABLE CSM_ROLE_PRIVILEGE (" + "ROLE_PRIVILEGE_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "ROLE_ID BIGINT NOT NULL," + "PRIVILEGE_ID BIGINT NOT NULL," + "PRIMARY KEY(ROLE_PRIVILEGE_ID)"
                + ")Type=InnoDB;";

        db.update(rp);

        String user = "CREATE TABLE CSM_USER (" + "USER_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "LOGIN_NAME VARCHAR(500) NOT NULL," + "MIGRATED_FLAG BOOL NOT NULL DEFAULT 0,"
                + "FIRST_NAME VARCHAR(100) NOT NULL," + "LAST_NAME VARCHAR(100) NOT NULL,"
                + "ORGANIZATION VARCHAR(100)," + "DEPARTMENT VARCHAR(100)," + "TITLE VARCHAR(100),"
                + "PHONE_NUMBER VARCHAR(15)," + "PASSWORD VARCHAR(100)," + "EMAIL_ID VARCHAR(100),"
                + "START_DATE DATE," + "END_DATE DATE," + "UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00',"
                + "PREMGRT_LOGIN_NAME VARCHAR(100)," + "PRIMARY KEY(USER_ID)" + ")Type=InnoDB;";

        db.update(user);

        String ug = "CREATE TABLE CSM_USER_GROUP (" + "USER_GROUP_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "USER_ID BIGINT NOT NULL," + "GROUP_ID BIGINT NOT NULL," + "PRIMARY KEY(USER_GROUP_ID)"
                + ")Type=InnoDB;";

        db.update(ug);

        String ugrp = "CREATE TABLE CSM_USER_GROUP_ROLE_PG ("
                + "USER_GROUP_ROLE_PG_ID BIGINT AUTO_INCREMENT  NOT NULL," + "USER_ID BIGINT," + "GROUP_ID BIGINT,"
                + "ROLE_ID BIGINT NOT NULL," + "PROTECTION_GROUP_ID BIGINT NOT NULL,"
                + "UPDATE_DATE DATE NOT NULL DEFAULT '0000-00-00'," + "PRIMARY KEY(USER_GROUP_ROLE_PG_ID)"
                + ")Type=InnoDB;";

        db.update(ugrp);

        String cup = "CREATE TABLE CSM_USER_PE (" + "USER_PROTECTION_ELEMENT_ID BIGINT AUTO_INCREMENT  NOT NULL,"
                + "PROTECTION_ELEMENT_ID BIGINT NOT NULL," + "USER_ID BIGINT NOT NULL,"
                + "PRIMARY KEY(USER_PROTECTION_ELEMENT_ID)" + ")Type=InnoDB;";
        db.update(cup);

        db.update("ALTER TABLE CSM_APPLICATION ADD CONSTRAINT UQ_APPLICATION_NAME UNIQUE (APPLICATION_NAME);");
        db.update("CREATE INDEX idx_APPLICATION_ID ON CSM_GROUP(APPLICATION_ID);");
        db.update("ALTER TABLE CSM_GROUP ADD CONSTRAINT UQ_GROUP_GROUP_NAME UNIQUE (APPLICATION_ID, GROUP_NAME);");
        db.update("ALTER TABLE CSM_PRIVILEGE ADD CONSTRAINT UQ_PRIVILEGE_NAME UNIQUE (PRIVILEGE_NAME);");
        db.update("CREATE INDEX idx_APPLICATION_ID ON CSM_PROTECTION_ELEMENT(APPLICATION_ID);");
        db
                .update("ALTER TABLE CSM_PROTECTION_ELEMENT ADD CONSTRAINT UQ_PE_PE_NAME_ATTRIBUTE_VALUE_APP_ID UNIQUE (OBJECT_ID, ATTRIBUTE, ATTRIBUTE_VALUE, APPLICATION_ID);");
        db.update("CREATE INDEX idx_APPLICATION_ID ON CSM_PROTECTION_GROUP(APPLICATION_ID);");
        db
                .update("ALTER TABLE CSM_PROTECTION_GROUP ADD CONSTRAINT UQ_PROTECTION_GROUP_PROTECTION_GROUP_NAME UNIQUE (APPLICATION_ID, PROTECTION_GROUP_NAME);");
        db.update("CREATE INDEX idx_PARENT_PROTECTION_GROUP_ID ON CSM_PROTECTION_GROUP(PARENT_PROTECTION_GROUP_ID);");
        db.update("CREATE INDEX idx_PROTECTION_ELEMENT_ID ON CSM_PG_PE(PROTECTION_ELEMENT_ID);");
        db.update("CREATE INDEX idx_PROTECTION_GROUP_ID ON CSM_PG_PE(PROTECTION_GROUP_ID);");
        db
                .update("ALTER TABLE CSM_PG_PE ADD CONSTRAINT UQ_PROTECTION_GROUP_PROTECTION_ELEMENT_PROTECTION_GROUP_ID UNIQUE (PROTECTION_ELEMENT_ID, PROTECTION_GROUP_ID);");
        db.update("CREATE INDEX idx_APPLICATION_ID ON CSM_ROLE(APPLICATION_ID);");
        db.update("ALTER TABLE CSM_ROLE ADD CONSTRAINT UQ_ROLE_ROLE_NAME UNIQUE (APPLICATION_ID, ROLE_NAME);");
        db.update("CREATE INDEX idx_PRIVILEGE_ID ON CSM_ROLE_PRIVILEGE(PRIVILEGE_ID);");
        db
                .update("ALTER TABLE CSM_ROLE_PRIVILEGE ADD CONSTRAINT UQ_ROLE_PRIVILEGE_ROLE_ID UNIQUE (PRIVILEGE_ID, ROLE_ID);");
        db.update("CREATE INDEX idx_ROLE_ID ON CSM_ROLE_PRIVILEGE(ROLE_ID);");
        db.update("ALTER TABLE CSM_USER ADD CONSTRAINT UQ_LOGIN_NAME UNIQUE (LOGIN_NAME);");
        db.update("CREATE INDEX idx_USER_ID ON CSM_USER_GROUP(USER_ID);");
        db.update("CREATE INDEX idx_GROUP_ID ON CSM_USER_GROUP(GROUP_ID);");
        db.update("CREATE INDEX idx_GROUP_ID ON CSM_USER_GROUP_ROLE_PG(GROUP_ID);");
        db.update("CREATE INDEX idx_ROLE_ID ON CSM_USER_GROUP_ROLE_PG(ROLE_ID);");
        db.update("CREATE INDEX idx_PROTECTION_GROUP_ID ON CSM_USER_GROUP_ROLE_PG(PROTECTION_GROUP_ID);");
        db.update("CREATE INDEX idx_USER_ID ON CSM_USER_GROUP_ROLE_PG(USER_ID);");
        db.update("CREATE INDEX idx_USER_ID ON CSM_USER_PE(USER_ID);");
        db.update("CREATE INDEX idx_PROTECTION_ELEMENT_ID ON CSM_USER_PE(PROTECTION_ELEMENT_ID);");
        db
                .update("ALTER TABLE CSM_USER_PE ADD CONSTRAINT UQ_USER_PROTECTION_ELEMENT_PROTECTION_ELEMENT_ID UNIQUE (USER_ID, PROTECTION_ELEMENT_ID);");
        db
                .update("ALTER TABLE CSM_GROUP ADD CONSTRAINT FK_APP_GRP FOREIGN KEY (APPLICATION_ID) REFERENCES CSM_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_FILTER_CLAUSE ADD CONSTRAINT FK_APP_FILT FOREIGN KEY (APPLICATION_ID) REFERENCES CSM_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_PROTECTION_ELEMENT ADD CONSTRAINT FK_PE_APP FOREIGN KEY (APPLICATION_ID) REFERENCES CSM_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_PROTECTION_GROUP ADD CONSTRAINT FK_PG_APP FOREIGN KEY (APPLICATION_ID) REFERENCES CSM_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_PROTECTION_GROUP ADD CONSTRAINT FK_PROT_GRP FOREIGN KEY (PARENT_PROTECTION_GROUP_ID) REFERENCES CSM_PROTECTION_GROUP (PROTECTION_GROUP_ID);");
        db
                .update("ALTER TABLE CSM_PG_PE ADD CONSTRAINT FK_PE_PE FOREIGN KEY (PROTECTION_ELEMENT_ID) REFERENCES CSM_PROTECTION_ELEMENT (PROTECTION_ELEMENT_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_PG_PE ADD CONSTRAINT FK_PG_PE FOREIGN KEY (PROTECTION_GROUP_ID) REFERENCES CSM_PROTECTION_GROUP (PROTECTION_GROUP_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_ROLE ADD CONSTRAINT FK_APP_ROLE FOREIGN KEY (APPLICATION_ID) REFERENCES CSM_APPLICATION (APPLICATION_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_ROLE_PRIVILEGE ADD CONSTRAINT FK_PRIV_ROLE FOREIGN KEY (PRIVILEGE_ID) REFERENCES CSM_PRIVILEGE (PRIVILEGE_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_ROLE_PRIVILEGE ADD CONSTRAINT FK_RL FOREIGN KEY (ROLE_ID) REFERENCES CSM_ROLE (ROLE_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_USER_GROUP ADD CONSTRAINT FK_USER_GRP FOREIGN KEY (USER_ID) REFERENCES CSM_USER (USER_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_USER_GROUP ADD CONSTRAINT FK_UG_GRP FOREIGN KEY (GROUP_ID) REFERENCES CSM_GROUP (GROUP_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_USER_GROUP_ROLE_PG ADD CONSTRAINT FK_UG_R_PG_G FOREIGN KEY (GROUP_ID) REFERENCES CSM_GROUP (GROUP_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_USER_GROUP_ROLE_PG ADD CONSTRAINT FK_UG_R_PG_R FOREIGN KEY (ROLE_ID) REFERENCES CSM_ROLE (ROLE_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_USER_GROUP_ROLE_PG ADD CONSTRAINT FK_UG_R_PG2 FOREIGN KEY (PROTECTION_GROUP_ID) REFERENCES CSM_PROTECTION_GROUP (PROTECTION_GROUP_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_USER_GROUP_ROLE_PG ADD CONSTRAINT FK_UG_R_PG_U FOREIGN KEY (USER_ID) REFERENCES CSM_USER (USER_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_USER_PE ADD CONSTRAINT FK_UPE_U FOREIGN KEY (USER_ID) REFERENCES CSM_USER (USER_ID) ON DELETE CASCADE;");
        db
                .update("ALTER TABLE CSM_USER_PE ADD CONSTRAINT FK_UPE_PE FOREIGN KEY (PROTECTION_ELEMENT_ID) REFERENCES CSM_PROTECTION_ELEMENT (PROTECTION_ELEMENT_ID) ON DELETE CASCADE;");
    }
}

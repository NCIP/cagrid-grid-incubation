package org.cagrid.i2b2.ontomapper.processor;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.FileReader;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.i2b2.ontomapper.utils.CdeIdMapper;
import org.cagrid.i2b2.ontomapper.utils.DomainModelCdeIdMapper;

/** 
 *  I2B2QueryProcessor
 *  Processes CQL against a I2B2 data source
 * 
 * @author Shannon Hastings
 * 
 */
public class I2B2QueryProcessor extends CQLQueryProcessor {
    
    // properties for configuration
    // the domain model document location for the concept code mapper
    public static final String DOMAIN_MODEL_FILE_NAME = "domainModelFileName";
    // the JDBC driver name
    public static final String JDBC_DRIVER_NAME = "jdbcDriverClassname";
    // the JDBC connection string
    public static final String JDBC_CONNECT_STRING = "jdbcConnectString";
    // the JDBC username
    public static final String JDBC_USERNAME = "jdbcUsername";
    // the JDBC password
    public static final String JDBC_PASSWORD = "jdbcPassword";
    // the database table name prefix
    public static final String TABLE_NAME_PREFIX = "tableNamePrefix";
    // the caDSR URL used for mapping attributes to CDEs
    public static final String CADSR_URL = "cadsrUrl";
    
    // default values for configuration
    public static final String DEFAULT_DOMAIN_MODEL_FILE_NAME = "domainModel.xml";
    // default caDSR URL -- OSU training for now
    public static final String DEFAULT_CADSR_URL = "osuDSR.osu.edu";
    // default DB table name prefix is i2b2demodata -- for demo stuff
    public static final String DEFAULT_TABLE_NAME_PREFIX = "i2b2demodata";
    
    static final Log LOG = LogFactory.getLog(I2B2QueryProcessor.class);
        
    private CdeIdMapper cdeIdMapper = null;
    private DatabaseConnectionSource connectionSource = null;
    private I2B2QueryFactory queryFactory = null;
    private I2B2DataAccessManager dataAccessManager = null;
    
    public I2B2QueryProcessor() {
        super();
    }
    
    
    public Properties getRequiredParameters() {
        Properties props = new Properties();
        props.setProperty(DOMAIN_MODEL_FILE_NAME, DEFAULT_DOMAIN_MODEL_FILE_NAME);
        props.setProperty(JDBC_DRIVER_NAME, "");
        props.setProperty(JDBC_CONNECT_STRING, "");
        props.setProperty(JDBC_USERNAME, "");
        props.setProperty(JDBC_PASSWORD, "");
        props.setProperty(CADSR_URL, DEFAULT_CADSR_URL);
        props.setProperty(TABLE_NAME_PREFIX, "");
        return props;
    }
    
    
    public Set<String> getPropertiesFromEtc() {
        Set<String> fromEtc = new HashSet<String>();
        fromEtc.add(DOMAIN_MODEL_FILE_NAME);
        return fromEtc;
    }
    
    
    /**
     * Overloaded to perform standard initialization and then 
     * implementation-specific initialization and configuration
     */
    public void initialize(Properties parameters, InputStream wsdd) throws InitializationException {
        super.initialize(parameters, wsdd);
        initCdeIdMapper();
        initConnectionSource();
        initQueryFactory();
        initDataAccessManager();
    }
    
    
    private void initCdeIdMapper() throws InitializationException {
        LOG.debug("Initializing cde id mapper");
        /* TODO: I'd love to use Spring here to load any arbitrary concept 
         * code mapper instance, but for now I'll hard code this as a
         * DomainModelConceptCodeMapper
         */
        String domainModelFile = getConfiguredParameters().getProperty(DOMAIN_MODEL_FILE_NAME);
        DomainModel model = null;
        try {
            LOG.debug("Reading domain model from file " + domainModelFile);
            FileReader reader = new FileReader(domainModelFile);
            model = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
        } catch (Exception ex) {
            String message = "Unable to deserialize specified domain model: " + ex.getMessage();
            LOG.error(message, ex);
            throw new InitializationException(message, ex);
        }
        cdeIdMapper = new DomainModelCdeIdMapper(model);
    }
    
    
    private void initConnectionSource() throws InitializationException {
        LOG.debug("Initializing database conection source");
        /* TODO: again, spring would be awesome here, but for now I'm just
         * loading a default implementation
         */
        String driverClassname = getConfiguredParameters().getProperty(JDBC_DRIVER_NAME);
        String connectString = getConfiguredParameters().getProperty(JDBC_CONNECT_STRING);
        String username = getConfiguredParameters().getProperty(JDBC_USERNAME);
        String password = getConfiguredParameters().getProperty(JDBC_PASSWORD);
        try {
            connectionSource = new PooledDatabaseConnectionSource(
                driverClassname, connectString, username, password);
        } catch (Exception ex) {
            String message = "Unable to initialize database connection source: " + ex.getMessage();
            LOG.error(message, ex);
            throw new InitializationException(message, ex);
        }
    }
    
    
    private void initQueryFactory() {
        LOG.debug("Initializing database query factory");
        String tablePrefix = getConfiguredParameters().getProperty(TABLE_NAME_PREFIX);
        LOG.debug("Table prefix found: " + tablePrefix);
        queryFactory = new I2B2QueryFactory(tablePrefix);
    }
    
    
    private void initDataAccessManager() {
        LOG.debug("Initializing data access manager");
        String encodingServiceURL = getConfiguredParameters().getProperty(CADSR_URL);
        LOG.debug("Encoding service URL found: " + encodingServiceURL);
        dataAccessManager = new I2B2DataAccessManager(
            connectionSource, cdeIdMapper, encodingServiceURL, queryFactory);
    }
    
    
    public void finalize() throws Throwable {
        try {
            connectionSource.shutdown();
        } catch (Exception ex) {
            LOG.error("Error shutting down connection source: " + ex.getMessage(), ex);
        }
        super.finalize();
    }


    public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        // for now, we can only do distinct attributes
        enforceDistinctAttribute(cqlQuery);
        
        // get some relevant information
        String className = cqlQuery.getTarget().getName();
        String attributeName = cqlQuery.getQueryModifier().getDistinctAttribute();
        
        List<String> values = dataAccessManager.getAttributeStringValues(className, attributeName);
        CQLQueryResults fakeResults = CQLResultsCreationUtil.createAttributeResults(
            values, className, new String[] {attributeName});
        
        return fakeResults;
    }
    
    
    private void enforceDistinctAttribute(CQLQuery cqlQuery) throws MalformedQueryException {
        if (cqlQuery.getQueryModifier() == null || cqlQuery.getQueryModifier().getDistinctAttribute() == null) {
            throw new MalformedQueryException("Only distinct attrbutes are currently supported");
        }
    }
}

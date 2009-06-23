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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.i2b2.ontomapper.utils.AttributeNotFoundInModelException;
import org.cagrid.i2b2.ontomapper.utils.CdeIdMapper;
import org.cagrid.i2b2.ontomapper.utils.ClassNotFoundInModelException;
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
    // the caDSR URL used for mapping attributes to CDEs
    public static final String CADSR_URL = "cadsrUrl";
    
    // default values for configuration
    public static final String DEFAULT_DOMAIN_MODEL_FILE_NAME = "domainModel.xml";
    // default caDSR URL -- OSU training for now
    public static final String DEFAULT_CADSR_URL = "osuDSR.osu.edu";
    
    private static final Log LOG = LogFactory.getLog(I2B2QueryProcessor.class);
        
    private CdeIdMapper cdeIdMapper = null;
    private DatabaseConnectionSource connectionSource = null;
    
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
    
    
    public void finalize() throws Throwable {
        try {
            connectionSource.shutdown();
        } catch (Exception ex) {
            LOG.error("Error shutting down connection source: " + ex.getMessage(), ex);
        }
        super.finalize();
    }


    public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        /*
         * Starting with a very simple implementation to grab attributes of the target object and nothing else
         */
        
        if (cqlQuery.getQueryModifier() == null ||
            cqlQuery.getQueryModifier().getDistinctAttribute() == null) {
            throw new QueryProcessingException("Only distinct attributes of a target are supported now");
        }
        
        // get the CDE of the attribute
        LOG.debug("Looking up CDE in id mapper");
        Long cde = null;
        try {
            cde = cdeIdMapper.getCdeIdForAttribute(
                cqlQuery.getTarget().getName(), cqlQuery.getQueryModifier().getDistinctAttribute());
        } catch (ClassNotFoundInModelException ex) {
            LOG.error(ex);
            throw new QueryProcessingException(ex.getMessage(), ex);
        } catch (AttributeNotFoundInModelException ex) {
            LOG.error(ex);
            throw new QueryProcessingException(ex.getMessage(), ex);
        }
        // cde has to exist
        if (cde == null) {
            throw new QueryProcessingException("No CDE found for attribute " 
                + cqlQuery.getQueryModifier().getDistinctAttribute());
        }
        
        // get paths for CDE
        List<String> paths = getPathsForCde(cde);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Paths for CDE " + cde);
            for (String path : paths) {
                LOG.debug("\t" + path);
            }
        }
        
        // for the moment, just return paths
        CQLQueryResults fakeResults = CQLResultsCreationUtil.createAttributeResults(
            paths, cqlQuery.getTarget().getName(),
            new String[] {cqlQuery.getQueryModifier().getDistinctAttribute()});
        
        return fakeResults;
    }
    
    
    private List<String> getPathsForCde(Long cde) throws QueryProcessingException {
        LOG.debug("Looking up paths for CDE " + cde);
        List<String> paths = new LinkedList<String>();
        Connection dbConnection = null;
        PreparedStatement pathsStatement = null;
        ResultSet pathsResult = null;
        String cadsrUrl = getConfiguredParameters().getProperty(CADSR_URL);
        String projectName = cdeIdMapper.getProjectShortName();
        String projectVersion = cdeIdMapper.getProjectVersion();
        try {
            dbConnection = connectionSource.getConnection();
            pathsStatement = dbConnection.prepareStatement(I2B2Queries.CDE_PATHS);
            pathsStatement.setInt(1, cde.intValue());
            pathsStatement.setString(2, cadsrUrl);
            pathsStatement.setString(3, projectName);
            pathsStatement.setString(4, projectVersion);
            pathsResult = pathsStatement.executeQuery();
            while (pathsResult.next()) {
                paths.add(pathsResult.getString(1));
            }
        } catch (SQLException ex) {
            String message = "Error querying for CDE paths: " + ex.getMessage();
            LOG.error(message, ex);
            throw new QueryProcessingException(message, ex);
        } finally {
            if (pathsResult != null) {
                try {
                    pathsResult.close();
                } catch (SQLException ex) {
                    LOG.error("Error closing result set: " + ex.getMessage(), ex);
                }
            }
            if (pathsStatement != null) {
                try {
                    pathsStatement.close();
                } catch (SQLException ex) {
                    LOG.error("Error closing statement: " + ex.getMessage(), ex);
                }
            }
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException ex) {
                    LOG.error("Error releasing DB connection: " + ex.getMessage(), ex);
                }
            }
        }
        return paths;
    }
}

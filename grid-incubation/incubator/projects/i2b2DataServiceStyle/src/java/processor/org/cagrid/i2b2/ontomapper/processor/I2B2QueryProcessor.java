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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    
    private static final Log LOG = LogFactory.getLog(I2B2QueryProcessor.class);
        
    private CdeIdMapper cdeIdMapper = null;
    private DatabaseConnectionSource connectionSource = null;
    private I2B2QueryFactory queryFactory = null;
    
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
        
        // figure out the CDE
        Long cde = getCdeForAttribute(className, attributeName);
        LOG.debug("CDE for " + className + "." + attributeName + " is " + String.valueOf(cde));
        if (cde == null) {
            throw new QueryProcessingException("No CDE found for " + className + "." + attributeName);
        }
        
        // get query paths for the CDE
        List<String> paths = getPathsForCde(cde);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Paths for CDE " + cde + ":");
            for (String path : paths) {
                LOG.debug("\t" + path);
            }
        }
        
        List<DataEntry> observationEntries = null;
        List<DataEntry> mapDataEntries = null;
        List<DataEntry> mapAggrEntries = null;
        if (paths.size() != 0) {
            // get observation instances from the DB based on each query path
            observationEntries = getFactEntriesByPaths(DatabaseFactTable.OBSERVATION, paths);
            mapDataEntries = getFactEntriesByPaths(DatabaseFactTable.MAP_DATA, paths);
            mapAggrEntries = getFactEntriesByPaths(DatabaseFactTable.MAP_AGGREGATE, paths);
        } else {
            // empty lists
            observationEntries = new LinkedList<DataEntry>();
            mapDataEntries = new LinkedList<DataEntry>();
            mapAggrEntries = new LinkedList<DataEntry>();
        }
        
        Set<String> distinctValues = new HashSet<String>();
        for (DataEntry entry : observationEntries) {
            distinctValues.add(entry.getActualValueAsString());
        }
        for (DataEntry entry : mapDataEntries) {
            distinctValues.add(entry.getActualValueAsString());
        }
        for (DataEntry entry : mapAggrEntries) {
            distinctValues.add(entry.getActualValueAsString());
        }
        List<String> values = new LinkedList<String>(distinctValues);
        CQLQueryResults fakeResults = CQLResultsCreationUtil.createAttributeResults(
            values, className, new String[] {attributeName});
        
        return fakeResults;
    }
    
    
    private void enforceDistinctAttribute(CQLQuery cqlQuery) throws MalformedQueryException {
        if (cqlQuery.getQueryModifier() == null || cqlQuery.getQueryModifier().getDistinctAttribute() == null) {
            throw new MalformedQueryException("Only distinct attrbutes are currently supported");
        }
    }
    
    
    private Long getCdeForAttribute(String className, String attributeName) throws QueryProcessingException {
        // get the CDE of the attribute
        LOG.debug("Looking up CDE in id mapper");
        Long cde = null;
        try {
            cde = cdeIdMapper.getCdeIdForAttribute(className, attributeName);
        } catch (ClassNotFoundInModelException ex) {
            LOG.error(ex);
            throw new QueryProcessingException(ex.getMessage(), ex);
        } catch (AttributeNotFoundInModelException ex) {
            LOG.error(ex);
            throw new QueryProcessingException(ex.getMessage(), ex);
        }
        // CDE has to exist
        if (cde == null) {
            throw new QueryProcessingException("No CDE found for attribute " 
                + className + "." + attributeName);
        }
        return cde;
    }
    
    
    private List<DataEntry> getFactEntriesByPaths(DatabaseFactTable table, List<String> paths) throws QueryProcessingException {
        List<DataEntry> entries = null;
        String parameterisedSql = null;
        switch (table) {
            case OBSERVATION:
                parameterisedSql = queryFactory.getObservationsByPathQuery(paths.size());
                break;
            case MAP_DATA:
                parameterisedSql = queryFactory.getMapDataByPathQuery(paths.size());
                break;
            case MAP_AGGREGATE:
                parameterisedSql = queryFactory.getMapAggrByPathQuery(paths.size());
                break;
        }
        Connection dbConnection = null;
        PreparedStatement statement = null;
        ResultSet results = null;
        try {
            dbConnection = connectionSource.getConnection();
            statement = dbConnection.prepareStatement(parameterisedSql);
            int index = 1;
            for (String path : paths) {
                statement.setString(index, path);
                index++;
            }
            results = statement.executeQuery();
            entries = convertResultsToEntries(results);
        } catch (SQLException ex) {
            String message = "Error querying for fact data by CDE paths: " + ex.getMessage();
            LOG.error(message, ex);
            LOG.error("SQL:\n" + parameterisedSql);
            throw new QueryProcessingException(message, ex);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException ex) {
                    LOG.error("Error closing result set: " + ex.getMessage(), ex);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
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
        return entries;
    }
    
    
    private Map<String, List<String>> getPathsForAttributeCdes(Map<String, Long> attributeCdes) throws QueryProcessingException {
        Map<String, List<String>> attributePaths = new HashMap<String, List<String>>();
        for (String attributeName : attributeCdes.keySet()) {
            Long cde = attributeCdes.get(attributeName);
            List<String> paths = getPathsForCde(cde);
            attributePaths.put(attributeName, paths);
        }
        return attributePaths;
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
            pathsStatement = dbConnection.prepareStatement(queryFactory.getCdePathsQuery());
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
    
    
    private List<DataEntry> convertResultsToEntries(ResultSet results) throws SQLException, QueryProcessingException {
        List<DataEntry> entries = new LinkedList<DataEntry>();
        ResultSetMetaData metadata = results.getMetaData();
        int columnCount = metadata.getColumnCount();
        if (!(columnCount == 3 || columnCount == 5)) {
            String message = "Unexpected number of columns in result set: " + metadata.getColumnCount();
            LOG.error(message);
            throw new QueryProcessingException(message);
        }
        while (results.next()) {
            DataEntry entry = null;
            String valueType = results.getString(I2B2QueryFactory.VALUE_TYPE_FIELD);
            String textValue = results.getString(I2B2QueryFactory.TEXT_VALUE_FIELD);
            String numValueString = results.getString(I2B2QueryFactory.NUMERIC_VALUE_FIELD);
            Double numValue = numValueString != null ? Double.valueOf(numValueString) : null;
            if (columnCount == 5) {
                String encounterNumString = results.getString(I2B2QueryFactory.ENCOUNTER_NUMBER_FIELD);
                String patientNumString = results.getString(I2B2QueryFactory.PATIENT_NUMBER_FIELD);
                Integer encounterNum = encounterNumString != null ? Integer.valueOf(encounterNumString) : null;
                Integer patientNum = patientNumString != null ? Integer.valueOf(patientNumString) : null;
                entry = new DataEntry(valueType, textValue, numValue, encounterNum, patientNum);
            } else {
                entry = new DataEntry(valueType, textValue, numValue);
            }
            entries.add(entry);
        }
        return entries;
    }
    
    
    private static class DataEntry {
        private String valueType = null;
        private String textValue = null;
        private Double numericValue = null;
        
        private Integer encounterNumber;
        private Integer patientNumber;
        
        public DataEntry(String valueType, String textValue, Double numericValue) {
            this(valueType, textValue, numericValue, null, null);
        }
        

        public DataEntry(String valueType, String textValue, Double numericValue, 
            Integer encounterNumber, Integer patientNumber) {
            this.valueType = valueType;
            this.textValue = textValue;
            this.numericValue = numericValue;
            this.encounterNumber = encounterNumber;
            this.patientNumber = patientNumber;
        }


        public String getValueType() {
            return valueType;
        }


        public String getTextValue() {
            return textValue;
        }


        public Double getNumericValue() {
            return numericValue;
        }


        public int getEncounterNumber() {
            return encounterNumber;
        }


        public int getPatientNumber() {
            return patientNumber;
        }
        
        
        public String getActualValueAsString() {
            String realValue = null;
            // T = text
            // N = number (double????)
            // D = date - time
            // B = ? (no actual values with this one)
            // @ = no data
            if ("T".equals(getValueType()) || "D".equals(getValueType()) || "B".equals(getValueType())) {
                realValue = getTextValue();
            } else if ("N".equals(getValueType())) {
                realValue = String.valueOf(getNumericValue());
            }
            LOG.debug("Unknown value type: " + getValueType() + ", returning null value");
            return realValue;
        }
    }
}

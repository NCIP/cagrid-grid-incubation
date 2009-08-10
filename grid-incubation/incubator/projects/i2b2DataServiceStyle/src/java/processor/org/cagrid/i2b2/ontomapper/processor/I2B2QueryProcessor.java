package org.cagrid.i2b2.ontomapper.processor;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.service.ServiceConfigUtil;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;
import gov.nih.nci.cagrid.data.utilities.ResultsCreationException;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
        // since this query processor is very alpha, check if the query is something it can do
        checkCurrentCapabilities(cqlQuery);
        
        // a place to put results
        CQLQueryResults results = null;
        
        // see what kind of query we're doing
        String className = cqlQuery.getTarget().getName();
        if (cqlQuery.getQueryModifier() != null && cqlQuery.getQueryModifier().getDistinctAttribute() != null) {
            // distinct attributes
            String attributeName = cqlQuery.getQueryModifier().getDistinctAttribute();
            List<String> values = dataAccessManager.getAttributeStringValues(className, attributeName);
            results = CQLResultsCreationUtil.createAttributeResults(
                values, className, new String[] {attributeName});
        } else {
            // objects
            List<Object> values = getObjectValues(className);
            try {
                results = CQLResultsCreationUtil.createObjectResults(values, className, getClassToQnameMappings());
            } catch (ResultsCreationException ex) {
                throw new QueryProcessingException("Error creating query results: " + ex.getMessage(), ex);
            } catch (Exception ex) {
                throw new QueryProcessingException("Error creating query results: " + ex.getMessage(), ex);
            }
        }
        
        return results;
    }
    
    
    private List<Object> getObjectValues(String className) throws QueryProcessingException {
        Map<String, List<FactDataEntry>> attributeValues = dataAccessManager.getAttributeValues(className);
        Map<Integer, Object> objectInstances = new HashMap<Integer, Object>(); // by encounter num
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new QueryProcessingException("Error loading class: " + ex.getMessage(), ex);
        }
        for (String attributeName : attributeValues.keySet()) {
            List<FactDataEntry> attributeEntries = attributeValues.get(attributeName);
            for (FactDataEntry attributeEntry : attributeEntries) {
                Integer encounterNum = attributeEntry.getEncounterNumber();
                // see if an object instance exists for this encounter number
                Object instance = null;
                if (objectInstances.containsKey(encounterNum) && objectInstances.get(encounterNum) != null) {
                    instance = objectInstances.get(encounterNum);
                } else {
                    // no instance, create a new one
                    try {
                        instance = clazz.newInstance();
                    } catch (InstantiationException ex) {
                        throw new QueryProcessingException("Unable to instantiate a new class instance: " + ex.getMessage(), ex);
                    } catch (IllegalAccessException ex) {
                        throw new QueryProcessingException("No default constructor found to create a new class instance: " + ex.getMessage(), ex);
                    }
                    objectInstances.put(encounterNum, instance);
                }
                Object attributeValue = null;
                try {
                    attributeValue = attributeEntry.getTypedValue();
                } catch (ParseException ex) {
                    throw new QueryProcessingException("Error parsing attribute value: " + ex.getMessage(), ex);
                }
                setAttributeValue(instance, attributeName, attributeValue);
            }
        }
        // package up object instances in a list
        List<Object> instances = new ArrayList<Object>();
        instances.addAll(objectInstances.values());
        return instances;
    }
    
    
    private void setAttributeValue(Object instance, String attributeName, Object value) throws QueryProcessingException {
        Class<?> clazz = instance.getClass();
        Field attributeField = null;
        try {
            attributeField = clazz.getField(attributeName);
        } catch (NoSuchFieldException ex) {
            // this happens, go check for a setter method instead
            LOG.debug("Field " + attributeName + " not found -- will check for a setter");
        }
        if (attributeField != null) {
            if (attributeField.isAccessible()) {
                // set it and forget it!
                try {
                    attributeField.set(instance, value);
                } catch (IllegalAccessException ex) {
                    // uh oh
                    throw new QueryProcessingException("Error setting attribute value: " + ex.getMessage(), ex);
                }
            } else {
                LOG.debug("Field for attribute " + attributeName + " found, but not accessable");
            }
        } else {
            String setterName = "set" + Character.toUpperCase(attributeName.charAt(0));
            if (attributeName.length() > 1) {
                setterName = setterName + attributeName.substring(1);
            }
            Method setter = null;
            try {
                setter = clazz.getMethod(setterName, value.getClass());
            } catch (NoSuchMethodException ex) {
                throw new QueryProcessingException("No accessable field or setter found for attribute: " + ex.getMessage(), ex);
            }
            try {
                setter.invoke(instance, value);
            } catch (IllegalAccessException ex) {
                throw new QueryProcessingException("Error setting attribute value: " + ex.getMessage(), ex);
            } catch (InvocationTargetException ex) {
                throw new QueryProcessingException("Error setting attribute value: " + ex.getMessage(), ex);
            }
        }
    }
    
    
    private void checkCurrentCapabilities(CQLQuery cqlQuery) throws MalformedQueryException {
        gov.nih.nci.cagrid.cqlquery.Object target = cqlQuery.getTarget();
        if (target.getAssociation() != null || target.getAttribute() != null || target.getGroup() != null) {
            throw new MalformedQueryException("Only top-level query targets are allowed");
        }
        if (cqlQuery.getQueryModifier() != null) {
            if (cqlQuery.getQueryModifier().getDistinctAttribute() == null) {
                throw new MalformedQueryException("Only distinct attrbutes in query modifiers are currently supported");
            }
        }
    }
    
    
    private Mappings getClassToQnameMappings() throws Exception {
        // get the mapping file name
        String filename = ServiceConfigUtil.getClassToQnameMappingsFile();
        // String filename = "mapping.xml";
        Mappings mappings = (Mappings) Utils.deserializeDocument(filename, Mappings.class);
        return mappings;
    }
}

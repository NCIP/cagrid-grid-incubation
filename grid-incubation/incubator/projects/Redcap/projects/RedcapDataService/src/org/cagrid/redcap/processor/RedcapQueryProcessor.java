package org.cagrid.redcap.processor;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import java.io.FileReader;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.redcap.Data;
import org.cagrid.redcap.EventArms;
import org.cagrid.redcap.Events;
import org.cagrid.redcap.EventsCalendar;
import org.cagrid.redcap.Forms;
import org.cagrid.redcap.Projects;
import org.cagrid.redcap.util.RedcapUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.cql.validation.DomainModelValidator;
import gov.nih.nci.cagrid.data.cql.validation.ObjectWalkingCQLValidator;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.service.ServiceConfigUtil;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;
import gov.nih.nci.cagrid.data.utilities.ResultsCreationException;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

/*
 * Query Processor to process queries against the
 * REDCap Database. Initializes the Database connection
 * with the configured parameters from the service.properties
 * Required service properties to connect to REDCap:
 * jdbcDriverName
 * jdbcConnectString
 * jdbcUserName
 * tableNamePrefix  
 * domainModelFileName(from etc)
 */
public class RedcapQueryProcessor extends CQLQueryProcessor{
	
	private static final String JDBC_DRIVER_NAME = "jdbcDriverName";
	private static final String JDBC_CONNECT_STRING = "jdbcConnectString";
	private static final String JDBC_USERNAME = "jdbcUserName";
	private static final String JDBC_PASSWORD = "jdbcPassword";
	//private static final String TABLE_NAME_PREFIX = "tableNamePrefix";
	
	private static final String HB_CONFIG_LOC = "hibernate.cfg.xml";
	private static final String DOMAIN_MODEL_FILE_NAME = "domainModelFileName";
	private static final String DEF_DOMAIN_MODEL = "RedcapDataServiceDomainModel.xml";
	
	private static final String HB_CONN_DRIVER = "hibernate.connection.driver_class";
	private static final String HB_CONN_URL = "hibernate.connection.url";
	private static final String HB_CONN_USERNAME = "hibernate.connection.username";
	private static final String HB_CONN_PWD = "hibernate.connection.password";
	private static final String HB_DIALECT = "org.hibernate.dialect.MySQLDialect";
	
	private SessionFactory sessionFactory;
	private AnnotationConfiguration annotationConfig;
	private DatabaseConnectionSource connectionSource;
		
	private String driverClassname;
	private String connectString;
	private String username; 
	private String password;
	
	private static final Log LOG = LogFactory.getLog(RedcapQueryProcessor.class);
	
	public RedcapQueryProcessor() {
        super();
    }
	
	public void initialize(Properties parameters, InputStream wsdd) throws InitializationException {
       	super.initialize(parameters, wsdd);
       	initConnectionSource();
       	initConnection();
    }
    
	/*
	 * Validates the CQLQuery structure
	 * and validates the CQL against the domain 
	 * model to check if the cql is semantically 
	 * correct.
	 * @param CQLQuery: cql 1.0 query
	 * @return MalformedQueryException - invalid query
	 */
	private void validateCqlStructure(CQLQuery cqlQuery) throws MalformedQueryException{
		try{
			LOG.debug("Validating CQLQuery..");
			ObjectWalkingCQLValidator  validator = new ObjectWalkingCQLValidator();
			validator.validateCqlStructure(cqlQuery);	
			LOG.debug("Validating Domain Model..");
			DomainModel model = getDomainModel();
			DomainModelValidator  val = new DomainModelValidator();
			val.validateDomainModel(cqlQuery, model);
		}catch(MalformedQueryException exp){
			LOG.error("Error validating.."+exp.getMessage(),exp);
			throw new MalformedQueryException(exp);
		}
	}
	
	 public Set<String> getPropertiesFromEtc() {
        Set<String> fromEtc = new HashSet<String>();
        fromEtc.add(DOMAIN_MODEL_FILE_NAME);
        return fromEtc;
	 }
	 
	 /*
	  * Gets the configured domain model 
	  */
	 private DomainModel getDomainModel() {
        String domainModelFile = getConfiguredParameters().getProperty(DOMAIN_MODEL_FILE_NAME);
        LOG.debug("Domain model file name"+domainModelFile);
        DomainModel model = null;
        try {
            FileReader reader = new FileReader(domainModelFile);
            model = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
        } catch (Exception ex) {
            String message = "Unable to deserialize specified domain model: " + ex.getMessage();
            LOG.error(message, ex);
        }
        return model;
	}
	
	public Properties getRequiredParameters() {
        Properties props = new Properties();
        props.setProperty(JDBC_DRIVER_NAME, "");
        props.setProperty(JDBC_CONNECT_STRING, "");
        props.setProperty(JDBC_USERNAME, "");
        props.setProperty(JDBC_PASSWORD, "");
        //props.setProperty(TABLE_NAME_PREFIX, "");
        props.setProperty(DOMAIN_MODEL_FILE_NAME,DEF_DOMAIN_MODEL);
        return props;
    }
    
    /*
     * Initializes the DB connection using the configured parameters.
     * jdbcDriverName, jdbcConnectString, jdbcUserName, jdbcPassword
     * have to be configured to connect to the DB.
     */
    private void initConnectionSource() throws InitializationException {
        
    	LOG.info("Initializing database conection..");
    	driverClassname = getConfiguredParameters().getProperty(JDBC_DRIVER_NAME);
        connectString = getConfiguredParameters().getProperty(JDBC_CONNECT_STRING);
        username = getConfiguredParameters().getProperty(JDBC_USERNAME);
        password = getConfiguredParameters().getProperty(JDBC_PASSWORD);
        
        StringBuffer logMessage = new StringBuffer();
        logMessage.append(JDBC_DRIVER_NAME).append("[").append(driverClassname).append("]");
        logMessage.append(JDBC_CONNECT_STRING ).append("[").append(connectString).append("]");
        logMessage.append(JDBC_USERNAME).append("[").append(username).append("]");
        logMessage.append(JDBC_DRIVER_NAME).append("[").append(password).append("]");
        LOG.info(logMessage);
        
        try {
            connectionSource = new PooledDatabaseConnectionSource(driverClassname, connectString, username, password);
          
        } catch (Exception ex) {
            String message = "Unable to initialize database connection source: " + ex.getMessage();
            LOG.error(message, ex);
            throw new InitializationException(message, ex);
        }
    }
    
    private void initConnection(){
		try {
		   	annotationConfig = new AnnotationConfiguration();
			annotationConfig.addAnnotatedClass(Projects.class);
			annotationConfig.addAnnotatedClass(EventArms.class);
			annotationConfig.addAnnotatedClass(Events.class);
			annotationConfig.addAnnotatedClass(EventsCalendar.class);
			annotationConfig.addAnnotatedClass(Forms.class);
			annotationConfig.addAnnotatedClass(Data.class);
			LOG.debug("Attempting to get Hibernate Configuration file from ["+ HB_CONFIG_LOC +"]");
			sessionFactory = getSessionFactory(annotationConfig);
			//sessionFactory = annotationConfig.configure(getClass().getResource(HB_CONFIG_LOC)).buildSessionFactory();
		} catch (Throwable ex) {
			StringBuffer logMessage = new StringBuffer();
	        logMessage.append(JDBC_DRIVER_NAME).append("[").append(driverClassname).append("]");
	        logMessage.append(JDBC_CONNECT_STRING ).append("[").append(connectString).append("]");
	        logMessage.append(JDBC_USERNAME).append("[").append(username).append("]");
	        logMessage.append(JDBC_DRIVER_NAME).append("[").append(password).append("]");
	        LOG.error("Failed to create session factory with service properties"+logMessage.toString()+ex.getMessage(),ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
    
    /*
     * Processes the CQLQuery by converting
     * CQL to ParameterizedHQL and querying against
     * the REDCap DB. 
     * @param CQLQuery: user request cql
     * @return CQLQueryResults: Object results to return top level objects
     */
	public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		CQLQueryResults cqlQueryResults = null;
		try{
			LOG.info("Processing CQLQuery for target"+cqlQuery.getTarget().getName());
			validateCqlStructure(cqlQuery);
			RedcapUtil util = new RedcapUtil();
			List<Object> objects = util.convert(cqlQuery, sessionFactory,annotationConfig, connectionSource);
			cqlQueryResults = getResults(cqlQuery, objects);
		}catch(SQLException sqlException){
			LOG.error("Error processing the query"+sqlException.getMessage(),sqlException);
			throw new QueryProcessingException(sqlException.getMessage());
		}
		return cqlQueryResults;
	}
	
	/*
	 * Wraps the results into CQLQueryResults
	 * to be returned back to user.
	 * CQLQueryResults can be iterated to get the
	 * required object results.
	 * @param CQLQuery
	 * @param List<Object>: results to be wrapped to CQLQueryResults
	 * @return CQLQueryResults
	 */
	private CQLQueryResults getResults(CQLQuery cqlQuery, List<Object> rawResults) throws MalformedQueryException, QueryProcessingException{
		CQLQueryResults cqlResults = null;
        
        if (cqlQuery.getQueryModifier() != null) {
        	QueryModifier modifier = cqlQuery.getQueryModifier();
           	if(modifier.isCountOnly()){
                long count = Long.parseLong(rawResults.get(0).toString());
                cqlResults = CQLResultsCreationUtil.createCountResults(count, cqlQuery.getTarget().getName());
            }else{
            	String[] attributeNames = null;
                List<Object[]> resultsAsArrays = null;
                
                if (modifier.getDistinctAttribute() != null) {
                    attributeNames = new String[] {modifier.getDistinctAttribute()};
                    resultsAsArrays = new LinkedList<Object[]>();
                    for(Object o : rawResults){
                        resultsAsArrays.add(new Object[] {o});
                    }
                } else { // multiple attributes
                    attributeNames = modifier.getAttributeNames();
                    resultsAsArrays = new LinkedList<Object[]>();
                    for (Object o : rawResults) {
                        Object[] array = null;
                        if (o.getClass().isArray()) {
                            array = (Object[]) o;
                        } else {
                            array = new Object[] {o};
                        }
                        resultsAsArrays.add(array);
                    }
                }
                cqlResults = CQLResultsCreationUtil.createAttributeResults(
                    resultsAsArrays, cqlQuery.getTarget().getName(), attributeNames);
            }
        } else {
            Mappings classToQname = null;
            try {
                classToQname = getClassToQnameMappings();
            } catch (Exception ex) {
            	LOG.error("Error loading class to QName mappings:"+ex.getMessage(),ex);
                throw new QueryProcessingException("Error loading class to QName mappings: " + ex.getMessage(), ex);
            }
            try {
                cqlResults = CQLResultsCreationUtil.createObjectResults(
                    rawResults, cqlQuery.getTarget().getName(), classToQname);
            } catch (ResultsCreationException ex) {
            	LOG.error("Error packaging query results: "+ex.getMessage(),ex);
                throw new QueryProcessingException("Error packaging query results: " + ex.getMessage(), ex);
            }
        }
        return cqlResults;
		
	}
	
	private Mappings getClassToQnameMappings() throws Exception {
       String filename = ServiceConfigUtil.getClassToQnameMappingsFile();
       Mappings mappings = (Mappings) Utils.deserializeDocument(filename, Mappings.class);
       return mappings;
	}
	
	public void finalize() throws Throwable {
        try {
            connectionSource.shutdown();
        } catch (Exception ex) {
            LOG.error("Error shutting down connection source: " + ex.getMessage(), ex);
        }
        super.finalize();
    }
	
	private SessionFactory getSessionFactory(AnnotationConfiguration configuration){
		configuration.setProperty(HB_CONN_DRIVER, driverClassname);
		configuration.setProperty(HB_CONN_URL, connectString);
		configuration.setProperty(HB_CONN_USERNAME, username);
		configuration.setProperty(HB_CONN_PWD, password);
		configuration.setProperty(HB_DIALECT, "org.hibernate.dialect.MySQLDialect");
		SessionFactory sessionFactory = configuration.buildSessionFactory();
		return sessionFactory;
	}
	
}
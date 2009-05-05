package org.cagrid.i2b2.ontomapper.processor;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.FileReader;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.i2b2.ontomapper.utils.ConceptCodeMapper;
import org.cagrid.i2b2.ontomapper.utils.DomainModelConceptCodeMapper;

/** 
 *  I2B2QueryProcessor
 *  Processes CQL against a I2B2 data source
 * 
 * @author Shannon Hastings
 * 
 */
public class I2B2QueryProcessor extends CQLQueryProcessor {
    
    // properties for configuration
    public static final String DOMAIN_MODEL_FILE_NAME = "domainModelFileName";
    
    // default values for configuration
    public static final String DEFAULT_DOMAIN_MODEL_FILE_NAME = "domainModel.xml";
    
    private static final Log LOG = LogFactory.getLog(I2B2QueryProcessor.class);
        
    private ConceptCodeMapper conceptMapper = null;
    
    public I2B2QueryProcessor() {
        super();
    }
    
    
    public Properties getRequiredParameters() {
        Properties props = new Properties();
        props.setProperty(DOMAIN_MODEL_FILE_NAME, DEFAULT_DOMAIN_MODEL_FILE_NAME);
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
        initConceptMapper();
    }
    
    
    private void initConceptMapper() throws InitializationException {
        LOG.debug("Initializing concept code mapper");
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
        conceptMapper = new DomainModelConceptCodeMapper(model);
    }


    public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

}

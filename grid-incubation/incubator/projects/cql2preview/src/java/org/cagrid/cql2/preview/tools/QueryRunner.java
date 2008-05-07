package org.cagrid.cql2.preview.tools;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cql2.components.CQLQuery;
import gov.nih.nci.cagrid.cql2.results.CQLQueryResults;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainTypesInformation;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.cagrid.cql2.preview.processor.CQL2QueryProcessor;
import org.cagrid.cql2.preview.processor.MappingFileQNameResolver;
import org.cagrid.cql2.preview.processor.QNameResolver;

/** 
 *  QueryRunner
 *  Simple utility to run a CQL 2 query
 * 
 * @author David Ervin
 * 
 * @created Apr 4, 2008 9:47:01 AM
 * @version $Id: QueryRunner.java,v 1.4 2008/04/15 15:52:48 dervin Exp $ 
 */
public class QueryRunner {
    public static final String DEFAULT_CONFIG_CLIENT_CONFIG_WSDD = "resources/config/client-config.wsdd";
    public static final String DEFAULT_SDK_URL = "http://kramer.bmi.ohio-state.edu:8080/example40";
    public static final String DEFAULT_DOMAIN_TYPES_INFO = "resources/sdkExampleDomainTypes.xml";
    public static final String DEFAULT_DOMAIN_MODEL = "resources/sdkExampleDomainModel.xml";
    public static final String DEFAULT_CLASS_TO_QNAME = "resources/sdkExampleClassToQname.xml";
    
    public static final QName QUERY_RESULTS_QNAME = new QName("http://CQL.caBIG/2/gov.nih.nci.cagrid.cql.Results", "CQLQueryResults");
    
    public static final String SDK_URL_PROPERTY = "sdk.application.url";
    public static final String DOMAIN_TYPES_PROPERTY = "domain.types.file";
    public static final String DOMAIN_MODEL_PROPERTY = "domain.model.file";
    public static final String CLASS_TO_QNAME_PROPERTY = "class.to.qname.file";
    public static final String CLIENT_CONFIG_WSDD_PROPERTY = "client.config.wsdd.file";
    
    private Properties configuration = null;
    
    private QueryRunner(Properties configuration) {
        this.configuration = configuration;
    }
    
    
    public static QueryRunner createQueryRunner() {
        return new QueryRunner(new Properties());
    }
    
    
    public static QueryRunner createConfiguredQueryRunner() throws Exception {
        Properties config = new Properties();
        InputStream configStream = null;
        Exception exception = null;
        File configFile = new File("config" + File.separator + "configuration.properties");
        if (configFile.exists() && configFile.canRead()) {
            try {
                configStream = new FileInputStream(configFile);
                System.out.println("Configuration from file '" + configFile.getAbsolutePath() + "'");
            } catch (Exception ex) {
                exception = ex;
            }
        } else {
            try {
                configStream = QueryRunner.class.getResourceAsStream("config/configuration.properties");
                System.out.println("Configuration from resource 'config/configuration.properties'");
            } catch (Exception ex) {
                exception = ex;
            }
        }
        if (configStream == null) {
            if (exception != null) {
                throw exception;
            } else {
                throw new Exception("No suitable configuration could be found");
            }
        }
        config.load(configStream);
        return new QueryRunner(config);
    }
    
    
    public CQLQueryResults executeQuery(CQLQuery query) throws QueryProcessingException {
        long start = System.currentTimeMillis();
        CQL2QueryProcessor queryProcessor = null;
        try {
            queryProcessor = new CQL2QueryProcessor(
                getApplicationService(), getDomainTypesInfo(), getDomainModel(), getQNameResolver());
        } catch (Exception ex) {
            throw new QueryProcessingException("Error initializing query processor: " + ex.getMessage(), ex);
        }
        System.out.println("Query Processor initiailzed in " + (System.currentTimeMillis() - start) + " ms");
        start = System.currentTimeMillis();
        CQLQueryResults results = queryProcessor.query(query);
        System.out.println("Query Processed in " + (System.currentTimeMillis() - start) + " ms");
        return results;
    }
    
    
    public ApplicationService getApplicationService() throws Exception {
        String url = configuration.getProperty(SDK_URL_PROPERTY, DEFAULT_SDK_URL);
        ApplicationService service = ApplicationServiceProvider.getApplicationServiceFromUrl(url);
        return service;
    }
    
    
    public DomainTypesInformation getDomainTypesInfo() throws Exception {
        String filename = configuration.getProperty(DOMAIN_TYPES_PROPERTY, DEFAULT_DOMAIN_TYPES_INFO);
        FileReader reader = new FileReader(filename);
        DomainTypesInformation typesInfo = 
            (DomainTypesInformation) Utils.deserializeObject(reader, DomainTypesInformation.class);
        reader.close();
        return typesInfo;
    }
    
    
    public DomainModel getDomainModel() throws Exception {
        String filename = configuration.getProperty(DOMAIN_MODEL_PROPERTY, DEFAULT_DOMAIN_MODEL);
        FileReader reader = new FileReader(filename);
        DomainModel model = (DomainModel) Utils.deserializeObject(reader, DomainModel.class);
        reader.close();
        return model;
    }
    
    
    public QNameResolver getQNameResolver() throws Exception {
        String filename = configuration.getProperty(CLASS_TO_QNAME_PROPERTY, DEFAULT_CLASS_TO_QNAME);
        FileReader reader = new FileReader(filename);
        Mappings mappings = (Mappings) Utils.deserializeObject(reader, Mappings.class);
        reader.close();
        QNameResolver resolver = new MappingFileQNameResolver(mappings);
        return resolver;
    }
    
    
    public InputStream getWsddInputStream() throws Exception {
        String filename = configuration.getProperty(CLIENT_CONFIG_WSDD_PROPERTY, DEFAULT_CONFIG_CLIENT_CONFIG_WSDD);
        FileInputStream wsddStream = new FileInputStream(filename);
        return wsddStream;
    }
    
    
    public static void main(String[] args) {
        System.out.println("Running query " + args[0]);
        try {
            QueryRunner runner = createQueryRunner();
            
            long start = System.currentTimeMillis();
            CQLQuery query = (CQLQuery) Utils.deserializeDocument(args[0], CQLQuery.class);
            System.out.println("Query deserialized in " + (System.currentTimeMillis() - start) + " ms");
            
            CQLQueryResults results = runner.executeQuery(query);start = System.currentTimeMillis();
            System.out.println("Query Processed in " + (System.currentTimeMillis() - start) + " ms");
            
            start = System.currentTimeMillis();
            StringWriter writer = new StringWriter();
            FileInputStream wsddStream = new FileInputStream(
                System.getProperty(CLIENT_CONFIG_WSDD_PROPERTY, DEFAULT_CONFIG_CLIENT_CONFIG_WSDD));
            Utils.serializeObject(results, QUERY_RESULTS_QNAME, writer, wsddStream);
            wsddStream.close();
            System.out.println("Results serialized in " + (System.currentTimeMillis() - start) + " ms");
            
            System.out.println(writer.getBuffer().toString());
        } catch (QueryProcessingException ex) {
            System.err.println("Error processing query: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("Error setting up query processor");
            ex.printStackTrace();
        }
    }
}

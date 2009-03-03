package org.cagrid.i2b2.ontomapper.processor;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *  SDK4QueryProcessor
 *  Processes CQL against a caCORE SDK 4.0 data source
 * 
 * @author Shannon Hastings
 * 
 */
public class I2B2QueryProcessor extends CQLQueryProcessor {
    
    private static final Log LOG = LogFactory.getLog(I2B2QueryProcessor.class);


    public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        // TODO Auto-generated method stub
        return null;
    }    

}

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
package org.cagrid.cql2.preview.processor;

import gov.nih.nci.cagrid.cql2.components.CQLQuery;
import gov.nih.nci.cagrid.cql2.results.CQLAggregateResult;
import gov.nih.nci.cagrid.cql2.results.CQLAttributeResult;
import gov.nih.nci.cagrid.cql2.results.CQLObjectResult;
import gov.nih.nci.cagrid.cql2.results.CQLQueryResults;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainTypesInformation;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

import org.apache.axis.message.MessageElement;

/** 
 *  CQL2QueryProcessor
 *  Processes CQL 2 queries and returns result lists
 * @author David Ervin
 * 
 * @created Apr 2, 2008 11:44:47 AM
 * @version $Id: CQL2QueryProcessor.java,v 1.3 2008/04/03 18:19:05 dervin Exp $ 
 */
public class CQL2QueryProcessor {
    private ApplicationService sdkService = null;
    private DomainTypesInformation typesInfo = null;
    private DomainModel model = null;
    
    private QNameResolver qnameResolver = null;
    private CQL2ParameterizedHQL cqlTranslator = null;
    
    public CQL2QueryProcessor(ApplicationService service,
        DomainTypesInformation typesInfo, DomainModel model) {
        this(service, typesInfo, model, null);
    }
    
    public CQL2QueryProcessor(ApplicationService service, 
        DomainTypesInformation typesInfo, DomainModel model, QNameResolver resolver) {
        this.sdkService = service;
        this.typesInfo = typesInfo;
        this.model = model;
        this.qnameResolver = resolver;
    }
    
    
    private CQL2ParameterizedHQL getCqlTranslator() throws ClassNotFoundException, IOException {
        if (cqlTranslator == null) {
            cqlTranslator = new CQL2ParameterizedHQL(typesInfo, model);
        }
        return cqlTranslator;
    }
    
    
    public CQLQueryResults query(CQLQuery query) throws QueryProcessingException {
        // empty results object
        CQLQueryResults queryResults = new CQLQueryResults();
        queryResults.setTargetClassname(query.getCQLTargetObject().getClassName());
        
        // convert the CQL to HQL
        ParameterizedHqlQuery hql = null;
        try {
            hql = getCqlTranslator().convertToHql(query);
        } catch (QueryProcessingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new QueryProcessingException("Error processing query: " + ex.getMessage(), ex);
        }
        
        HQLCriteria criteria = new HQLCriteria(hql.getHql(), hql.getParameters());
        
        // query the SDK
        List rawResults = null;
        try {
            rawResults = sdkService.query(criteria);
        } catch (Exception ex) {
            throw new QueryProcessingException("Error querying caCORE service: " + ex.getMessage(), ex);
        }
        
        // see if there is further processing to be done
        if (query.getCQLQueryModifier() != null) {
            Object moddedResults = QueryModifierProcessor.applyQueryModifiers(
                query.getCQLTargetObject().getClassName(), rawResults, query.getCQLQueryModifier());
            // either aggregate results or attribute results
            if (moddedResults instanceof CQLAggregateResult) {
                queryResults.setAggregationResult((CQLAggregateResult) moddedResults);
            } else {
                queryResults.setAttributeResult((CQLAttributeResult[]) moddedResults);
            }
        } else {
            QName targetQName = null;
            if (qnameResolver != null) {
                targetQName = qnameResolver.getQName(query.getCQLTargetObject().getClassName());
            }
            CQLObjectResult[] objectResults = createObjectResults(rawResults, targetQName);
            queryResults.setObjectResult(objectResults);
        }
        
        return queryResults;
    }
    
    
    private CQLObjectResult[] createObjectResults(List rawObjects, QName targetQName) throws QueryProcessingException {
        CQLObjectResult[] objectResults = new CQLObjectResult[rawObjects.size()];
        try {
            for (int i = 0; i < rawObjects.size(); i++) {
                MessageElement elem = null;
                if (targetQName != null) {
                    elem = new MessageElement(targetQName, rawObjects.get(i));
                } else {
                    elem = new MessageElement();
                    elem.setObjectValue(rawObjects.get(i));
                }
                objectResults[i] = new CQLObjectResult(new MessageElement[] {elem});
            }
        } catch (SOAPException ex) {
            throw new QueryProcessingException("Error creating object message elements: " + ex.getMessage(), ex);
        }
        return objectResults;
    }
    

    public static void main(String[] args) {
        
    }
}

package org.cagrid.iso21090.sdkquery.test;

import gov.nih.nci.cacoresdk.domain.other.datatype.EnDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.IiDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.ScDataType;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.iso21090.Cd;
import gov.nih.nci.iso21090.En;
import gov.nih.nci.iso21090.Enxp;
import gov.nih.nci.iso21090.Ii;
import gov.nih.nci.iso21090.Sc;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.iso21090.sdkquery.translator.CQL2ParameterizedHQL;
import org.cagrid.iso21090.sdkquery.translator.HibernateConfigTypesInformationResolver;
import org.cagrid.iso21090.sdkquery.translator.ParameterizedHqlQuery;
import org.cagrid.iso21090.sdkquery.translator.QueryTranslationException;
import org.cagrid.iso21090.sdkquery.translator.TypesInformationResolver;
import org.hibernate.cfg.Configuration;

public class IsoQueriesTestCase extends TestCase {
    
    public static Log LOG = LogFactory.getLog(IsoQueriesTestCase.class);
    
    private ApplicationService sdkService = null;
    private CQL2ParameterizedHQL queryTranslator = null;

    public IsoQueriesTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        long start = System.currentTimeMillis();
        // gets the local SDK service instance
        try {
            sdkService = ApplicationServiceProvider.getApplicationService();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining application service instance: " + ex.getMessage());
        }
        LOG.info("Application service initialized in " + (System.currentTimeMillis() - start));
        System.out.println("Application service initialized in " + (System.currentTimeMillis() - start));
        
        start = System.currentTimeMillis();
        InputStream is = getClass().getResourceAsStream("/hibernate.cfg.xml");
        Configuration config = new Configuration();
        config.addInputStream(is);
        config.buildMappings();
        config.configure();
        TypesInformationResolver typesInfoResolver = new HibernateConfigTypesInformationResolver(config, true);
        try {
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error closing hibernate configuration input stream: " + ex.getMessage());
        }
        LOG.info("Types information resolver initialized in " + (System.currentTimeMillis() - start));
        System.out.println("Types information resolver initialized in " + (System.currentTimeMillis() - start));
        
        queryTranslator = new CQL2ParameterizedHQL(typesInfoResolver, false);
    }
    
    
    public void testQueryIiDataType() {
        CQLQuery query = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName(IiDataType.class.getName());
        Association assoc = new Association();
        assoc.setName(Ii.class.getName());
        assoc.setRoleName("value1");
        Attribute attrib = new Attribute("extension", Predicate.EQUAL_TO, "II_Extension");
        assoc.setAttribute(attrib);
        target.setAssociation(assoc);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryScDataType() {
        CQLQuery query = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName(ScDataType.class.getName());
        Association assoc1 = new Association();
        assoc1.setName(Sc.class.getName());
        assoc1.setRoleName("value2");
        Association assoc2 = new Association();
        assoc2.setName(Cd.class.getName());
        assoc2.setRoleName("code");
        Attribute attrib = new Attribute("code", Predicate.EQUAL_TO, "VALUE2_CODE_CODE1");
        assoc2.setAttribute(attrib);
        assoc1.setAssociation(assoc2);
        target.setAssociation(assoc1);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryEnDataType() {
        CQLQuery query = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName(EnDataType.class.getName());
        Association assoc1 = new Association();
        assoc1.setName(En.class.getName());
        assoc1.setRoleName("value5");
        Association assoc2 = new Association();
        assoc2.setName(Enxp.class.getName());
        assoc2.setRoleName("part");
        Attribute attrib = new Attribute("value", Predicate.EQUAL_TO, "Mr. John Doe1");
        assoc2.setAttribute(attrib);
        assoc1.setAssociation(assoc2);
        target.setAssociation(assoc1);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    private List<?> executeQuery(CQLQuery query) {
        ParameterizedHqlQuery hql = null;
        try {
            hql = queryTranslator.convertToHql(query);
        } catch (QueryTranslationException e) {
            e.printStackTrace();
            fail("Error translating CQL to HQL: " + e.getMessage());
        }
        LOG.info("The translated query is:");
        LOG.info(hql);
        System.out.println("The translated query is:");
        System.out.println(hql);
        System.out.flush();
        HQLCriteria criteria = new HQLCriteria(hql.getHql(), hql.getParameters());
        List<?> results = null;
        try {
            results = sdkService.query(criteria);
        } catch (ApplicationException e) {
            e.printStackTrace();
            fail("Error executing query: " + e.getMessage());
        }
        return results;
    }
}

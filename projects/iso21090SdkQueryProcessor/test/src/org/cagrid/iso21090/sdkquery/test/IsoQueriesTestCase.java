package org.cagrid.iso21090.sdkquery.test;

import gov.nih.nci.cacoresdk.domain.other.datatype.AdDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.CdDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.DsetIiDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.EnDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.IiDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.IvlIntDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.IvlPqDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.IvlRealDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.IvlTsDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.RealDataType;
import gov.nih.nci.cacoresdk.domain.other.datatype.ScDataType;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.iso21090.Ad;
import gov.nih.nci.iso21090.AddressPartType;
import gov.nih.nci.iso21090.Adxp;
import gov.nih.nci.iso21090.Cd;
import gov.nih.nci.iso21090.DSet;
import gov.nih.nci.iso21090.En;
import gov.nih.nci.iso21090.Enxp;
import gov.nih.nci.iso21090.Ii;
import gov.nih.nci.iso21090.Int;
import gov.nih.nci.iso21090.Ivl;
import gov.nih.nci.iso21090.NullFlavor;
import gov.nih.nci.iso21090.Sc;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.iso21090.sdkquery.translator.CQL2ParameterizedHQL;
import org.cagrid.iso21090.sdkquery.translator.ParameterizedHqlQuery;
import org.cagrid.iso21090.sdkquery.translator.QueryTranslationException;

public class IsoQueriesTestCase extends TestCase {
    
    public static Log LOG = LogFactory.getLog(IsoQueriesTestCase.class);
    
    private ApplicationService sdkService = null;
    private CQL2ParameterizedHQL queryTranslator = null;

    public IsoQueriesTestCase(String name) {
        super(name);
    }
    
    
    public void setUp() {
        try {
            this.sdkService = QueryTestsHelper.getSdkApplicationService();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining cacore service: " + ex.getMessage());
        }
        try {
            this.queryTranslator = QueryTestsHelper.getCqlTranslator();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error obtaining cql query translator: " + ex.getMessage());
        }
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
    
    
    public void testQueryAdDataType() {
        CQLQuery query = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName(AdDataType.class.getName());
        Association assoc1 = new Association();
        assoc1.setName(Ad.class.getName());
        assoc1.setRoleName("value1");
        Association assoc2 = new Association();
        assoc2.setName(Adxp.class.getName());
        assoc2.setRoleName("part");
        Attribute attrib = new Attribute("value", Predicate.LIKE, "%1");
        assoc2.setAttribute(attrib);
        assoc1.setAssociation(assoc2);
        target.setAssociation(assoc1);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryCdDataTypeAgainstConstant() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(CdDataType.class.getName());
        Association assoc = new Association();
        assoc.setName(Cd.class.getName());
        assoc.setRoleName("value3");
        Attribute attrib = new Attribute("codeSystem", Predicate.EQUAL_TO, "CODESYSTEM");
        assoc.setAttribute(attrib);
        target.setAssociation(assoc);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryIiDataTypeAgainstConstant() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(IiDataType.class.getName());
        Association assoc = new Association();
        assoc.setName(Ii.class.getName());
        assoc.setRoleName("value3");
        assoc.setAttribute(new Attribute("root", Predicate.EQUAL_TO, "2.16.12.123.456"));
        target.setAssociation(assoc);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryCdNullFlavorNi() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(CdDataType.class.getName());
        Association assoc = new Association();
        assoc.setName(Cd.class.getName());
        assoc.setRoleName("value3");
        Attribute attrib = new Attribute("nullFlavor", Predicate.EQUAL_TO, NullFlavor.NA.name());
        assoc.setAttribute(attrib);
        target.setAssociation(assoc);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryAdNullFlavorNi() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(AdDataType.class.getName());
        Association assoc = new Association();
        assoc.setName(Ad.class.getName());
        assoc.setRoleName("value1");
        assoc.setAttribute(new Attribute("nullFlavor", Predicate.EQUAL_TO, "NI"));
        target.setAssociation(assoc);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryAdxpAddressPartTypeADL() {
        CQLQuery query = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName(AdDataType.class.getName());
        Association assoc1 = new Association();
        assoc1.setName(Ad.class.getName());
        assoc1.setRoleName("value1");
        Association assoc2 = new Association();
        assoc2.setName(Adxp.class.getName());
        assoc2.setRoleName("part");
        Attribute attrib = new Attribute("type", Predicate.EQUAL_TO, AddressPartType.ADL.name());
        assoc2.setAttribute(attrib);
        assoc1.setAssociation(assoc2);
        target.setAssociation(assoc1);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryRealDataType() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(RealDataType.class.getName());
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryIvlPqDataType() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(IvlPqDataType.class.getName());
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryIvlRealDataType() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(IvlRealDataType.class.getName());
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryIvlTsDataType() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(IvlTsDataType.class.getName());
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryIvlIntDataType() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(IvlIntDataType.class.getName());
        Association association1 = new Association();
        association1.setName(Ivl.class.getName());
        association1.setRoleName("value1");
        target.setAssociation(association1);
        Association association2 = new Association();
        association2.setName(Int.class.getName());
        association2.setRoleName("low");
        association1.setAssociation(association2);
        Attribute attr = new Attribute();
        attr.setName("value");
        attr.setPredicate(Predicate.EQUAL_TO);
        attr.setValue("1");
        association2.setAttribute(attr);
        query.setTarget(target);
        
        List<?> results = executeQuery(query);
        Iterator<?> iter = results.iterator();
        ArrayList<gov.nih.nci.cacoresdk.domain.other.datatype.IvlIntDataType> result = new
        ArrayList<gov.nih.nci.cacoresdk.domain.other.datatype.IvlIntDataType>();
        while (iter.hasNext()) {
            result.add((gov.nih.nci.cacoresdk.domain.other.datatype.IvlIntDataType)iter.next());
        }
        gov.nih.nci.cacoresdk.domain.other.datatype.IvlIntDataType testResultClass = result.get(0);
        assertEquals(3, result.size()); // Ye's test has 4 here
        assertEquals(Integer.valueOf(1), testResultClass.getValue1().getLow().getValue()); // Ye's test used "1".  String vs Integer
    }
    
    
    public void testQueryDsetIiDataType() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(DsetIiDataType.class.getName());
        Association assoc1 = new Association();
        assoc1.setName(DSet.class.getName());
        assoc1.setRoleName("value1");
        Association assoc2 = new Association();
        assoc2.setName(Ii.class.getName());
        assoc2.setRoleName("item");
        assoc2.setAttribute(new Attribute("extension", Predicate.EQUAL_TO, "II_Extension"));
        assoc1.setAssociation(assoc2);
        target.setAssociation(assoc1);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryDsetIiDataTypeAgainstConstant() {
        CQLQuery query = new CQLQuery();
        Object target = new Object();
        target.setName(DsetIiDataType.class.getName());
        Association a1 = new Association();
        a1.setName(DSet.class.getName());
        a1.setRoleName("value1");
        Association a2 = new Association();
        a2.setName(Ii.class.getName());
        a2.setRoleName("item");
        a2.setAttribute(new Attribute("root", Predicate.EQUAL_TO, "2.16.12.123.456"));
        a1.setAssociation(a2);
        target.setAssociation(a1);
        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    public void testQueryDsetTelDataType() {
        CQLQuery query = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.cacoresdk.domain.other.datatype.DsetTelDataType");
        Association association1 = new Association();
        association1.setName("gov.nih.nci.iso21090.DSet");
        association1.setRoleName("value1");
        target.setAssociation(association1);

        Association association2 = new Association();
        association2.setName("gov.nih.nci.iso21090.Tel");
        association2.setRoleName("item");
        association1.setAssociation(association2);

        Attribute attribute3 = new Attribute();
        attribute3.setName("value");
        attribute3.setPredicate(Predicate.EQUAL_TO);
        attribute3.setValue("tel://123-456-7891");
        association2.setAttribute(attribute3);

        query.setTarget(target);
        
        executeQuery(query);
    }
    
    
    private List<?> executeQuery(CQLQuery query) {
        if (LOG.isDebugEnabled()) {
            StringWriter writer = new StringWriter();
            try {
                Utils.serializeObject(query, DataServiceConstants.CQL_QUERY_QNAME, writer);
                LOG.debug(writer.getBuffer().toString());
            } catch (Exception ex) {
                // ignore
            }
        }
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
            LOG.info("Found " + results.size() + " results");
        } catch (ApplicationException e) {
            e.printStackTrace();
            fail("Error executing query: " + e.getMessage());
        }
        return results;
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(IsoQueriesTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}

package org.cagrid.redcap.test.system.steps;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.axis.types.URI.MalformedURIException;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.BaseStep;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.cqlresultset.TargetAttribute;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collection;
import java.lang.reflect.Method;

public class InvokeClientStep extends BaseStep {
    public static final String TEST_URL_SUFFIX = "/wsrf/services/cagrid/";

    private TestCaseInfo tci;
    private ServiceContainer container;
   
    private static final String TEST_QUERIES_DIR = "/tests/";
    private static final String TEST_RESULTS_DIR = "/results/";
    
    private static final Log LOG = LogFactory.getLog(InvokeClientStep.class);
    
    public InvokeClientStep(ServiceContainer container, TestCaseInfo tci) throws Exception {
        super(tci.getDir(), false);
        this.tci = tci;
        this.container = container;
    }

    public void runStep() throws Throwable {
    	testObjectsOfGivenType();
    	testObjectsWithSingleAttribute();
    	testCountObjectsOfGivenType();
    	testDistinctAttributesOfObject();
    	testMultipleAttributesOfObject();
    	testObjectsWithSingleAssociation();
    	testObjectsWithNestedAssociations();
    	testAssociationsWithGroups();
    	testGroups();
    	testGroupsWithAssociationGroup();
    	testGroupsWithAttributeGroup();
    	testGroupsWithAttributes();
    	testGroupsWithGroups();
    	testGroupsWithAssociations();
    	
    	//invalid queries
    	testNonExistantTarget();
        testNonExistantAssociation();
        testNonExistantAttribute();
        testAssociationWithWrongAttributeDatatype();
    }
    
    private void testNonExistantTarget() {
        LOG.debug("testNonExistantTarget");
        CQLQuery query = loadQuery("invalid_nonExistantTarget.xml");
        invokeInvalidQuery(query);
    }
    
    private void testNonExistantAssociation() {
        LOG.debug("testNonExistantAssociation");
        CQLQuery query = loadQuery("invalid_nonExistantAssociation.xml");
        invokeInvalidQuery(query);
    }
    
    private void testNonExistantAttribute() {
        LOG.debug("testNonExistantAttribute");
        CQLQuery query = loadQuery("invalid_nonExistantAttribute.xml");
        invokeInvalidQuery(query);
    }
    
    private void testAssociationWithWrongAttributeDatatype() {
        LOG.debug("testAssociationWithWrongAttributeDatatype");
        CQLQuery query = loadQuery("invalid_associationWithWrongAttributeDatatype.xml");
        invokeInvalidQuery(query);
    }
    
    /**
     * Executes a query, which is expected to be invalid and fail
     * @param query
     *      The expected invalid query
     */
    private void invokeInvalidQuery(CQLQuery query) {
        DataServiceClient client = getServiceClient();
        try {
            client.query(query);
            fail("Query returned results, should have failed");
        } catch (Exception ex) {
           LOG.info("Got Exception as expected");
        	// expected
        }
    }
    
    private void testObjectsOfGivenType() {
        LOG.debug("testObjectsOfGivenType");
        CQLQuery query = loadQuery("ObjectOfGivenType.xml");
        CQLQueryResults results = loadQueryResults("ObjectOfGivenTypeResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testObjectsWithSingleAttribute(){
    	LOG.debug("testObjectsWithSingleAttribute");
        CQLQuery query = loadQuery("ObjectsWithSingleAttribute.xml");
        CQLQueryResults results = loadQueryResults("ObjectsWithSingleAttributeResults.xml");
        invokeValidQueryValidResults(query, results);
    }

    private void testCountObjectsOfGivenType(){
    	LOG.debug("testCountObjectsOfGivenType");
        CQLQuery query = loadQuery("CountObjectsOfGivenType.xml");
        CQLQueryResults results = loadQueryResults("CountObjectsOfGivenTypeResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testDistinctAttributesOfObject(){
    	LOG.debug("testDistinctAttributesOfObject");
        CQLQuery query = loadQuery("DistinctAttributesOfObject.xml");
        CQLQueryResults results = loadQueryResults("DistinctAttributesOfObjectResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testMultipleAttributesOfObject(){
    	LOG.debug("testMultipleAttributesOfObject");
        CQLQuery query = loadQuery("MultipleAttributesOfObject.xml");
        CQLQueryResults results = loadQueryResults("MultipleAttributesOfObjectResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testObjectsWithSingleAssociation(){
    	LOG.debug("testObjectsWithSingleAssociation");
        CQLQuery query = loadQuery("ObjectsWithSingleAssociation.xml");
        CQLQueryResults results = loadQueryResults("ObjectsWithSingleAssociationResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testObjectsWithNestedAssociations(){
    	LOG.debug("testObjectsWithNestedAssociations");
        CQLQuery query = loadQuery("ObjectsWithNestedAssociations.xml");
        CQLQueryResults results = loadQueryResults("ObjectsWithNestedAssociationsResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testAssociationsWithGroups(){
    	LOG.debug("testAssociationsWithGroups");
        CQLQuery query = loadQuery("AssociationsWithGroups.xml");
        CQLQueryResults results = loadQueryResults("AssociationsWithGroupsResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testGroups(){
    	LOG.debug("testGroups");
        CQLQuery query = loadQuery("Groups.xml");
        CQLQueryResults results = loadQueryResults("GroupsResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testGroupsWithAssociationGroup(){
    	LOG.debug("testGroupsWithAssociationGroup");
        CQLQuery query = loadQuery("GroupsWithAssociationGroup.xml");
        CQLQueryResults results = loadQueryResults("GroupsWithAssociationGroupResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testGroupsWithAttributeGroup(){
    	LOG.debug("testGroupsWithAttributeGroup");
        CQLQuery query = loadQuery("GroupsWithAttributeGroup.xml");
        CQLQueryResults results = loadQueryResults("GroupsWithAttributeGroupResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testGroupsWithAttributes(){
    	LOG.debug("testGroupsWithAttributes");
        CQLQuery query = loadQuery("GroupsWithAttributes.xml");
        CQLQueryResults results = loadQueryResults("GroupsWithAttributesResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testGroupsWithGroups(){
    	LOG.debug("testGroupsWithGroups");
        CQLQuery query = loadQuery("GroupsWithGroups.xml");
        CQLQueryResults results = loadQueryResults("GroupsWithGroupsResults.xml");
        invokeValidQueryValidResults(query, results);
    }
    
    private void testGroupsWithAssociations(){
    	LOG.debug("testGroupsWithAssociations");
        CQLQuery query = loadQuery("GroupsWithAssociations.xml");
        CQLQueryResults results = loadQueryResults("GroupsWithAssociationsResults.xml");
        invokeValidQueryValidResults(query, results);
    }
	
    private CQLQuery loadQuery(String filename) {
        String fullFilename = TEST_QUERIES_DIR + filename;
        CQLQuery query = null;
        try {
        	InputStream queryInputStream = InvokeClientStep.class.getResourceAsStream(fullFilename);
            InputStreamReader reader = new InputStreamReader(queryInputStream);
        	query = (CQLQuery) Utils.deserializeObject(reader, CQLQuery.class);
        	reader.close();
            queryInputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing query (" + fullFilename + "): " + ex.getMessage());
        }
        return query;
    }
    
    private CQLQueryResults loadQueryResults(String filename)  {
        String fullFilename = TEST_RESULTS_DIR + filename;
    	CQLQueryResults results = null;
        try {
        	InputStream resultInputStream = InvokeClientStep.class.getResourceAsStream(fullFilename);
            InputStreamReader reader = new InputStreamReader(resultInputStream);
            results = (CQLQueryResults) Utils.deserializeObject(reader, CQLQueryResults.class);
            reader.close();
            resultInputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing query results (" + fullFilename + "): " + ex.getMessage());
        }
        return results;
    }
    
    private void invokeValidQueryValidResults(CQLQuery query, CQLQueryResults goldResults) {
        DataServiceClient client = getServiceClient();
        CQLQueryResults queryResults = null;
        try {
        	LOG.debug("Querying.....");
            queryResults = client.query(query);
            Iterator<?> iter = new CQLQueryResultsIterator(queryResults, true);
            while (iter.hasNext()) {
                System.out.println(iter.next());
                if (iter.hasNext()) {
                    System.out.println();
                }
            }
            // If this fails, we need to still be able to exit the jvm
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Query failed to execute: " + ex.getMessage());
        }
        compareResults(goldResults, queryResults);
    }
    
    private DataServiceClient getServiceClient() {
        DataServiceClient client = null;
        try {
            client = new DataServiceClient(getServiceUrl()); 
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating data service client: " + ex.getMessage());
        }
        return client;
    }
    
    private String getServiceUrl() {
        String url = null;
        try {
            URI baseUri = container.getContainerBaseURI();
            url = baseUri.toString() + "cagrid/" + tci.getName();
        } catch (MalformedURIException ex) {
            ex.printStackTrace();
            fail("Error generating service url: " + ex.getMessage());
        }
        LOG.debug("Data service url: " + url);
        return url;
    }
    
    private void compareResults(CQLQueryResults gold, CQLQueryResults test) {
        List<Object> goldObjects = new ArrayList<Object>();
        List<Object> testObjects = new ArrayList<Object>();
        
        boolean goldIsAttributes = false;
        CQLQueryResultsIterator goldIter = new CQLQueryResultsIterator(gold, getClientConfigStream());
        while (goldIter.hasNext()) {
            Object o = goldIter.next();
            if (o instanceof TargetAttribute[]) {
                goldIsAttributes = true;
            }
            goldObjects.add(o);
        }
        
        boolean testIsAttributes = false;
        CQLQueryResultsIterator testIter = new CQLQueryResultsIterator(test, getClientConfigStream());
        while (testIter.hasNext()) {
            Object o = testIter.next();
            if (o instanceof TargetAttribute[]) {
                testIsAttributes = true;
            }
            testObjects.add(o);
        }
        
        assertEquals("Number of results differed from expected", goldObjects.size(), testObjects.size());
        assertEquals("Test results as attributes differed from expected", goldIsAttributes, testIsAttributes);
        
        if (goldIsAttributes) {
            List<TargetAttribute[]> goldAttributes = recastList(goldObjects);
            List<TargetAttribute[]> testAttributes = recastList(testObjects);
            compareTargetAttributes(goldAttributes, testAttributes);
        } else {
            // assertTrue("Gold and Test contained different objects", goldObjects.containsAll(testObjects));
            compareObjects(goldObjects, testObjects);
        }
    }
    
    private InputStream getClientConfigStream() {
        InputStream is = null;
        //String resourceName = "/RedcapDataService/src/org/cagrid/redcap/client/client-config.wsdd";
        String resourceName = "/src/org/cagrid/redcap/client/client-config.wsdd";
        try {
        	is = InvokeClientStep.class.getResourceAsStream(resourceName);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining client config input stream: " + ex.getMessage());
        }
        return is;
    }
    
    @SuppressWarnings("unchecked")
    private <T> List<T> recastList(List<?> set) {
        List<T> returnme = new ArrayList<T>();
        for (Object o : set) {
            returnme.add((T) o);
        }
        return returnme;
    }
    
    private void compareTargetAttributes(List<TargetAttribute[]> gold, List<TargetAttribute[]> test) {
        // Must find each array of gold attributes (in any order) 
        // in the test attributes list
        
        // sorts attributes for consistency of comparison
        Comparator<TargetAttribute> attributeSorter = new Comparator<TargetAttribute>() {
            public int compare(TargetAttribute o1, TargetAttribute o2) {
                String att1String = o1.getName() + "=" + o1.getValue();
                String att2String = o2.getName() + "=" + o2.getValue();
                return att1String.compareTo(att2String);
            }
        };
        
        // walk the gold attribute arrays
        for (TargetAttribute[] goldAttributes : gold) {
            // sort that array
            Arrays.sort(goldAttributes, attributeSorter);
            
            // find a matching array of attributes in the test set
            Iterator<TargetAttribute[]> testIter = test.iterator();
            while (testIter.hasNext()) {
                TargetAttribute[] testAttributes = testIter.next();
                
                // sort them out
                Arrays.sort(testAttributes, attributeSorter);
                
                // veriy the same number of attributes.  This should be true for every array
                assertEquals("Number of attributes differed from expected", goldAttributes.length, testAttributes.length);
                
                // check that the current goldAttribute[] matches the test[]
                boolean matching = true;
                for (int i = 0; i < goldAttributes.length && matching; i++) {
                    assertEquals("Unexpected attribute name in test results", 
                        goldAttributes[i].getName(), testAttributes[i].getName());
                    String goldValue = goldAttributes[i].getValue();
                    String testValue = testAttributes[i].getValue();
                    matching = String.valueOf(goldValue).equals(String.valueOf(testValue));
                }
                if (matching) {
                    // found a matching TargetAttribute[] in test for one in gold.
                    // remove it from the test set so anything left in there is
                    // not a valid result when this process completes
                    testIter.remove();
                }
            }
        }
        
        if (test.size() != 0) {
            StringBuffer errors = new StringBuffer();
            errors.append("The following attribute arrays were not expected in the test results:");
            for (TargetAttribute[] atts : test) {
                errors.append("---------\n");
                for (TargetAttribute ta : atts) {
                    errors.append("Attribute: ").append(ta.getName()).append("\t\tValue: ")
                        .append(ta.getValue()).append("\n");
                }
            }
            fail(errors.toString());
        }
    }
    
    private void compareObjects(List<Object> gold, List<Object> test) {
        if (!gold.containsAll(test)) {
            // fail, but why?
            List<Object> tempGold = new ArrayList<Object>();
            tempGold.addAll(gold);
            tempGold.removeAll(test);
            StringBuffer errors = new StringBuffer();
            errors.append("The following objects were expected but not found\n");
            dumpGetters(tempGold, errors);
            List<Object> tempTest = new ArrayList<Object>();
            tempTest.addAll(test);
            tempTest.removeAll(gold);
            errors.append("\n\nThe following objects were found, but not expected\n");
            dumpGetters(tempTest, errors);
            fail(errors.toString());
        }
    }
    
    private void dumpGetters(Collection<Object> objs, StringBuffer buff) {
        for (Object o : objs) {
            buff.append(o.getClass().getName()).append("\n");
            Method[] methods = o.getClass().getMethods();
            for (Method m : methods) {
                if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
                    try {
                        Object value = m.invoke(o, new Object[0]);
                        buff.append(m.getName());
                        buff.append(" --> ");
                        buff.append(String.valueOf(value));
                        buff.append("\n");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}

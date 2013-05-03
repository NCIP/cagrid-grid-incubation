/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
/**
 */
package edu.emory.cci.cqlCsm.cqlCsmCql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.namespace.QName;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;

import edu.emory.cci.domainModel.DomainModelAccessor;
import edu.emory.cci.domainModel.DomainModelAccessorTest;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

/**
 * Here is a subset of the domain model being used with filters
 * 
 * <pre>
 *                                                        +--------------+
 * +-------------+           +-------------+              |v3.hl7_org.ED |
 * |v3.hl7_org.CD|           |v3.hl7_org.CR|              +--------------+
 * +-------------+           +-------------+ originalText |compression   |
 * | code        | qualifier |code         |------------->|encoding      |
 * | codeSystem  |---------->|codeSystem   |              |mediaType     |
 * | displayName |           |displayName  |              |nullFlavor    |
 * | nullFlavor  |           |inverted     |              |integrityCheck|
 * +-------------+           |nullFlavor   |              +--------------+
 *                           +-----+-------+
 *                                 |                  +--------------+
 *                                 |                  |v3.hl7_org.CV |
 *                                 |             name +--------------+
 *                                 +----------------->|code          |
 *                                                    |codeSystem    |
 *                                                    |codeSystemName|
 *                                                    |displayName   |
 *                                                    |nullFlavor    |
 *                                                    +--------------+
 *</pre>
 * 
 * Filters are
 * <ul>
 * <li>CD - nullFlavor = true
 * <li>CD - qualifier - code = z1,z2,z3
 * <li>CD - qualifier - name - nullFlavor = false
 * <li>ED - mediaType = 3
 * </ul>
 * 
 *<br/>
 * <br/>
 *Here is is a subset without filters:
 * 
 * <pre>
 * +--------------------------+
 * |PORT_MT020001.AnnotatedECG|           +-----------+
 * +--------------------------+           | ID        |
 * |classCode                 |           +-----------+
 * |moodCode                  |       id  |displayable|
 * |nullFlavor                |---------->|extension  |
 * |realmCode                 |           |nullFlavor |
 * |templateId                |           |root       |
 * |type                      |           +-----------+
 * |typeId                    |
 * +--------------------------+
 *</pre>
 * 
 * @author Mark Grand
 */
public class CqlCsmPreprocessorTest {
    /**
     * User who is authorized to access some objects
     */
    private static final String GENERAL_USER = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=jdoe";

    /**
     * User who is not authorized to access any objects.
     */
    private static final String UNAUTHORIZED_USER = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=ssack";

    /**
     * The name of the file that contains the domain model.
     */
    private static final String DOMAIN_MODEL_FILE_NAME = "HL7AECGDataService-domainmodel.xml";

    private static CqlCsmPreprocessor preprocessor;

    /**
     * Deserialize the model that will be used for tests.
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            BasicConfigurator.configure();
            LogManager.getRootLogger().setLevel(Level.INFO);
            InputStream in = DomainModelAccessorTest.class.getResourceAsStream(DOMAIN_MODEL_FILE_NAME);
            if (in == null) {
                System.out.println("Could not find " + DOMAIN_MODEL_FILE_NAME + " as a resource.");
            } else {
                System.out.println("Found " + DOMAIN_MODEL_FILE_NAME);
                Reader reader = new InputStreamReader(in);
                DomainModel model = (DomainModel) Utils.deserializeObject(reader, DomainModel.class);
                DomainModelAccessor dma = new DomainModelAccessor(model);
                preprocessor = new CqlCsmPreprocessor(dma);
            }
        } catch (Exception e) {
            System.out.println("Failed getting domain model that will drive the tests");
            e.printStackTrace(System.out);
            throw e;
        }
    }

    /**
     * Test queries with an unconstrained, unfiltered target.
     */
    @Test
    public void testUnconstrainedUnfilteredTarget() throws Exception {
        String query = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n"
                + " <Target name=\"v3.hl7_org.PORT_MT020001.AnnotatedECG\" >\n" //
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery inputQuery = makeQuery(query);
        assertEquals(inputQuery, preprocessor.preprocess(inputQuery, GENERAL_USER));
        assertEquals(inputQuery, preprocessor.preprocess(inputQuery, UNAUTHORIZED_USER));
    }

    /**
     * Test queries with an unconstrained target. Test the sub-cases of having
     * no filters, Having filters for a user ID that is authorized for some
     * values and for a user ID that is authorized for no values.
     */
    @Test
    public void testUnconstrainedFilteredTarget() throws Exception {
        String inputQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" // 
                + " <Target name=\"v3.hl7_org.CD\" >\n" + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery inputQuery = makeQuery(inputQueryString);
        CQLQuery outputQuery = preprocessor.preprocess(inputQuery, GENERAL_USER);
        String expectedQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n"//
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Group logicRelation=\"OR\">\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z1\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z2\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z3\"/>\n"//
                + "    </Group>\n"//
                + "   </Association>\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Association name=\"v3.hl7_org.CV\" roleName=\"name\">\n"//
                + "     <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"false\"/>\n"//
                + "    </Association>\n"//
                + "   </Association>\n"//
                + "   <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"true\"/>\n"//
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery expectedQuery = makeQuery(expectedQueryString);
        try {
            assertEquals(expectedQuery, outputQuery);
        } catch (AssertionError e) {
            System.err.println("Actual query: " + ObjectSerializer.toString(outputQuery, new QName("CQLQuery")));
            throw e;
        }
        String noAccessQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" //
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Attribute name=\"code\" predicate=\"IS_NULL\" />\n"
                + "   <Attribute name=\"code\" predicate=\"IS_NOT_NULL\" />\n"
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery noAccessQuery = makeQuery(noAccessQueryString);
        CQLQuery outputQuery2 = preprocessor.preprocess(inputQuery, UNAUTHORIZED_USER);
        try {
            assertEquals(noAccessQuery, outputQuery2);
        } catch (AssertionError e) {
            System.err.println("Actual query: " + ObjectSerializer.toString(outputQuery2, new QName("CQLQuery")));
            throw e;
        }
    }

    /**
     * Test queries with a target that is constrained with a single attribute.
     * Test the sub-cases of having no filters, Having filters for a user ID
     * that is authorized for some values and for a user ID that is authorized
     * for no values.
     */
    @Test
    public void testTargetWithSingleAttributesConstraint() throws Exception {
        String inputQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" // 
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Attribute name=\"codeSystem\" predicate=\"EQUAL_TO\" value=\"zz\"/>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery inputQuery = makeQuery(inputQueryString);
        CQLQuery outputQuery = preprocessor.preprocess(inputQuery, GENERAL_USER);
        String expectedQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n"//
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Group logicRelation=\"OR\">\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z1\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z2\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z3\"/>\n"//
                + "    </Group>\n"//
                + "   </Association>\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Association name=\"v3.hl7_org.CV\" roleName=\"name\">\n"//
                + "     <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"false\"/>\n"//
                + "    </Association>\n"//
                + "   </Association>\n"//
                + "   <Attribute name=\"codeSystem\" predicate=\"EQUAL_TO\" value=\"zz\"/>\n"//
                + "   <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"true\"/>\n"//
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery expectedQuery = makeQuery(expectedQueryString);
        try {
            assertEquals(expectedQuery, outputQuery);
        } catch (AssertionError e) {
            System.err.println("Actual query: " + ObjectSerializer.toString(outputQuery, new QName("CQLQuery")));
            throw e;
        }
        String noAccessQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" //
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Attribute name=\"code\" predicate=\"IS_NULL\" />\n"
                + "   <Attribute name=\"code\" predicate=\"IS_NOT_NULL\" />\n"
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery noAccessQuery = makeQuery(noAccessQueryString);
        CQLQuery outputQuery2 = preprocessor.preprocess(inputQuery, UNAUTHORIZED_USER);
        try {
            assertEquals(noAccessQuery, outputQuery2);
        } catch (AssertionError e) {
            System.err.println("Actual query: " + ObjectSerializer.toString(outputQuery2, new QName("CQLQuery")));
            throw e;
        }
    }

    /**
     * Test queries with a target that is constrained with a multiple ANDed
     * attributes. Test the sub-cases of having no filters, Having filters for a
     * user ID that is authorized for some values and for a user ID that is
     * authorized for no values.
     */
    @Test
    public void testTargetWithMultipleAndedAttrbutesConstraint() throws Exception {
        String inputQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" // 
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"24\"/>\n"//
                + "   <Attribute name=\"codeSystem\" predicate=\"EQUAL_TO\" value=\"zz\"/>\n"//
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery inputQuery = makeQuery(inputQueryString);
        CQLQuery outputQuery = preprocessor.preprocess(inputQuery, GENERAL_USER);
        String expectedQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n"//
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Group logicRelation=\"OR\">\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z1\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z2\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z3\"/>\n"//
                + "    </Group>\n"//
                + "   </Association>\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Association name=\"v3.hl7_org.CV\" roleName=\"name\">\n"//
                + "     <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"false\"/>\n"//
                + "    </Association>\n"//
                + "   </Association>\n"//
                + "   <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"24\"/>\n"//
                + "   <Attribute name=\"codeSystem\" predicate=\"EQUAL_TO\" value=\"zz\"/>\n"//
                + "   <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"true\"/>\n"//
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery expectedQuery = makeQuery(expectedQueryString);
        try {
            assertEquals(expectedQuery, outputQuery);
        } catch (AssertionError e) {
            System.err.println("Actual query: " + ObjectSerializer.toString(outputQuery, new QName("CQLQuery")));
            throw e;
        }
        String noAccessQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" //
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Attribute name=\"code\" predicate=\"IS_NULL\" />\n"
                + "   <Attribute name=\"code\" predicate=\"IS_NOT_NULL\" />\n"
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery noAccessQuery = makeQuery(noAccessQueryString);
        CQLQuery outputQuery2 = preprocessor.preprocess(inputQuery, UNAUTHORIZED_USER);
        try {
            assertEquals(noAccessQuery, outputQuery2);
        } catch (AssertionError e) {
            System.err.println("Actual query: " + ObjectSerializer.toString(outputQuery2, new QName("CQLQuery")));
            throw e;
        }
    }

    /**
     * Test queries with a target that is constrained with a multiple ANDed
     * attributes. Test the sub-cases of having no filters, Having filters for a
     * user ID that is authorized for some values and for a user ID that is
     * authorized for no values.
     */
    @Test
    public void testTargetWithMultipleOredAttrbutesConstraint() throws Exception {
        String inputQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" // 
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"OR\">\n"//
                + "   <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"24\"/>\n"//
                + "   <Attribute name=\"codeSystem\" predicate=\"EQUAL_TO\" value=\"zz\"/>\n"//
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery inputQuery = makeQuery(inputQueryString);
        CQLQuery outputQuery = preprocessor.preprocess(inputQuery, GENERAL_USER);
        String expectedQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n"//
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Group logicRelation=\"OR\">\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z1\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z2\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z3\"/>\n"//
                + "    </Group>\n"//
                + "   </Association>\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Association name=\"v3.hl7_org.CV\" roleName=\"name\">\n"//
                + "     <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"false\"/>\n"//
                + "    </Association>\n"//
                + "   </Association>\n"//
                + "   <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"true\"/>\n"//
                + "   <Group logicRelation=\"OR\">\n"//
                + "    <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"24\"/>\n"//
                + "    <Attribute name=\"codeSystem\" predicate=\"EQUAL_TO\" value=\"zz\"/>\n"//
                + "   </Group>\n"//
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery expectedQuery = makeQuery(expectedQueryString);
        try {
            assertEquals(expectedQuery, outputQuery);
        } catch (AssertionError e) {
            System.err.println("Actual query: " + ObjectSerializer.toString(outputQuery, new QName("CQLQuery")));
            throw e;
        }
        String noAccessQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" //
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Attribute name=\"code\" predicate=\"IS_NULL\" />\n"
                + "   <Attribute name=\"code\" predicate=\"IS_NOT_NULL\" />\n"
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery noAccessQuery = makeQuery(noAccessQueryString);
        CQLQuery outputQuery2 = preprocessor.preprocess(inputQuery, UNAUTHORIZED_USER);
        try {
            assertEquals(noAccessQuery, outputQuery2);
        } catch (AssertionError e) {
            System.err.println("Actual query: " + ObjectSerializer.toString(outputQuery2, new QName("CQLQuery")));
            throw e;
        }
    }

    /**
     * Test queries with a target that is constrained with an association. Test
     * the sub-cases of having no filters, having filters on the target for a
     * user ID that is authorized for some values and for a user ID that is
     * authorized for no values.
     */
    @Test
    public void testAssociationParent() throws Exception {
        String inputQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" // 
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "   <Attribute name=\"codeSystem\" predicate=\"EQUAL_TO\" value=\"zz\"/>\n"//
                + "  </Association>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery inputQuery = makeQuery(inputQueryString);
        CQLQuery outputQuery = preprocessor.preprocess(inputQuery, GENERAL_USER);
        String expectedQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n"//
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Attribute name=\"codeSystem\" predicate=\"EQUAL_TO\" value=\"zz\"/>\n"//
                + "   </Association>\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Group logicRelation=\"OR\">\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z1\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z2\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z3\"/>\n"//
                + "    </Group>\n"//
                + "   </Association>\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Association name=\"v3.hl7_org.CV\" roleName=\"name\">\n"//
                + "     <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"false\"/>\n"//
                + "    </Association>\n"//
                + "   </Association>\n"//
                + "   <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"true\"/>\n"//
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery expectedQuery = makeQuery(expectedQueryString);
        try {
            assertEquals(expectedQuery, outputQuery);
        } catch (AssertionError e) {
            System.err.println("Actual query: " + ObjectSerializer.toString(outputQuery, new QName("CQLQuery")));
            throw e;
        }
        String noAccessQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" //
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Attribute name=\"code\" predicate=\"IS_NULL\" />\n"
                + "   <Attribute name=\"code\" predicate=\"IS_NOT_NULL\" />\n"
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery noAccessQuery = makeQuery(noAccessQueryString);
        CQLQuery outputQuery2 = preprocessor.preprocess(inputQuery, UNAUTHORIZED_USER);
        try {
            assertEquals(noAccessQuery, outputQuery2);
        } catch (AssertionError e) {
            System.err.println("Actual query: " + ObjectSerializer.toString(outputQuery2, new QName("CQLQuery")));
            throw e;
        }
    }

    /**
     * Test queries with a target that is constrained with an association. Test
     * the sub-cases of having filters on the child for a user ID that is
     * authorized for some values and for a user ID that is authorized for no
     * values.
     */
    @Test
    public void testAssociationChild() throws Exception {
        String inputQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" // 
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "   <Association name=\"v3.hl7_org.ED\" roleName=\"originalText\">\n"//
                + "    <Attribute name=\"integrityCheck\" predicate=\"EQUAL_TO\" value=\"true\"/>\n"//
                + "   </Association>\n"//
                + "  </Association>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery inputQuery = makeQuery(inputQueryString);
        CQLQuery outputQuery = preprocessor.preprocess(inputQuery, GENERAL_USER);
        String expectedQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n"//
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Association name=\"v3.hl7_org.ED\" roleName=\"originalText\">\n"//
                + "     <Group logicRelation=\"AND\">\n"//
                + "      <Attribute name=\"integrityCheck\" predicate=\"EQUAL_TO\" value=\"true\"/>\n"//
                + "      <Attribute name=\"mediaType\" predicate=\"EQUAL_TO\" value=\"3\"/>\n"//
                + "     </Group>\n"//
                + "    </Association>\n"//
                + "   </Association>\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Group logicRelation=\"OR\">\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z1\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z2\"/>\n"//
                + "     <Attribute name=\"code\" predicate=\"EQUAL_TO\" value=\"z3\"/>\n"//
                + "    </Group>\n"//
                + "   </Association>\n"//
                + "   <Association name=\"v3.hl7_org.CR\" roleName=\"qualifier\">\n"//
                + "    <Association name=\"v3.hl7_org.CV\" roleName=\"name\">\n"//
                + "     <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"false\"/>\n"//
                + "    </Association>\n"//
                + "   </Association>\n"//
                + "   <Attribute name=\"nullFlavor\" predicate=\"EQUAL_TO\" value=\"true\"/>\n"//
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery expectedQuery = makeQuery(expectedQueryString);
        assertEquals(serializeQuery(expectedQuery), serializeQuery(outputQuery));
        String noAccessQueryString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
                + "<CQLQuery xmlns=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n" //
                + " <Target name=\"v3.hl7_org.CD\" >\n" //
                + "  <Group logicRelation=\"AND\">\n"//
                + "   <Attribute name=\"code\" predicate=\"IS_NULL\" />\n"
                + "   <Attribute name=\"code\" predicate=\"IS_NOT_NULL\" />\n"
                + "  </Group>\n"//
                + " </Target>\n" //
                + "</CQLQuery>";
        CQLQuery noAccessQuery = makeQuery(noAccessQueryString);
        CQLQuery outputQuery2 = preprocessor.preprocess(inputQuery, UNAUTHORIZED_USER);
        assertEquals(serializeQuery(noAccessQuery), serializeQuery(outputQuery2));
    }
    
    private String serializeQuery(CQLQuery query) throws Exception {
        return ObjectSerializer.toString(query, new QName("CQLQuery"));
    }

    // /**
    // * Test queries with a target that is constrained with two anded
    // * associations. Test the sub-cases of having filters on the target and
    // one
    // * of the associated classes for a user ID that is authorized for some
    // * values and for a user ID that is authorized for no values.
    // */
    // @Test
    // public void testAndedAssociations() {
    // fail("Not yet implemented");
    // }
    //
    // /**
    // * Test queries with a target that is constrained with two or'ed
    // * associations. Test the sub-cases of having filters on the target and
    // one
    // * of the associated classes for a user ID that is authorized for some
    // * values and for a user ID that is authorized for no values.
    // */
    // @Test
    // public void testOredAssocations() {
    // fail("Not yet implemented");
    // }
    //
    // /**
    // * Test queries with a target that is constrained with the AND of two
    // pairs
    // * of or'ed associations. Test the sub-cases of having filters on the
    // target
    // * and one of the associated classes for a user ID that is authorized for
    // * some values and for a user ID that is authorized for no values.
    // */
    // @Test
    // public void testAndedOredAssociations() {
    // fail("Not yet implemented");
    // }
    //
    // /**
    // * Test queries with a target that is constrained with the OR of two pairs
    // * of and'ed associations. Test the sub-cases of having filters on the
    // * target and one of the associated classes for a user ID that is
    // authorized
    // * for some values and for a user ID that is authorized for no values.
    // */
    // @Test
    // public void testOredAndedAssociations() {
    // fail("Not yet implemented");
    // }

    private CQLQuery makeQuery(String queryString) throws Exception {
        // System.out.println("Making query:");
        // System.out.println(queryString);
        return makeQuery(new StringReader(queryString));
    }

    /**
     * @param reader
     * @return
     * @throws DeserializationException
     */
    private CQLQuery makeQuery(Reader reader) throws DeserializationException {
        InputSource queryInput = new InputSource(reader);
        CQLQuery newQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
        assertNotNull(newQuery);
        return newQuery;
    }
}

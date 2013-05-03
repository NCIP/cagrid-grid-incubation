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
package edu.emory.cci.domainModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Junit test for DomainModelAccessor
 * 
 * @author Mark Grand
 */
public class DomainModelAccessorTest {
    static final String DOMAIN_MODEL_FILE_NAME = "HL7AECGDataService-domainmodel.xml";
    static DomainModel model;

    /**
     * Deserialize the model that will be used for tests.
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {
            InputStream in = DomainModelAccessorTest.class.getResourceAsStream(DOMAIN_MODEL_FILE_NAME);
            if (in == null) {
                System.out.println("Could not find " + DOMAIN_MODEL_FILE_NAME + " as a resource.");
            } else {
                System.out.println("Found " + DOMAIN_MODEL_FILE_NAME);
                Reader reader = new InputStreamReader(in);
                model = (DomainModel) Utils.deserializeObject(reader, DomainModel.class);
            }
        } catch (Exception e) {
            System.out.println("Failed getting domain model that will drive the tests");
            e.printStackTrace(System.out);
            throw e;
        }
    }

    /**
     * @throws java.lang.Exception
     */
    // private static void setUpDomainModel() throws Exception {
    // BeanFactory bf = new BeanFactory();
    // ServiceResourceRef obj;
    // CompositeName name;
    // NamingContext nameCtx;
    // Hashtable<String, String> environment = new Hashtable<String, String>();
    // environment.put("java.naming.factory.initial",
    // "org.apache.naming.java.javaURLContextFactory");
    // environment.put("java.naming.factory.url.pkgs",
    // "org.apache.naming:org.apache.naming");
    // environment.put("org.apache.naming.synchronization", "true");
    // //Object bean = bf.getObjectInstance(obj, name, nameCtx, environment);
    //        
    // AxisClient engine = new AxisClient();
    // MessageContext mc = new MessageContext(engine);
    // accessor = new DomainModelAccessor();
    // }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#DomainModelAccessor()}
     * .
     */
    // @Test
    // public void testDomainModelAccessor() {
    // fail("Not yet implemented");
    // }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#DomainModelAccessor(gov.nih.nci.cagrid.metadata.dataservice.DomainModel)}
     * .
     */
    @Test
    public void testDomainModelAccessorDomainModel() {
        new DomainModelAccessor(model);
    }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#fetchDomainModel()}.
     */
    // @Test
    // public void testFetchDomainModel() {
    // fail("Not yet implemented");
    // }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#isExposedClass(java.lang.String)}
     * .
     */
    @Test
    public void testIsExposedClass() {
        DomainModelAccessor accessor = new DomainModelAccessor(model);
        assertTrue(accessor.isExposedClass("v3.hl7_org.InfiltrationRoute"));
        assertTrue(accessor.isExposedClass("v3.hl7_org.HemClinPracticeSetting"));
        assertFalse(accessor.isExposedClass("HemClinPracticeSetting"));
        assertFalse(accessor.isExposedClass("Zzxu8d2jdfh"));
        assertFalse(accessor.isExposedClass(""));
    }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#isAllowableAsTarget(java.lang.String)}
     * .
     */
    @Test
    public void testIsAllowableAsTarget() {
        DomainModelAccessor accessor = new DomainModelAccessor(model);
        assertTrue(accessor.isAllowableAsTarget("voc.meta.v3.hl7_org.vocabItem"));
        assertFalse(accessor.isAllowableAsTarget("vocabItem"));
        assertFalse(accessor.isAllowableAsTarget("InfiltrationRoute"));
        assertFalse(accessor.isAllowableAsTarget("abcdefg"));
        assertFalse(accessor.isAllowableAsTarget(""));
    }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#getUMLClassNames()}.
     */
    @Test
    public void testGetUMLClassNames() {
        DomainModelAccessor accessor = new DomainModelAccessor(model);
        String[] classNames = accessor.getUMLClassNames();
        assertEquals(1164, classNames.length);
    }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#classHasAttributes(java.lang.String)}
     * .
     */
    @Test
    public void testClassHasAttributes() {
        DomainModelAccessor accessor = new DomainModelAccessor(model);
        assertTrue(accessor.classHasAttributes("voc.meta.v3.hl7_org.vocabItem"));
        assertFalse(accessor.classHasAttributes("vocabItem"));
        assertFalse(accessor.classHasAttributes("GingivalRoute"));
        assertFalse(accessor.classHasAttributes("zaizag2"));

        // Inheritance: CD has attributes and and subclass CS does not
        assertTrue(accessor.classHasAttributes("v3.hl7_org.CD"));
        assertTrue(accessor.classHasAttributes("v3.hl7_org.CS"));
    }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#classHasAttribute(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testClassHasAttribute() {
        DomainModelAccessor accessor = new DomainModelAccessor(model);
        assertTrue(accessor.classHasAttribute("voc.meta.v3.hl7_org.vocabItem", "printName"));
        assertFalse(accessor.classHasAttribute("vocabItem", "printName"));
        assertFalse(accessor.classHasAttribute("vocabItem", "asdfi"));
        assertFalse(accessor.classHasAttribute("GingivalRoute", "printName"));
        assertFalse(accessor.classHasAttribute("zaizag2", "printName"));

        // Inheritance: CD has attributes and and subclass CS does not
        assertTrue(accessor.classHasAttribute("v3.hl7_org.CD", "code"));
        assertTrue(accessor.classHasAttribute("v3.hl7_org.CS", "code"));
    }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#getAttributeNames(java.lang.String)}
     * .
     */
    @Test
    public void testGetAttributeNames() {
        DomainModelAccessor accessor = new DomainModelAccessor(model);
        // Inheritance: CD has attributes and and subclass CS does not
        String[] cdAttributeNamesArray = accessor.getAttributeNames("v3.hl7_org.CD");
        assertEquals(6, cdAttributeNamesArray.length);
        List<String> cdAttributeList = Arrays.asList(cdAttributeNamesArray);
        assertTrue(cdAttributeList.contains("codeSystemVersion"));
        assertTrue(cdAttributeList.contains("displayName"));
        assertTrue(cdAttributeList.contains("codeSystem"));
        assertTrue(cdAttributeList.contains("code"));
        assertTrue(cdAttributeList.contains("codeSystemName"));
        assertTrue(cdAttributeList.contains("nullFlavor"));

        String[] csAttributeNamesArray = accessor.getAttributeNames("v3.hl7_org.CS");
        assertEquals(6, csAttributeNamesArray.length);
        List<String> csAttributeList = Arrays.asList(csAttributeNamesArray);
        assertTrue(csAttributeList.contains("codeSystemVersion"));
        assertTrue(csAttributeList.contains("displayName"));
        assertTrue(csAttributeList.contains("codeSystem"));
        assertTrue(csAttributeList.contains("code"));
        assertTrue(csAttributeList.contains("codeSystemName"));
        assertTrue(csAttributeList.contains("nullFlavor"));
    }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#getAssociatedClass(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetAssociatedClass() {
        DomainModelAccessor accessor = new DomainModelAccessor(model);
        // Inheritance: CD has associations and and subclass CS does not
        assertEquals("v3.hl7_org.CR", accessor.getAssociatedClass("v3.hl7_org.CD", "qualifier"));
        assertEquals("v3.hl7_org.CD", accessor.getAssociatedClass("v3.hl7_org.CD", "translation"));
        assertEquals("v3.hl7_org.ED", accessor.getAssociatedClass("v3.hl7_org.CD", "originalText"));

        assertEquals("v3.hl7_org.CR", accessor.getAssociatedClass("v3.hl7_org.CS", "qualifier"));
        assertEquals("v3.hl7_org.CD", accessor.getAssociatedClass("v3.hl7_org.CS", "translation"));
        assertEquals("v3.hl7_org.ED", accessor.getAssociatedClass("v3.hl7_org.CS", "originalText"));
    }

    /**
     * Test method for
     * {@link edu.emory.cci.domainModel.DomainModelAccessor#getAssociationsAndClasses(java.lang.String)}
     * .
     */
    @Test
    public void testGetAssociationsAndClasses() {
        DomainModelAccessor accessor = new DomainModelAccessor(model);

        HashMap<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("qualifier", "v3.hl7_org.CR");
        expectedMap.put("translation", "v3.hl7_org.CD");
        expectedMap.put("originalText", "v3.hl7_org.ED");

        // Inheritance: CD has associations and and subclass CS does not
        assertEquals(expectedMap, accessor.getAssociationsAndClasses("v3.hl7_org.CD"));
        assertEquals(expectedMap, accessor.getAssociationsAndClasses("v3.hl7_org.CS"));
    }

}
